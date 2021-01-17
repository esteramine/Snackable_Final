package com.example.snackable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FAGridAdapter extends BaseAdapter {
    Context c;
    List<Map<String, String>> foodAdditivesList;
    LayoutInflater inflater;

    public FAGridAdapter(Context c, List<Map<String, String>> foodAdditivesList) {
        this.c = c;
        this.foodAdditivesList = foodAdditivesList;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return foodAdditivesList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodAdditivesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.item_adapter_food_additive, parent, false);
        ImageView FASymbol = v.findViewById(R.id.FASymbol);
        TextView FAName = v.findViewById(R.id.FAName);
        v.setEnabled(false);

        String additive_name = foodAdditivesList.get(position).get("Name");
        FAName.setText(additive_name.substring(0,Math.min(additive_name.length(),16)));

        String level = foodAdditivesList.get(position).get("Level");
        if (level == "Safe"){ //safe
            FASymbol.setImageResource(R.drawable.final_safe_symbol);
        }
        else if (level == "Gray"){ //cut back
            FASymbol.setImageResource(R.drawable.final_not_available_symbol);
        }
        else if (level == "Avoid"){
            FASymbol.setImageResource(R.drawable.final_avoid_symbol);
        }
        else{
            FASymbol.setImageResource(R.drawable.final_cut_back_symbol);
        }
        return v;
    }
}
