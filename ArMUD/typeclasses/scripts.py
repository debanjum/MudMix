"""
Scripts

Scripts are powerful jacks-of-all-trades. They have no in-game
existence and can be used to represent persistent game systems in some
circumstances. Scripts can also have a time component that allows them
to "fire" regularly or a limited number of times.

There is generally no "tree" of Scripts inheriting from each other.
Rather, each script tends to inherit from the base Script class and
just overloads its hooks to have it perform its function.

"""

from evennia import DefaultScript, utils
from evennia.utils.spawner import spawn
import objects, random
import urllib2, simplejson, forecastio

class Script(DefaultScript):
    """
    A script type is customized by redefining some or all of its hook
    methods and variables.

    * available properties

     key (string) - name of object
     name (string)- same as key
     aliases (list of strings) - aliases to the object. Will be saved
              to database as AliasDB entries but returned as strings.
     dbref (int, read-only) - unique #id-number. Also "id" can be used.
     date_created (string) - time stamp of object creation
     permissions (list of strings) - list of permission strings

     desc (string)      - optional description of script, shown in listings
     obj (Object)       - optional object that this script is connected to
                          and acts on (set automatically by obj.scripts.add())
     interval (int)     - how often script should run, in seconds. <0 turns
                          off ticker
     start_delay (bool) - if the script should start repeating right away or
                          wait self.interval seconds
     repeats (int)      - how many times the script should repeat before
                          stopping. 0 means infinite repeats
     persistent (bool)  - if script should survive a server shutdown or not
     is_active (bool)   - if script is currently running

    * Handlers

     locks - lock-handler: use locks.add() to add new lock strings
     db - attribute-handler: store/retrieve database attributes on this
                        self.db.myattr=val, val=self.db.myattr
     ndb - non-persistent attribute handler: same as db but does not
                        create a database entry when storing data

    * Helper methods

     start() - start script (this usually happens automatically at creation
               and obj.script.add() etc)
     stop()  - stop script, and delete it
     pause() - put the script on hold, until unpause() is called. If script
               is persistent, the pause state will survive a shutdown.
     unpause() - restart a previously paused script. The script will continue
                 from the paused timer (but at_start() will be called).
     time_until_next_repeat() - if a timed script (interval>0), returns time
                 until next tick

    * Hook methods (should also include self as the first argument):

     at_script_creation() - called only once, when an object of this
                            class is first created.
     is_valid() - is called to check if the script is valid to be running
                  at the current time. If is_valid() returns False, the running
                  script is stopped and removed from the game. You can use this
                  to check state changes (i.e. an script tracking some combat
                  stats at regular intervals is only valid to run while there is
                  actual combat going on).
      at_start() - Called every time the script is started, which for persistent
                  scripts is at least once every server start. Note that this is
                  unaffected by self.delay_start, which only delays the first
                  call to at_repeat().
      at_repeat() - Called every self.interval seconds. It will be called
                  immediately upon launch unless self.delay_start is True, which
                  will delay the first call of this method by self.interval
                  seconds. If self.interval==0, this method will never
                  be called.
      at_stop() - Called as the script object is stopped and is about to be
                  removed from the game, e.g. because is_valid() returned False.
      at_server_reload() - Called when server reloads. Can be used to
                  save temporary variables you want should survive a reload.
      at_server_shutdown() - called at a full server shutdown.

    """
    pass



class Weather(DefaultScript): 
    "Displays weather info. Meant to be attached to a room."

    def at_script_creation(self):
        "Called once, during initial creation"
        self.key = "weather_script"
        self.desc = "Gives random weather messages."
        self.interval = 30 * 60 # every 30 minutes
        self.persistent = True
        self.db.API_Key = 'e07c570e1f5f85a42dacce70bc6c63ce'

    def at_repeat(self):
        "called every self.interval seconds."
        loc = self.obj.db.location.split(',')

        # if location contains lat, lng
        if len(loc)==2:
            # Get Weather from Forecast.io
            forecast = forecastio.load_forecast(self.db.API_Key, loc[0], loc[1])

        # Extract current weather summary
        self.obj.db.cweather = forecast.currently().summary


class RoomState(DefaultScript): 
    "Displays weather info. Meant to be attached to a room."

    def at_script_creation(self):
        "Called once, during initial creation"
        self.key = "roomstate_script"
        self.desc = "main script for creating and maintaining room state."
        self.interval = 2 * 60 # every 5 minutes
        self.persistent = True
        self.db.available_veggies = ["orange", "tomato", "potato", "apple", "rutabega", "pineapple", "blueberry", "mushroom"]
        self.db.fogmessage = ['A mist hangs over everything','The fog turns everything into murky shadows','You worry about your footing in the dense fog']
        self.db.clearmessage = ['A clear day','Sunlight streams across','The trees are bright and green','You hear the laughter of undergrads','You wish you had a frisbee']
        self.db.cloudmessage = ['Clouds sweep across the sky.','A faint breeze is felt','A cloud in the distance reminds you of something','Tree branches creak and sway in the wind','A chill comes over you','You see a mountain in the distance']
        self.db.rainmessage = ['The rain falls heavily on the ground','The dark clouds are pregnant with rain','The ground is slick, be careful']
        self.db.snowmessage = ['White snow blankets the world','The cold bites at your face']
        self.db.API_Key = 'e07c570e1f5f85a42dacce70bc6c63ce'

    def at_start(self):
        print self.obj.db.roomtype_value,self.obj.db.roomtype_key

        if not self.db.fogmessage:
            self.db.fogmessage = ['A mist hangs over everything','The fog turns everything into murky shadows','You worry about your footing in the dense fog']
            self.db.clearmessage = ['A clear day','Sunlight streams across','The trees are bright and green','You hear the laughter of undergrads','You wish you had a frisbee']
            self.db.cloudmessage = ['Clouds sweep across the sky.','A faint breeze is felt','A cloud in the distance reminds you of something','Tree branches creak and sway in the wind','A chill comes over you','You see a mountain in the distance']
            self.db.rainmessage = ['The rain falls heavily on the ground','The dark clouds are pregnant with rain','The ground is slick, be careful']
            self.db.snowmessage = ['White snow blankets the world','The cold bites at your face']


        if self.obj.db.roomtype_value == 'university':
            pass

        if self.obj.db.roomtype_key != 'building':
            # search for veggies in room 
            veggies_in_room = [obj for obj in self.obj.contents_get() if utils.inherits_from(obj, objects.Vegetable)]
            # if not veggies in room
            if not veggies_in_room:
                prototype = random.choice(self.db.available_veggies)
                # use the spawner to create a new Vegetable from the spawner dictionary
                veggie = spawn(objects.VEGETABLE_PROTOTYPES[prototype], prototype_parents=objects.VEGETABLE_PROTOTYPES)[0]
                veggie.location = self.obj
                veggiestring = ("A %s ripens" % veggie)
                self.obj.msg_contents(veggiestring)
                self.obj.msg_contents("DATA,obj_add," + veggie + veggie.dbref)

            # if weather not set for room 
            if not self.db.cweather:
                loc = self.obj.db.location.split(',')
                # if location contains lat, lng
                if len(loc)==2:
                    # Get Weather from Forecast.io
                    forecast = forecastio.load_forecast(self.db.API_Key, loc[0], loc[1])
                    # Extract and store current weather summary
                    self.obj.cweather = forecast.currently().summary


    def at_repeat(self):
        "called every self.interval seconds."

        weathermessage = 'A clear day'

        if self.obj.db.roomtype_key != 'building' and self.obj.db.cweather: # 'building'
            "weather updates if outdoors"
            # trigger random message of type found from last weather summary of room
            if 'Haze' in self.obj.db.cweather or 'Fog' in self.obj.db.cweather:
                weathermessage = random.choice(self.db.fogmessage)
            elif 'Clear' in self.obj.db.cweather:
                weathermessage = random.choice(self.db.clearmessage)
            elif 'Cloud' in self.obj.db.cweather or 'Overcast' in self.obj.db.cweather:
                weathermessage = random.choice(self.db.cloudmessage)
            elif 'Rain' in self.obj.db.cweather or 'Thunder' in self.obj.db.cweather or 'thunder' in self.obj.db.cweather or 'Drizzle' in self.obj.db.cweather:
                weathermessage = random.choice(self.db.rainmessage)
            elif 'Snow' in self.obj.db.cweather or 'snow' in self.obj.db.cweather or 'flurry' in self.obj.db.cweather:
                weathermessage = random.choice(self.db.snowmessage)

        # send this message to everyone inside the object this script is attached to (likely a room)
        self.obj.msg_contents(weathermessage)

        # vegetable spawn control
        veggies_in_room = [obj for obj in self.obj.contents_get() if utils.inherits_from(obj, objects.Vegetable)]

        if self.obj.db.roomtype_key == 'leisure' and not veggies_in_room:
            "vegetables spawned if less than threshold in farms"
            prototype = random.choice(self.db.available_veggies)
            # use the spawner to create a new Vegetable from the spawner dictionary
            veggie = spawn(objects.VEGETABLE_PROTOTYPES[prototype], prototype_parents=objects.VEGETABLE_PROTOTYPES)[0]
            veggie.location = self.obj
            veggiestring = ("A %s ripens" % veggie)
            self.obj.msg_contents(veggiestring)
            self.obj.msg_contents("DATA,obj_add," + veggie + veggie.dbref)
