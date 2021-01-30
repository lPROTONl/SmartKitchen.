package com.proton.smartkitchen.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MealsDBTable.class, MealCloudDBTable.class}, version = 1)
public abstract class MealDatabase extends RoomDatabase {

    public abstract MealDao mealDao();

    public static MealDatabase ourInstance;

    public static MealDatabase getInstance(Context context) {

        if (ourInstance == null) {

            ourInstance = Room.databaseBuilder(context,

                    MealDatabase.class, "Meals.db")
                    .createFromAsset("database/Meals.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return ourInstance;
    }
}
