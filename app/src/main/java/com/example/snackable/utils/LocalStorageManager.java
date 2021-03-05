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
    SharedPreferences sharedPreferences;

    /*public LocalStorageManager() {
        // constructor
    }*/

    public LocalStorageManager(Context context) {
        // constructor
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    }


    // Compare List Operations
    public ArrayList<ProductItemModel> getCompare(){
        ArrayList<ProductItemModel> itemList = new ArrayList<>();
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!=""){
            itemList = gson.fromJson(json, type);
        }
        return itemList;
    }

    public void updateCompare(ArrayList<ProductItemModel> compareList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(compareList);
        editor.putString(ITEMLIST, json);
        editor.commit();
    }

    public void addToCompare(ProductItemModel model){
        ArrayList<ProductItemModel> compareList = new ArrayList<>();

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

    public void updateBookmarkedItemInCompare(ProductItemModel m, boolean bookmarked){
        ArrayList<ProductItemModel> itemList = new ArrayList<>();
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!=""){
            itemList = gson.fromJson(json, type);
            for (int i = 0; i < itemList.size(); i++){
                if (m.getProductBarcode().equals(itemList.get(i).getProductBarcode())){
                    itemList.get(i).setBookmarked(bookmarked);
                    //save back to saved list
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String jsonSaved = gson.toJson(itemList);
                    editor.putString(ITEMLIST, jsonSaved);
                    editor.commit();
                    break;
                }
            }
        }
        else
            return;
    }

    // Sort Setting Operations
    public String getSortChoice(){
        String sortChoice = sharedPreferences.getString(SORT_OPT, "Sugars");
        return sortChoice;
    }
    public boolean getSortOrder(){
        boolean sortOrder = sharedPreferences.getBoolean(SORT_ORDER, true);
        return sortOrder;
    }
    // Display Setting Operations
    public boolean[] getDisplayOptions(){
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
    public ArrayList<ProductItemModel> getHistory(){
        ArrayList<ProductItemModel> historyList = new ArrayList<>();

        String json = sharedPreferences.getString(HISTORY, "");
        if (json!=""){
            historyList = gson.fromJson(json, type);
        }
        return historyList;
    }

    public void updateHistory(ArrayList<ProductItemModel> historyList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(HISTORY, json);
        editor.commit();
    }

    public void saveDataToHistory(ProductItemModel model){ //do not have to care whether the product scanned before or not
        ArrayList<ProductItemModel> historyList = new ArrayList<>();

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
    public ArrayList<ProductItemModel> getSaved(){
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        String json = sharedPreferences.getString(SAVED, "");
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
        return savedList;
    }

    public void updateSaved(ArrayList<ProductItemModel> savedList){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonSaved = gson.toJson(savedList);
        editor.putString(SAVED, jsonSaved);
        editor.commit();
    }

    public void saveDataToSaved(ProductItemModel model){
        ArrayList<ProductItemModel> savedList = new ArrayList<>();

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

    public void removeFromSaved(ProductItemModel model){
        String json = sharedPreferences.getString(SAVED, "");
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        if (json!=""){
            savedList = gson.fromJson(json, type);
            for (int i = 0; i < savedList.size(); i++){
                if (model.getProductBarcode().equals(savedList.get(i).getProductBarcode())){
                    savedList.remove(i);

                    //save back to saved list
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String jsonSaved = gson.toJson(savedList);
                    editor.putString(SAVED, jsonSaved);
                    editor.commit();
                    break;
                }
            }
        }
        else
            return;
    }

}
