package com.example.snackable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SavedFragment extends Fragment {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String SAVED = "SAVED";
    private final static String ITEMLIST = "ITEMLIST";
    ArrayList<ProductItemModel> savedList = new ArrayList<>();
    RecyclerView recyclerView;
    YourListAdapter adapter;
    Context context;

    public SavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadSavedList();
        this.context = this.getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        recyclerView = view.findViewById(R.id.savedRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new YourListAdapter(this.getContext(), savedList, false);
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
                                builder.setMessage("Are you sure you want to remove "+ savedList.get(pos).getProductName()+" from saved list?")
                                        .setPositiveButton(Html.fromHtml("<font color='#FF0000'>DELETE</font>"), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(context, savedList.get(pos).getProductName()+ " Removed!", Toast.LENGTH_LONG).show();
                                                savedList.remove(pos);
                                                adapter.notifyItemRemoved(pos);
                                                updateSavedList();
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
                                //Toast toast = Toast.makeText(getContext(), savedList.get(pos).getProductName()+ "\n Add to Compare!", Toast.LENGTH_LONG);
                                Toast toast = Toast.makeText(getContext(), "Added to Compare", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                saveToCompare(savedList.get(pos));
                                adapter.notifyDataSetChanged();
                            }
                        }
                ));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    private void loadSavedList() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(SAVED, "");
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
    }

    private void updateSavedList(){
        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();

        Gson gsonSaved = new Gson();
        String jsonSaved = gsonSaved.toJson(savedList);
        editor.putString(SAVED, jsonSaved);
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


}