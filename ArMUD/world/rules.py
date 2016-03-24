from evennia import create_object, search_object, logger
from random import randint

def roll_hit():
    "Roll 1d100"
    return randint(1, 10)

def roll_dmg():
    "Roll 1d6"
    return randint(1, 6)

def check_defeat(character):
    "Checks if a character is defeated."
    if character.db.HP <= 0:
        character.msg("You fall down, defeated!")
        if character.db.full_HP:
            character.db.HP = character.db.full_HP
        else:
            character.db.HP = 100   # reset
        # call enemy hook
        if hasattr(character, "set_dead"):
            character.location.msg_contents(character.db.death_msg)
            # find robot log object
            objs = [obj for obj in character.contents_get() if obj.name==character.db.log]

            if not objs:
                return
            else:
                obj = objs[0]

            print obj.name

            # drop robot log in room    
            obj.move_to(character.location, quiet=True)
            character.location.msg_contents("DATA,obj_add," + obj.name + obj.dbref)
            character.location.msg_contents("The %s falls to the ground." % obj.name, exclude=character)
            
            # call the object script's at_drop() method.
            obj.at_drop(character)

            # should return True if target is defeated, False otherwise.
            return character.set_dead()



def add_XP(character, amount):
    "Add XP to character, tracking level increases."
    character.db.XP += amount
    character.msg("DATA,xp,%i" % character.db.XP)
    if character.db.XP >= (character.db.level + 1) ** 2:
        character.db.level += 1
        character.db.STR += 1
        character.db.combat += 2
        character.msg("You're now level %i!" % character.db.level)
        character.msg("DATA,level,%i" % character.db.level)

def skill_combat(*args):
    """
    This determines outcome of combat. The one who
    rolls under their combat skill AND higher than
    their opponent's roll hits.
    """     
    char1, char2 = args
    roll1, roll2 = roll_hit(), roll_hit()        
    print roll1, roll2, char1.db.combat, char2.db.combat
    failtext = "%s hits you! %i damage!"
    wintext = "You hit %s! %i damage!"
    xp_gain = randint(1, 3)
    if char1.db.combat*2 >= roll1 *2 > roll2:
        # char 1 hits
        dmg = roll_dmg() + char1.db.STR
        char1.msg(wintext % (char2, dmg))        
        add_XP(char1, xp_gain)
        char2.msg(failtext % (char1, dmg))
        char2.db.HP -= dmg
        char2.msg("DATA,health,%s" % char2.db.HP)
        check_defeat(char2) 
        """
        if char2.db.combat >= roll2 > roll1:
        # char 2 hits
        dmg = roll_dmg() + char2.db.STR
        char1.msg(failtext % (char2, dmg))
        char1.db.HP -= dmg
        char1.msg("DATA,hp,%s" % char1.db.HP)
        check_defeat(char1)
        char2.msg(wintext % (char1, dmg))       
        add_XP(char2, xp_gain)
        """
    else:
        # a draw
        drawtext = "You miss."
        char1.msg(drawtext)
        char2.msg(drawtext)

SKILLS = {"kickbox": skill_combat}

def roll_challenge(character1, character2, skillname):
    """
    Determine the outcome of a skill challenge between
    two characters based on the skillname given. 
    """
    if skillname in SKILLS:
        SKILLS[skillname](character1, character2)
    else: 
        raise RunTimeError("Skillname %s not found." % skillname)


def create_room(room_name, character, location, roomtype_key="Generic", roomtype_value="Generic"):
    """
    Create room(if doesn't exist) based on location metadata, 
    attach script to control room state and move player to the room
    """
    rooms = search_object(room_name, typeclass='typeclasses.rooms.Room')

    if not rooms:                                                      # If room doesn't exists
        room = create_object("typeclasses.rooms.Room", key=room_name)  # then create room
        logger.log_info("Room %s Created" % room)
    else:
        room=rooms[0]

    # set room type if changed or new room
    if room.db.roomtype_key != roomtype_key or room.db.roomtype_value != roomtype_value:
        room.db.roomtype_key = roomtype_key
        room.db.roomtype_value = roomtype_value

        if roomtype_key != 'building':  # if outdoors
            room.scripts.add("typeclasses.scripts.Weather")             # attach script to get weather
            room.scripts.add("typeclasses.scripts.Outdoors")            # and attach script to control room state
        
        if roomtype_value == 'library':  # if library
            room.scripts.add('typeclasses.scripts.Library')

        logger.log_info("Room Type Updated to %s: %s" % (room.db.roomtype_key,room.db.roomtype_value))

    if not room.db.location:
        room.db.location = location

    # teleport character to room, if not already in room
    if character.location.name != room_name:
        character.move_to(room, quiet=True)
        logger.log_info("User entered %s" % room_name)
        character.msg("You've entered %s" % room_name)
        character.db.just_entered = False

    elif character.db.just_entered:
        character.msg("You're in %s" % room_name)
        character.db.just_entered = False
