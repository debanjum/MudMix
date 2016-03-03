package in.rade.armud.armudclient.data;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import in.rade.armud.armudclient.Globals;

public class PhoneDataLayerListenerService extends WearableListenerService {

    private static final String TAG = "PhoneMessageListener";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.i(TAG, messageEvent.toString());
        if(messageEvent.getPath().equals(Globals.COMMAND_PATH)) {
            Log.d(TAG, "Message is command");
            final String message = new String(messageEvent.getData());
            Intent intent = new Intent(Globals.COMMAND_PATH);
            intent.putExtra("message", message);
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
            if("/COMMAND".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                String command = map.get("command");
                String obj = map.get("obj");
                Intent intent = new Intent(Globals.COMMAND_PATH);
                intent.putExtra("command", command);
                intent.putExtra("obj", obj);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }
    */
}
