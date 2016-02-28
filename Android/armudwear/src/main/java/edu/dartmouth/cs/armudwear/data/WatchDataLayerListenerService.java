package edu.dartmouth.cs.armudwear.data;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import edu.dartmouth.cs.armudwear.Globals;

public class WatchDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayer";

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
}
