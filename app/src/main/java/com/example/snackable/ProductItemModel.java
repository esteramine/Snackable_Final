package com.example.snackable;

import com.example.snackable.FoodAdditiveDetailActivity.AdditivesDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ProductItemModel implements Serializable {
    private String productBarcode = "";
    private int productRanking = 1;
    private boolean bookmarked = false;
    private String productName = "";
    private int productWeight = 100; //default 100g
    //private byte[] productImg = null;
    private String productImg = "";
    private ArrayList<String> ingredientsList = new ArrayList<>();
    private ArrayList<String> foodAdditivesList = new ArrayList<>();
    HashMap<String, String> nutritionContents = new HashMap<>();
    private long productSavedTime = 1608221637191L; //only for history list

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public int getProductRanking() {
        return productRanking;
    }
    public void setProductRanking(int productRanking) {
        this.productRanking = productRanking;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductWeight() {
        return productWeight;
    }
    public void setProductWeight(int productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductImg() {
        return productImg;
    }
    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public ArrayList<String> getIngredientsList() {
        return ingredientsList;
    }
    public void setIngredientsList(ArrayList<String> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
    public void addIngredientToList(String ingredient){
        this.ingredientsList.add(ingredient);
    }
    public void clearIngredientsList(){
        this.ingredientsList.clear();
    }

    public ArrayList<String> getFoodAdditivesList() {
        return foodAdditivesList;
    }

    private ArrayList<String> sortFoodAdditivesList(ArrayList<String> foodAdditivesList){
        Collections.sort(foodAdditivesList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String s1Type = AdditivesDatabase.getAdditiveType(AdditivesDatabase.getAdditiveIndex(s1));
                if(s1Type=="Avoid")
                    return -1;
                if(s1Type=="Gray")
                    return 1;
                String s2Type = AdditivesDatabase.getAdditiveType(AdditivesDatabase.getAdditiveIndex(s2));

                if(s2Type=="Avoid")
                    return 1;
                if(s2Type=="Gray")
                    return -1;

                if(s1Type=="Safe")
                    return 1;
                return -1;
            }
        });
        return foodAdditivesList;
    }

    public void setFoodAdditivesList(ArrayList<String> foodAdditivesList) {
        this.foodAdditivesList = sortFoodAdditivesList(foodAdditivesList);
    }
    public void addFoodAdditiveToList(String foodAdditive){
        this.foodAdditivesList.add(foodAdditive);
        this.foodAdditivesList = sortFoodAdditivesList(this.foodAdditivesList);
    }
    public void clearFoodAdditiveList(){
        this.foodAdditivesList.clear();
    }

    public HashMap<String, String> getNutritionContents() {
        return nutritionContents;
    }
    public void setNutritionContents(HashMap<String, String> nutritionContents) {
        this.nutritionContents = nutritionContents;
    }
    public void addNutritionContentToList(String NutritionName, String Amount){
        this.nutritionContents.put(NutritionName, Amount);
    }
    public void clearNutritionContents(){
        this.nutritionContents.clear();
    }

    public long getProductSavedTime() {
        return productSavedTime;
    }
    public void setProductSavedTime(long productSavedTime) {
        this.productSavedTime = productSavedTime;
    }
}
