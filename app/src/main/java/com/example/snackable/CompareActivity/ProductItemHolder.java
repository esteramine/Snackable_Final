package com.example.snackable.CompareActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snackable.utils.ItemClickListener;
import com.example.snackable.R;
import com.example.snackable.utils.ExpandableHeightGridView;

public class ProductItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView itemRanking;
    ImageView itemCrownImg;
    ImageView itemImg;
    TextView itemName;
    //GridView FAGridView;
    ExpandableHeightGridView FAGridView;
    TextView itemDes;
    ImageView itemBookmark;
    ItemClickListener itemClickListener;
    CardView cardView;

    public ProductItemHolder(@NonNull View itemView) {
        super(itemView);
        this.itemRanking = itemView.findViewById(R.id.itemRanking);
        this.itemCrownImg = itemView.findViewById(R.id.itemCrownImg);
        this.itemImg = itemView.findViewById(R.id.itemImg);
        this.itemName = itemView.findViewById(R.id.itemName);
        this.FAGridView = itemView.findViewById(R.id.itemFAGridView);
        this.itemDes = itemView.findViewById(R.id.itemDes);
        this.itemBookmark = itemView.findViewById(R.id.itemBookmark);
        this.cardView = itemView.findViewById(R.id.itemCardView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClickListener(v, getLayoutPosition());
    }
    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener = ic;
    }
}
