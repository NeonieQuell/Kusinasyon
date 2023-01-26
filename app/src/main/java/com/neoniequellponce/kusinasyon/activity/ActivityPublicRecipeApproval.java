package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterRecipe;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.ActivityPublicRecipeApprovalBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.List;

public class ActivityPublicRecipeApproval extends AppCompatActivity implements
        AdapterRecipe.OnRecipeClickListener {

    private DbUsers mDbUsers;
    private List<ModelRecipe> mRecipeList;

    private ActivityPublicRecipeApprovalBinding mBinding;

    private AdapterRecipe mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPublicRecipeApprovalBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mDbUsers = new DbUsers();

        setActionBar();
        setRecyclerView();

        mBinding.swipeLayout.setOnRefreshListener(this::loadRecipes);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Public Recipes for Approval");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        mRecipeList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new AdapterRecipe(this, mRecipeList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.swipeLayout.setRefreshing(true);
        loadRecipes();
    }

    private void loadRecipes() {
        mDbUsers.getRecipeApproval(recipeList -> {
            mRecipeList.clear();
            if (recipeList.isEmpty()) {
                mBinding.recyclerView.setVisibility(View.GONE);
                mBinding.tvIndicator.setVisibility(View.VISIBLE);
            } else {
                mBinding.recyclerView.setVisibility(View.VISIBLE);
                mBinding.tvIndicator.setVisibility(View.GONE);
                mRecipeList.addAll(recipeList);
                mAdapter.notifyDataSetChanged();
            }
            mBinding.swipeLayout.setRefreshing(false);
        });
    }

    @Override
    public void onRecipeClick(int position) {
        ModelRecipe recipe = mRecipeList.get(position);

        Intent intent = new Intent(this, ActivityViewRecipeApproval.class);
        intent.putExtra(ActivityViewRecipeApproval.RECIPE, recipe);
        startActivity(intent);
    }
}
