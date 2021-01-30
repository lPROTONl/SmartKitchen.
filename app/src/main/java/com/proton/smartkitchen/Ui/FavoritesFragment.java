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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.proton.smartkitchen.Database.MealDatabase;
import com.proton.smartkitchen.Database.Meals;
import com.proton.smartkitchen.Database.MealsDBTable;
import com.proton.smartkitchen.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavoritesFragment extends Fragment {

    List<Meals> meals;
    RecyclerView favRecyclerView;
    Adapter adapter;
    DetailFragment fragment;

    public FavoritesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        fragment = null;

        favRecyclerView= view.findViewById(R.id.favRecyclerView);
        favRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new Adapter();

        meals = MealDatabase.getInstance(getActivity()).mealDao().getFavMeals();

        favRecyclerView.setAdapter(adapter);

        return view;
    }


    private class Adapter extends RecyclerView.Adapter<FavoritesFragment.Adapter.ViewHolder> {
        @NonNull
        @Override
        public FavoritesFragment.Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FavoritesFragment.Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favitem,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FavoritesFragment.Adapter.ViewHolder holder, int position) {

            Glide.with(getActivity()).load(meals.get(position).getMealPic()).centerCrop()
                    .placeholder(R.drawable.placeholder).into(holder.imageView);
            holder.tv.setText(meals.get(position).getMealName());

            //Execute onClickListener if user pressed on any favorite meal item.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Send meal detail to Detail Fragment.
                    List<Meals> mealDetail = MealDatabase.getInstance(getActivity()).mealDao()
                            .getMealDetail(meals.get(position).getMealName());
                    Intent intent = new Intent(getActivity(), DetailFragment.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mealInfo",mealDetail.get(0));
                    bundle.putString("favoriteFragment","favoriteFragment");

                    if (fragment == null) {
                        fragment = null;
                        fragment = new DetailFragment();
                        fragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().addToBackStack("Main").replace(R.id.cont,fragment).commit();
                    }
                }
            });

            //Execute onClickListener if user pressed on remove icon.
            holder.removeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mealName = meals.get(position).getMealName();
                    MealDatabase.getInstance(getActivity()).mealDao().delete(mealName);
                    meals.remove(meals.get(position));
                    adapter.notifyDataSetChanged();

                    Snackbar snackbar = Snackbar
                            .make(view, "Removed", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        //Execute onClick if user want to undo removal action.
                        @Override
                        public void onClick(View view) {
                            List<Meals> mealDetail = MealDatabase.getInstance(getActivity()).mealDao().isExistsInDB(mealName);
                            MealsDBTable meal = new MealsDBTable();

                            meal.mealName = mealDetail.get(0).getMealName();
                            meal.type = mealDetail.get(0).getType();
                            meal.mealPic = mealDetail.get(0).getMealPic();
                            meal.mealIngredient = mealDetail.get(0).getMealIngredient();
                            meal.mealRecipe = mealDetail.get(0).getMealRecipe();
                            MealDatabase.getInstance(getActivity()).mealDao().insert(meal);
                            meals.add(mealDetail.get(0));
                            adapter.notifyDataSetChanged();
                        }
                    });
                    snackbar.setBackgroundTint(Color.rgb(34,163,255));
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return meals.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            ImageView removeImage;
            TextView tv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                removeImage = itemView.findViewById(R.id.removeImage);
                tv = itemView.findViewById(R.id.tv);
            }
        }
    }
}