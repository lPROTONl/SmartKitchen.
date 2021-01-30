package com.proton.smartkitchen.Ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.proton.smartkitchen.Database.MealDatabase;
import com.proton.smartkitchen.Database.Meals;
import com.proton.smartkitchen.Database.MealsDBTable;
import com.proton.smartkitchen.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;


public class DetailFragment extends Fragment {

    ImageView mealImage;
    ImageView favImage;
    TextView recipeTv, ingredientTv;
    Meals meal = new Meals();
    TextView detailName;

    public DetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void displayMealInfo(Serializable info){
        meal = (Meals) info;

        Glide.with(getActivity()).load(meal.getMealPic()).centerCrop()
                .placeholder(R.drawable.placeholder).into(mealImage);
        detailName.setText(meal.getMealName());

        String[] split = meal.getMealIngredient().split("_");
        for (String s : split) {
            ingredientTv.append(s+"\n"+"\n");
        }
        String[] split2 = meal.getMealRecipe().split("_");
        for (String s : split2) {
            recipeTv.append(s+"\n"+"\n");
        }

        //Execute onClickListener if user want to add meal item to favorites.
        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = meal.getMealName();
                List<Meals> mealExist = MealDatabase.getInstance(getActivity()).mealDao().isExists(name);

                if (!mealExist.isEmpty()){
                    Toast.makeText(getActivity(), "Already Exists in Your Favorites", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Execute else if meal doesn't exist in favorite meals Database.
                else{

                    YoYo.with(Techniques.FlipInY).duration(1500).playOn(favImage);
                    favImage.setImageResource(R.drawable.red_heart);

                    MealsDBTable meal1 = new MealsDBTable();

                    meal1.mealName = meal.getMealName();
                    meal1.type = meal.getType();
                    meal1.mealPic = meal.getMealPic();
                    meal1.mealIngredient = meal.getMealIngredient();
                    meal1.mealRecipe = meal.getMealRecipe();
                    MealDatabase.getInstance(getActivity()).mealDao().insert(meal1);

                    Snackbar snackbar = Snackbar
                            .make(view, "Saved", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        //Execute onClick if user want to undo save action.
                        @Override
                        public void onClick(View view) {
                            MealDatabase.getInstance(getActivity()).mealDao().delete(name);
                            YoYo.with(Techniques.FlipInY).duration(1500).playOn(favImage);
                            favImage.setImageResource(R.drawable.white_heart);
                        }
                    });
                    snackbar.setBackgroundTint(Color.rgb(34,163,255));
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mealImage = view.findViewById(R.id.mealImage);
        favImage = view.findViewById(R.id.favImage);
        detailName = view.findViewById(R.id.detailName);
        recipeTv = view.findViewById(R.id.recipeTv);
        recipeTv.setMovementMethod(new ScrollingMovementMethod());
        ingredientTv = view.findViewById(R.id.ingredientTv);
        ingredientTv.setMovementMethod(new ScrollingMovementMethod());

        //Check if intent extras coming from notification isn't null.
        if (getActivity().getIntent().getStringExtra("From") != null){
            //Check if user is coming from(pressed on) notification
            if (getActivity().getIntent().getStringExtra("From").equalsIgnoreCase("notifyFrag")){
                Serializable data = getActivity().getIntent().getSerializableExtra("Data");
                displayMealInfo(data);
                getActivity().getIntent().putExtra("From", "");
            }
            //The below else will be executed if intent extras coming from notification isn't null
            //but the user isn't coming from (pressed on) notification
            else{
                Serializable mealInfo = getArguments().getSerializable("mealInfo");
                String mealsFragment = getArguments().getString("mealsFragment");
                String favoriteFragment = getArguments().getString("favoriteFragment");
                if ( mealsFragment != null){
                    if (mealInfo != null){
                      displayMealInfo(mealInfo);
                    }
                }
                else if (favoriteFragment != null){
                    if (mealInfo != null){
                       displayMealInfo(mealInfo);
                    }
                }
            }
        }
        //below else will be executed if intent extras from notification equal null
        //(if the user never pressed on the notification)
        else{
            Serializable mealInfo = getArguments().getSerializable("mealInfo");
            String mealsFragment = getArguments().getString("mealsFragment");
            String favoriteFragment = getArguments().getString("favoriteFragment");
            if ( mealsFragment != null){
                if (mealInfo != null){
                   displayMealInfo(mealInfo);
                }
            }
            else if (favoriteFragment != null){
                if (mealInfo != null){
                    displayMealInfo(mealInfo);
                }
            }
        }
        return view;
    }
}