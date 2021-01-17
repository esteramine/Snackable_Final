package com.example.snackable;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ScannedItemModel {
    private int img;

    private String productName;
    private int productWeight;
    private Dictionary<String, Integer> basicInfo = new Hashtable<>();
    private ArrayList<String> foodAdditives = new ArrayList<>();

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
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

    public Dictionary<String, Integer> getBasicInfo() {
        return basicInfo;
    }

    public void setBasicInfo(Dictionary<String, Integer> basicInfo) { //basicInfo.set(basicInfo.put())
        this.basicInfo = basicInfo;
    }
    public void addBasicInfo(String name, int amount) { //basicInfo.set(basicInfo.put())
        this.basicInfo.put(name, amount);
    }

    public ArrayList<String> getFoodAdditives() {
        return foodAdditives;
    }

    public void setFoodAdditives(ArrayList<String> foodAdditives) {
        this.foodAdditives = foodAdditives;
    }

    public void addFoodAdditives(String foodAdditive) {
        this.foodAdditives.add(foodAdditive);
    }
}
