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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // Title for the group
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

        // Set description for the child
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.buttonDelete);

        // Set description text
        textViewDescription.setText((String) getChild(groupPosition, childPosition));

        // Set click listener for the edit button
        btnEdit.setOnClickListener(v -> {
            String guideTitle = listDataHeader.get(groupPosition);
            String guideDescription = listDataChild.get(guideTitle).get(childPosition);

            // Fetch the guide ID and navigate to the edit activity
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
        });

        // Set click listener for the delete button
        btnDelete.setOnClickListener(v -> {
            String guideTitle = listDataHeader.get(groupPosition);
            String guideDescription = listDataChild.get(guideTitle).get(childPosition);

            // Confirm and delete the guide
            fetchGuideId(guideTitle, guideDescription, guideId -> {
                if (guideId == null) {
                    Toast.makeText(context, "Guide not found", Toast.LENGTH_SHORT).show();
                } else {
                    deleteGuide(guideId, groupPosition, childPosition);
                }
            });
        });

        return convertView;
    }

    private void fetchGuideId(String guideTitle, String guideDescription, GuideIdCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("DisasterGuides");

        // Query the entire DisasterGuides node
        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot guideSnapshot : task.getResult().getChildren()) {
                    String title = guideSnapshot.child("title").getValue(String.class);
                    String description = guideSnapshot.child("description").getValue(String.class);

                    // Check if both title and description match
                    if (guideTitle.equals(title) && guideDescription.equals(description)) {
                        String guideId = guideSnapshot.getKey(); // Retrieve the unique ID
                        callback.onGuideIdRetrieved(guideId);
                        return;
                    }
                }
            }
            // If no match is found
            callback.onGuideIdRetrieved(null);
        });
    }

    private void deleteGuide(String guideId, int groupPosition, int childPosition) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("DisasterGuides").child(guideId);

        // Remove the guide from Firebase
        dbRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Update local data
                String guideTitle = listDataHeader.get(groupPosition);
                List<String> childList = listDataChild.get(guideTitle);

                if (childList != null) {
                    // Remove the specific child (description)
                    childList.remove(childPosition);

                    // If no descriptions remain for this title, remove the group
                    if (childList.isEmpty()) {
                        listDataChild.remove(guideTitle);
                        listDataHeader.remove(groupPosition);
                    }

                    // Notify adapter about the changes
                    notifyDataSetChanged();
                    Toast.makeText(context, "Guide deleted successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = ((PanduanActivity) context).getIntent();
                    ((PanduanActivity) context).finish();
                    context.startActivity(intent);
                }
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
