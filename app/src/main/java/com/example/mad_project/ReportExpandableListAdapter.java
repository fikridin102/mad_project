package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportExpandableListAdapter extends android.widget.BaseExpandableListAdapter {

    private Context context;
    private List<String> categoryList;
    private HashMap<String, List<String>> reportList;
    private Map<String, String> reportEmails;

    public ReportExpandableListAdapter(Context context, List<String> categoryList, HashMap<String, List<String>> reportList, Map<String, String> reportEmails) {
        this.context = context;
        this.categoryList = categoryList;
        this.reportList = reportList;
        this.reportEmails = reportEmails;
    }

    @Override
    public int getGroupCount() {
        return categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return reportList.get(categoryList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categoryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reportList.get(categoryList.get(groupPosition)).get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String category = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }

        TextView type = convertView.findViewById(R.id.type);
        type.setText(category);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String reportDetails = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.report_item, null);
        }

        TextView reportTitle = convertView.findViewById(R.id.reportTitle);
        reportTitle.setText(reportDetails);

        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        btnEdit.setOnClickListener(v -> {
            String category = categoryList.get(groupPosition);
            String reportDetail = reportList.get(category).get(childPosition);

            getReportIdForPosition(groupPosition, childPosition, reportId -> {
                if (reportId == null) {
                    Toast.makeText(context, "Report ID not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String reportEmail = reportEmails.get(reportId);
                showEmailValidationDialog(reportDetail, reportEmail, category, reportId);
            });
        });

        btnDelete.setOnClickListener(v -> {
            String category = categoryList.get(groupPosition);
            String reportDetail = reportList.get(category).get(childPosition);

            getReportIdForPosition(groupPosition, childPosition, reportId -> {
                if (reportId == null) {
                    Toast.makeText(context, "Report ID not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String reportEmail = reportEmails.get(reportId);
                showEmailValidationDialogForDelete(reportDetail, reportEmail, category, reportId);
            });
        });

        // Ensure proper layout for buttons
        LinearLayout buttonLayout = convertView.findViewById(R.id.buttonLayout);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL); // Place buttons side by side
        btnEdit.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        btnDelete.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        return convertView;
    }


    private void getReportIdForPosition(int groupPosition, int childPosition, ReportIdCallback callback) {
        String category = categoryList.get(groupPosition);
        String reportDetails = reportList.get(category).get(childPosition);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("Aduan").child(category);

        dbRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot reportSnapshot : task.getResult().getChildren()) {
                    String reportId = reportSnapshot.getKey();
                    String description = reportSnapshot.child("Description").getValue(String.class);
                    String reporterName = reportSnapshot.child("Reporter Name").getValue(String.class);
                    String address = reportSnapshot.child("Address").getValue(String.class);

//

                    if (description != null && reporterName != null && address != null ) {

                        callback.onReportIdRetrieved(reportId);
                        return;
                    }


                }
            }
            callback.onReportIdRetrieved(null);
        });
    }



    private void showEmailValidationDialog(String reportDetail, String email, String category, String reportId) {
        // Create the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Construct the dialog title with email and report ID
        String dialogTitle = "Semakan (Ubah)";
        builder.setTitle(dialogTitle);

        // Create EditText for email input
        final EditText emailInput = new EditText(context);
        emailInput.setHint("Masukkan Emel Anda");
        builder.setView(emailInput);

        builder.setPositiveButton("Semak", (dialog, which) -> {
            String enteredEmail = emailInput.getText().toString().trim();
            if (enteredEmail.equalsIgnoreCase(email.trim())) {
                // Email matches, proceed to ReportForm
                Intent form = new Intent(context, ReportForm.class);
                form.putExtra("title", category);
                form.putExtra("reportId", reportId);
                context.startActivity(form);
            } else {
                // Email does not match
                Toast.makeText(context, "Incorrect email, please try again.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEmailValidationDialogForDelete(String reportDetail, String email, String category, String reportId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String dialogTitle = "Semakan (Padam)";
        builder.setTitle(dialogTitle);

        final EditText emailInput = new EditText(context);
        emailInput.setHint("Masukkan Emel Anda");
        builder.setView(emailInput);

        builder.setPositiveButton("Semak", (dialog, which) -> {
            String enteredEmail = emailInput.getText().toString().trim();
            if (enteredEmail.equalsIgnoreCase(email.trim())) {
                // Email matches, proceed to delete report
                deleteReport(reportId, category);
            } else {
                Toast.makeText(context, "Incorrect email, please try again.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteReport(String reportId, String category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        DatabaseReference dbRef = database.getReference("Aduan").child(category).child(reportId);

        dbRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Report deleted successfully.", Toast.LENGTH_SHORT).show();
                // Optionally update the UI or the adapter data to reflect the deletion
            } else {
                Toast.makeText(context, "Failed to delete report.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private interface ReportIdCallback {
        void onReportIdRetrieved(String reportId);
    }
}

