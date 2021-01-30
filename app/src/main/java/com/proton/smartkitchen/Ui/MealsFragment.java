package com.proton.smartkitchen.Ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.proton.smartkitchen.Database.MealDatabase;
import com.proton.smartkitchen.Database.Meals;
import com.proton.smartkitchen.Database.MealsDBTable;
import com.proton.smartkitchen.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;


import java.util.List;

public class MealsFragment extends Fragment implements OnSpinnerItemSelectedListener<String> {

    RecyclerView recyclerView;
    DetailFragment fragment;
    ProgressBar progressBar;
    PowerSpinnerView powerSpinner;

    Adapter adapter;

    List<Meals> mainMeals;

    public MealsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_meals, container, false);

        fragment = null;

        progressBar = view.findViewById(R.id.progress);
        Sprite threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

        powerSpinner = view.findViewById(R.id.powerSpinner);
        powerSpinner.setOnSpinnerItemSelectedListener(MealsFragment.this);

        recyclerView= view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new Adapter();

        fetchMeals();

        return view;
    }

    private void fetchMeals() {
        mainMeals = MealDatabase.getInstance(getActivity()).mealDao().getMainMeals();

        if (mainMeals.isEmpty()){
            DataQueryBuilder builder = DataQueryBuilder.create();
            builder.setPageSize(50);
            Backendless.Data.of(Meals.class).find(builder,new AsyncCallback<List<Meals>>() {
                @Override
                public void handleResponse(List<Meals> response) {
                    for (Meals meals : response) {
                        mainMeals.add(meals);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemSelected(int i, String t) {
        if (i == 0){
            //Get all Meals.
            List<Meals> meals = MealDatabase.getInstance(getActivity()).mealDao().getMainMeals();
            mainMeals.clear();
            mainMeals.addAll(meals);
            adapter.notifyDataSetChanged();
        }
        else if (i == 1){
            //Get vegan Meals.
            mainMeals.clear();
            List<Meals> meals = MealDatabase.getInstance(getActivity()).mealDao().getByType(powerSpinner.getText().toString().toLowerCase());
            mainMeals.addAll(meals);
            adapter.notifyDataSetChanged();
        }
        else{
            //Get vegetarian Meals.
            mainMeals.clear();
            List<Meals> meals = MealDatabase.getInstance(getActivity()).mealDao().getByType(powerSpinner.getText().toString().toLowerCase());
            mainMeals.addAll(meals);
            adapter.notifyDataSetChanged();
        }
    }


    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {

            Glide.with(getActivity()).load(mainMeals.get(position).getMealPic()).centerCrop()
                    .placeholder(R.drawable.placeholder).into(holder.imageView);
            holder.tv.setText(mainMeals.get(position).getMealName());

            //Execute onClickListener if user pressed on any meal item.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name = mainMeals.get(position).getMealName();
                    List<Meals> mealExistDB = MealDatabase.getInstance(getActivity()).mealDao().isExistsInDB(name);
                    //Get meal if exists in Database
                    if (!mealExistDB.isEmpty()){
                        Intent intent = new Intent( getActivity(), DetailFragment.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("mealInfo",mealExistDB.get(0));
                        bundle.putString("mealsFragment","mealsFragment");
                        if (fragment == null) {
                            fragment = new DetailFragment();
                            fragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().addToBackStack("Main").replace(R.id.cont,fragment).commit();
                        }
                    }
                    //Get meal from cloud if it doesn't exist in Database
                    else{
                        DataQueryBuilder builder = DataQueryBuilder.create();
                        builder.setWhereClause("mealName = '"+name+"\'");
                        Backendless.Data.of(Meals.class).find(builder, new AsyncCallback<List<Meals>>() {
                            @Override
                            public void handleResponse(List<Meals> response) {
                                Intent intent = new Intent( getActivity(), DetailFragment.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("mealInfo",response.get(0));
                                bundle.putString("mealsFragment","mealsFragment");
                                if (fragment == null) {
                                    fragment = new DetailFragment();
                                    fragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction().addToBackStack("Main").replace(R.id.cont,fragment).commit();
                                }
                            }
                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(getActivity(), "Internet Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            //Add meal in favorites.
            holder.starImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = mainMeals.get(position).getMealName();
                    List<Meals> mealExist = MealDatabase.getInstance(getActivity()).mealDao().isExists(name);
                    if (!mealExist.isEmpty()){
                        Toast.makeText(getActivity(), "Already Exists in Your Favorites", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        YoYo.with(Techniques.FlipInY).duration(1500).playOn(holder.starImage);
                        holder.starImage.setImageResource(R.drawable.red_heart);

                        MealsDBTable meal = new MealsDBTable();

                        meal.mealName = mainMeals.get(position).getMealName();
                        meal.type = mainMeals.get(position).getType();
                        meal.mealPic = mainMeals.get(position).getMealPic();
                        meal.mealIngredient = mainMeals.get(position).getMealIngredient();
                        meal.mealRecipe = mainMeals.get(position).getMealRecipe();
                        MealDatabase.getInstance(getActivity()).mealDao().insert(meal);

                        Snackbar snackbar = Snackbar
                                .make(view, "Saved", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            //remove meal from favorites
                            @Override
                            public void onClick(View view) {
                                MealDatabase.getInstance(getActivity()).mealDao().delete(name);
                                YoYo.with(Techniques.FlipInY).duration(1500).playOn(holder.starImage);
                                holder.starImage.setImageResource(R.drawable.white_heart);
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
        public int getItemCount() {
            return mainMeals.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            ImageView starImage;
            TextView tv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                starImage = itemView.findViewById(R.id.favImage);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }


}