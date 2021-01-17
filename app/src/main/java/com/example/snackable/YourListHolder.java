package com.example.snackable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class YourListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView yourListItemImg;
    TextView yourListItemName;
    TextView yourListItemQuantity;
    TextView yourListItemSavedTime;
    ImageView yourListItemSavedImg;
    ItemClickListener itemClickListener;

    public YourListHolder(@NonNull View itemView) {
        super(itemView);
        this.yourListItemImg = itemView.findViewById(R.id.yourListItemImg);
        this.yourListItemName = itemView.findViewById(R.id.yourListItemName);
        this.yourListItemQuantity = itemView.findViewById(R.id.yourListItemQuantity);
        this.yourListItemSavedTime = itemView.findViewById(R.id.yourListItemSavedTime);
        this.yourListItemSavedImg = itemView.findViewById(R.id.yourListItemSavedImg);
        itemView.setOnClickListener(this);
    }

    public void onClick(View v) {
        this.itemClickListener.onItemClickListener(v, getLayoutPosition());
    }
    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener = ic;
    }
}
