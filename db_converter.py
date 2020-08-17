import os
import sys

in_file = open(sys.argv[1], 'r')
can_subsystem = sys.argv[2] # Marking for which subsystem of can this is

output_dir = "app/src/main/java/com/rndash/mbheadunit/nativeCan/can{0}/".format(can_subsystem.upper())

print("OUTPUT DIR: {0} - CANBUS {1}".format(output_dir, can_subsystem))

class enum_object:
    def __init__(self, line: str, existing_names: []):
        self.line = line
        self.has_error = False # Marked if parsing failed initially
        self.raw = self.line.split("RAW:")[1].split("-")[0].strip()
        if "/" in self.line: # check if we can generate a valid key name
            self.desc = self.line.split(" - ")[1].split("/")[0].strip()
            self.key_name = self.line.split("/")[-1].strip().replace(" ", "_").strip()
        else: # No key name? WTF
            self.has_error = True # Mark so we can comment the original data in the file
            self.desc = self.line.split(" - ")[1]
            self.key_name = self.desc.upper().replace(" ", "_").replace("-", "_").replace("(","").replace(")","").strip()

        if self.key_name in existing_names:
            print("Enum existing value detected")
            self.key_name = self.key_name + "_{0}".format(self.raw) # Ensure enums are not duplicate values!
        self.key_name = self.key_name.encode("ascii", errors="ignore").decode().upper() # Strip all the German characters from string (Compiler no likey!)


class code_object:
    def __init__(self, lines: []):
        self.lines = lines
        self.__name__ = None
        self.__description__ = None
        self.__lines__ = lines
        self.__data_offset__ = None
        self.__data_length__ = None
        self.__data_type__ = None
        self.__is_enum__ = False
        self.__enum__data__ = []
        self.__function_getter__ = None
        self.__function_setter__ = None
    
    def get_signal_attributes(self, line: str):
        self.__name__ = line.split(": ")[1].split(",")[0].replace(" ", "_").upper().strip().encode("ascii", errors="ignore").decode() # Strip all the German characters from string (Compiler no likey!)
        self.__data_offset__ = int(line.split("OFFSET ")[1].split(" LEN ")[0])
        self.__data_length__ = int(line.split("LEN ")[1].split(" ")[0])
        if (self.__data_type__ == "E"): # Enum temp, replace with name of enum
            self.__data_type__ = self.__name__
        try:
            self.__description__ = line.split(" - ")[1]
        except IndexError:
            print("Error generating description for {0}".format(self.__name__))
        self.__function_getter__ = "get_{0}".format(self.__name__.lower())
        self.__function_setter__ = "set_{0}".format(self.__name__.lower())
        

    def process_object(self):
        e_list = []
        for l in self.__lines__:
            if l.startswith("B: "):
                self.__data_type__ = "Boolean"
                return self.get_signal_attributes(l)
            elif l.startswith("I: "):
                self.__data_type__ = "Int"
                return self.get_signal_attributes(l)
            elif l.startswith("E: "):
                self.__is_enum__ = True
                self.__data_type__ = "E"
                self.get_signal_attributes(l) # Don't return. Need to process rest of values
            elif self.__is_enum__ and l.startswith("RAW: "):
                try:
                    enum = enum_object(l, e_list)
                    self.__enum__data__.append(enum)
                    e_list.append(enum.key_name)
                except IndexError:
                    print("WARNING: Ignoring undefined enum value - {0} in {1}".format(l, self.__name__))


    # ECU Name needed for function call!
    def build_function_getter(self, ecuName: str) -> str:
        fun_name = "CanBusNative.getECUParameter{0}".format(can_subsystem)
        ecu_param = "Can{0}Addrs.{1}".format(can_subsystem, ecuName)
        buf = ""
        try:
            if self.__data_offset__ == None:
                raise Exception("Missing data offset")
            if self.__data_length__ == None:
                raise Exception("Missing data length")
            if self.__data_type__ == None:
                raise Exception("Missing data type")
            # Emplace comment based on description
            if self.__description__:
                buf = "/** Gets {0} **/\n".format(self.__description__)
            else:
                buf = "/** UNKNOWN DESCRIPTION **/\n"
            buf += "fun {0}() : {1} = ".format(self.__function_getter__, self.__data_type__)
            if self.__data_type__ == "Boolean": # Bo olean - just check return value is not 0
                buf += fun_name + "({0}, {1}, {2}) != 0\n".format(ecu_param, self.__data_offset__, self.__data_length__)
            elif self.__data_type__ == "Int": # Int - return value is OK!
                buf += fun_name + "({0}, {1}, {2})\n".format(ecu_param, self.__data_offset__, self.__data_length__)
            else: # Enum value - need to write a 'when' code block
                buf += "when({0}({1}, {2}, {3})) {{\n".format(fun_name, ecu_param, self.__data_offset__, self.__data_length__)
                for enum in self.__enum__data__:
                    buf += "\t {0} -> {1}.{2}\n".format(enum.raw, self.__data_type__, enum.key_name)
                buf += "\t else -> throw Exception(\"Invalid raw value for {0}\")\n".format(self.__data_type__)
                buf +="}\n"
            return buf
        except Exception as e:
            print("WARNING: Code generation error for {0} failed due to {1}".format(self.__name__, e))
            return ""


    def build_function_setter(self) -> str:
        buf = ""
        try:
            if self.__data_offset__ == None:
                raise Exception("Missing data offset")
            if self.__data_length__ == None:
                raise Exception("Missing data length")
            if self.__data_type__ == None:
                raise Exception("Missing data type")
            # Emplace comment based on description
            if self.__description__:
                buf = "/** Sets {0} **/\n".format(self.__description__)
            else:
                buf = "/** UNKNOWN DESCRIPTION **/\n"
            buf += "fun {0}(f: CanFrame, p: {1}) = ".format(self.__function_setter__, self.__data_type__)
            if self.__data_type__ == "Boolean": # Bo olean - just check return value is not 0
                buf += "CanBusNative.setFrameParameter(f, {0}, {1}, if(p) 1 else 0)\n".format(self.__data_offset__, self.__data_length__)
            elif self.__data_type__ == "Int": # Int - return value is OK!
                buf += "CanBusNative.setFrameParameter(f, {0}, {1}, p)\n".format(self.__data_offset__, self.__data_length__)
            else: # Extract value from enum
                buf += "CanBusNative.setFrameParameter(f, {0}, {1}, p.raw)\n".format(self.__data_offset__, self.__data_length__)
            return buf
        except Exception as e:
            print("WARNING: Code generation error for {0} failed due to {1}".format(self.__name__, e))
            return ""

    def generate_enum_code(self) -> str:
        if not self.__enum__data__: # Return None if we are not an enum
            return None
        buf = "enum class {0}(val raw: Int) {{\n".format(self.__data_type__)
        for enum in self.__enum__data__:
            buf += "\t /** {0} **/\n".format(enum.desc)
            buf += "\t {0}({1}),".format(enum.key_name, enum.raw)
            if enum.has_error:
                buf += " /* PROCESSING ERROR. Original Data: {0} */".format(enum.line)
            buf += "\n"
        buf += "}\n"
        return buf

# ECU Frame data from processed text
class frame_code:
    def __init__(self, header: str):
        self.__header__ = header
        self.lines = []
        self.__code_objects__ = []

    def get_id(self) -> str:
        return self.__header__.split("(")[1].replace(")", "")
    
    def get_name(self) -> str:
        return self.__header__.split(" ")[1]

    def add_line(self, l: str):
        self.lines.append(l)

    def get_lines(self) -> []:
        return self.lines

    def get_signal_count(self) -> int:
        return len(self.__code_objects__)

    def process(self):
        buffer = []
        for line in self.lines:
            if line.startswith("E:"): # New enum!
                if len(buffer) != 0:
                    self.__code_objects__.append(code_object(buffer)) # Add old buffer
                    buffer = [] # Now empty buffers content
                buffer.append(line)
            elif line.startswith("RAW:"): # Continued enum
                buffer.append(line)
            else: # its an integer or boolean
                if len(buffer) != 0: # Nothing to buffer first
                    self.__code_objects__.append(code_object(buffer)) # Add old buffer
                    buffer = [] # Now empty buffers content
                self.__code_objects__.append(code_object([line])) # Buffer current line
        if len(buffer) > 1: # Any remaining buffer should be added to code_objects
            self.__code_objects__.append(code_object(buffer))
        # Now process each code object!
        for o in self.__code_objects__:
            o.process_object()

    def generate_class_body(self) -> str: # Returns a all the code for the ECU Frame object
        buf = ""
        for o in self.__code_objects__:
            buf += o.build_function_getter(self.get_name()) + "\n"
            buf += o.build_function_setter() + "\n"
        return buf

    def generate_enum_block(self) -> str:
        buf = ""
        for obj in self.__code_objects__:
            s = obj.generate_enum_code()
            if s:
                buf += s + "\n"
        return buf

class Parser:
    def __init__(self, lines: []):
        self.__lines__ = lines

    def sanitize_line(self, input: str) -> str:
        return input.replace("\n", "").replace("\t", "")

    def read_frame_block(self) -> frame_code:
        in_frame = False
        ecu_frame = None
        while True:
            if len(self.__lines__) == 0:
                return ecu_frame
            if self.__lines__[0].strip().startswith("FRAME"): # its data
                if in_frame:
                    return ecu_frame
                if not in_frame:
                    in_frame = True
                    ecu_frame = frame_code(self.sanitize_line(self.__lines__[0]))
            elif len(self.__lines__[0].strip().split(" ")) > 1: # contains data
                ecu_frame.add_line(self.sanitize_line(self.__lines__[0]))
            self.__lines__ = self.__lines__[1:]


# Function to generate JVM Kotlin class
def generate_class_content(frame: frame_code) -> str:
    buf =  """
@file:Suppress("unused", "FunctionName")
package com.rndash.mbheadunit.nativeCan.can{2}
import com.rndash.mbheadunit.CanFrame // AUTO GEN
import com.rndash.mbheadunit.nativeCan.CanBusNative // AUTO GEN

/**
 *   Generated by db_converter.py
 *   Object for {0} (ID {1})
**/

object {0} {{\n
    """.format(frame.get_name(), frame.get_id(), can_subsystem)
    for l in frame.generate_class_body().split("\n"):
        buf += "\t" + l + "\n"
    buf += "}\n"
    return buf

def generate_addr_file(f: []):
    buf = """
@file:Suppress("unused")
package com.rndash.mbheadunit.nativeCan.can{0}

/**
 *   Generated by db_converter.py
 *   ECU Address data for CAN {0}
**/

enum class Can{0}Addrs(val addr: Int) {{
""".format(can_subsystem)
    for e in f: # Get all the frame ID's
        buf += "\t{0}({1}),\n".format(e.get_name(), e.get_id())
    buf += "}\n"
    return buf

def generate_enum_file(f: []):
    buf = """
// Disable some inspections due to enum names/values not matching kotlin style
@file:Suppress("unused", "EnumEntryName", "ClassName")
package com.rndash.mbheadunit.nativeCan.can{0}

/**
 *   Generated by db_converter.py
 *   ECU Enum values for data on CAN {0}
**/

""".format(can_subsystem)
    enums_seen=[]
    for x in f:
        string = x.generate_enum_block()
        if string:
            # Check if enum already exists, if it does, ignore it!
            enum_name = string.split("\n")[0].split("enum class ")[1].split("(")[0]
            print(enum_name)
            if enum_name not in enums_seen:
                enums_seen.append(enum_name)
                buf += string
            else:
                print("Duplicate enum {0}. Ignoring".format(enum_name))
    return buf




p = Parser(in_file.readlines())
frames=[]

# Returns if the frame can be added to DB (Filters out diagnostic messages)
def is_frame_name_valid(f: str) -> bool:
    if f.startswith("D_RQ") or f.startswith("D_RS") or f.startswith("SD_") or f.startswith("APPL_") or f.startswith("NM_") or f.startswith("SG_"):
        return False
    return True

while True:
    e = p.read_frame_block()
    if (not e):
        print("Read complete")
        break # Reading complete!
    add = True
    e.process()
    for pos, x in enumerate(frames):
        if x.get_id() == e.get_id():
            print("Duplicate entry for ", x.get_name())
            # Check - Does the new instance have more data?
            if e.get_signal_count() > x.get_signal_count():
                print("Replacing {2}. {0} signals to {1} signals".format(e.get_signal_count(), x.get_signal_count(), e.get_name()))
                frames[pos] = e
            add = False # Still mark as false as we replaced rather than insert
    if add:
        frames.append(e) # Needed to generate enums late
    if is_frame_name_valid(e.get_name()) and add: # Only create a class for frames that aren't diag
        o = open(output_dir + e.get_name() + ".kt", "w")
        o.write(generate_class_content(e))    

o = open(output_dir + "Can{0}Addrs.kt".format(can_subsystem), "w")
o.write(generate_addr_file(frames)) # Generate all the enums into 1 file for ECU Addresses

o = open(output_dir + "Can{0}Enums.kt".format(can_subsystem), "w")
o.write(generate_enum_file(frames))