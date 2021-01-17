package com.example.snackable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class YourListActivity extends AppCompatActivity implements HistoryFragment.OnSavedButtonClickListener{
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
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                overridePendingTransition(0,0);
            }
        });
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
        private final static String SHARED_PREFS = "SHARED_PREFS";
        private final static String SAVED = "SAVED";
        ArrayList<ProductItemModel> savedList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        public MainAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            titleList.add(0, title);
            fragmentList.add(0, fragment);
        }

        public void removeFragments(){
            titleList.clear();
            fragmentList.clear();
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