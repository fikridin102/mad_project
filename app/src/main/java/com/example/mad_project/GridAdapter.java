package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Map;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private String[] labels;
    private int[] images;
    private Map<Integer, Class<?>> activityMap; // Map grid item index to target activity

    // Constructor accepts the context, labels, images, and a map of item-to-activity navigation
    public GridAdapter(Context context, String[] labels, int[] images, Map<Integer, Class<?>> activityMap) {
        this.context = context;
        this.labels = labels;
        this.images = images;
        this.activityMap = activityMap;
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, null); // Ensure this layout exists
        }

        ImageView imageView = view.findViewById(R.id.grid_image);
        TextView textView = view.findViewById(R.id.grid_label);

        imageView.setImageResource(images[position]);
        textView.setText(labels[position]);

        // Set the onClickListener for each grid item
        view.setOnClickListener(v -> {
            // Check if there's a mapping for the current position
            if (activityMap != null && activityMap.containsKey(position)) {
                // Get the target activity from the map and start it
                Class<?> targetActivity = activityMap.get(position);
                Intent intent = new Intent(context, targetActivity);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
