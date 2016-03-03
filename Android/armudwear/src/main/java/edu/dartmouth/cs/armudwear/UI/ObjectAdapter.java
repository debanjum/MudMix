package edu.dartmouth.cs.armudwear.UI;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.dartmouth.cs.armudwear.R;

/**
 * Created by michael1 on 2/28/16.
 */
public class ObjectAdapter extends WearableListView.Adapter {
    private ArrayList<String> mDataset;
    private Context mContext;

    public ObjectAdapter(Context context, ArrayList<String> items) {
        this.mContext = context;
        this.mDataset = items;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(new ObjectView(mContext));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, final int position) {
        ObjectView objectView = (ObjectView) viewHolder.itemView;
        final String item = position + ".            " + mDataset.get(position);

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
