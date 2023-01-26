package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterRecipe;
import com.neoniequellponce.kusinasyon.database.DbPublicRecipes;
import com.neoniequellponce.kusinasyon.databinding.ActivityRecommendCuisineBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.List;

public class ActivityRecommendCuisine extends AppCompatActivity implements
        AdapterRecipe.OnRecipeClickListener {

    public static final String LIST = "list";

    private ArrayList<String> mIngredientList;
    private List<ModelRecipe> mMatchedCuisineList;

    private ActivityRecommendCuisineBinding mBinding;
    private AdapterRecipe mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRecommendCuisineBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        //Get final selection of ingredients
        mIngredientList = getIntent().getStringArrayListExtra(LIST);
        mMatchedCuisineList = new ArrayList<>();

        setActionBar();
        matchRecipes();
        setRecyclerView();

        if (!mMatchedCuisineList.isEmpty()) mBinding.tvIndicator.setVisibility(View.GONE);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Recommended Cuisines");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void matchRecipes() {
        DbPublicRecipes dbPublicRecipes = new DbPublicRecipes();
        dbPublicRecipes.getAllRecipes(new DbPublicRecipes.OnPublicRecipesGetListener() {
            @Override
            public void getSystemRecipes(List<ModelRecipe> systemRecipeList) {
                matchRecipesHelper(systemRecipeList);
                setSnackbarMatchCuisine();
                setIndicatorVisibility();
            }

            @Override
            public void getUserRecipes(List<ModelRecipe> userRecipeList) {
                matchRecipesHelper(userRecipeList);
                setSnackbarMatchCuisine();
                setIndicatorVisibility();
            }
        });
    }

    private void matchRecipesHelper(List<ModelRecipe> recipeList) {
        for (ModelRecipe recipe : recipeList) {
            int containsCtr = 0;

            for (String ingredient : mIngredientList) {
                //Get recipe that matches the input ingredients
                if (recipe.getKeyIngredients().contains(ingredient)) {
                    //Increase matched recipe size
                    containsCtr += 1;

                    if (containsCtr == recipe.getKeyIngredients().size()) {
                        mMatchedCuisineList.add(recipe);
                        if (mAdapter != null) mAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void setSnackbarMatchCuisine() {
        String msg = "Matched cuisines: " + mMatchedCuisineList.size();
        Snackbar snackbar = Snackbar.make(mBinding.layoutParent, msg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
        snackbar.setAction("DISMISS", v -> snackbar.dismiss());
        snackbar.show();
    }

    private void setIndicatorVisibility() {
        if (!mMatchedCuisineList.isEmpty()) {
            mBinding.progressIndicator.setVisibility(View.GONE);
            mBinding.tvIndicator.setVisibility(View.GONE);
        } else {
            mBinding.progressIndicator.setVisibility(View.GONE);
            mBinding.tvIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new AdapterRecipe(this, mMatchedCuisineList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRecipeClick(int position) {
        ModelRecipe recipe = mMatchedCuisineList.get(position);

        Intent intent = new Intent(this, ActivityViewRecipe.class);
        intent.putExtra(ActivityViewRecipe.RECIPE, recipe);
        startActivity(intent);
    }
}
