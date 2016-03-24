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
        item = self.args    # gets rid of the 1st space

        # search for object referred in argument
        if '#' in item:  # if '#' in argument search by dbref
            vegetablename = "#"+item.split('#')[1]

            # object in room with same dbref
            vegetable = [obj for obj in character.contents_get() if vegetablename==obj.dbref]

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
        character.msg("DATA,inv_remove,%s" % item[1:])
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
        if not self.args and self.args.split()>1:
            self.caller.msg("You need to pick a single target to act on.")
            return

        character = self.caller
        itempassed = self.args.split()[0] # gets rid of the 1st space
        # search for item referred in argument
        # by dbref
        if '#' in itempassed:  # if '#' in argument search by dbref
            itemname = "#"+itempassed.split('#')[1]

            # check if object with requested dbref on self
            item = [obj for obj in character.contents_get() if itemname==obj.dbref]
            # check if object with requested dbref in room
            if not item:
                item = [obj for obj in character.location.contents_get() if itemname==obj.dbref]

            # if item asked for was found on self or in room
            if item:
                item = item[0]
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

        # if object of class weapon
        if (utils.inherits_from(item, Weapon)):
            # if weapon was equipped
            if character.db.equip==item.dbref:
                # dequip
                character.db.equip = ""
                character.msg("You've unequipped yourself")
            # if weapon was dequipped
            else:
                # equip
                character.db.equip = item.dbref
                character.msg("You've equipped yourself with %s" % item.name)
            return 

        # if item to be acted on is in the room/inventory and doesn't belong to above categories
        # if has hook at_defact, call it
        if hasattr(item, "at_defact"):
            return item.at_defact(self.caller)
        # else if has default action associated with it, call that
        elif item.db.defact:
            command=item.db.defact+" "+ item.name+item.dbref
        # else just execute a look on the object
        else:
            command="look " + item.name+item.dbref

        character.execute_cmd(command)


class CmdStats(BaseCommand):
    """
    get player self, current room stats in machine readable format

    Usage:
        stats

    This will retrieve player stats, and player current room stats in machine readable format
    Includes player health, level, current room and objects, characters etc in that room.
    """

    key = "stats"
    locks = "cmd:all()"
    help_category = "General"

    def func(self):

        # pass stats to the player
        self.caller.msg("DATA,name,%s" % self.caller.name)
        self.caller.msg("DATA,level,%d" % self.caller.db.level)
        self.caller.msg("DATA,health,%d" % self.caller.db.HP)
        self.caller.msg("DATA,xp,%d" % self.caller.db.XP)
        self.caller.msg("DATA,strength,%d" % self.caller.db.STR)
        self.caller.msg("DATA,combat,%d" % self.caller.db.combat)
        self.caller.msg("DATA,LOC,%s" % self.caller.location.name)

        # pass room items to the player
        for item in self.caller.location.contents:
            if item.dbref != self.caller.dbref: 
                if utils.inherits_from(item, Character):
                    self.caller.msg("DATA,char_add," + item.name + item.dbref)
                    item.msg("DATA,char_add," + item.name + item.dbref)
                elif utils.inherits_from(item, Mob):
                    self.caller.msg("DATA,char_add," + item.name + item.dbref)
                else:
                    self.caller.msg("DATA,obj_add," + item.name + item.dbref)
            else:
                pass

        # pass inventory items to the player 
        for item in self.caller.contents:
            self.caller.msg("DATA,inv_add," + item.name + item.dbref)



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
            latlng = self.loc[0]+','+self.loc[1]

            # Extract Location Metadata, Create New Room if doesn't exist and Move Player to New Room if required
            if 'elements' in response:
		TAG   = 'DATA,LOC'

                # Find smallest enclosing feature
                level = 0
                index = 0
                for i in xrange(0,len(response['elements'])):
                    # if enclosing feature with no admin_level exists than 0th element is the smallest enclosing feature
                    if not 'admin_level' in response['elements'][i]['tags']:
                        index=0
                    # else find smallest admin_level(=largest admin_level no.) enclosing feature to put player in
                    elif level<response['elements'][i]['tags']['admin_level']:
                        level=response['elements'][i]['tags']['admin_level']
                        index=i
                        
                room_name = response['elements'][index]['tags']['name']

                if 'leisure' in response['elements'][index]['tags']:
                    self.caller.msg(TAG + ","  + room_name + "," + response['elements'][index]['tags']['leisure'])
                    rules.create_room(room_name, self.caller, latlng, 'leisure', response['elements'][index]['tags']['leisure'])

                elif 'amenity' in response['elements'][index]['tags']:
                    self.caller.msg(TAG  + "," + room_name + "," + response['elements'][index]['tags']['amenity'])
                    rules.create_room(room_name, self.caller, latlng, 'amenity', response['elements'][index]['tags']['amenity'])

                elif 'building' in response['elements'][index]['tags']:
                    self.caller.msg(TAG  + "," + room_name + "," + response['elements'][index]['tags']['building'])
                    rules.create_room(room_name, self.caller, latlng, 'building', response['elements'][index]['tags']['building'])

                else:
                    self.caller.msg(TAG  + "," + room_name)
                    rules.create_room(room_name, self.caller, latlng)

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
        item = self.args # gets rid of the 1st space

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

        if not self.args and self.args.split()>1:
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
                caller.msg("You can't get that.")
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
        caller.msg("You pick up %s." % obj.name)

        # pass inventory addition to caller in machine readable format
        caller.msg("DATA,inv_add," + obj.name + obj.dbref)

        # pass object removal from room to characters in room in machine readable format
        for item in caller.location.contents:
            if utils.inherits_from(item, Character):
                item.msg("DATA,obj_remove," + obj.name + obj.dbref)
        # pass object removal from room to characters in room in human readable format
        caller.location.msg_contents("%s picks up %s." % (caller.name, obj.name), exclude=caller)

        # if object of type Weapon
        if (utils.inherits_from(obj, Weapon)):
            # if user wasn't equipped with any weapon
            if caller.db.equip=="":
                caller.db.equip = obj.dbref  # equip with got object
                caller.msg("You equip yourself with %s" % obj.name)

        # calling hook method
        obj.at_get(caller)

        if obj.name == "The Quantum Spanner":
            caller.location.msg_contents("The earth opens up beneath you. You fall down a deep hole. You land in a pile of robot parts. One of the robot's faces looks very familiar. How can this be?! The face of the robot is.... Michael's face! Michael must be a robot!")


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
        caller.msg("You drop %s." % (obj.name,))
        caller.msg("DATA,inv_remove," + obj.name + obj.dbref)
        for item in caller.location.contents:
            if utils.inherits_from(item, Character):
                item.msg("DATA,obj_add," + obj.name + obj.dbref)

        caller.location.msg_contents("%s drops %s." %
                                         (caller.name, obj.name),
                                     exclude=caller)
        # if object of type Weapon
        if (utils.inherits_from(obj, Weapon)):
            # if user wasn't equipped with any weapon
            if caller.db.equip==obj.dbref:
                caller.db.equip = ""  # equip with got object
                caller.msg("You unequip %s" % obj.name)
        # Call the object script's at_drop() method.
        obj.at_drop(caller)


class CmdLook(MuxCommand):
    """
    look at location or object

    Usage:
      look
      look <obj>
      look *<player>

    Observes your location or objects in your vicinity.
    """
    key = "look"
    aliases = ["l", "ls"]
    locks = "cmd:all()"
    arg_regex = r"\s|$"

    def func(self):
        """
        Handle the looking.
        """
        if not self.args:
            target = self.caller.location
            if not target:
                self.caller.msg("You have no location to look at!")
                return
        else:
            # search if dbref of target passed 
            if '#' in self.args:  # if '#' in argument search by dbref
                targetdbref = "#"+self.args.split('#')[1]
                
                # search for target name with same dbref
                targets = [item for item in [self.caller.search(self.args.split('#')[0])] if targetdbref==item.dbref]

                # if target asked found
                if targets:
                    target = targets[0]
                else:
                    caller.msg("You aren't carrying %s." % self.args)
                    return

            else: # else search for target by only name
                target = self.caller.search(self.args)
                if not target:
                    return
            
        self.msg(self.caller.at_look(target))


class ObjManipCommand(MuxCommand):
    """
    This is a parent class for some of the defining objmanip commands
    since they tend to have some more variables to define new objects.

    Each object definition can have several components. First is
    always a name, followed by an optional alias list and finally an
    some optional data, such as a typeclass or a location. A comma ','
    separates different objects. Like this:

        name1;alias;alias;alias:option, name2;alias;alias ...

    Spaces between all components are stripped.

    A second situation is attribute manipulation. Such commands
    are simpler and offer combinations

        objname/attr/attr/attr, objname/attr, ...

    """
    # OBS - this is just a parent - it's not intended to actually be
    # included in a commandset on its own!

    def parse(self):
        """
        We need to expand the default parsing to get all
        the cases, see the module doc.
        """
        # get all the normal parsing done (switches etc)
        super(ObjManipCommand, self).parse()

        obj_defs = ([], [])    # stores left- and right-hand side of '='
        obj_attrs = ([], [])  #                   "

        for iside, arglist in enumerate((self.lhslist, self.rhslist)):
            # lhslist/rhslist is already split by ',' at this point
            for objdef in arglist:
                aliases, option, attrs = [], None, []
                if ':' in objdef:
                    objdef, option = [part.strip() for part in objdef.rsplit(':', 1)]
                if ';' in objdef:
                    objdef, aliases = [part.strip() for part in objdef.split(';', 1)]
                    aliases = [alias.strip() for alias in aliases.split(';') if alias.strip()]
                if '/' in objdef:
                    objdef, attrs = [part.strip() for part in objdef.split('/', 1)]
                    attrs = [part.strip().lower() for part in attrs.split('/') if part.strip()]
                # store data
                obj_defs[iside].append({"name":objdef, 'option':option, 'aliases':aliases})
                obj_attrs[iside].append({"name":objdef, 'attrs':attrs})

        # store for future access
        self.lhs_objs = obj_defs[0]
        self.rhs_objs = obj_defs[1]
        self.lhs_objattr = obj_attrs[0]
        self.rhs_objattr = obj_attrs[1]


class CmdCreate(ObjManipCommand):
    """
    create new objects

    Usage:
      @create[/drop] objname[;alias;alias...][:typeclass], objname...

    switch:
       drop - automatically drop the new object into your current
              location (this is not echoed). This also sets the new
              object's home to the current location rather than to you.

    Creates one or more new objects. If typeclass is given, the object
    is created as a child of this typeclass. The typeclass script is
    assumed to be located under types/ and any further
    directory structure is given in Python notation. So if you have a
    correct typeclass 'RedButton' defined in
    types/examples/red_button.py, you could create a new
    object of this type like this:

       @create/drop button;red : examples.red_button.RedButton

    """

    key = "@create"
    locks = "cmd:perm(create) or perm(Builders)"
    help_category = "Building"

    def func(self):
        """
        Creates the object.
        """

        caller = self.caller

        if not self.args:
            string = "Usage: @create[/drop] <newname>[;alias;alias...] [:typeclass_path]"
            caller.msg(string)
            return

        # create the objects
        for objdef in self.lhs_objs:
            string = ""
            name = objdef['name']
            aliases = objdef['aliases']
            typeclass = objdef['option']

            # create object (if not a valid typeclass, the default
            # object typeclass will automatically be used)
            lockstring = "control:id(%s);delete:id(%s) or perm(Wizards)" % (caller.id, caller.id)
            obj = create.create_object(typeclass, name, caller,
                                       home=caller, aliases=aliases,
                                       locks=lockstring, report_to=caller)
            if not obj:
                continue
            if aliases:
                string = "You create a new %s: %s (aliases: %s)."
                string = string % (obj.typename, obj.name, ", ".join(aliases))
            else:
                string = "You create a new %s: %s."
                string = string % (obj.typename, obj.name)
            # set a default desc
            if not obj.db.desc:
                obj.db.desc = "You see nothing special."
            if 'drop' in self.switches:
                if caller.location:
                    obj.home = caller.location
                    obj.move_to(caller.location, quiet=True)
                    caller.msg("DATA,obj_add," + obj.name + obj.dbref)
        if string:
            caller.msg(string)
