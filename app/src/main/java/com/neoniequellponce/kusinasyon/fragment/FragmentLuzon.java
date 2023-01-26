package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.activity.ActivityViewRecipe;
import com.neoniequellponce.kusinasyon.adapter.AdapterRecipe;
import com.neoniequellponce.kusinasyon.database.DbPublicRecipes;
import com.neoniequellponce.kusinasyon.databinding.FragmentLuzViMinMoreBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.List;

public class FragmentLuzon extends Fragment implements AdapterRecipe.OnRecipeClickListener {

    private List<ModelRecipe> mRecipeList;

    private FragmentLuzViMinMoreBinding mBinding;
    private Context mContext;
    private AdapterRecipe mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLuzViMinMoreBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mRecipeList = new ArrayList<>();

        setRecyclerView();
        getRecipes();

        mBinding.swipeLayout.setRefreshing(true);
        mBinding.swipeLayout.setOnRefreshListener(this::getRecipes);

        return mBinding.getRoot();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mAdapter = new AdapterRecipe(mContext, mRecipeList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void getRecipes() {
        DbPublicRecipes dbPublicRecipes = new DbPublicRecipes();

        List<ModelRecipe> sysRecipeTempList = new ArrayList<>();
        List<ModelRecipe> userRecipeTempList = new ArrayList<>();

        dbPublicRecipes.getRecipesByOrigin("luzon",
                new DbPublicRecipes.OnPublicRecipesGetListener() {
                    @Override
                    public void getSystemRecipes(List<ModelRecipe> systemRecipeList) {
                        sysRecipeTempList.clear();
                        sysRecipeTempList.addAll(systemRecipeList);
                        getRecipesHelper(sysRecipeTempList, userRecipeTempList);
                    }

                    @Override
                    public void getUserRecipes(List<ModelRecipe> userRecipeList) {
                        userRecipeTempList.clear();
                        userRecipeTempList.addAll(userRecipeList);
                        getRecipesHelper(sysRecipeTempList, userRecipeTempList);
                    }
                });
    }

    private void getRecipesHelper(List<ModelRecipe> sysRecipeList,
                                  List<ModelRecipe> userRecipeList) {
        mRecipeList.clear();
        mRecipeList.addAll(sysRecipeList);
        mRecipeList.addAll(userRecipeList);
        mAdapter.notifyDataSetChanged();
        mBinding.swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRecipeClick(int position) {
        ModelRecipe recipe = mRecipeList.get(position);

        Intent intent = new Intent(getActivity(), ActivityViewRecipe.class);
        intent.putExtra(ActivityViewRecipe.RECIPE, recipe);
        startActivity(intent);
    }
}
