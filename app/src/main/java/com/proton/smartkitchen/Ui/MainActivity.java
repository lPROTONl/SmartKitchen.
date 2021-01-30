package com.proton.smartkitchen.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.graphics.Color;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.backendless.Backendless;
import com.proton.smartkitchen.R;
import com.proton.smartkitchen.Service.MyWorker;
import com.proton.smartkitchen.Service.MyWorkerDaily;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AHBottomNavigation.OnTabSelectedListener {

    MealsFragment fragmentMeals;

    FavoritesFragment fragmentFavorites;

    AHBottomNavigation bottomNavigation;

    private static final String TAG = "MealAPP";
    private static final String TAG2 = "MealAPP2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constraints.Builder builder = new Constraints.Builder();
        builder.setRequiredNetworkType(NetworkType.CONNECTED);
        Constraints constraints = builder.build();

        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(MyWorker.class,7, TimeUnit.DAYS);
        workBuilder.setConstraints(constraints);
        PeriodicWorkRequest request = workBuilder.build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP,request);

        Constraints.Builder builder2 = new Constraints.Builder();
        builder2.setRequiredNetworkType(NetworkType.CONNECTED);
        Constraints constraints2 = builder2.build();

        PeriodicWorkRequest.Builder workBuilder2 = new PeriodicWorkRequest.Builder(MyWorkerDaily.class,1, TimeUnit.DAYS);
        workBuilder2.setConstraints(constraints2);
        PeriodicWorkRequest request2 = workBuilder2.build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(TAG2, ExistingPeriodicWorkPolicy.KEEP,request2);

        fragmentMeals = new MealsFragment();

        String type = getIntent().getStringExtra("From");
        if (type != null) {
            switch (type) {
                case "notifyFrag":
                    DetailFragment fragment = new DetailFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.cont, fragment).commit();
                    break;
            }
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.cont,fragmentMeals).commit();
        }

        fragmentFavorites = null;

        Backendless.initApp( this, "AppID", "AndroidKey" );

        bottomNavigation= findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Meals", R.drawable.eat);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favorites", R.drawable.white_heart);

        //set properties
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#fedbd5"));

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(this);
    }

    @Override
    public boolean onTabSelected(int position, boolean wasSelected) {
        if (position == 0 ){
            getSupportFragmentManager().beginTransaction().addToBackStack("Main").replace(R.id.cont,fragmentMeals).commit();
        }
        else {
            if (fragmentFavorites ==null){
                fragmentFavorites = new FavoritesFragment();
            }
            getSupportFragmentManager().beginTransaction().addToBackStack("Main").replace(R.id.cont,fragmentFavorites).commit();
        }

        return true;
    }
}