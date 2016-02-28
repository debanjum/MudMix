package edu.dartmouth.cs.armudwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class ARMudWearActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTitleView;
    private TextView mClockView;
    private ListView mFocusListView;

    private ArrayList<String> mCharArray;
    private ArrayList<String> mObjArray;
    private ArrayList<String> mInvArray;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armud_wear);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTitleView = (TextView) findViewById(R.id.titleText);
        mClockView = (TextView) findViewById(R.id.clock);

        mFocusListView = (ListView) findViewById(R.id.focusListView);
        mCharArray = new ArrayList<String>();
        mObjArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                mObjArray.toArray(new String[mObjArray.size()]));
                mFocusListView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("data_changed"));
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String command = intent.getStringExtra("command");
            String obj = intent.getStringExtra("obj");
            Log.d("receiver", "Got commmand: " + command + "  " + obj);
            switch (command) {
                case "char_add":
                    mCharArray.add(obj);
                    break;
                case "char_remove":
                    mCharArray.remove(obj);
                    break;
                case "obj_add":
                    mObjArray.add(obj);
                    break;
                case "obj_remove":
                    mObjArray.remove(obj);
                    break;
                case "inv_add":
                    mInvArray.add(obj);
                    break;
                case "inv_remove":
                    mInvArray.remove(obj);
                    break;
                case "LOC":
                    mTitleView.setText(obj);
            }
            updateListView();
        }
    };


    private void updateListView() {
        adapter.clear();
        adapter.addAll(mCharArray);
        adapter.addAll(mObjArray);
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
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTitleView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTitleView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
