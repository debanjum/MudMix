package edu.dartmouth.cs.armudwear;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.wearable.view.WearableListView;
import edu.dartmouth.cs.armudwear.R;

public final class ObjectView extends FrameLayout implements WearableListView.OnCenterProximityListener {

    final TextView text;

    public ObjectView(Context context) {
        super(context);
        View.inflate(context, R.layout.list_item, this);
        text = (TextView) findViewById(R.id.text);
        text.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void onCenterPosition(boolean b) {

        //Animation example to be ran when the view becomes the centered one
        text.animate().scaleX(1f).scaleY(1f).alpha(1);

    }

    @Override
    public void onNonCenterPosition(boolean b) {

        //Animation example to be ran when the view is not the centered one anymore
        text.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);

    }

    public TextView getText() {
        return text;
    }
}