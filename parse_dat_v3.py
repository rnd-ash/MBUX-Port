import os
import struct
import codecs
import sys
from typing import Tuple
from googletrans import Translator

file=open(sys.argv[1], "rb")
output = codecs.open(sys.argv[2], 'w', encoding='utf8')
translate = False
try:
    sys.argv[3]
    translate = True
except:
    pass

translator = Translator(service_urls=["translate.google.com", "translate.google.co.kr",
                      "translate.google.at", "translate.google.de",
                      "translate.google.ru", "translate.google.ch",
                      "translate.google.fr", "translate.google.es"])

class Table:
    def __init__(self, name: str, id: int, list: tuple):
        self.name = name
        self.id = id
        self.list = list

class can_signal:
    def __init__(self, name: str, desc: str, offset: int, len: int, unit: str):
        self.name = name
        self.desc = desc
        self.offset = offset
        self.len = len
        self.unit = unit
        self.enum = None

    def to_string(self) -> str:
        data_type = ""
        if self.len == 1:
            data_type = "B"
        elif self.enum:
            data_type = "E"
        else:
            data_type = "I"
        ret = "{4}: {0}, OFFSET {1} LEN {2} - {3}".format(self.name, self.offset, self.len, self.desc, data_type)
        if self.unit:
            ret += " UNIT: {0}".format(self.unit)
        if self.enum:
            for e in self.enum.list:
                ret += " \n\t\t\tRAW: {0} - {1}".format(e[0], e[1])
        return ret

    def register_enum(self, t: Table):
        self.enum = t

class ecu_frame:
    def __init__(self, name: str, id: int, numSignals: int):
        self.name = name
        self.id = id
        self.numSignals = numSignals
        self.signal_list = []
    
    def add_signal(self, sig: can_signal):
        self.signal_list.append(sig)

    def to_string(self) -> str:
        ret = "\tFRAME {0} ({1})\n".format(self.name, "0x%04X" % self.id)
        for s in self.signal_list:
            ret += "\t\t" + s.to_string() + "\n"
        if translate:
            try:
                print("TRANSLATING {0}".format(self.name))
                to_translate = ret.encode('utf8').decode('utf8') + "\n"
                ret = ""
                for line in translator.translate(to_translate, src="DE", dest="EN").text.split("\n"):
                    # Re-instate the tab markers based on keywords
                    if line.startswith("FRAME"):
                        ret += "\t" + line + "\n"
                    elif line.startswith("RAW"):
                        ret += "\t\t\t" + line + "\n"
                    else:
                        ret += "\t\t" + line + "\n"
            except Exception as e:
                # Translation failed
                print("ERROR TRANSLATION FAILED due to {0}".format(e))
        return ret


class ecu:
    def __init__(self, name: str):
        self.name = name
        self.frames = []

    def put_frame(self, f: ecu_frame):
        self.frames.append(f)
    
    def to_string(self) -> str:
        ret = self.name + "\n"
        for f in self.frames:
            ret += f.to_string()
        return ret


class Parser:
    def __init__(self, bytes: bytearray):
        self.__bytes__ = bytes
        self.__byte_buffer__ = bytearray()
        self.__catTable__=[]
        self.__read_first_ecu__ = False

    def read_range(self, len: int) -> bytearray:
        ret = self.__bytes__[0:len]
        self.__bytes__ = self.__bytes__[len:]
        return ret

    def parse_str(self, b: bytearray) -> str:
        return b.decode('iso-8859-1')

    def read_until(self, target: bytearray):
        curr = bytearray(len(target))
        while curr != target:
            curr = bytearray(curr[1:len(target)])
            x = self.read_range(1)[0]
            self.__byte_buffer__.append(x)
            curr.append(x)

    def readString(self):
        len = self.read_range(1)[0]
        while self.__bytes__[0] == 0x00:
            self.read_range(1)
        return self.parse_str(self.read_range(len))


    def readTableBlock(self):
        self.read_until("CCatTable".encode('iso-8859-1')) # Read just up until CCatTable part
        while True:
            table_id = int.from_bytes(bytes(self.read_range(2)), "little") # ID of the table (2 bytes)
            tab_name = self.readString().replace("Tab", "") # Name of the table
            #print("Found table {0} (ID: {1})".format(tab_name, table_id))
            elm_ids=[] # List of numbers
            elm_names=[] # List of names
            elm_count = self.read_range(1)[0] # Number of raw values
            self.read_range(1) # Padding
            for i in range(0, elm_count):
                value = int.from_bytes(bytes(self.read_range(4)), "little")
                elm_ids.append(value)
            elm_count = self.read_range(1)[0] # Number of strings
            self.read_range(1) # Padding
            for i in range(0, elm_count):
                elm_names.append(self.readString())
            entrys=[]
            for i in range(0, min(len(elm_ids), len(elm_names))): # Min call because sometimes values are unused
                entrys.append((elm_ids[i], elm_names[i]))
            self.__catTable__.append(Table(tab_name, table_id, entrys)) # Place table entries
            if self.read_range(2) == bytearray([0x00, 0x00]):
                break

    def readECUBlock(self) -> ecu:
        ecu_number = int(struct.unpack("<H", self.read_range(2))[0])
        ecu_name = self.readString()
        print("ECU {0}: (Number {1})".format(ecu_name, ecu_number))
        if not self.__read_first_ecu__:
            r = self.read_range(8)
            print(r)
            self.readString() # CECU
        else:
            r = self.read_range(6)
            print(r)
        frames = []
        while True:
            res = self.readFrameBlock()
            frames.append(res[1])
            if res[0] == True:
                break
        e = ecu(ecu_name)
        for i in frames:
            e.put_frame(i)
        return e


    def readFrameBlock(self) -> Tuple[bool, ecu_frame]:
        frame_number = int(struct.unpack("<H", self.read_range(2))[0])
        print("FRAME NUMBER {0}".format(frame_number))
        frame_name = self.readString()
        self.read_range(1) # null pad before start of string
        frame_id = int(struct.unpack("<H", self.read_range(2))[0])
        print("\tFRAME: {0} ({1})".format(frame_name, "0x%04X" % frame_id))
        r = self.read_range(8)
        print("METADATA {1} (PRE SIG COUNT) {0}".format(r.hex(), frame_name))
        frame_signal_count = r[4]
        if not self.__read_first_ecu__:
            print(self.read_range(2))
            self.readString() # CFrame
        signal_count = 0
        ret = False
        signals = [] # List of all signals
        while signal_count != frame_signal_count:
            signal_count += 1
            signals.append(self.readSignalBlock(frame_name))
            signal_end = self.read_range(2) # End for signal
            print("SIG END: {0}".format(signal_end))
            if signal_end == bytearray([0x05, 0x80]): # End of Signal entry
                break
            elif signal_end == bytearray([0x03, 0x80]): # End of Frame entry
                ret = True
                break
            elif signal_end[1] != 0x80: # End of ECU Table
                ret = True
                break
        if not self.__read_first_ecu__:
            self.__read_first_ecu__ = True

        cf = ecu_frame(frame_name, frame_id, signal_count)
        for i in signals:
            cf.add_signal(i)
        return ret, cf

    def readSignalBlock(self, f: str) -> can_signal:
        sig_name = ""
        sig_desc = ""
        sig_unit = ""
        sig_id = self.read_range(3)[1]
        sig_name = self.readString()
        sig_offset = int(self.read_range(1)[0])
        sig_len = int(self.read_range(1)[0])
        print("SIGNAL METADATA ({0} - {1}): {2}".format(f, sig_name, self.read_range(14).hex()))
        if self.__bytes__[0] != 0x00:
            sig_unit = self.readString() #CSignal
        else:
            self.read_range(1) # Skip over
        sig_desc = self.readString()
        #print("\t\tSignal: "+ sig_name + " ID:"+str(sig_id)+" - OFFSET:"+str(sig_offset)+" LEN:"+str(sig_len)+" (UNIT: "+ sig_unit +"): "+sig_desc)
        return can_signal(sig_name, sig_desc, sig_offset, sig_len, sig_unit)

    def parseFile(self):
        self.read_range(8)
        print("DATABASE NAME: " + self.readString())
        print(self.read_range(11))
        print("Columns: " + self.readString() + " " + self.readString())
        print(self.read_range(8))
        print(self.readString())
        print("PROCESSING ECU TABLE")
        ecus = []
        while True:
            ecus.append(self.readECUBlock())
            if self.__bytes__[:2] == bytearray([0xFF, 0xFF]): # Check for end of ECU block
                break
        print("PROCESSING ENUM TABLE")
        self.readTableBlock()
        print("COMPLETE - WRITING OUTPUT")
        # Now link the Enum table to signals
        for e in ecus:
            for f in e.frames:
                for s in f.signal_list:
                    for k in self.__catTable__:
                        if s.name == k.name:
                            s.register_enum(k)
        for e in ecus:
            output.write(e.to_string() + "\n")
        print("DONE. HAVE A NICE DAY")

buffer = bytearray()
while True:
    x = file.read(1)
    if len(x) == 0:
        break
    buffer += x

p = Parser(buffer)
p.parseFile()