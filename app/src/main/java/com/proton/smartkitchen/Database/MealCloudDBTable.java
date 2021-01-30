package com.proton.smartkitchen.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MealCloud")
public class MealCloudDBTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String mealName, type, mealIngredient, mealRecipe, mealPic;
    public Integer mealId;
}
