import sys
import cantools

from db_converter import Parser, is_frame_name_valid

in_file = sys.argv[1]
can_subsystem = sys.argv[2] # Marking for which subsystem of can this is
dbc_file = in_file.split('.')[0] + ".dbc"

f = open(in_file, 'r')
p = Parser(f.readlines())
frames=[]

db = cantools.database.Database()
can_db = cantools.db
senders=[]

while True:
  e = p.read_frame_block()
  if (not e):
      print("Read complete")
      break # Reading complete!
  add = True
  e.process()
  for pos, x in enumerate(frames):
      if x.get_id() == e.get_id():
          print("Duplicate entry for {}".format(x.get_name()))
          # Check - Does the new instance have more data?
          if e.get_signal_count() > x.get_signal_count():
              print("Replacing {0}. {1} signals to {2} signals".format(e.get_name(), e.get_signal_count(), x.get_signal_count()))
              frames[pos] = e
          add = False # Still mark as false as we replaced rather than insert
  if add:
      frames.append(e) # Needed to generate enums late
  if is_frame_name_valid(e.get_name()) and add: # Only create a class for frames that aren't diag
    signals = []
    vals = None
    for sig in e.__code_objects__:
      if sig.__is_enum__:
        vals = {}
        for enum in sig.__enum__data__:
          vals[enum.raw] = enum.key_name
      signals.append(can_db.Signal(name=sig.__name__,
                                    start=sig.__data_offset__,
                                    length=sig.__data_length__,
                                    byte_order="little_endian",
                                    is_signed=False,
                                    initial=None,
                                    scale=1,
                                    offset=0,
                                    minimum=None,
                                    maximum=None,
                                    unit=sig.__raw_unit__,
                                    comment=sig.__description__,
                                    choices=vals,
                                    receivers=None,
                                    is_multiplexer=False,
                                    is_float=False,
                                    decimal=None))

    sender = e.get_name().split("_")[0]
    if not sender in senders:
      senders.append(sender)
    msg = can_db.Message(frame_id=int(e.get_id(), base=16),
                          name=e.get_name(),
                          length=8,
                          signals=signals,
                          comment="",
                          is_extended_frame=False,
                          senders=[sender],
                          bus_name=None)

    db.messages.append(msg)

for sender in senders:
  db.nodes.append(can_db.Node(name=sender, comment=None))

cantools.database.dump_file(db, dbc_file)
