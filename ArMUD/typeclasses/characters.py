"""
Characters

Characters are (by default) Objects setup to be puppeted by Players.
They are what you "see" in game. The Character class in this module
is setup to be the "default" character type created by the default
creation commands.

"""
from evennia import DefaultCharacter, utils
from random import randint
from typeclasses.mob import Mob
from typeclasses.objects import Weapon

class Character(DefaultCharacter):
    """
    Custom rule-restricted character. We randomize
    the initial skill and ability values between 1-10.

    The Character defaults to reimplementing some of base Object's hook methods with the
    following functionality:

    at_basetype_setup - always assigns the DefaultCmdSet to this object type
                    (important!)sets locks so character cannot be picked up
                    and its commands only be called by itself, not anyone else.
                    (to change things, use at_object_creation() instead).
    at_after_move - Launches the "look" command after every move.
    at_post_unpuppet(player) -  when Player disconnects from the Character, we
                    store the current location in the pre_logout_location Attribute and
                    move it to a None-location so the "unpuppeted" character
                    object does not need to stay on grid. Echoes "Player has disconnected" 
                    to the room.
    at_pre_puppet - Just before Player re-connects, retrieves the character's
                    pre_logout_location Attribute and move it back on the grid.
    at_post_puppet - Echoes "PlayerName has entered the game" to the room.

    """
    def at_object_creation(self):
        """
        Called only when first created
        """
        self.db.level = 1 
        self.db.HP = 100
        self.db.XP = 0
        self.db.STR = randint(1, 10)
        self.db.combat = randint(5, 10)
        self.db.equip = ""
        self.db.just_entered=False
        self.msg("Welcome to the Matrix, %s" % self.name)

    def at_post_puppet(self):
        """
        Called when user logs in and starts puppeting a character
        """

        # pass stats to the player
        self.msg("DATA,level,%d" % self.db.level)
        self.msg("DATA,health,%d" % self.db.HP)
        self.msg("DATA,xp,%d" % self.db.XP)
        self.msg("DATA,strength,%d" % self.db.STR)
        self.msg("DATA,combat,%d" % self.db.combat)
        self.msg("DATA,LOC,%s" % self.location.name)
        self.msg("Welcome back to the Matrix, %s" % self.name)
        self.db.just_entered=True

        # pass room items to the player
        for item in self.location.contents:
            if item.dbref != self.dbref: 
                if utils.inherits_from(item, Character):
                    self.msg("DATA,char_add," + item.name + item.dbref)
                    item.msg("DATA,char_add," + item.name + item.dbref)
                elif utils.inherits_from(item, Mob):
                    self.msg("DATA,char_add," + item.name + item.dbref)
                else:
                    self.msg("DATA,obj_add," + item.name + item.dbref)
            else:
                pass

        # pass inventory items to the player 
        for item in self.contents:
            self.msg("DATA,inv_add," + item.name + item.dbref)

        self.msg("In Game News today, the Federal Bureau of Intution closed its investigation into the recent spurt of attacks on the AI community at Dartmouth. It claims the case is unresolvable by its psychic experts.")

    def at_post_unpuppet(self, player, session=None):
        """
        We stove away the character when the player goes ooc/logs off,
        otherwise the character object will remain in the room also
        after the player logged off ("headless", so to say).

        Args:
            player (Player): The player object that just disconnected
                from this object.
            session (Session): Session controlling the connection that
                just disconnected.
        """
        if not self.sessions.count():
            # only remove this char from grid if no sessions control it anymore.
            if self.location:
                def message(obj, from_obj):
                    obj.msg("%s has left the game." % self.get_display_name(obj), from_obj=from_obj)
                    obj.msg("DATA,char_remove,%s" % self.get_display_name(obj), from_obj=from_obj)
                self.location.for_contents(message, exclude=[self], from_obj=self)
                self.db.prelogout_location = self.location
                self.location = None


    def return_appearance(self, looker):
        """
        The return from this method is what
        looker sees when looking at this object.
        """
        text = super(Character, self).return_appearance(looker)
        lookcontrolstring = "\n(level: %s)\n(health: %s)\n(xp: %s)\n(strength: %s)\n(combat score: %s)"
        cscore = lookcontrolstring % (self.db.level, self.db.HP, self.db.XP, self.db.STR, self.db.combat)
        if "\n" in text:
            # text is multi-line, add score after first line
            first_line, rest = text.split("\n", 1)
            text = first_line + cscore + "\n" + rest
        else:
            # text is only one line; add score to end
            text += cscore
        return text


    def at_after_move(self, source_location):
        """
        Default is to look around after a move 
        Note:  This has been moved to room.at_object_receive
        """
        #self.execute_cmd('look')
        pass
