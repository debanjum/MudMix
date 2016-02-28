package in.rade.armud.armudclient.data;

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

public class PhoneDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayer";

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG, "created");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            Log.d(TAG, "data item uri: " + uri.toString());
            final String path = uri!=null ? uri.getPath() : null;
            if("/COMMAND".equals(path)) {
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
