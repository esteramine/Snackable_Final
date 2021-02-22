package com.example.snackable.FoodAdditiveDetailActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snackable.R;

public class FoodAdditiveDetailActivity extends AppCompatActivity {
    ImageView additiveIcon;
    TextView additiveTitle;
    TextView additiveUses;
    TextView additiveDescription;
    TextView additiveFoundInTitle;
    TextView additiveFoundIn;

    String additive;
    int additive_index;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_additive_detail);

        Intent intent = getIntent();
        additive = (String)intent.getSerializableExtra("Additive");
        additive_index = (int) intent.getSerializableExtra("AdditiveIndex");
        toolbar = findViewById(R.id.itemDetailToolbarAdditives);
        //set up the toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        additiveIcon = (ImageView)findViewById(R.id.additiveIcon);
        additiveTitle = (TextView)findViewById(R.id.additiveTitle);
        additiveUses = (TextView)findViewById(R.id.additive_uses);
        additiveDescription = (TextView)findViewById(R.id.additive_description);
        additiveFoundInTitle = (TextView)findViewById(R.id.additive_foundin_title);
        additiveFoundIn = (TextView)findViewById(R.id.additive_foundin);

        fillPageInfo(additive_index);
    }

    public void fillPageInfo(int additiveIndex) {
        String additiveType = AdditivesDatabase.getAdditiveType(additiveIndex);
        if (additiveType == "Safe")
            additiveIcon.setImageResource(R.drawable.final_safe_symbol);
        else if (additiveType == "Gray")
            additiveIcon.setImageResource(R.drawable.final_not_available_symbol);
        else if (additiveType == "Avoid")
            additiveIcon.setImageResource(R.drawable.final_avoid_symbol);
        else
            additiveIcon.setImageResource(R.drawable.final_cut_back_symbol);

        if (additiveIndex == 0)
            additiveTitle.setText(additive);
        else
            additiveTitle.setText(AdditivesDatabase.getAdditiveName(additiveIndex));

        additiveUses.setText(AdditivesDatabase.getAdditiveUses(additiveIndex));

        additiveDescription.setText(AdditivesDatabase.getAdditiveDescription(additiveIndex));

        if (AdditivesDatabase.getAdditiveFoundin(additiveIndex) != "") {
            additiveFoundIn.setText(AdditivesDatabase.getAdditiveFoundin(additiveIndex));
        } else {
            additiveFoundInTitle.setText("");
            additiveFoundIn.setText("");
        }
    }
}