package com.example.mad_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PanduanAdapter extends RecyclerView.Adapter<PanduanAdapter.PanduanViewHolder> {

    private List<String> titles;
    private List<String> descriptions;

    public PanduanAdapter(List<String> titles, List<String> descriptions) {
        this.titles = titles;
        this.descriptions = descriptions;
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
        holder.textViewTitle.setText(titles.get(position));
        holder.textViewDescription.setText(descriptions.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
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
