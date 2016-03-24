/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.rade.armud.armudclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import in.rade.armud.armudclient.data.PhoneDataLayerListenerService;
import in.rade.armud.armudclient.websocket.WebSocketClient;


public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, TextToSpeech.OnInitListener {

    protected static final String LOCATION_TAG = "location-updates-sample";
    protected static final String WEBSOCKET_TAG = "Websocket-client";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static int count = 0;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical / virtual location.
     */
    protected Location mCurrentLocation;
    protected String mCurrentRoom;

    // for help in smoothing location updates
    private float mLatestLocAccuracy;
    private long mRoomArrivalTime;
    protected float mAccuracyThresh = 30; // dynamic location update threshold


    // UI Widgets.
    protected Button mSubmitNameButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView mLocInfoTextView;
    protected EditText mCharacterNameEdit;
    private boolean mSubmitSuccess;


    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;
    protected String mLocInfoLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    protected Boolean mConnected;
    protected Boolean amWaitingOnSubmit;
    protected Boolean mLoggedIn;
    private boolean mReceiverRegistered;
    protected String mLoginString;

    protected SharedPreferences mPrefs;

    private TextToSpeech tts;       //Declare text to speech variable

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    List<BasicNameValuePair> extraHeaders = Arrays.asList(
            new BasicNameValuePair("Cookie", "session=abcd")
    );



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Locate the UI widgets.
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);
        mLocInfoTextView = (TextView) findViewById(R.id.locinfo_text);
        mSubmitNameButton = (Button) findViewById(R.id.enterButton);
        mCharacterNameEdit = (EditText) findViewById(R.id.editName);

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";
        mLocInfoLabel = "";
        mConnected = false;
        amWaitingOnSubmit = false;
        mLoggedIn = false;
        mSubmitSuccess = false;

        mLatestLocAccuracy = 30;
        mRoomArrivalTime = 0;
        mCurrentRoom = "";


        // Add the project titles to display in a list for the listview adapter.

        // Initialise a listview adapter with the project titles and use it
        // in the listview to show the list of project.

        // Initialise TTS engine
        tts = new TextToSpeech(this, this);
        tts.setSpeechRate(1.2f);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        //getting gesture data from watchlistener service
        LocalBroadcastManager.getInstance(this).registerReceiver(mMsgFromWearReceiver,
                new IntentFilter(Globals.COMMAND_PATH));
        mReceiverRegistered = true;


        startService(new Intent(this, PhoneDataLayerListenerService.class));

        client.connect();

        mPrefs = getPreferences(MODE_PRIVATE);
        if (mPrefs.contains("LOGIN_STRING")) {
            mLoginString = mPrefs.getString("LOGIN_STRING", "connect test test");
            Log.d("login string", mLoginString);
            logintoMUD();
            mSubmitNameButton.setVisibility(View.GONE);
            mCharacterNameEdit.setVisibility(View.GONE);
            mLatitudeTextView.setText("");
        }
    }


    @Override
    public void onInit(int code) {
        if (code==TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            Log.d("TTS initialised. Code: ", String.valueOf(code));
        } else {
            tts = null;
            Log.d("TTS failed. Code:", String.valueOf(code));
        }
    }

    private BroadcastReceiver mMsgFromWearReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("watch command received", message);
            client.send(message);
        }
    };



    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void submitNameButtonHandler(View view) {
        if (!amWaitingOnSubmit && !mSubmitSuccess) {
            String name = mCharacterNameEdit.getText().toString();
            boolean hasNonAlpha = name.matches("^.*[^a-zA-Z0-9 ].*$");

            if (name == null || name.equals("")) {
                mCharacterNameEdit.setText("Please Enter a Name!");
            } else if (hasNonAlpha) {
                mCharacterNameEdit.setText("Name must be alphanumeric!");
            } else if (name.length() > 12) {
                mCharacterNameEdit.setText("Name must be  < 12 chars");
            } else {
                Random r = new Random();
                int password = r.nextInt(10000 - 100 + 1) + 100;
                String createString = "create " + name + " " + password;
                client.send(createString);
                amWaitingOnSubmit = true;

                mLoginString = "connect " + name + " " + password;
                Log.d("login init", mLoginString);
                mSubmitNameButton.setText("Try again / Start game");
            }
        } else {
            if (mSubmitSuccess) {
                mCharacterNameEdit.setText("");
                mSubmitNameButton.setVisibility(View.GONE);
                mCharacterNameEdit.setVisibility(View.GONE);
            } else {
                mCharacterNameEdit.setText("");
                mSubmitNameButton.setText("Submit");
            }
        }
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        if (null != mCurrentLocation && mLoggedIn) {
            mLatitudeTextView.setText(String.format("%s: %f", mLatitudeLabel,
                    mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.format("%s: %f", mLongitudeLabel,
                    mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                    mLastUpdateTime));
            mLocInfoTextView.setText(String.format("%s", mLocInfoLabel));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        if (!mReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMsgFromWearReceiver,
                    new IntentFilter(Globals.COMMAND_PATH));
            mReceiverRegistered = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (!mReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMsgFromWearReceiver,
                    new IntentFilter(Globals.COMMAND_PATH));
            mReceiverRegistered = true;
        }
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMsgFromWearReceiver);
        mReceiverRegistered = false;
        super.onStop();
        /*
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        */
    }

    /**
     * WEBSOCKET CONDUIT CODE
     */

    WebSocketClient client = new WebSocketClient(URI.create("ws://rade.in:8001"), new WebSocketClient.Listener() {
        @Override
        public void onConnect() {
            Log.d(WEBSOCKET_TAG, "Connected!");
            speech("Armud connected");
            mConnected = true;
        }

        @Override
        public void onMessage(String message) {
            Log.d(WEBSOCKET_TAG, String.format("Got string message! %s", message));
            message = message.substring(3, message.length());
            parseMessage(message.split(","));
        }

        @Override
        public void onMessage(byte[] data) {
            Log.d(WEBSOCKET_TAG, String.format("Got binary message! %s", data.toString()));
            mLocInfoLabel = data.toString();
        }

        @Override
        public void onDisconnect(int code, String reason) {
            Log.d(WEBSOCKET_TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            mConnected = false;
            speech("Armud Disconnected");
            while (!mConnected) {
                try {
                    Log.d(WEBSOCKET_TAG, "trying to connect to server");
                    Thread.sleep(1000);
                    client.connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(Exception error) {
           // client.disconnect();
            Log.e(WEBSOCKET_TAG, "Error!", error);
            if (error.toString().contains("EPIPE")) {
                mConnected = false;
                speech("Armud Disconnected");
                while (!mConnected) {
                    try {
                        Log.d(WEBSOCKET_TAG, "trying to connect to server");
                        Thread.sleep(1000);
                        client.connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }, extraHeaders);

    /**
     * Initiate Connection and Login MUD over websocket
     */
    public void logintoMUD()
    {
        Log.d(WEBSOCKET_TAG, "Connecting to ARMud Server");

        client.connect();

        while (!mConnected) {
            try {
                Log.d(WEBSOCKET_TAG, "trying to connect to server");
                Thread.sleep(1000);
                client.connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(WEBSOCKET_TAG, mLoginString);
        client.send(mLoginString);
        String charName = mLoginString.split(" ")[1];
        new SendMessageToWearThread(Globals.ARMUD_DATA_PATH, "name," + charName).start();
        mLoggedIn = true;
    }

    /**
     * Communicate with MUD over websocket
     */
    public void talkwithMUD(final Location location)
    {
        if(null == location) {
            Toast.makeText(this, "Make sure location is turned on!", Toast.LENGTH_LONG);
        } else if(mConnected) {
            String querystring = "location " + location.getLatitude() + " " + location.getLongitude() + " " + location.getAccuracy();
            client.send(querystring);
        }
    }

    private void parseMessage(String[] splitMessage)
    {
        Log.d(WEBSOCKET_TAG, "parsing");
        if (splitMessage[0].equals("DATA")) {
            Log.d(WEBSOCKET_TAG, "Sending message to watch");
            new SendMessageToWearThread(Globals.ARMUD_DATA_PATH, splitMessage[1] + "," + splitMessage[2]).start();
            if (splitMessage[1].equals("LOC") && !splitMessage[2].equals(mCurrentRoom)){
                mAccuracyThresh = mLatestLocAccuracy < 10 ? 10 : mLatestLocAccuracy;
                mCurrentRoom = splitMessage[2];
                mRoomArrivalTime = System.currentTimeMillis();
            }

        } else {
            //keep in mind that the message either can't have commas or the splitMessage array needs to be reworked
            if (amWaitingOnSubmit) {
                if (!"Sorry".equals(splitMessage[0])) {
                    mSubmitSuccess = true;
                    SharedPreferences.Editor edit = mPrefs.edit();
                    edit.putString("LOGIN_STRING", mLoginString);
                    while (!edit.commit()) {
                        Log.d("login init", "commit failed, trying again");
                    }
                    logintoMUD();
                }
                amWaitingOnSubmit = false;
            }
            String message = "";
            for (int i = 0 ; i < splitMessage.length; i++) {
                message = i == 0 ? splitMessage[i] : message + ", " + splitMessage[i];
            }
            message = Html.fromHtml(message).toString();
            speech(message);
            mLocInfoLabel = mLocInfoLabel  + "\n ======== \n" + message;
        }
    }

    private void speech(String Message)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(Message, TextToSpeech.QUEUE_ADD, null, null);
            Log.d("Message Passed to TTS:", Message);
        }
        else{
            Log.d("SDK Version less than:", String.valueOf(Build.VERSION_CODES.LOLLIPOP));
        }
    }



/**
 * END OF WEBSOCKETS
 *
 * LOCATION SERVICES FROM HERE DOWN
 */

 /**
 * Getting Location Updates.
 *
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 *
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(LOCATION_TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(LOCATION_TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOCATION_TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.d("onConnected", "talk with mud");
            talkwithMUD(mCurrentLocation);
            //client.send("location 72.012 23.231");
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("onLocationChanged", "Accuracy: " + mCurrentLocation.getAccuracy() );

        //increase threshold by one meter every ten seconds, until the threshold has been increased by 20 meters
        float timeBasedThresholdAugment = (System.currentTimeMillis() - mRoomArrivalTime) / 3000;
        timeBasedThresholdAugment = timeBasedThresholdAugment > 20 ? 20 : timeBasedThresholdAugment;

        if (mCurrentLocation.getAccuracy() < mAccuracyThresh + timeBasedThresholdAugment) {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mLatestLocAccuracy = mCurrentLocation.getAccuracy();
            Log.d("onLocationChanged", "talk with mud");
            talkwithMUD(mCurrentLocation);
            //client.send("location 72.012 23.231");
            updateUI();
            Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(LOCATION_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(LOCATION_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    class SendMessageToWearThread extends Thread {
        String path;
        String message;

        // Constructor for sending data objects to the data layer
        SendMessageToWearThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for(Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                while(!result.getStatus().isSuccess()){
                    Log.e("SendMessageToWearThread", "error");
                    result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                }
                Log.i("SendMessageToWearThread", "success!! sent to: " + node.getDisplayName());
            }
        }
    }

}
