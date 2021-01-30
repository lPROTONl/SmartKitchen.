package com.proton.smartkitchen.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Meals")
public class MealsDBTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String mealName, type, mealIngredient, mealRecipe, mealPic;
}
