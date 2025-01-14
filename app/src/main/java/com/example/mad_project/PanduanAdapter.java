package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PanduanAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // Guide titles
    private HashMap<String, List<String>> listDataChild; // Descriptions

    public PanduanAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;

        // Enable Firebase offline persistence
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.panduan_group_item, null);
        }

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        textViewTitle.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.panduan_child_item, null);
        }

        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.buttonDelete);

        textViewDescription.setText((String) getChild(groupPosition, childPosition));

        // Check internet connectivity to enable/disable edit and delete buttons
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isConnected = snapshot.getValue(Boolean.class);

                if (!isConnected) {
                    // Offline mode: Disable edit and delete buttons
                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnEdit.setAlpha(0.5f);
                    btnDelete.setAlpha(0.5f);
                } else {
                    // Online mode: Enable edit and delete buttons
                    btnEdit.setEnabled(true);
                    btnDelete.setEnabled(true);
                    btnEdit.setAlpha(1.0f);
                    btnDelete.setAlpha(1.0f);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        // Edit button functionality
        btnEdit.setOnClickListener(v -> {
            if (btnEdit.isEnabled()) {
                String guideTitle = listDataHeader.get(groupPosition);
                String guideDescription = listDataChild.get(guideTitle).get(childPosition);

                fetchGuideId(guideTitle, guideDescription, guideId -> {
                    if (guideId == null) {
                        Toast.makeText(context, "Guide not found", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, EditPanduanActivity.class);
                        intent.putExtra("guideId", guideId);
                        intent.putExtra("title", guideTitle);
                        intent.putExtra("description", guideDescription);
                        context.startActivity(intent);
                    }
                });
            }
        });

        // Delete button functionality
        btnDelete.setOnClickListener(v -> {
            if (btnDelete.isEnabled()) {
                String guideTitle = listDataHeader.get(groupPosition);
                String guideDescription = listDataChild.get(guideTitle).get(childPosition);

                fetchGuideId(guideTitle, guideDescription, guideId -> {
                    if (guideId == null) {
                        Toast.makeText(context, "Guide not found", Toast.LENGTH_SHORT).show();
                    } else {
                        deleteGuide(guideId, groupPosition, childPosition);
                    }
                });
            }
        });

        return convertView;
    }

    private void fetchGuideId(String guideTitle, String guideDescription, GuideIdCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("DisasterGuides");

        dbRef.keepSynced(true); // Ensure offline syncing

        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot guideSnapshot : task.getResult().getChildren()) {
                    String title = guideSnapshot.child("title").getValue(String.class);
                    String description = guideSnapshot.child("description").getValue(String.class);

                    if (guideTitle.equals(title) && guideDescription.equals(description)) {
                        String guideId = guideSnapshot.getKey();
                        callback.onGuideIdRetrieved(guideId);
                        return;
                    }
                }
            }
            callback.onGuideIdRetrieved(null);
        });
    }

    private void deleteGuide(String guideId, int groupPosition, int childPosition) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("DisasterGuides").child(guideId);

        dbRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String guideTitle = listDataHeader.get(groupPosition);
                listDataChild.get(guideTitle).remove(childPosition);

                if (listDataChild.get(guideTitle).isEmpty()) {
                    listDataChild.remove(guideTitle);
                    listDataHeader.remove(groupPosition);
                }

                notifyDataSetChanged();
                Toast.makeText(context, "Guide deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete guide", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private interface GuideIdCallback {
        void onGuideIdRetrieved(String guideId);
    }
}
