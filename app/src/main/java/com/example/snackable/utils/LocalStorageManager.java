package com.example.snackable.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.snackable.ProductItemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class LocalStorageManager {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String HISTORY = "HISTORY";
    private final static String SAVED = "SAVED";
    private final static String ITEMLIST = "ITEMLIST";
    private final static String SORT_OPT = "SORT_OPT";
    private final static String SORT_ORDER = "SORT_ORDER";
    private final static String DISPLAY_OPTS = "DISPLAY_OPTS";

    Gson gson = new Gson();
    Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();

    public LocalStorageManager() {
        // constructor
    }


    // Compare List Operations
    public ArrayList<ProductItemModel> getCompare(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ArrayList<ProductItemModel> itemList = new ArrayList<>();
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!=""){
            itemList = gson.fromJson(json, type);
        }
        return itemList;
    }

    public void updateCompare(Context context, ArrayList<ProductItemModel> compareList){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        String json = gson.toJson(compareList);
        editor.putString(ITEMLIST, json);
        editor.commit();
    }

    public void addToCompare(Context context, ProductItemModel model){
        ArrayList<ProductItemModel> compareList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        //load data from Compare
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!="") {
            compareList = gson.fromJson(json, type);
        }
        //check whether there is same product in the compare list
        for (int i = 0; i < compareList.size(); i++){
            if (compareList.get(i).getProductBarcode().equals(model.getProductBarcode())){
                return;
            }
        }
        compareList.add(model);

        //add to Compare
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonSaved = gson.toJson(compareList);
        editor.putString(ITEMLIST, jsonSaved);
        editor.commit();
    }

    // Sort Setting Operations
    public String getSortChoice(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String sortChoice = sharedPreferences.getString(SORT_OPT, "Sugars");
        return sortChoice;
    }
    public boolean getSortOrder(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean sortOrder = sharedPreferences.getBoolean(SORT_ORDER, true);
        return sortOrder;
    }
    // Display Setting Operations
    public boolean[] getDisplayOptions(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean[] displayOpts = new boolean[9];
        Type typeDisplay = new TypeToken<boolean[]>(){}.getType();
        String json = sharedPreferences.getString(DISPLAY_OPTS, "");
        if (json!=""){
            displayOpts = gson.fromJson(json, typeDisplay);
        }
        else{
            displayOpts = new boolean[] {true, true, false, false, false, false, false, false, true};
        }
        return displayOpts;
    }




    // History List Operations
    public ArrayList<ProductItemModel> getHistory(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ArrayList<ProductItemModel> historyList = new ArrayList<>();

        String json = sharedPreferences.getString(HISTORY, "");
        if (json!=""){
            historyList = gson.fromJson(json, type);
        }
        return historyList;
    }

    public void updateHistory(Context context, ArrayList<ProductItemModel> historyList){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(HISTORY, json);
        editor.commit();
    }

    public void saveDataToHistory(Context context, ProductItemModel model){ //do not have to care whether the product scanned before or not
        ArrayList<ProductItemModel> historyList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // load History from shared prefs
        String json = sharedPreferences.getString(HISTORY, "");
        if (json!=""){
            historyList = gson.fromJson(json, type);
        }

        //save new product item model to History
        SharedPreferences.Editor editor = sharedPreferences.edit();
        historyList.add(0, model);
        String jsonSave = gson.toJson(historyList);
        editor.putString(HISTORY, jsonSave);
        editor.commit();
    }




    // Saved List Operations
    public ArrayList<ProductItemModel> getSaved(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        String json = sharedPreferences.getString(SAVED, "");
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
        return savedList;
    }

    public void updateSaved(Context context, ArrayList<ProductItemModel> savedList){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();

        Gson gson = new Gson();
        String jsonSaved = gson.toJson(savedList);
        editor.putString(SAVED, jsonSaved);
        editor.commit();
    }

    public void saveDataToSaved(Context context, ProductItemModel model){
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // load Saved from shared prefs
        String json = sharedPreferences.getString(SAVED, "");
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
        //check whether there is same product in Saved
        for (int i = 0; i < savedList.size(); i++){
            if (savedList.get(i).getProductBarcode().equals(model.getProductBarcode())){
                return;
            }
        }
        savedList.add(0, model);

        //add to saved list
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonSaved = gson.toJson(savedList);
        editor.putString(SAVED, jsonSaved);
        editor.commit();
    }
}
