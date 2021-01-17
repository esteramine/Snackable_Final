package com.example.snackable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductItemDetail extends AppCompatActivity {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String SAVED = "SAVED";
    private final static String ITEMLIST = "ITEMLIST";
    private static final String NAME = "Name";
    private static final String AMOUNT = "Amount";
    private static final String DRI = "DRI";
    private static final String CHECK_ITEMS[] = {"kcal", "Sugar", "Fat", "Protein", "Sodium", "Fibers"};
    private static final String DRI_AMOUNT[] = {"1728", "25", "60", "46", "2.3", "25"};
    //TODO productImg
    ImageView productImg;
    TextView productName;
    TextView productWeight;
    RecyclerView recyclerView;
    ProductItemFAAdapter adapter;
    ProductItemModel model;
    //GridView gridView;
    ExpandableHeightGridView gridView;
    NutritionChartAdapter gridAdapter;
    ImageView infoBtn;
    Button addToCompareBtn;
    NestedScrollView nestedScrollView;
    TextView noneText;
    Context c;

    Toolbar toolbar;

    boolean isScannedModel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_item_detail);

        Intent intent = getIntent();
        model = (ProductItemModel) intent.getSerializableExtra("ModelScanned");
        if (model==null){
            model = (ProductItemModel) intent.getSerializableExtra("Model"); //get model from ProductItemAdapter
            isScannedModel = false;
        }

        hooks();
        // add to compare btn set up
        if (isScannedModel){
            addToCompareBtn.setVisibility(View.VISIBLE);
        }
        else {
            addToCompareBtn.setVisibility(View.GONE);
        }

        // set up the toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        infoBtn = findViewById(R.id.info_button);
        infoBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductItemDetail.this);
                builder.setTitle("Safety Ratings Key");
                //builder.setPositiveButton(android.R.string.yes, null);
                final AlertDialog dialog = builder.create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.safety_ratings_dialog, null);
                dialog.setView(dialogLayout);
                dialog.show();
            }
        });

        addToCompareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ProductItemDetail.this, CompareActivity.class);
                intent1.putExtra("NewModel", model); // model already saved to history in scan activity
                ProductItemDetail.this.startActivity(intent1);
            }
        });


        //item detail page set up
        if (model!=null){
            productName.setText(model.getProductName());
            productWeight.setText(new Integer(model.getProductWeight()).toString()+"g");
            if (model.getProductImg()!=""){
                Picasso.get().load(model.getProductImg()).placeholder(R.drawable.loading)
                        .error(R.drawable.not_found).into(productImg);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.miBookmark:
                if(item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_bookmark_dark).getConstantState())){
                    item.setIcon(R.drawable.ic_bookmark_snackable);
                    model.setBookmarked(true);
                    saveToSavedList(model);
                    Toast.makeText(getBaseContext(), "SAVED", Toast.LENGTH_LONG).show();
                    if(!isScannedModel){
                        updateCompareListBookmarked(model, true);
                    }
                }
                else {
                    item.setIcon(R.drawable.ic_bookmark_dark);
                    model.setBookmarked(false);
                    bookmarkRemoved(model);
                    Toast.makeText(getBaseContext(), "REMOVE from Saved", Toast.LENGTH_LONG).show();
                    if(!isScannedModel){
                        updateCompareListBookmarked(model, false);
                    }
                }
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    void hooks(){
        toolbar = findViewById(R.id.itemDetailToolbar);

        productName = findViewById(R.id.productName);
        productWeight = findViewById(R.id.productWeight);
        productImg = findViewById(R.id.productImg);
        addToCompareBtn = findViewById(R.id.addToCompareButton);

        nestedScrollView = findViewById(R.id.itemDetailScrollView);
        noneText = findViewById(R.id.productAdditiveNoneText);

        recyclerView = findViewById(R.id.foodAdditivesRecyclerView);
        recyclerView.setLayoutManager(new UnscrollableLinearLayoutManager(this, false));
        ArrayList<String> foodAdditiveList = new ArrayList<>();
        foodAdditiveList = getFoodAdditivesList(model);
        if (foodAdditiveList.size()!=0){
            noneText.setVisibility(View.GONE);
            adapter = new ProductItemFAAdapter(this, foodAdditiveList);
            recyclerView.setAdapter(adapter);
        }
        else{
            noneText.setVisibility(View.VISIBLE);
        }

        gridView = findViewById(R.id.chartGridView);

        gridAdapter = new NutritionChartAdapter(this, getChartList());
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_detail_menu, menu);
        if (model.isBookmarked()){
            menu.findItem(R.id.miBookmark).setIcon(R.drawable.ic_bookmark_snackable);
        }
        return super.onCreateOptionsMenu(menu);
    }

    ArrayList<String> getFoodAdditivesList(ProductItemModel model){
        ArrayList<String> foodAdditivesList = new ArrayList<>();
        if (model!=null){
            for (String additive: model.getFoodAdditivesList()){
                foodAdditivesList.add(additive);
            }
        }
        return foodAdditivesList;
    }
    List<Map<String, String>> getChartList(){
        List<Map<String, String>> nutritionContent = new ArrayList<>();
        /*for (int i = 0; i < 6; i++){
            Map<String, String> chartInfo = new HashMap<>();
            chartInfo.put(NAME, "Calorie");
            chartInfo.put(AMOUNT, "123kcal");
            nutritionContent.add(chartInfo);
        }*/
        if (model!=null){
            for (int i = 0; i < CHECK_ITEMS.length; i++){ //add alll the required item to new list of maps
                for (String entry: model.getNutritionContents().keySet()) {
                    if (entry.contains(CHECK_ITEMS[i])){
                        if (!model.getNutritionContents().get(entry).replaceAll("[^\\d.]", "").replaceAll(" ","").isEmpty()) {//eliminate value with "?"
                            Map<String, String> chartInfo = new HashMap<>();
                            if(CHECK_ITEMS[i]=="kcal"){
                                chartInfo.put(NAME, "Calorie");
                            }
                            else{
                                chartInfo.put(NAME, CHECK_ITEMS[i]);
                            }
                            chartInfo.put(AMOUNT, model.getNutritionContents().get(entry));
                            System.out.println(model.getNutritionContents().get(entry).replaceAll("[^\\d.]", ""));
                            chartInfo.put(DRI, DRI_AMOUNT[i]);
                            nutritionContent.add(chartInfo);
                            break;
                        }
                    }
                }
            }
        }

        return nutritionContent;
    }

    void bookmarkRemoved(ProductItemModel m){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(SAVED, "");
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        if (json!=""){
            savedList = gson.fromJson(json, type);
            for (int i = 0; i < savedList.size(); i++){
                if (m.getProductBarcode().equals(savedList.get(i).getProductBarcode())){
                    savedList.remove(i);

                    //save back to saved list
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gsonSaved = new Gson();
                    String jsonSaved = gsonSaved.toJson(savedList);
                    editor.putString(SAVED, jsonSaved);
                    editor.commit();
                    break;
                }
            }
        }
        else
            return;
    }

    void updateCompareListBookmarked(ProductItemModel m, boolean bookmarked){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ArrayList<ProductItemModel> itemList = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(ITEMLIST, "");
        if (json!=""){
            itemList = gson.fromJson(json, type);
            for (int i = 0; i < itemList.size(); i++){
                if (m.getProductBarcode().equals(itemList.get(i).getProductBarcode())){
                    itemList.get(i).setBookmarked(bookmarked);

                    //save back to saved list
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gsonSaved = new Gson();
                    String jsonSaved = gsonSaved.toJson(itemList);
                    editor.putString(ITEMLIST, jsonSaved);
                    editor.commit();
                    break;
                }
            }
        }
        else
            return;
    }

    void saveToSavedList(ProductItemModel model){
        //load to saved list
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductItemModel>>(){}.getType();
        String json = sharedPreferences.getString(SAVED, "");
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        if (json!=""){
            savedList = gson.fromJson(json, type);
        }
        //check whether there is same product in the compare list
        for (int i = 0; i < savedList.size(); i++){
            if (savedList.get(i).getProductBarcode().equals(model.getProductBarcode())){
                return;
            }
        }
        savedList.add(0, model);

        //add to saved list
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gsonSaved = new Gson();
        String jsonSaved = gsonSaved.toJson(savedList);
        editor.putString(SAVED, jsonSaved);
        editor.commit();
    }

}