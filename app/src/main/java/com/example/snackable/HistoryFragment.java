package com.example.snackable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String HISTORY = "HISTORY";
    private final static String SAVED= "SAVED";
    private final static String ITEMLIST = "ITEMLIST";
    ArrayList<ProductItemModel> historyList = new ArrayList<>();
    RecyclerView recyclerView;
    YourListAdapter adapter;
    Context context;
    LinearLayout linearLayout;
    
    
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get shared preferences of history
        loadHistory();
        // Initialize view
        context = this.getContext();
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new YourListAdapter(this.getContext(), historyList, true);
        recyclerView.setAdapter(adapter);

        SwipeHelper swipeHelper = new SwipeHelper(context, recyclerView) {

            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "REMOVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#F84949"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Are you sure you want to remove "+ historyList.get(pos).getProductName()+" from history?")
                                        .setPositiveButton(Html.fromHtml("<font color='#FF0000'>REMOVE</font>"), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast toast = Toast.makeText(getContext(), historyList.get(pos).getProductName()+ " Removed!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                historyList.remove(pos);
                                                adapter.notifyItemRemoved(pos);
                                                updateHistoryData();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User cancelled the dialog
                                            }
                                        });
                                builder.create();
                                builder.show();
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "COMPARE",
                        R.drawable.ic_delete,
                        Color.parseColor("#FDC605"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                //Toast toast = Toast.makeText(getContext(), historyList.get(pos).getProductName()+ "\n Add to Compare!", Toast.LENGTH_LONG);
                                Toast toast = Toast.makeText(getContext(), "Added to Compare", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                saveToCompare(historyList.get(pos));
                                adapter.notifyDataSetChanged();
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "SAVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#00BF7C"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                //Toast toast = Toast.makeText(getActivity().getBaseContext(), historyList.get(pos).getProductName()+ " Saved!", Toast.LENGTH_LONG);
                                Toast toast = Toast.makeText(getActivity().getBaseContext(), "SAVED", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                historyList.get(pos).setBookmarked(true);
                                saveToSavedList(historyList.get(pos));

                                OnSavedButtonClickListener listener = (OnSavedButtonClickListener) getActivity();
                                listener.onSavedButtonClicked();
                            }
                        }
                ));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    historyList.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(context, R.color.chartColorWarning))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    void loadHistory(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(HISTORY, "");
        if (json!=""){
            historyList = gson.fromJson(json, type);
        }
    }

    void saveToSavedList(ProductItemModel m){
        //load data
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(SAVED, "");
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
        //check whether there is same product in the saved list
        for (int i = 0; i < savedList.size(); i++){
            if (savedList.get(i).getProductBarcode().equals(m.getProductBarcode())){
                return;
            }
        }
        savedList.add(0, m);

        //save to saved list
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (savedList.size()!=0) {
            Gson gsonSaved = new Gson();
            String jsonSaved = gsonSaved.toJson(savedList);
            editor.putString(SAVED, jsonSaved);
            editor.commit();
        }
    }

    void updateHistoryData(){
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        Gson gsonSaved = new Gson();
        String jsonSaved = gsonSaved.toJson(historyList);
        editor.putString(HISTORY, jsonSaved);
        editor.commit();
    }

    private void saveToCompare(ProductItemModel m){
        ArrayList<ProductItemModel> compareList = new ArrayList<>();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!="") {
            compareList = gson.fromJson(json, type);
        }
        //check whether there is same product in the compare list
        for (int i = 0; i < compareList.size(); i++){
            if (compareList.get(i).getProductBarcode().equals(m.getProductBarcode())){
                return;
            }
        }

        compareList.add(m);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gsonSaved = new Gson();
        String jsonSaved = gsonSaved.toJson(compareList);
        editor.putString(ITEMLIST, jsonSaved);
        editor.commit();
    }

    public interface OnSavedButtonClickListener{
        void onSavedButtonClicked();
    }

}