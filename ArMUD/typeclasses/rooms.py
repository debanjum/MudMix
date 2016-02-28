"""
Room

Rooms are simple containers that has no location of their own.

"""

from evennia import DefaultRoom, utils
from characters import Character
from npc import Npc
from evennia import TICKER_HANDLER as tickerhandler

class Room(DefaultRoom):
    """
    Rooms are like any Object, except their location is None
    (which is default). They also use basetype_setup() to
    add locks so they cannot be puppeted or picked up.
    (to change that, use at_object_creation instead)

    See examples/object.py for a list of
    properties and methods available on all Objects.
    """

    def at_object_creation(self):
        "Called at first creation of the object"
        super(Room, self).at_object_creation()
        self.db.roomtype_key = "Generic"     # Room Type Key
        self.db.roomtype_value = "Generic"   # Room Type Value
        self.db.location = ""                # Room Location


    def at_object_receive(self, obj, source_location):
        if utils.inherits_from(obj, Npc): # An NPC has entered
            pass
        else:
            # Else if a PC has entered
            if utils.inherits_from(obj, Character):
                # Cause the character to look around
                obj.execute_cmd('look')
                # Any NPCs in the room ?
                for item in self.contents:
                    if utils.inherits_from(item, Npc):
                        # Notify NPCs that a PC entered the room
                        item.at_char_entered(obj)
                        tickerhandler.add(item,1)

                    if utils.inherits_from(item, Character) and item.dbref != obj.dbref:
                        obj.msg("DATA,char_add," + item.name + item.dbref)
			item.msg("DATA,char_add," + obj.name + obj.dbref)


    def at_object_leave(self, obj, target_location):
        if utils.inherits_from(obj, Character):
            for item in self.contents:
                if utils.inherits_from(item, Character) and item.dbref != obj.dbref:
                    item.msg("DATA,char_remove," + obj.name + obj.dbref)


        if utils.inherits_from(obj, Npc): # An NPC has left
            pass
        else:
            # Else if a PC has left the room
            if utils.inherits_from(obj, Character):
                # Any NPCs in the room ?
                for item in self.contents:
                    if utils.inherits_from(item, Npc):
                        # Notify NPCs that a PC left the room
                        tickerhandler.remove(item,1)
