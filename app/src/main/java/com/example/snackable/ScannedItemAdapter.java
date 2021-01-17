package com.example.snackable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScannedItemAdapter extends RecyclerView.Adapter<ScannedItemHolder> {
    Context c;
    ArrayList<ScannedItemModel> models;

    public ScannedItemAdapter(Context c, ArrayList<ScannedItemModel> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public ScannedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);

        return new ScannedItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedItemHolder holder, int position) {
        holder.img.setImageResource(models.get(position).getImg());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
