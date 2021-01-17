package com.example.snackable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplayChoicesActivity extends AppCompatActivity{
    private final static String SHARED_PREFS = "SHARED_PREFS";
    private final static String DISPLAY_OPTS = "DISPLAY_OPTS";
    Toolbar toolbar;
    Button doneBtn;
    private CheckBox chbAdditives, chbNutrients, chbCalories, chbCarbohydrates,
            chbDietaryFiber, chbFat, chbProtein, chbSodium, chbSugar;
    // Start with additives, and sugar set to true.
    private boolean[] checkBoxes = new boolean[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_choices);

        hooks();
        loadDisplayData();
        updateDisplayListView();

        // set up done button
        doneBtn.setVisibility(View.GONE);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDisplayOpt();
                onBackPressed();
            }
        });

        // set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Display Settings");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDisplayOpt();
                onBackPressed();
            }
        });
    }

    private void hooks() {
        toolbar = findViewById(R.id.displayChoicesToolbar);

        chbAdditives = findViewById(R.id.checkAdditives);
        chbNutrients = findViewById(R.id.checkNutrients);
        chbCalories = findViewById(R.id.checkCalories);
        chbCarbohydrates = findViewById(R.id.checkCarbohydrates);
        chbDietaryFiber = findViewById(R.id.checkDietaryFiber);
        chbFat = findViewById(R.id.checkFat);
        chbProtein = findViewById(R.id.checkProtein);
        chbSodium = findViewById(R.id.checkSodium);
        chbSugar = findViewById(R.id.checkSugar);

        doneBtn = findViewById(R.id.displayChoiceDone);
    }

    public void onDisplayClicked(View view){
        doneBtn.setVisibility(View.VISIBLE);
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.checkAdditives:
                if(checked)
                    checkBoxes[0] = true;
                else
                    checkBoxes[0] = false;
                break;
            case R.id.checkNutrients:
                if(checked){
                    for (int i = 1; i < checkBoxes.length; i++){
                        checkBoxes[i] = true;
                        selectAllNutrients(true);
                    }
                }
                else{ //deselect all nutrients
                    for (int i = 1; i < checkBoxes.length; i++){
                        checkBoxes[i] = false;
                        selectAllNutrients(false);
                    }
                }
                break;
            case R.id.checkCalories:
                if(checked){
                    checkBoxes[2] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[2] = false;
                break;
            case R.id.checkCarbohydrates:
                if(checked) {
                    checkBoxes[3] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[3] = false;
                break;
            case R.id.checkDietaryFiber:
                if(checked) {
                    checkBoxes[4] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[4] = false;
                break;
            case R.id.checkFat:
                if(checked) {
                    checkBoxes[5] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[5] = false;
                break;
            case R.id.checkProtein:
                if(checked) {
                    checkBoxes[6] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[6] = false;
                break;
            case R.id.checkSodium:
                if(checked) {
                    checkBoxes[7] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[7] = false;
                break;
            case R.id.checkSugar:
                if(checked) {
                    checkBoxes[8] = true;
                    setCheckNutrientsChecked();
                }
                else
                    checkBoxes[8] = false;
                break;
        }

    }

    private void selectAllNutrients(boolean selected) {
        chbCalories.setChecked(selected);
        chbCarbohydrates.setChecked(selected);
        chbDietaryFiber.setChecked(selected);
        chbFat.setChecked(selected);
        chbProtein.setChecked(selected);
        chbSodium.setChecked(selected);
        chbSugar.setChecked(selected);
    }

    private void setCheckNutrientsChecked() {
        chbNutrients.setChecked(true);
        checkBoxes[1] = true;
    }


    void loadDisplayData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        /*for (int i = 0; i < checkBoxes.length; i++){
            if (i == 0 || i == 1 || i == 8){
                checkBoxes[i] = sharedPreferences.getBoolean(DISPLAY_OPTS + i, true);
            }
            else{
                checkBoxes[i] = sharedPreferences.getBoolean(DISPLAY_OPTS + i, false);
            }
        }*/
        Gson gson = new Gson();
        Type type = new TypeToken<boolean[]>(){}.getType();
        String json = sharedPreferences.getString(DISPLAY_OPTS, "");
        if (json!=""){
            checkBoxes = gson.fromJson(json, type);
        }
        else{
            checkBoxes = new boolean[] {true, true, false, false, false, false, false, false, true};
        }
    }

    void updateDisplayListView(){
        for (int i = 0; i < checkBoxes.length; i++){
            if (checkBoxes[i]){
                if (i==0) chbAdditives.setChecked(true);
                else if (i==1) chbNutrients.setChecked(true);
                else if (i==2) chbCalories.setChecked(true);
                else if (i==3) chbCarbohydrates.setChecked(true);
                else if (i==4) chbDietaryFiber.setChecked(true);
                else if (i==5) chbFat.setChecked(true);
                else if (i==6) chbProtein.setChecked(true);
                else if (i==7) chbSodium.setChecked(true);
                else if (i==8) chbSugar.setChecked(true);
            }
        }
    }

    void saveDisplayOpt(){
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        /*for (int i = 0; i < checkBoxes.length; i++){
            editor.putBoolean(DISPLAY_OPTS + i, checkBoxes[i]);
            Log.d("1234566677878", String.valueOf(checkBoxes[i]));
        }*/
        Gson gson = new Gson();
        String json = gson.toJson(checkBoxes);
        editor.putString(DISPLAY_OPTS, json);
        editor.commit();
    }
}