package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class PanduanAdapter extends RecyclerView.Adapter<PanduanAdapter.PanduanViewHolder> {

    private List<Map<String, String>> panduanList;

    public PanduanAdapter(List<Map<String, String>> panduanList) {
        this.panduanList = panduanList;
    }

    @NonNull
    @Override
    public PanduanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guide, parent, false);
        return new PanduanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanduanViewHolder holder, int position) {
        Map<String, String> panduan = panduanList.get(position);
        holder.textViewTitle.setText(panduan.get("title"));
        holder.textViewDescription.setText(panduan.get("description"));
    }

    @Override
    public int getItemCount() {
        return panduanList.size();
    }

    static class PanduanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription;

        public PanduanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
