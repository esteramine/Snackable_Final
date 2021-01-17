package com.example.snackable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutritionChartAdapter extends BaseAdapter {
    Context c;
    List<Map<String, String>> nutritionContents;
    //HashMap<String, String> nutritionContents = new HashMap<>();
    LayoutInflater inflater;

    public NutritionChartAdapter(Context c, List<Map<String, String>> nutritionContents) {
        this.c = c;
        this.nutritionContents = nutritionContents;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return nutritionContents.size();
    }

    @Override
    public Object getItem(int position) {
        return nutritionContents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.dri_chart_info, parent, false);
        TextView nutritionName = v.findViewById(R.id.nutritionName);
        TextView nutritionAmount = v.findViewById(R.id.nutritionAmount);
        PieChart pieChart = v.findViewById(R.id.nutritionChart);

        nutritionName.setText(nutritionContents.get(position).get("Name").toUpperCase());
        nutritionAmount.setText(nutritionContents.get(position).get("Amount"));

        //pie chart set up
        ArrayList<PieEntry> entry = new ArrayList<>();
        float driAmount = Float.parseFloat(nutritionContents.get(position).get("DRI"));
        float consumed = Float.parseFloat(nutritionContents.get(position).get("Amount").replaceAll("[^\\d.]", ""));
        float percentage = consumed*100/driAmount;
        if (consumed > driAmount){ //over 100%
            entry.add(new PieEntry(driAmount));
            entry.add(new PieEntry(0));
        }
        else{
            entry.add(new PieEntry(consumed));
            entry.add(new PieEntry(driAmount-(float)consumed));
        }

        PieDataSet pieDataSet = new PieDataSet(entry, "type");

        ArrayList<Integer> colors = new ArrayList<>();
        if(percentage > 70){ //over how much percent -> danger
            colors.add(Color.parseColor("#F96E6E"));
        }
        else if(percentage < 30){ //over how much percent -> okay
            colors.add(Color.parseColor("#00BF7C"));
        }
        else { //over how much percent -> warning
            colors.add(Color.parseColor("#FDC605"));
        }
        colors.add(Color.parseColor("#55C4C4C4"));

        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieChart.setHoleRadius(62f*100f/76f);
        pieChart.setRotationEnabled(false);
        pieChart.setCenterText(String.valueOf((int)(consumed*100/driAmount))+ "%");
        pieChart.setCenterTextSize(20);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();


        //pie chart set up
        /*ArrayList<PieEntry> entry = new ArrayList<>();
        float driAmount = Float.parseFloat(nutritionContents.get(position).get("DRI"));
        float consumed = Float.parseFloat(nutritionContents.get(position).get("Amount").replaceAll("[^\\d.]", ""));
        float percentage = consumed*100/driAmount;
        if (consumed > driAmount){ //over 100%
            entry.add(new PieEntry(driAmount));
            entry.add(new PieEntry(0));
        }
        else{
            entry.add(new PieEntry(consumed));
            entry.add(new PieEntry(driAmount-(float)consumed));
        }

        PieDataSet pieDataSet = new PieDataSet(entry, "type");

        ArrayList<Integer> colors = new ArrayList<>();
        if(percentage > 70){ //over how much percent -> danger
            colors.add(Color.parseColor("#F96E6E"));
        }
        else if(percentage < 30){ //over how much percent -> okay
            colors.add(Color.parseColor("#00BF7C"));
        }
        else { //over how much percent -> warning
            colors.add(Color.parseColor("#FDC605"));
        }
        colors.add(Color.parseColor("#C4C4C4"));

        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(false);
        pieChart.setHoleRadius(62f*100f/76f);
        pieChart.setCenterText(String.valueOf((int)(consumed*100/driAmount))+ "%\nDRI");
        pieChart.setCenterTextSize(20);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();*/

        return v;
    }

}
