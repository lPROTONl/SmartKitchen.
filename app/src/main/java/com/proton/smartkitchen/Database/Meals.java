package com.proton.smartkitchen.Database;


import java.io.Serializable;

public class Meals implements Serializable {

    private String mealName, type, mealIngredient, mealRecipe, mealPic;
    private int id;

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getMealName() {
        return mealName;
    }

    public String getType() {
        return type;
    }

    public String getMealPic() {
        return mealPic;
    }

    public String getMealIngredient() {
        return mealIngredient;
    }

    public String getMealRecipe() {
        return mealRecipe;
    }


    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMealIngredient(String mealIngredient) {
        this.mealIngredient = mealIngredient;
    }

    public void setMealRecipe(String mealRecipe) {
        this.mealRecipe = mealRecipe;
    }

    public void setMealPic(String mealPic) {
        this.mealPic = mealPic;
    }
}
