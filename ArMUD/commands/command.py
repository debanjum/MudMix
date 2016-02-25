"""
Commands

Commands describe the input the player can do to the game.

"""

from evennia import Command as BaseCommand
from evennia import default_cmds
import overpass, json

class Location(BaseCommand):
    """
    Inherit from this if you want to create your own
    command styles. Note that Evennia's default commands
    use MuxCommand instead (next in this module).

    Note that the class's `__doc__` string (this text) is
    used by Evennia to create the automatic help entry for
    the command, so make sure to document consistently here.

    Each Command implements the following methods, called
    in this order:
        - at_pre_command(): If this returns True, execution is aborted.
        - parse(): Should perform any extra parsing needed on self.args
            and store the result on self.
        - func(): Performs the actual work.
        - at_post_command(): Extra actions, often things done after
            every command, like prompts.

    """
    # these need to be specified

    key = "location"
    aliases = []
    locks = "cmd:all()"
    help_category = "General"
    loc = []
    # optional
    # auto_help = False      # uncomment to deactive auto-help for this command.
    # arg_regex = r"\s.*?|$" # optional regex detailing how the part after
                             # the cmdname must look to match this command.

    # (we don't implement hook method access() here, you don't need to
    #  modify that unless you want to change how the lock system works
    #  (in that case see evennia.commands.command.Command))

    def at_pre_cmd(self):
        """
        This hook is called before `self.parse()` on all commands.
        """
        pass

    def parse(self):
        """
        This method is called by the `cmdhandler` once the command name
        has been identified. It creates a new set of member variables
        that can be later accessed from `self.func()` (see below).

        The following variables are available to us:
           # class variables:

           self.key - the name of this command ('mycommand')
           self.aliases - the aliases of this cmd ('mycmd','myc')
           self.locks - lock string for this command ("cmd:all()")
           self.help_category - overall category of command ("General")

           # added at run-time by `cmdhandler`:

           self.caller - the object calling this command
           self.cmdstring - the actual command name used to call this
                            (this allows you to know which alias was used,
                             for example)
           self.args - the raw input; everything following `self.cmdstring`.
           self.cmdset - the `cmdset` from which this command was picked. Not
                         often used (useful for commands like `help` or to
                         list all available commands etc).
           self.obj - the object on which this command was defined. It is often
                         the same as `self.caller`.
        """
        self.loc = self.args.split()
        if len(self.loc)<2:
            self.loc = self.args.split(',')
        pass

    def func(self):
        """
        This is the hook function that actually does all the work. It is called
        by the `cmdhandler` right after `self.parser()` finishes, and so has access
        to all the variables defined therein.
        """

        # Invalid Arguments
        if len(self.loc)<2:
            self.caller.msg("Invalid Input")

        else:
            api = overpass.API()
            map_query = '[out:json][timeout:25]; is_in('+self.loc[0]+','+self.loc[1]+'); (._;>;); out 5;'
            response = json.loads(api.Get(map_query))

            # Extract Location Metadata 
            if 'elements' in response:
                if 'leisure' in response['elements'][0]['tags']:
                    self.caller.msg(response['elements'][0]['tags']['name'] + "," + response['elements'][0]['tags']['leisure'])
                elif 'amenity' in response['elements'][0]['tags']:
                    self.caller.msg(response['elements'][0]['tags']['name'] + "," + response['elements'][0]['tags']['amenity'])
                elif 'building' in response['elements'][0]['tags']:
                    self.caller.msg(response['elements'][0]['tags']['name'] + "," + response['elements'][0]['tags']['building'])
                else:
                    self.caller.msg(response['elements'][0]['tags']['name'])
            # If No Relevant Metadata 
            else:
                self.caller.msg("You are in unchartered territory")

    def at_post_cmd(self):
        """
        This hook is called after `self.func()`.
        """
        pass
