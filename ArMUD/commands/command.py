"""
Commands

Commands describe the input the player can do to the game.

"""

from evennia import Command as BaseCommand
from evennia import default_cmds
from world import rules
import overpass, json


class Location(BaseCommand):
    """
    get player real-world location

    Usage:
      location <latitude> <longitude> <target>

    This will retrieve enclosing feature metadata related to
    the submitted location from openstreetmaps using overpass
    """

    key = "location"
    aliases = []
    locks = "cmd:all()"
    help_category = "General"
    loc = []

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
		loc_tag = 'MSG'	
		room_name = response['elements'][0]['tags']['name']
                if 'leisure' in response['elements'][0]['tags']:
                    self.caller.msg(loc_tag + ","  + room_name + "," + response['elements'][0]['tags']['leisure'])
                elif 'amenity' in response['elements'][0]['tags']:
                    self.caller.msg(loc_tag  + "," + room_name + "," + response['elements'][0]['tags']['amenity'])
                elif 'building' in response['elements'][0]['tags']:
                    self.caller.msg(loc_tag  + "," + room_name + "," + response['elements'][0]['tags']['building'])
                else:
                    self.caller.msg(loc_tag  + "," + room_name)
            # If No Relevant Metadata 
            else:
                self.caller.msg("You are in unchartered territory")


class CmdAttack(BaseCommand):
    """
    attack an opponent

    Usage:
      attack <target>

    This will attack a target in the same room, dealing 
    damage with your bare hands. 
    """
    key = "attack"
    aliases = ["hit", "kill"]
    locks = "cmd:all()"
    help_category = "General"
    loc = []

    def func(self):
        "Implementing combat"

        if not self.args:
            caller = self.caller
            caller.msg("You need to pick a target to attack.")
            return

        character1 = self.caller
        character2 = self.caller.search(self.args)

        rules.roll_challenge(character1, character2, "combat")
