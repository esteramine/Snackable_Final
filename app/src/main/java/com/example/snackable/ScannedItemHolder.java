package com.example.snackable;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScannedItemHolder extends RecyclerView.ViewHolder {
    public ImageView img;
    public ScannedItemHolder(@NonNull View itemView) {
        super(itemView);
        this.img = itemView.findViewById(R.id.scannedItem);
    }
}
