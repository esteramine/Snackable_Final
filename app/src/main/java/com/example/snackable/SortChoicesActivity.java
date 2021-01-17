package com.example.snackable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class SortChoicesActivity extends AppCompatActivity {
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String SORT_ORDER = "SORT_ORDER";
    private final static String SORT_OPT = "SORT_OPT";
    Toolbar toolbar;
    RadioButton radioContainsLeast, radioContainsMost;
    RadioButton radioSugar, radioCalories, radioCarbohydrates, radioDietaryFiber, radioFat, radioProtein, radioSodium;
    Button doneBtn;

    private boolean sortOrder; // true: ascending (contains least); false: descending (contains most)
    private String sortOpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_choices);

        hooks();

        loadSortData();

        // set up done button
        doneBtn.setVisibility(View.GONE);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sort Settings");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveSortOrder(sortOrder);
                //saveSortOpt(sortOpt);
                onBackPressed();
            }
        });
    }

    private void hooks() {
        toolbar = findViewById(R.id.sortChoicesToolbar);
        radioContainsLeast = findViewById(R.id.radioContainsLeast);
        radioContainsMost = findViewById(R.id.radioContainsMost);

        //radioCalories, radioCarbohydrates, radioDietaryFiber, radioFat, radioProtein, radioSodium
        radioSugar = findViewById(R.id.radioSugar);
        radioCalories = findViewById(R.id.radioCalories);
        radioCarbohydrates = findViewById(R.id.radioCarbohydrates);
        radioDietaryFiber = findViewById(R.id.radioDietaryFiber);
        radioFat = findViewById(R.id.radioFat);
        radioProtein = findViewById(R.id.radioProtein);
        radioSodium = findViewById(R.id.radioSodium);

        doneBtn = findViewById(R.id.sortChoiceDone);
    }

    public void onOrderClicked(View view){
        doneBtn.setVisibility(View.VISIBLE);
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.radioContainsLeast:
                if(checked)
                    saveSortOrder(true);
                break;
            case R.id.radioContainsMost:
                if(checked)
                    saveSortOrder(false);
                break;
        }
    }

    public void onSortOptClicked(View view){
        doneBtn.setVisibility(View.VISIBLE);
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.radioCalories:
                if(checked)
                    saveSortOpt("Calories");
                break;
            case R.id.radioCarbohydrates:
                if(checked)
                    saveSortOpt("Carbohydrates");
                break;
            case R.id.radioDietaryFiber:
                if(checked)
                    saveSortOpt("Dietary Fiber");
                break;
            case R.id.radioFat:
                if(checked)
                    saveSortOpt("Fat");
                break;
            case R.id.radioProtein:
                if(checked)
                    saveSortOpt("Proteins");
                break;
            case R.id.radioSodium:
                if(checked)
                    saveSortOpt("Sodium");
                break;
            case R.id.radioSugar:
                if(checked)
                    saveSortOpt("Sugars");
                break;
        }
    }


    void loadSortData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sortOrder = sharedPreferences.getBoolean(SORT_ORDER, true);
        setRadioContainsButton(sortOrder);
        sortOpt = sharedPreferences.getString(SORT_OPT, "Sugars");
        setRadioSortOptButton(sortOpt);
    }

    void saveSortOrder(boolean ascending){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putBoolean(SORT_ORDER,ascending);
        editor.apply();
    }

    void saveSortOpt(String sortOption){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putString(SORT_OPT, sortOption);
        editor.apply();
    }

    void setRadioContainsButton(boolean containsLeast){
        if (containsLeast){
            radioContainsLeast.setChecked(true);
        }
        else
            radioContainsMost.setChecked(true);
    }

    void setRadioSortOptButton(String option){
        switch (option){
            case "Sugars":
                radioSugar.setChecked(true);
                break;
            case "Calories":
                radioCalories.setChecked(true);
                break;
            case "Carbohydrates":
                radioCarbohydrates.setChecked(true);
                break;
            case "Dietary Fiber":
                radioDietaryFiber.setChecked(true);
                break;
            case "Fat":
                radioFat.setChecked(true);
                break;
            case "Proteins":
                radioProtein.setChecked(true);
                break;
            case "Sodium":
                radioSodium.setChecked(true);
                break;
        }
    }


}