package com.example.snackable.ListsActivity;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snackable.ItemClickListener;
import com.example.snackable.ProductItemDetail;
import com.example.snackable.ProductItemModel;
import com.example.snackable.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class YourListAdapter extends RecyclerView.Adapter<YourListHolder> {
    Context c;
    ArrayList<ProductItemModel> itemList = new ArrayList<>();
    boolean isHistory = false;

    public YourListAdapter(Context c, ArrayList<ProductItemModel> itemList, boolean isHistory) {
        this.c = c;
        this.itemList = itemList;
        this.isHistory = isHistory;
    }

    @NonNull
    @Override
    public YourListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.your_list_item, parent, false);
        return new YourListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YourListHolder holder, int position) {
        if (!itemList.get(position).getProductImg().isEmpty()){
            Picasso.get().load(itemList.get(position).getProductImg()).placeholder(R.drawable.loading)
                    .error(R.drawable.not_found).into(holder.yourListItemImg);
        }
        holder.yourListItemName.setText(itemList.get(position).getProductName());
        holder.yourListItemQuantity.setText(String.valueOf(itemList.get(position).getProductWeight())+"g");

        if(isHistory){ //set clock image view to visible
            holder.yourListItemSavedTime.setText(DateUtils.getRelativeTimeSpanString(itemList.get(position).getProductSavedTime(), System.currentTimeMillis(), 0));
        }
        else {
            holder.yourListItemSavedImg.setVisibility(View.GONE);
            holder.yourListItemSavedTime.setVisibility(View.GONE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                showPopup(itemList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    void showPopup(ProductItemModel model){
        Intent intent = new Intent(c, ProductItemDetail.class);
        intent.putExtra("Model", model);
        c.startActivity(intent);
    }
}
