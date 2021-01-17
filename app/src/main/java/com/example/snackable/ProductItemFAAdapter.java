package com.example.snackable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductItemFAAdapter extends RecyclerView.Adapter<ProductItemFAHolder> {
    Context c;
    ArrayList<String> foodAdditivesList;

    public ProductItemFAAdapter(Context c, ArrayList<String> foodAdditivesList) {
        this.c = c;
        this.foodAdditivesList = foodAdditivesList;
    }

    @NonNull
    @Override
    public ProductItemFAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_additive, parent, false);
        return new ProductItemFAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemFAHolder holder, int position) {
        int additive_index = AdditivesDatabase.getAdditiveIndex(foodAdditivesList.get(position));

        String additiveType = AdditivesDatabase.getAdditiveType(additive_index);
        if (additiveType == "Safe")
            holder.FASymbol.setImageResource(R.drawable.final_safe_symbol);
        else if (additiveType == "Gray")
            holder.FASymbol.setImageResource(R.drawable.final_not_available_symbol);
        else if (additiveType == "Avoid")
            holder.FASymbol.setImageResource(R.drawable.final_avoid_symbol);
        else
            holder.FASymbol.setImageResource(R.drawable.final_cut_back_symbol);


        holder.FAName.setText(foodAdditivesList.get(position));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Intent intent = new Intent(c, ProductItemDetailInfoBtn.class);
                intent.putExtra("Additive", foodAdditivesList.get(position));
                intent.putExtra("AdditiveIndex", additive_index);
                c.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodAdditivesList.size();
    }
}
