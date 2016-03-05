from characters import Character
from time import time
from random import randint
from world import rules
from evennia import TICKER_HANDLER as tickerhandler

class Npc(Character):
    """
    A NPC typeclass which extends the character class.
    """

    def at_object_creation(self):
        """
        Called only when first created
        """
        self.db.attacking = None
        self.db.full_HP = 100
        self.db.HP = 30          # as npc is dying
        self.db.STR = 1
        self.db.combat = 2


    def at_char_entered(self, character):
        """
         A simple is_aggressive check.
         Can be expanded upon later.
        """
        self.db.attacking = None

        # if npc aggressive
        if self.db.is_aggressive:
            # Note the entered character
            self.db.attacking=character
            self.db.count=0
            self.db.last_attack=time()

        else:
            self.execute_cmd("say Ahh, the pain! I need water. Help me %s!"% character)


    def at_tick(self):
        if self.db.attacking != None:
            print self.attacking.location
            # if character in same roome as self and aggressive
            if self.location==self.db.attacking.location and self.db.count<10:
                # attack with probability and after cooldown
                if (time() - self.db.last_attack)>randint(1,3):
                    self.execute_cmd("say Graaah, die %s!" % self.db.attacking)
                    rules.roll_challenge(self, self.db.attacking, "combat")
                    self.db.last_attack=time()
                    self.db.count+=1
            else:
                tickerhandler.remove(self,1)
        else:
            tickerhandler.remove(self,1)
