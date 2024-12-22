package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    private Map<Integer, Class<?>> activityMap;

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
            view = inflater.inflate(R.layout.grid_item, null);
        }

        ImageView imageView = view.findViewById(R.id.grid_image);
        TextView textView = view.findViewById(R.id.grid_label);

        imageView.setImageResource(images[position]);
        textView.setText(labels[position]);

        view.setOnClickListener(v -> {
            if (activityMap != null && activityMap.containsKey(position)) {
                Intent intent = new Intent(context, activityMap.get(position));
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, ReportForm.class);
                intent.putExtra("title", labels[position]);
                intent.putExtra("id", position);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
