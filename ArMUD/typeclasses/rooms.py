"""
Room

Rooms are simple containers that are to gps metadata of their own.

"""

from evennia import DefaultRoom, utils
from characters import Character
from npc import Npc
from mob import Mob
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
        self.db.cweather = ""                # Room Weather


    def at_object_receive(self, obj, source_location):
        if utils.inherits_from(obj, Npc): # An NPC has entered
            pass
        else:
            # Else if a PC has entered
            if utils.inherits_from(obj, Character):
                # Cause the character to look around
                #obj.execute_cmd('look')

                for item in self.contents:
                    # Any NPCs in the room ?
                    if utils.inherits_from(item, Npc):
                        # Notify NPCs that a PC entered the room
                        item.at_char_entered(obj)
                        tickerhandler.add(item,1)
                    
                    # if item in room not self
                    if item.dbref != obj.dbref:
                        # if item is of class Character
                        if utils.inherits_from(item, Character):
                            obj.msg("DATA,char_add," + item.name + item.dbref)
                            item.msg("DATA,char_add," + obj.name + obj.dbref)
                        # else if item is of class Mob
                        elif utils.inherits_from(item, Mob):
                            obj.msg("DATA,char_add," + item.name + item.dbref)
                        # else if item is of class Npc
                        elif utils.inherits_from(item, Npc):
                            obj.msg("DATA,char_add," + item.name + item.dbref)
                        # else (an object)
                        else:
                            obj.msg("DATA,obj_add," + item.name + item.dbref)



    def at_object_leave(self, obj, target_location):
        if utils.inherits_from(obj, Character):
            for item in self.contents:
                # if item in room not self
                if item.dbref != obj.dbref:
                    # if item is of class Character
                    if utils.inherits_from(item, Character):
                        obj.msg("DATA,char_remove," + item.name + item.dbref)
                        item.msg("DATA,char_remove," + obj.name + obj.dbref)
                    # else if item is of class Mob
                    elif utils.inherits_from(item, Mob):
                        obj.msg("DATA,char_remove," + item.name + item.dbref)
                    # else if item is of class Npc
                    elif utils.inherits_from(item, Npc):
                        obj.msg("DATA,char_remove," + item.name + item.dbref)
                    # else (an object)
                    else:
                        obj.msg("DATA,obj_remove," + item.name + item.dbref)


        if utils.inherits_from(obj, Npc): # An NPC has left
            pass
        elif utils.inherits_from(obj, Mob): # A Mob has left
            pass
        else:
            # Else if a PC has left the room
            if utils.inherits_from(obj, Character):
                # Any NPCs in the room ?
                for item in self.contents:
                    if utils.inherits_from(item, Npc):
                        # Notify NPCs that a PC left the room
                        tickerhandler.remove(item,1)
