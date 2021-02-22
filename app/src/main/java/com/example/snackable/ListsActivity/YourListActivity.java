package com.example.snackable.ListsActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.snackable.CompareActivity.CompareActivity;
import com.example.snackable.R;
import com.example.snackable.ScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class YourListActivity extends AppCompatActivity implements HistoryFragment.OnSavedButtonClickListener{
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    BottomNavigationView bottomNavigationView;
    //Toolbar toolbar;
    FloatingActionButton fab;

    //tab layout
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<String> tabList = new ArrayList<>();

    MainAdapter adapter = new MainAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_list);

        hooks();

        //tablayout set up
        tabList.add("HISTORY");
        tabList.add("SAVED");
        prepareViewPager(viewPager, tabList);
        tabLayout.setupWithViewPager(viewPager);

        //tool bar set up
        /*setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Lists");*/

        //bottom nav bar set up
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
        bottomNavigationView.setSelectedItemId(R.id.miYourList);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavItemSelectedListener);

        //fab set up
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
    }

    //camera permission check
    private void checkCameraPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else {
            startActivity(new Intent(getApplicationContext(), ScanActivity.class));
            overridePendingTransition(0,0);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                overridePendingTransition(0,0);
            } else {
                Toast.makeText(this, "You cannot scan because you deny the camera permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> tabList) {
        HistoryFragment historyFragment = new HistoryFragment();
        SavedFragment savedFragment = new SavedFragment();
        adapter.addFragment(historyFragment, tabList.get(0));
        adapter.addFragment(savedFragment, tabList.get(1));
        viewPager.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.miYourList:
                    // remain in same page
                    return true;
                case R.id.miCompare:
                    // jump to my list page
                    Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
                    //TODO saveData?? or saveData in onStop is enough?
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;

            }
            return false;
        }
    };

    void hooks(){
        tabLayout = findViewById(R.id.yourListTabLayout);
        viewPager = findViewById(R.id.yourListViewPager);

        //toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

    }

    @Override
    public void onSavedButtonClicked() {
        /*adapter.removeFragments();
        adapter.addFragment(historyFragment, tabList.get(0));
        adapter.addFragment(savedFragment, tabList.get(1));*/
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
    }


    private class MainAdapter extends FragmentPagerAdapter {
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            titleList.add(0, title);
            fragmentList.add(0, fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            //return fragment posistion
            //return fragmentList.get(position);
            if (position == 1) {
                return new HistoryFragment();
            } else{
                return new SavedFragment();
            }
        }

        @Override
        public int getCount() {
            //return fragment list size
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            //return array list position
            return titleList.get(position);
        }

    }
}