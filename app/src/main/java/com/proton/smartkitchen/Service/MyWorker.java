package com.proton.smartkitchen.Service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.proton.smartkitchen.Database.MealCloudDBTable;
import com.proton.smartkitchen.Database.MealDatabase;
import com.proton.smartkitchen.Database.Meals;

import java.util.List;

public class MyWorker extends Worker {
    MealDatabase instance;
//    private static final String TAG = "MyWorker25";
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        instance = MealDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(50);
        Backendless.Data.of(Meals.class).find(queryBuilder,new AsyncCallback<List<Meals>>() {
            @Override
            public void handleResponse(List<Meals> response) {
                        instance.mealDao().deleteMainMeals();
                        MealCloudDBTable mealCloudDBTable = new MealCloudDBTable();
                        for (Meals meal : response) {
                            mealCloudDBTable.mealId = meal.getId();
                            mealCloudDBTable.mealName = meal.getMealName();
                            mealCloudDBTable.type = meal.getType();
                            mealCloudDBTable.mealPic = meal.getMealPic();
                            mealCloudDBTable.mealIngredient = meal.getMealIngredient();
                            mealCloudDBTable.mealRecipe = meal.getMealRecipe();
                            instance.mealDao().insertMainMeal(mealCloudDBTable);
                        }
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        return ListenableWorker.Result.success();
    }
}
