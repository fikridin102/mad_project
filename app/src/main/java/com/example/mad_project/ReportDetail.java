package com.example.mad_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDetail extends Activity {

    private ExpandableListView expandableListView;
    private List<String> categoryList = new ArrayList<>();
    private HashMap<String, List<String>> reportList = new HashMap<>();
    private Map<String, String> reportEmails = new HashMap<>();

    private ReportExpandableListAdapter adapter;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_detail);

        expandableListView = findViewById(R.id.expandableListViewReports);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent report = new Intent(ReportDetail.this, Report.class);
                startActivity(report);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mad-project-2fa59-default-rtdb.firebaseio.com/");
        dbRef = database.getReference("Aduan");

        loadReports();

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedReport = reportList.get(categoryList.get(groupPosition)).get(childPosition);
                Toast.makeText(ReportDetail.this, "Selected: " + selectedReport, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void loadReports() {
        categoryList.clear();
        reportList.clear();
        reportEmails.clear();

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DataSnapshot categorySnapshot : task.getResult().getChildren()) {
                        String category = categorySnapshot.getKey();
                        categoryList.add(category);

                        List<String> reports = new ArrayList<>();
                        for (DataSnapshot reportSnapshot : categorySnapshot.getChildren()) {
                            String reportId = reportSnapshot.getKey();
                            String reportDescription = reportSnapshot.child("Perincian Laporan").getValue(String.class);
                            String reporterName = reportSnapshot.child("Nama Pengadu").getValue(String.class);
                            String email = reportSnapshot.child("Emel").getValue(String.class);
                            String address = reportSnapshot.child("Alamat").getValue(String.class);
                            String type = reportSnapshot.child("Jenis").getValue(String.class);

                            String reportDetails = "Perincian Laporan: " + reportDescription +
                                    "\nNama Pengadu: " + reporterName +
                                    "\nAlamat: " + address +
                                    (type != null ? "\nJenis: " + type : "");

                            reports.add(reportDetails);
                            reportEmails.put(reportId, email);
                        }
                        reportList.put(category, reports);
                    }

                    adapter = new ReportExpandableListAdapter(ReportDetail.this, categoryList, reportList, reportEmails);
                    expandableListView.setAdapter(adapter);
                } else {
                    Toast.makeText(ReportDetail.this, "Failed to load reports.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
