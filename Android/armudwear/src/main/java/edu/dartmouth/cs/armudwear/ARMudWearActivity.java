package edu.dartmouth.cs.armudwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import edu.dartmouth.cs.armudwear.data.WatchDataLayerListenerService;
import edu.dartmouth.cs.armudwear.gesture.SensorsService;


public class ARMudWearActivity extends WearableActivity
implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
WearableListView.OnCentralPositionChangedListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private WatchViewStub mContainerView;
    private TextView mTitleView;
    private WearableListView mFocusListView;

    private ArrayList<String> mCharArray;
    private ArrayList<String> mObjArray;
    private ArrayList<String> mInvArray;
    private ArrayList<String> mFocusArray;
    private ObjectAdapter adapter;

    private int mCurrentFocusContext;
    private String mCurrentFocusObject;
    Intent mClassifyIntent;

    protected GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armud_wear);
        setAmbientEnabled();


        mTitleView = (TextView) findViewById(R.id.titleText);

        mContainerView= (WatchViewStub) findViewById(R.id.watch_view_stub);


        mContainerView.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mFocusListView = (WearableListView) stub.findViewById(R.id.focusListView);
                loadAdapter();
            }
        });

        mCharArray = new ArrayList<String>();
        mObjArray = new ArrayList<String>();
        mInvArray = new ArrayList<String>();
        mFocusArray = new ArrayList<String>();
        mFocusArray.add("Nothing Here");
        mCurrentFocusObject = "";
        mCurrentFocusContext = Globals.FOCUS_CONTEXT_IDLE;
        adapter = new ObjectAdapter(this, mFocusArray);

        buildGoogleApiClient();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMsgToWearReceiver,
                new IntentFilter("data_changed"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mCommandReceiver,
                new IntentFilter(Globals.COMMAND_UPDATED));

        startService(new Intent(this, WatchDataLayerListenerService.class));
        Log.d("onCreate", "complete");
    }

    // mMsgToWearReceiver will be called whenever an Intent
    // with an action named "data_changed" is broadcast.
    // This receiver deals with object changes from server
    private BroadcastReceiver mMsgToWearReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String command = intent.getStringExtra("command");
            String obj = intent.getStringExtra("obj");
            Log.d("command receiver", "Got command: " + command + " " + obj);
            switch (command) {
                case "char_add":
                    Log.d("command receiver", "adding character");
                    mCharArray.add(obj);
                    if (mCurrentFocusObject.equals("")) {
                        mCurrentFocusObject = obj;
                        if (mCurrentFocusContext != Globals.FOCUS_CONTEXT_CHARACTER) {
                            switchFocusContext(Globals.FOCUS_CONTEXT_CHARACTER);
                        }
                    }
                    break;
                case "char_remove":
                    if (mCharArray.contains(obj))
                        mCharArray.remove(obj);
                    break;
                case "obj_add":
                    mObjArray.add(obj);
                    break;
                case "obj_remove":
                    if (mObjArray.contains(obj))
                        mObjArray.remove(obj);
                    break;
                case "inv_add":
                    mInvArray.add(obj);
                    break;
                case "inv_remove":
                    if (mInvArray.contains(obj))
                        mInvArray.remove(obj);
                    break;
                case "LOC":
                    //Log.d("mtitleview", mTitleView.toString());
                    //mTitleView.setText(obj);
            }
            updateFocusArray();
        }
    };

    private void switchFocusContext(int focusContext) {
        if (mCurrentFocusContext == focusContext){
            return;
        }
        if (mCurrentFocusContext != Globals.FOCUS_CONTEXT_IDLE) {
            stopService(mClassifyIntent);
        }
        mClassifyIntent = new Intent(this, SensorsService.class);
        mClassifyIntent.putExtra(Globals.CONTEXT_KEY, focusContext);
        startService(mClassifyIntent);
    }

    // mCommandReceiver will be called whenever an Intent
    // with an action named Globals.COMMAND_UPDATED is broadcast.
    // This receiver deals with object changes from server
    private BroadcastReceiver mCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int commandNumber = intent.getIntExtra("commandNumber", Globals.NO_COMMAND_DETECTED);
            String command = "";
            switch (commandNumber) {
                case Globals.COMMAND_ID_ATTACK:
                    command = "attack";
                    break;
                case Globals.COMMAND_ID_CLAP:
                    command = "default";
                    break;
                case Globals.COMMAND_ID_DROP:
                    command = "drop";
                    break;
                case Globals.COMMAND_ID_THROW:
                    break;
                case Globals.COMMAND_ID_GET:
                    command = "get";
                    break;
            }
            String obj = mCurrentFocusObject;
            Log.d("Send command to phone", command + "  " + obj);
            final PutDataMapRequest putRequest = PutDataMapRequest.create("/COMMAND");
            final DataMap map = putRequest.getDataMap();
            map.putString("command", command);
            map.putString("obj", obj);
            Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
        }
    };


    private void updateFocusArray() {
        mFocusArray.clear();
        mFocusArray.addAll(mCharArray);
        mFocusArray.addAll(mObjArray);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        startService(new Intent(this, WatchDataLayerListenerService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        stopService(new Intent(this, WatchDataLayerListenerService.class));
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
 //           mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
 //          mTitleView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
//            mContainerView.setBackground(null);
 //           mTitleView.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
    protected synchronized void buildGoogleApiClient() {
        Log.i("Startup", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i("GoogleApiClient", "Connected!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("GoogleApiClient", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("GoogleApiClient", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onCentralPositionChanged(int i) {
        mCurrentFocusObject = mFocusArray.get(i);
    }

    private void loadAdapter() {
        ObjectAdapter mAdapter = new ObjectAdapter(this, mFocusArray);
        mFocusListView.setAdapter(mAdapter);
    }


    private static final class ObjectAdapter extends WearableListView.Adapter {
        private ArrayList<String> mDataset;
        private final Context mContext;
      //  private final LayoutInflater mInflater;
/*
        // Provide a suitable constructor (depends on the kind of dataset)
        public Adapter(Context context, ArrayList<String> dataset) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mDataset = dataset;
        }

        // Provide a reference to the type of views you're using
        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;
            public ItemViewHolder(View itemView) {
                super(itemView);
                // find the text view within the custom item's layout
                textView = (TextView) itemView.findViewById(R.id.name);
            }
        }

        // Create new views for list items
        // (invoked by the WearableListView's layout manager)
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // Inflate our custom layout for list items
            return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
        }

        // Replace the contents of a list item
        // Instead of creating new views, the list tries to recycle existing ones
        // (invoked by the WearableListView's layout manager)
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder,
                                     int position) {
            // retrieve the text view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView view = itemHolder.textView;
            // replace text contents
            view.setText(mDataset.get(position));
            // replace list item's metadata
            holder.itemView.setTag(position);
        }
        */
      public ObjectAdapter(Context context, ArrayList<String> items) {
          mContext = context;
          mDataset = items;
      }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new WearableListView.ViewHolder(new ObjectView(mContext));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder viewHolder, final int position) {
            ObjectView objectView = (ObjectView) viewHolder.itemView;
            final String item = mDataset.get(position);

            TextView textView = (TextView) objectView.findViewById(R.id.text);
            textView.setText(item);
        }

        // Return the size of your dataset
        // (invoked by the WearableListView's layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
