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
       character.db.HP = 100   # reset

def add_XP(character, amount):
    "Add XP to character, tracking level increases."
    character.db.XP += amount
    if character.db.XP >= (character.db.level + 1) ** 2:
        character.db.level += 1
        character.db.STR += 1
        character.db.combat += 2
        character.msg("You are now level %i!" % character.db.level)

def skill_combat(*args):
    """
    This determines outcome of combat. The one who
    rolls under their combat skill AND higher than
    their opponent's roll hits.
    """     
    char1, char2 = args
    roll1, roll2 = roll_hit(), roll_hit()        
    print roll1, roll2, char1.db.combat, char2.db.combat
    failtext = "You are hit by %s for %i damage!"
    wintext = "You hit %s for %i damage!"
    xp_gain = randint(1, 3)
    if char1.db.combat >= roll1 > roll2:
        # char 1 hits
        dmg = roll_dmg() + char1.db.STR
        char1.msg(wintext % (char2, dmg))        
        add_XP(char1, xp_gain)
        char2.msg(failtext % (char1, dmg))
        char2.db.HP -= dmg
        check_defeat(char2) 
    elif char2.db.combat >= roll2 > roll1:
        # char 2 hits
        dmg = roll_dmg() + char2.db.STR
        char1.msg(failtext % (char2, dmg))
        char1.db.HP -= dmg
        check_defeat(char1)
        char2.msg(wintext % (char1, dmg))       
        add_XP(char2, xp_gain) 
    else:
        # a draw
        drawtext = "Neither of you can find an opening."
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


def create_room(room_name, character, roomtype_key="Generic", roomtype_value="Generic"):
    """
    Create room(if doesn't exist) based on location metadata, 
    attach script to control room state and move player to the room
    """
    rooms = search_object(room_name, typeclass='typeclasses.rooms.Room')

    if not rooms:                                                      # If room doesn't exists
        room = create_object("typeclasses.rooms.Room", key=room_name)  # then create room
        room.scripts.add("typeclasses.scripts.RoomState")              # and attach script to control room state
        logger.log_info("Room %s Created" % room)
    else:
        room=rooms[0]

    # set room type if changed or new room
    if room.db.roomtype_key != roomtype_key or room.db.roomtype_value != roomtype_value:
        room.db.roomtype_key = roomtype_key
        room.db.roomtype_value = roomtype_value
        logger.log_info("Room Type Updated to %s: %s" % (room.db.roomtype_key,room.db.roomtype_value))

    # teleport character to room, if not already in room
    if character.location!=room_name:
        character.move_to(room, quiet=True)                            
        logger.log_info("User entered %s" % room)
