package com.example.snackable.ProductDetailActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snackable.utils.ItemClickListener;
import com.example.snackable.R;

public class ProductItemFAHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView FASymbol;
    TextView FAName;
    ;
    ItemClickListener itemClickListener;
    public ProductItemFAHolder(@NonNull View itemView) {
        super(itemView);
        this.FASymbol = itemView.findViewById(R.id.FASymbol);
        this.FAName = itemView.findViewById(R.id.FAName);
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
