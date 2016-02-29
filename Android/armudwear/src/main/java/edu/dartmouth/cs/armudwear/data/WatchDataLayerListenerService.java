package edu.dartmouth.cs.armudwear.data;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import edu.dartmouth.cs.armudwear.Globals;

public class WatchDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, messageEvent.toString());
        if(messageEvent.getPath().equals(Globals.ARMUD_DATA_PATH)) {
            final String message = new String(messageEvent.getData());
            String[] splitMessage = message.split(",");
            Intent intent = new Intent(Globals.ARMUD_DATA_PATH);
            intent.putExtra("command", splitMessage[0]);
            intent.putExtra("obj", splitMessage[1]);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    /*
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.d(TAG, "onDataChanged: " + dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            Log.d(TAG, "data item uri: " + uri.toString());
            final String path = uri!=null ? uri.getPath() : null;
            if(Globals.ARMUD_DATA_PATH.equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String command = map.get("command");
                String obj = map.get("obj");
                Intent intent = new Intent("data_changed");
                intent.putExtra("obj", obj);
                intent.putExtra("command", command);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }
    */
}
