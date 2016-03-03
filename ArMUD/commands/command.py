"""
Commands

Commands describe the input the player can do to the game.

"""

from evennia import Command as BaseCommand
from evennia import default_cmds, utils
from evennia.typeclasses import managers
from world import rules
from typeclasses.characters import Character
from typeclasses.mob import Mob
from typeclasses.objects import Weapon
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

        if not self.args:
            caller = self.caller
            caller.msg("You need to choose a vegetable to eat.")
            return

        # if vegetable to be eaten is in the room
        for obj in self.args.split():
            veggie_inroom = [room_obj for room_obj in self.caller.location.contents_get() if utils.inherits_from(room_obj, objects.Vegetable) if obj in room_obj.name]
            if veggie_inroom:
                string = ("You eat your %s" % obj)
                self.caller.msg(string)
                self.caller.db.HP += veggie_inroom[0].db.heal
                self.caller.db.STR += veggie_inroom[0].db.strength
                veggie_inroom[0].delete()


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

            # Extract Location Metadata 
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

                self.caller.msg("You are now in " + room_name)

               #TODO: change room if different from current
               #if self.caller.current_room != room_name:
               #    self.caller

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

        # test iff not one argument passed
        if not self.args or len(self.args.split())>1:
            caller = self.caller
            caller.msg("You need to pick a single target to attack.")
            return

        character = self.caller

        # search for object referred in argument
        if '#' in self.args:  # if '#' in argument search by dbref
            enemyname = "#"+self.args.split('#')[1]

            # object in room with same dbref
            enemy = [obj for obj in self.caller.location.contents_get() if enemyname==obj.dbref]

            # if enemy asked for in room
            if enemy:
                enemy = enemy[0]
            else:
                self.caller.msg("%s doesn't exist in this room" % self.args)
                return
        else:                 # else search by name
            # object in room with same name
            enemy = self.caller.search(self.args.split()[0])
        
        # if object not of type character or mob
        if not (utils.inherits_from(enemy, Character) or utils.inherits_from(enemy, Mob)):
            self.caller.msg("%s can't be attacked" % self.args)
            return

        # if user has weapon get first weapon
        weapons = [pobj for pobj in self.caller.contents_get() if utils.inherits_from(pobj, Weapon)]
        print weapons
        if weapons:
            character.msg("You slash at %s" % enemy)
            self.caller.execute_cmd("slash %s" % enemy)
        else:
            character.msg("You punch %s" % enemy)
            rules.roll_challenge(character, enemy, "kickbox")
