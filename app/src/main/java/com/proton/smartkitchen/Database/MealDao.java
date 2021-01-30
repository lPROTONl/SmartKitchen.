package com.proton.smartkitchen.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao {

    //Queries On Favorite Meals
    @Query("SELECT * FROM Meals")
    List<Meals> getFavMeals();

    @Insert
    long insert(MealsDBTable meal);

    @Query("DELETE FROM Meals WHERE mealName = :name")
    int delete (String name);

    @Query("SELECT * FROM Meals WHERE mealName = :name LIMIT 1")
    List<Meals> getMealDetail(String name);

    @Query("SELECT * FROM Meals WHERE mealName = :name LIMIT 1")
    List<Meals> isExists(String name);


    //Queries On Main Meals
    @Query("SELECT * FROM MealCloud")
    List<Meals> getMainMeals();

    @Query("DELETE FROM MealCloud")
    int deleteMainMeals();

    @Insert
    long insertMainMeal(MealCloudDBTable meal);

    @Query("SELECT * FROM MealCloud WHERE mealName = :name LIMIT 1")
    List<Meals> isExistsInDB(String name);

    @Query("SELECT * FROM MealCloud WHERE type = :type")
    List<Meals> getByType(String type);

    @Query("SELECT *  FROM MealCloud where mealId = :id")
    List<Meals> getRandomMeal(int id);

}
