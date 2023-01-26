package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterCheckedIngredient;
import com.neoniequellponce.kusinasyon.databinding.ActivityIngredientsBagBinding;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;
import com.neoniequellponce.kusinasyon.model.ModelIngredient;

import java.util.ArrayList;

public class ActivityIngredientsBag extends AppCompatActivity {

    private HolderIngredients mHolder;
    private ArrayList<ModelIngredient> mIngredientList;

    private ActivityIngredientsBagBinding mBinding;
    private AdapterCheckedIngredient mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityIngredientsBagBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mHolder = HolderIngredients.getInstance();
        mIngredientList = new ArrayList<>();

        setActionBar();
        setRecyclerView();
        verifyCheckedIngred();

        mBinding.btnRecommendCuisine.setOnClickListener(v -> recommendCuisine());
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ingredients Bag");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void verifyCheckedIngred() {
        if (mHolder.getCheckedIngredientList() != null) {
            if (!mHolder.getCheckedIngredientList().isEmpty()) {
                for (ModelIngredient ingred : mHolder.getCheckedIngredientList()) {
                    ModelIngredient ingredient = new ModelIngredient(
                            ingred.getGroup(), ingred.getName()
                    );
                    mIngredientList.add(ingredient);
                }
                ingredBagIsNotEmpty();
            } else ingredBagIsEmpty();
        } else ingredBagIsEmpty();
    }

    private void ingredBagIsEmpty() {
        mBinding.tvAssumedIngredients.setVisibility(View.GONE);
        mBinding.cvSelection.setVisibility(View.GONE);
        mBinding.tvIndicator.setVisibility(View.VISIBLE);
        mBinding.llBottom.setVisibility(View.GONE);
    }

    private void ingredBagIsNotEmpty() {
        mAdapter.notifyDataSetChanged();
        mBinding.tvAssumedIngredients.setVisibility(View.VISIBLE);
        mBinding.cvSelection.setVisibility(View.VISIBLE);
        mBinding.tvIndicator.setVisibility(View.GONE);
        mBinding.llBottom.setVisibility(View.VISIBLE);
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mAdapter = new AdapterCheckedIngredient(mIngredientList, deleted -> {
            if (mIngredientList.isEmpty()) ingredBagIsEmpty();
        });

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void recommendCuisine() {
        ArrayList<String> ingredientList = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        for (ModelIngredient ingredient : mIngredientList) {
            builder.delete(0, builder.length());
            builder.append(ingredient.getName().toLowerCase());

            ingredientList.add(String.valueOf(builder));
        }

        //Add assumed ingredient(s) in LOWERCASE
        ingredientList.add("water");

        Intent intent = new Intent(this, ActivityRecommendCuisine.class);
        intent.putExtra(ActivityRecommendCuisine.LIST, ingredientList);
        startActivity(intent);
    }
}
