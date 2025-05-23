package com.example.magic8ballapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private List<com.example.magic8ballapp.PredictionItem> items = new ArrayList<>();

    public void setItems(List<com.example.magic8ballapp.PredictionItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prediction, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        com.example.magic8ballapp.PredictionItem item = items.get(position);
        holder.textView.setText("❓ " + item.question + "\n🎱 " + item.answer);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
        }
    }
}