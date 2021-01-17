package com.example.snackable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 1500;
    ImageView logo;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        hooks();

        //animations set up
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top_anim);
        logo.setAnimation(topAnim);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom_anim);
        appName.setAnimation(bottomAnim);

        //Jump to compare page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, CompareActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }

    void hooks(){
        logo = findViewById(R.id.snackableLogo);
        appName = findViewById(R.id.snackableName);
    }
}