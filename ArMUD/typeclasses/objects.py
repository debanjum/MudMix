"""
Object

The Object is the "naked" base class for things in the game world.

Note that the default Character, Room and Exit does not inherit from
this Object, but from their respective default implementations in the
evennia library. If you want to use this class as a parent to change
the other types, you can do so by adding this as a multiple
inheritance.

"""
from future.utils import listvalues

import random

from evennia import DefaultObject, DefaultExit, Command, CmdSet, default_cmds
from evennia import utils
from evennia.utils import search
from evennia.utils.spawner import spawn

class Object(DefaultObject):
    pass


class TutorialObject(DefaultObject):
    """
    This is the baseclass for all objects in the tutorial.
    """

    def at_object_creation(self):
        "Called when the object is first created."
        super(TutorialObject, self).at_object_creation()
        self.db.tutorial_info = "No tutorial info is available for this object."

    def reset(self):
        "Resets the object, whatever that may mean."
        self.location = self.home


#------------------------------------------------------------
#
# Vegetable - object type
#
# Food is required to heal or gain strenth in this world.
#------------------------------------------------------------

class VegetableEat(Command):
    """
    Eat the vegetable. Commands:
      eat <vegetable>
    """

    # this is an example of implementing many commands as a single
    # command class, using the given command alias to separate between them.

    key = "eat"
    aliases = ["consume"]
    locks = "cmd:all()"
    help_category = "TutorialWorld"

    def func(self):
        "Implements eating"

        # parry mode
        string = "You eat your veggies"
        self.caller.msg(string)
        self.caller.db.HP += self.obj.db.heal
        self.caller.db.STR += self.obj.db.strength
        self.obj.delete()

class CmdSetVegetable(CmdSet):
    "Holds the attack command."
    def at_cmdset_creation(self):
        "called at first object creation."
        self.add(VegetableEat())


class Vegetable(TutorialObject):
    """
    This defines a vegetable or fruit.
    Important attributes (set at creation):
      heal - HP gain
      strength - STR gain 
    """
    def at_object_creation(self):
        "Called at first creation of the object"
        super(Vegetable, self).at_object_creation()
        self.db.heal = 1.0       # increases HP
        self.db.strength = 1.0   # increases STR
        self.cmdset.add_default(CmdSetVegetable, permanent=True)

    def reset(self):
        """
        When reset, the vegetable is simply deleted, unless it has a place
        to return to.
        """
        if self.location.has_player and self.home == self.location:
            self.location.msg_contents("%s suddenly and magically fades into nothingness, as if it was never there ..." % self.key)
            self.delete()
        else:
            self.location = self.home


#------------------------------------------------------------
#
# Vegetables  - spawns vegetables and fruits
#
# This is a spawner mechanism that creates custom vegetables from a
# spawner prototype dictionary. Note that we only create a single typeclass
# (Vegetabels) yet customize all these different vegetables using the spawner.
# The spawner dictionaries could easily sit in separate modules and be
# used to create unique and interesting variations of typeclassed
# objects.
#
#------------------------------------------------------------
VEGETABLE_PROTOTYPES = {
    "vegetable": {
        "typeclass": "typeclasses.objects.Vegetable",
        "key": "Vegetable",
        "heal": 1,
        "strength": 1,
        "desc": "A generic vegetable."},
    "potato": {
        "prototype": "vegetable",
        "aliases": ["aloo"],
        "key": " potato",
        "desc":"A russel potato."},
    "tomato": {
        "prototype": "vegetable",
        "key": "tomato",
        "aliases": ["tamatar"],
        "desc": "A tomato on vine.",
        "heal": 0.5,
        "strength": 0.0},
    "orange": {
        "prototype": "vegetable",
        "key": "orange",
        "aliases": ["santra"],
        "desc": "A valencia orange!",
        "heal": 1.5,
        "strength": 0.5},
    }



#------------------------------------------------------------
#
# Weapon - object type
#
# A weapon is necessary in order to fight in the tutorial
# world. A weapon (which here is assumed to be a bladed
# melee weapon for close combat) has three commands,
# stab, slash and defend. Weapons also have a property "magic"
# to determine if they are usable against certain enemies.
#
# Since Characters don't have special skills in the tutorial,
# we let the weapon itself determine how easy/hard it is
# to hit with it, and how much damage it can do.
#
#------------------------------------------------------------

class WeaponAttack(Command):
    """
    Attack the enemy. Commands:
      stab <enemy>
      slash <enemy>
      parry
    stab - (thrust) makes a lot of damage but is harder to hit with.
    slash - is easier to land, but does not make as much damage.
    parry - forgoes your attack but will make you harder to hit on next
            enemy attack.
    """

    # this is an example of implementing many commands as a single
    # command class, using the given command alias to separate between them.

    key = "attack"
    aliases = ["hit","kill", "fight", "thrust", "pierce", "stab",
               "slash", "chop", "parry", "defend"]
    locks = "cmd:all()"
    help_category = "TutorialWorld"

    def func(self):
        "Implements the stab"

        cmdstring = self.cmdstring

        if cmdstring in ("attack", "fight"):
            string = "How do you want to fight? Choose one of 'stab', 'slash' or 'defend'."
            self.caller.msg(string)
            return

        # parry mode
        if cmdstring in ("parry", "defend"):
            string = "You raise your weapon in a defensive pose, ready to block the next enemy attack."
            self.caller.msg(string)
            self.caller.db.combat_parry_mode = True
            self.caller.location.msg_contents("%s takes a defensive stance" % self.caller, exclude=[self.caller])
            return

        if not self.args:
            self.caller.msg("Who do you attack?")
            return
        target = self.caller.search(self.args.strip())
        if not target:
            return

        string = ""
        tstring = ""
        ostring = ""
        if cmdstring in ("thrust", "pierce", "stab"):
            hit = float(self.obj.db.hit) * 0.7  # modified due to stab
            damage = self.obj.db.damage * 2  # modified due to stab
            string = "You stab with %s. " % self.obj.key
            tstring = "%s stabs at you with %s. " % (self.caller.key, self.obj.key)
            ostring = "%s stabs at %s with %s. " % (self.caller.key, target.key, self.obj.key)
            self.caller.db.combat_parry_mode = False
        elif cmdstring in ("slash", "chop"):
            hit = float(self.obj.db.hit)   # un modified due to slash
            damage = self.obj.db.damage  # un modified due to slash
            string = "You slash with %s. " % self.obj.key
            tstring = "%s slash at you with %s. " % (self.caller.key, self.obj.key)
            ostring = "%s slash at %s with %s. " % (self.caller.key, target.key, self.obj.key)
            self.caller.db.combat_parry_mode = False
        else:
            self.caller.msg("You fumble with your weapon, unsure of whether to stab, slash or parry ...")
            self.caller.location.msg_contents("%s fumbles with their weapon." % self.caller, exclude=self.caller)
            self.caller.db.combat_parry_mode = False
            return

        if target.db.combat_parry_mode:
            # target is defensive; even harder to hit!
            target.msg("{GYou defend, trying to avoid the attack.{n")
            hit *= 0.5

        if random.random() <= hit:
            self.caller.msg(string + "{gIt's a hit!{n")
            target.msg(tstring + "{rIt's a hit!{n")
            self.caller.location.msg_contents(ostring + "It's a hit!", exclude=[target,self.caller])

            # call enemy hook
            if hasattr(target, "at_hit"):
                # should return True if target is defeated, False otherwise.
                return target.at_hit(self.obj, self.caller, damage)
            elif target.db.HP:
                target.db.HP -= damage
            else:
                # sorry, impossible to fight this enemy ...
                self.caller.msg("The enemy seems unaffacted.")
                return False
        else:
            self.caller.msg(string + "{rYou miss.{n")
            target.msg(tstring + "{gThey miss you.{n")
            self.caller.location.msg_contents(ostring + "They miss.", exclude=[target, self.caller])


class CmdSetWeapon(CmdSet):
    "Holds the attack command."
    def at_cmdset_creation(self):
        "called at first object creation."
        self.add(WeaponAttack())


class Weapon(TutorialObject):
    """
    This defines a bladed weapon.
    Important attributes (set at creation):
      hit - chance to hit (0-1)
      parry - chance to parry (0-1)
      damage - base damage given (modified by hit success and
               type of attack) (0-10)
    """
    def at_object_creation(self):
        "Called at first creation of the object"
        super(Weapon, self).at_object_creation()
        self.db.hit = 0.4    # hit chance
        self.db.parry = 0.8  # parry chance
        self.db.damage = 1.0
        self.db.magic = False
        self.cmdset.add_default(CmdSetWeapon, permanent=True)

    def reset(self):
        """
        When reset, the weapon is simply deleted, unless it has a place
        to return to.
        """
        if self.location.has_player and self.home == self.location:
            self.location.msg_contents("%s suddenly and magically fades into nothingness, as if it was never there ..." % self.key)
            self.delete()
        else:
            self.location = self.home


#------------------------------------------------------------
#
# Weapon rack - spawns weapons
#
# This is a spawner mechanism that creates custom weapons from a
# spawner prototype dictionary. Note that we only create a single typeclass
# (Weapon) yet customize all these different weapons using the spawner.
# The spawner dictionaries could easily sit in separate modules and be
# used to create unique and interesting variations of typeclassed
# objects.
#
#------------------------------------------------------------

WEAPON_PROTOTYPES = {
    "weapon": {
        "typeclass": "typeclasses.objects.Weapon",
        "key": "Weapon",
        "hit": 0.2,
        "parry": 0.2,
        "damage": 1.0,
        "magic": False,
        "desc": "A generic blade."},
    "knife": {
        "prototype": "weapon",
        "aliases": "sword",
        "key": "Kitchen knife",
        "desc":"A rusty kitchen knife. Better than nothing.",
        "damage": 3},
    "dagger": {
        "prototype": "knife",
        "key": "Rusty dagger",
        "aliases": ["knife", "dagger"],
        "desc": "A double-edged dagger with a nicked edge and a wooden handle.",
        "hit": 0.25},
    "sword": {
        "prototype": "weapon",
        "key": "Rusty sword",
        "aliases": ["sword"],
        "desc": "A rusty shortsword. It has a leather-wrapped handle covered i food grease.",
        "hit": 0.3,
        "damage": 5,
        "parry": 0.5},
    "club": {
        "prototype": "weapon",
        "key":"Club",
        "desc": "A heavy wooden club, little more than a heavy branch.",
        "hit": 0.4,
        "damage": 6,
        "parry": 0.2},
    "axe": {
        "prototype": "weapon",
        "key":"Axe",
        "desc": "A woodcutter's axe with a keen edge.",
        "hit": 0.4,
        "damage": 6,
        "parry": 0.2},
    "ornate longsword": {
        "prototype":"sword",
        "key": "Ornate longsword",
        "desc": "A fine longsword with some swirling patterns on the handle.",
        "hit": 0.5,
        "magic": True,
        "damage": 5},
    "warhammer": {
        "prototype": "club",
        "key": "Silver Warhammer",
        "aliases": ["hammer", "warhammer", "war"],
        "desc": "A heavy war hammer with silver ornaments. This huge weapon causes massive damage - if you can hit.",
        "hit": 0.4,
        "magic": True,
        "damage": 8},
    "rune axe": {
        "prototype": "axe",
        "key": "Runeaxe",
        "aliases": ["axe"],
        "hit": 0.4,
        "magic": True,
        "damage": 6},
    "thruning": {
        "prototype": "ornate longsword",
        "key": "Broadsword named Thruning",
        "desc": "This heavy bladed weapon is marked with the name 'Thruning'. It is very powerful in skilled hands.",
        "hit": 0.6,
        "parry": 0.6,
        "damage": 7},
    "slayer waraxe": {
        "prototype": "rune axe",
        "key": "Slayer waraxe",
        "aliases": ["waraxe", "war", "slayer"],
        "desc": "A huge double-bladed axe marked with the runes for 'Slayer'. It has more runic inscriptions on its head, which you cannot decipher.",
        "hit": 0.7,
        "damage": 8},
    "ghostblade": {
        "prototype": "ornate longsword",
        "key": "The Ghostblade",
        "aliases": ["blade", "ghost"],
        "desc": "This massive sword is large as you are tall, yet seems to weigh almost nothing. It's almost like it's not really there.",
        "hit": 0.9,
        "parry": 0.8,
        "damage": 10},
    "hawkblade": {
        "prototype": "ghostblade",
        "key": "The Hawblade",
        "aliases": ["hawk", "blade"],
        "desc": "The weapon of a long-dead heroine and a more civilized age, the hawk-shaped hilt of this blade almost has a life of its own.",
        "hit": 0.85,
        "parry": 0.7,
        "damage": 11}
    }


class CmdGetWeapon(Command):
    """
    Usage:
      get weapon
    This will try to obtain a weapon from the container.
    """
    key = "get weapon"
    aliases = "get weapon"
    locks = "cmd:all()"
    help_cateogory = "TutorialWorld"

    def func(self):
        """
        Get a weapon from the container. It will
        itself handle all messages.
        """
        self.obj.produce_weapon(self.caller)

class CmdSetWeaponRack(CmdSet):
    """
    The cmdset for the rack.
    """
    key = "weaponrack_cmdset"

    def at_cmdset_creation(self):
        "Called at first creation of cmdset"
        self.add(CmdGetWeapon())


class WeaponRack(TutorialObject):
    """
    This object represents a weapon store. When people use the
    "get weapon" command on this rack, it will produce one
    random weapon from among those registered to exist
    on it. This will also set a property on the character
    to make sure they can't get more than one at a time.
    Attributes to set on this object:
        available_weapons: list of prototype-keys from
            WEAPON_PROTOTYPES, the weapons available in this rack.
        no_more_weapons_msg - error message to return to players
            who already got one weapon from the rack and tries to
            grab another one.
    """
    def at_object_creation(self):
        """
        called at creation
        """
        self.cmdset.add_default(CmdSetWeaponRack, permanent=True)
        self.db.rack_id = "weaponrack_1"
        # these are prototype names from the prototype
        # dictionary above.
        self.db.get_weapon_msg = "You find {c%s{n."
        self.db.no_more_weapons_msg = "you find nothing else of use."
        self.db.available_weapons = ["knife", "dagger",
                                     "sword", "club"]

    def produce_weapon(self, caller):
        """
        This will produce a new weapon from the rack,
        assuming the caller hasn't already gotten one. When
        doing so, the caller will get Tagged with the id
        of this rack, to make sure they cannot keep
        pulling weapons from it indefinitely.
        """
        rack_id = self.db.rack_id
        if caller.tags.get(rack_id, category="tutorial_world"):
            caller.msg(self.db.no_more_weapons_msg)
        else:
            prototype = random.choice(self.db.available_weapons)
            # use the spawner to create a new Weapon from the
            # spawner dictionary, tag the caller
            wpn = spawn(WEAPON_PROTOTYPES[prototype], prototype_parents=WEAPON_PROTOTYPES)[0]
            caller.tags.add(rack_id, category="tutorial_world")
            wpn.location = caller
            caller.msg(self.db.get_weapon_msg % wpn.key)
