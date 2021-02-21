package com.example.snackable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.snackable.utils.LocalStorageManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompareActivity extends AppCompatActivity{
    private boolean[] displayOpts = new boolean[9];
    LocalStorageManager localStorageManager = new LocalStorageManager();
    Context context;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    Toolbar longclickToolbar;
    RecyclerView recyclerView;
    ProductItemAdapter adapter;
    TextView itemListSize;
    TextView sortIndicator;
    ImageView welcomeDialog;
    ArrayList<ProductItemModel> itemList = new ArrayList<>();
    boolean sortOrder; //true: ascending; false: descending
    String sortChoice;
    String blinkedBarcodeNum = "";
    int scrollToPos = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        context = getApplicationContext();
        displayOpts = new boolean[9];
        displayOpts = new boolean[] {true, true, false, false, false, false, false, false, true};
        loadData(); //load itemList, sort option

        Intent intent = getIntent();
        ProductItemModel newModel = (ProductItemModel) intent.getSerializableExtra("NewModel");
        if (newModel!=null){
            boolean foundSame = false;
            for (int i = 0; i < itemList.size(); i++){
                if (itemList.get(i).getProductBarcode().equals(newModel.getProductBarcode())){
                    foundSame = true;
                    break;
                }
            }
            if (!foundSame){
                itemList.add(newModel);
                localStorageManager.updateCompare(context, itemList);
                loadData();
            }
            blinkedBarcodeNum = newModel.getProductBarcode();
        }

        //sort itemlist
        itemList = sort(itemList, sortChoice, sortOrder);

        hooks();

        // top text set up
        String mostOrLeast = sortOrder? "Least":"Most";
        sortIndicator.setText("Sorted by "+ mostOrLeast + " " +sortChoice);
        itemListSize.setText(itemList.size()+" items");

        // recyclerview set up
        if (scrollToPos!= -1){
            recyclerView.smoothScrollToPosition(scrollToPos);
        }

        //tool bar set up
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Compare");

        //bottom nav bar set up
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.miCompare);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavItemSelectedListener);

        //fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                overridePendingTransition(0,0);
            }
        });
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.miDisplay:
                blinkedBarcodeNum = "";
                Intent intent = new Intent(CompareActivity.this, DisplayChoicesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return false;
            case R.id.miSort:
                blinkedBarcodeNum = "";
                Intent intent1 = new Intent(CompareActivity.this, SortChoicesActivity.class);
                startActivity(intent1);
                overridePendingTransition(0, 0);
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        localStorageManager.updateCompare(context, itemList);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.miCompare:
                    // remain in same page
                    return true;
                case R.id.miYourList:
                    // jump to my list page
                    Intent intent = new Intent(getApplicationContext(), YourListActivity.class);
                    //TODO saveData?? or saveData in onStop is enough?
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;

            }
            return false;
        }
    };

    void hooks(){
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        longclickToolbar = findViewById(R.id.longclick_toolbar);
        itemListSize = findViewById(R.id.itemListSize);
        sortIndicator = findViewById(R.id.sortIndicator);
        welcomeDialog = findViewById(R.id.welcomeDialog);

        recyclerView = findViewById(R.id.compareRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ProductItemAdapter(this, itemList, displayOpts, blinkedBarcodeNum);
        recyclerView.setAdapter(adapter);

        showComponents();

        SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView) {

            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "REMOVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#F84949"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CompareActivity.this);
                                builder.setMessage("Are you sure you want to remove "+ itemList.get(pos).getProductName()+" from compare list?")
                                        .setPositiveButton(Html.fromHtml("<font color='#FF0000'>REMOVE</font>"), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast toast = Toast.makeText(CompareActivity.this, itemList.get(pos).getProductName()+ " Removed!", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                itemList.remove(pos);
                                                //adapter.notifyItemRemoved(pos);
                                                updateCompareList();
                                                showComponents();
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
                        "SAVE",
                        R.drawable.ic_delete,
                        Color.parseColor("#00BF7C"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Toast toast = Toast.makeText(CompareActivity.this, "SAVED", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                itemList.get(pos).setBookmarked(true);
                                adapter.notifyDataSetChanged();
                                localStorageManager.saveDataToSaved(context, itemList.get(pos));
                            }
                        }
                ));
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ArrayList<ProductItemModel> sort(ArrayList<ProductItemModel> items, String sortOpt, boolean ascending){
        Collections.sort(items, new Comparator<ProductItemModel>() {
            @Override
            public int compare(ProductItemModel p1, ProductItemModel p2) {
                if(!ascending) { //Descending
                    if(p1.getNutritionContents().containsKey(sortOpt) && p2.getNutritionContents().containsKey(sortOpt)){
                        return Float.compare(Float.parseFloat("0"+p2.getNutritionContents().get(sortOpt).replaceAll("[^\\d.]", "")),Float.parseFloat("0"+p1.getNutritionContents().get(sortOpt).replaceAll("[^\\d.]", "")));
                    }
                    else if (!p1.getNutritionContents().containsKey(sortOpt) && p2.getNutritionContents().containsKey(sortOpt)){
                        return 1;

                    }
                    else if (p1.getNutritionContents().containsKey(sortOpt) && !p2.getNutritionContents().containsKey(sortOpt)){
                        return -1;
                    }
                    else{
                        return 0;
                    }
                }
                else { //Ascending
                    if(p1.getNutritionContents().containsKey(sortOpt) && p2.getNutritionContents().containsKey(sortOpt)){
                        return Float.compare(Float.parseFloat("0"+p1.getNutritionContents().get(sortOpt).replaceAll("[^\\d.]", "")),Float.parseFloat("0"+p2.getNutritionContents().get(sortOpt).replaceAll("[^\\d.]", "")));
                    }
                    else if (!p1.getNutritionContents().containsKey(sortOpt) && p2.getNutritionContents().containsKey(sortOpt)){
                        return 1;
                    }
                    else if (p1.getNutritionContents().containsKey(sortOpt) && !p2.getNutritionContents().containsKey(sortOpt)){
                        return -1;
                    }
                    else{
                        return 0;
                    }
                }
            }
        });
        for(int i = 0; i < items.size(); i++) {
            items.get(i).setProductRanking(i + 1);
            if (items.get(i).getProductBarcode().equals(blinkedBarcodeNum)){
                scrollToPos = i;
            }
        }
        return items;
    }


    void loadData(){
        //load Compare list
        itemList = localStorageManager.getCompare(context);

        //load sortOption
        sortChoice = localStorageManager.getSortChoice(context);
        if (sortChoice=="Calories"){
            sortChoice = "Energy(kcal)";
        }
        else if (sortChoice == "Dietary Fiber"){
            sortChoice = "Fibers";
        }

        //load sort order
        sortOrder = localStorageManager.getSortOrder(context);

        //load display options
        displayOpts = localStorageManager.getDisplayOptions(context);
    }


    private void updateCompareList(){
        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setProductRanking(i+1);
        }
        itemListSize.setText(itemList.size()+" items");
        localStorageManager.updateCompare(context, itemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();
        String mostOrLeast = sortOrder? "Least":"Most";
        sortIndicator.setText("Sorted by "+ mostOrLeast + " " +sortChoice);

        itemList = sort(itemList, sortChoice, sortOrder);
        //addSortOptToDisplay();
        adapter = new ProductItemAdapter(this, itemList, displayOpts, blinkedBarcodeNum);
        recyclerView.setAdapter(adapter);
        blinkedBarcodeNum = "";
        // recyclerview set up
        if (scrollToPos!= -1){
            recyclerView.smoothScrollToPosition(scrollToPos);
        }
    }

    void showComponents(){ //recyclerview or welcome dialog
        if (itemList.size()>0){
            welcomeDialog.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            welcomeDialog.setVisibility(View.VISIBLE);
            Animation upDownAnim = AnimationUtils.loadAnimation(this, R.anim.welcome_dialog_anim);
            /*Animation upDownAnim = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f);
            upDownAnim.setDuration(800);
            upDownAnim.setRepeatCount(Animation.INFINITE);
            upDownAnim.setRepeatMode(Animation.REVERSE);
            upDownAnim.setInterpolator(new LinearInterpolator());*/
            welcomeDialog.setAnimation(upDownAnim);
            recyclerView.setVisibility(View.GONE);
        }
    }

    void addSortOptToDisplay(){
        if (sortChoice=="Energy(kcal)")
            displayOpts[2] = true;
        else if (sortChoice=="Carbohydrates")
            displayOpts[3] = true;
        else if (sortChoice=="Fibers")
            displayOpts[4] = true;
        else if (sortChoice=="Fat")
            displayOpts[5] = true;
        else if (sortChoice=="Proteins")
            displayOpts[6] = true;
        else if (sortChoice=="Sodium")
            displayOpts[7] = true;
        else if (sortChoice=="Sugars")
            displayOpts[8] = true;

    }

}