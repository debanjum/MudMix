"""
Commands

Commands describe the input the player can do to the game.

"""

from evennia import Command as BaseCommand
from evennia.commands.default.muxcommand import MuxCommand
from evennia import default_cmds, utils
from evennia.typeclasses import managers
from world import rules
from typeclasses.characters import Character
from typeclasses.mob import Mob
from typeclasses.npc import Npc
from typeclasses.objects import Weapon
from typeclasses.objects import Vegetable
import overpass, json

class EatVegetable(BaseCommand):
    """
    Eat the vegetable
    Usage:
      eat <vegetable>
    This will eat a vegetable in the same room with you bare hands.
    """
    key = "eat"
    aliases = ["consume"]
    locks = "cmd:all()"
    help_category = "General"

    def func(self):
        "Implements eating"

        # pass iff argument. assumes one argument passed
        if not self.args:
            caller = self.caller
            caller.msg("You need to pick a single target to attack.")
            return

        character = self.caller
        item = self.args[1:]    # gets rid of the 1st space

        # search for object referred in argument
        if '#' in item:  # if '#' in argument search by dbref
            vegetablename = "#"+item.split('#')[1]

            # object in room with same dbref
            vegetable = [obj for obj in character.location.contents_get() if vegetablename==obj.dbref]

            # if vegetable asked for in room
            if vegetable:
                vegetable = vegetable[0]
            else:
                character.msg("%s doesn't exist in this room" % item)
                return
        else: # else search in room for object with same name
            vegetable = character.search(item, location=character.location)
        
        # if object not of class vegetable
        if not (utils.inherits_from(vegetable, Vegetable)):
            character.msg("%s can't be eaten" % item)
            return

        # if vegetable to be eaten is in the room
        string = ("You eat your %s" % vegetable)
        character.msg(string)
        character.db.HP += vegetable.db.heal
        character.db.STR += vegetable.db.strength
        vegetable.delete()


class DefAct(BaseCommand):
    """
    do default action on object based on its type(class)

    Usage:
      defact <object>

    This will do the default action that is associated with the objects type
    """

    key = "defact"
    aliases = ""
    locks = "cmd:all()"
    help_category = "General"

    def func(self):
        # pass iff argument. assumes one argument passed
        if not self.args:
            self.caller.msg("You need to pick a single target to act on.")
            return

        character = self.caller
        itempassed = self.args[1:]  # gets rid of the 1st space

        # search for item referred in argument
        # by dbref
        if '#' in itempassed:  # if '#' in argument search by dbref
            itemname = "#"+itempassed.split('#')[1]

            # check if object with requested dbref on self
            item = [obj for obj in character.contents if itemname==obj.dbref]
            # check if object with requested dbref in room
            if not item:
                item = [obj for obj in character.location.contents if itemname==obj.dbref]

            # if item asked for was found on self or in room
            if item:
                item = item[0]
            # if no such item found
            else:
                character.msg("%s doesn't exist in this room" % itempassed)
                return
        # by name
        else: 
            # search on self for object with same name
            item = character.search(itempassed, location=character)
            # if not found on self
            if not item:
                # search in room for object with same name
                item = character.search(itempassed, location=character.location)
            # if still not found
            if not item:
                character.msg("%s doesn't exist here" % itempassed)
                return

        # if object of class character return
        if (utils.inherits_from(item, Character)):
            character.msg("You can't %s a character" % itempassed)
            return

        # if object of class weapon
        if (utils.inherits_from(item, Weapon)):
            # if weapon was equipped
            if character.db.equip==item.dbref:
                # dequip
                character.db.equip = ""
                character.msg("You've dequipped yourself")
            # if weapon was dequipped
            else:
                character.db.equip = item.dbref
                character.msg("You've equipped yourself with %s" % item.name)
#                # search for any pre-existing equipped weapons on self
#                weapons = [obj for obj in character.contents if utils.inherits_from(obj, Weapon) and obj.dbref!=item.dbref and obj.db.equip]
#                # set them to dequipped (as player can be equipped with only one weapon)
#                for weapon in weapons:
#                    weapon.db.equip = False
#                # set current selected item to equipped
#                item.db.equip = True
            return 

        # if item to be acted on is in the room
        command=item.db.defact+" "+ item.name+item.dbref
        character.execute_cmd(command)


class Location(BaseCommand):
    """
    get player real-world location

    Usage:
      location <latitude> <longitude> <target>

    This will retrieve enclosing feature metadata related to
    the submitted location from openstreetmaps using overpass
    """

    key = "location"
    aliases = ["loc"]
    locks = "cmd:all()"
    help_category = "General"

    def parse(self):
        self.loc = self.args.split()
        if len(self.loc)<2:
            self.loc = self.args.split(',')
        pass

    def func(self):
        # Invalid Arguments
        if len(self.loc)<2:
            self.caller.msg("Invalid Input")
        # Valid Arguments
        else:
            api = overpass.API()
            map_query = '[out:json][timeout:25]; is_in('+self.loc[0]+','+self.loc[1]+'); (._;>;); out 5;'
            response = json.loads(api.Get(map_query))

            # Extract Location Metadata, Create New Room if doesn't exist and Move Player to New Room if required
            if 'elements' in response:
		TAG = 'DATA,LOC'	
		room_name = response['elements'][0]['tags']['name']
                if 'leisure' in response['elements'][0]['tags']:
                    self.caller.msg(TAG + ","  + room_name + "," + response['elements'][0]['tags']['leisure'])
                    rules.create_room(room_name, self.caller, 'leisure', response['elements'][0]['tags']['leisure'])

                elif 'amenity' in response['elements'][0]['tags']:
                    self.caller.msg(TAG  + "," + room_name + "," + response['elements'][0]['tags']['amenity'])
                    rules.create_room(room_name, self.caller, 'amenity', response['elements'][0]['tags']['amenity'])

                elif 'building' in response['elements'][0]['tags']:
                    self.caller.msg(TAG  + "," + room_name + "," + response['elements'][0]['tags']['building'])
                    rules.create_room(room_name, self.caller, 'building', response['elements'][0]['tags']['building'])

                else:
                    self.caller.msg(TAG  + "," + room_name)
                    rules.create_room(room_name, self.caller)

            # If No Relevant Metadata 
            else:
                self.caller.msg("You are in unchartered territory")



class CmdAttack(BaseCommand):
    """
    attack an opponent with bare hands, legs

    Usage:
      punch/kick/box <target>

    This will attack a target in the same room, dealing 
    damage with your bare hands. 
    """
    key = "attack"
    aliases = ["kill", "hit"]
    locks = "cmd:all()"
    help_category = "General"

    def func(self):
        "Implementing combat"

        # pass iff argument. assumes one argument passed
        if not self.args:
            caller = self.caller
            caller.msg("You need to pick a single target to attack.")
            return

        character = self.caller
        item = self.args[1:] # gets rid of the 1st space

        # search for object referred in argument
        if '#' in item:  # if '#' in argument search by dbref
            enemyname = "#"+item.split('#')[1]

            # object in room with same dbref
            enemy = [obj for obj in character.location.contents_get() if enemyname==obj.dbref]

            # if enemy asked for in room
            if enemy:
                enemy = enemy[0]
            else:
                character.msg("%s doesn't exist in this room" % item)
                return
        else:                 # else search by name
            # object in room with same name
            enemy = character.search(item, location=character.location)
        
        # if object not of type character or mob or npc
        if not (utils.inherits_from(enemy, Character) or utils.inherits_from(enemy, Mob) or utils.inherits_from(enemy, Npc)):
            character.msg("%s can't be attacked" % item)
            return

        # if enemy = self
        if enemy.dbref==character.dbref:
            character.msg("Stop being suicidal! I've set an appointment for you with the shrink")
            return

        # if user has equipped weapon
        weapons = [pobj for pobj in character.contents_get() if utils.inherits_from(pobj, Weapon) and pobj.dbref==character.db.equip]

        if weapons:
            character.msg("You slash at %s" % enemy)
            character.execute_cmd("slash %s" % enemy)
        else:
            character.msg("You punch %s" % enemy)
            rules.roll_challenge(character, enemy, "kickbox")


class CmdGet(MuxCommand):
    """
    pick up something

    Usage:
      get <obj>

    Picks up an object from your location and puts it in
    your inventory.
    """
    key = "get"
    aliases = "grab"
    locks = "cmd:all()"
    arg_regex = r"\s|$"

    def func(self):
        "implements the command."

        caller = self.caller

        if not self.args:
            caller.msg("Get what?")
            return

        # search for object referred in argument [custom added to deal with dbref]
        if '#' in self.args:  # if '#' in argument search by dbref
            objname = "#"+self.args.split('#')[1]

            # object in room with same dbref
            objs = [item for item in caller.location.contents_get() if objname==item.dbref]

            # if object asked for in room
            if objs:
                obj = objs[0]
            else:
                caller.msg("You can't get that." % self.args)
                return

        else: # else search in room for object with same name
            obj = caller.search(self.args, location=caller.location)


        if not obj:
            return
        if caller == obj:
            caller.msg("You can't get yourself.")
            return
        if not obj.access(caller, 'get'):
            if obj.db.get_err_msg:
                caller.msg(obj.db.get_err_msg)
            else:
                caller.msg("You can't get that.")
            return

        obj.move_to(caller, quiet=True)
        caller.msg("You pick up %s." % obj.name[1:])
        caller.msg("DATA,inv_add," + obj.name[1:] + obj.dbref)   # custom added
        caller.location.msg_contents("%s picks up %s." %
                                        (caller.name,
                                         obj.name[1:]),
                                     exclude=caller)
        # calling hook method
        obj.at_get(caller)



class CmdDrop(MuxCommand):
    """
    drop something

    Usage:
      drop <obj>

    Lets you drop an object from your inventory into the
    location you are currently in.
    """

    key = "drop"
    locks = "cmd:all()"
    arg_regex = r"\s|$"

    def func(self):
        "Implement command"

        caller = self.caller
        if not self.args:
            caller.msg("Drop what?")
            return

        # search for object referred in argument [custom added to deal with dbref]
        if '#' in self.args:  # if '#' in argument search by dbref
            objname = "#"+self.args.split('#')[1]

            # object on player with same dbref
            objs = [item for item in caller.contents_get() if objname==item.dbref]

            # if object asked for on player
            if objs:
                obj = objs[0]
            else:
                caller.msg("You aren't carrying %s." % self.args)
                return

        else: # else search in room for object with same name
              # Because the DROP command by definition looks for items
              # in inventory, call the search function using location = caller
            obj = caller.search(self.args, location=caller,
                                nofound_string="You aren't carrying %s." % self.args,
                                multimatch_string="You carry more than one %s:" % self.args)
        if not obj:
            return

        obj.move_to(caller.location, quiet=True)
        caller.msg("You drop %s." % (obj.name[1:],))
        caller.msg("DATA,inv_remove," + obj.name[1:] + obj.dbref)
        caller.location.msg_contents("%s drops %s." %
                                         (caller.name, obj.name[1:]),
                                     exclude=caller)
        # Call the object script's at_drop() method.
        obj.at_drop(caller)
