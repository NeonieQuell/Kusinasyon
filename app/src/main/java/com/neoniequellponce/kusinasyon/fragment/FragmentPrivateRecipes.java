package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.activity.ActivityViewRecipe;
import com.neoniequellponce.kusinasyon.adapter.AdapterRecipe;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.FragmentPrivateRecipesBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogCircleManagement;
import com.neoniequellponce.kusinasyon.dialogs.DialogMyCircles;
import com.neoniequellponce.kusinasyon.dialogs.DialogShareRecipe;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.List;

public class FragmentPrivateRecipes extends Fragment implements
        DialogMyCircles.OnCurrentCircleGetListener,
        AdapterRecipe.OnRecipeClickListener {

    private FirebaseAuth mAuth;
    private DbCircle mDbCircle;

    private List<ModelRecipe> mRecipeList;
    private ModelCircle mCurrentCircle;

    private FragmentPrivateRecipesBinding mBinding;
    private Context mContext;

    private DialogMyCircles mDialogMyCircles;
    private DialogCircleManagement mDialogCircleManage;
    private DialogShareRecipe mDialogShareRecipe;
    private Toast mToast;

    private AdapterRecipe mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPrivateRecipesBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mAuth = FirebaseAuth.getInstance();
        mDbCircle = new DbCircle();

        setActionbar();
        setRecyclerView();
        setDialogs();

        mBinding.btnMyCircles.setOnClickListener(myCircles);
        mBinding.btnCircleManagement.setOnClickListener(circleManagement);
        mBinding.btnShareRecipe.setOnClickListener(shareRecipe);
        mBinding.swipeLayout.setOnRefreshListener(this::loadCircles);

        return mBinding.getRoot();
    }


    private void setActionbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        mRecipeList = new ArrayList<>();
        mAdapter = new AdapterRecipe(mContext, mRecipeList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void loadCircles() {
        //Circles are ordered by their keys in Firebase
        mDbCircle.getCircles(mAuth, circleList -> {
            mRecipeList.clear();

            if (circleList.isEmpty()) {
                mCurrentCircle = null;

                //Set my circles button text to default
                mBinding.btnMyCircles.setText("My Circles");
                //Set user has not joined circles message
                mBinding.tvIndicator.setText("You have not joined any circles.");

                //Hide recyclerview shared recipes
                mBinding.recyclerView.setVisibility(View.GONE);

                mBinding.tvIndicator.setVisibility(View.VISIBLE);
                mBinding.swipeLayout.setRefreshing(false);
            } else {
                //Set my circles dialog
                mDialogMyCircles = new DialogMyCircles(mContext, circleList, this);
                mDialogMyCircles.mAdapter.notifyDataSetChanged();

                mCurrentCircle = circleList.get(0);
                mBinding.btnMyCircles.setText(mCurrentCircle.getName());

                loadSharedRecipes();
            }
        });
    }

    private void loadSharedRecipes() {
        mDbCircle.getSharedRecipes(mCurrentCircle.getKey(), recipeList -> {
            if (recipeList.isEmpty()) {
                mBinding.tvIndicator.setText("No shared recipes to this circle.");
                mBinding.tvIndicator.setVisibility(View.VISIBLE);
                mBinding.recyclerView.setVisibility(View.GONE);
            } else {
                mRecipeList.clear();
                mRecipeList.addAll(recipeList);
                mBinding.tvIndicator.setVisibility(View.GONE);
                mBinding.recyclerView.setVisibility(View.VISIBLE);
            }

            mAdapter.notifyDataSetChanged();
            mBinding.swipeLayout.setRefreshing(false);
        });
    }

    private void setDialogs() {
        //Dialog circle management
        mDialogCircleManage = new DialogCircleManagement(mContext, () -> {
            loadCircles();
            mToast = Toast.makeText(mContext, "You have left the circle", Toast.LENGTH_SHORT);
            mToast.show();
        });

        //Dialog share recipe
        mDialogShareRecipe = new DialogShareRecipe(mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.swipeLayout.setRefreshing(true);
        loadCircles();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mToast != null) mToast.cancel();
    }

    @Override
    public void getCurrentCircle(ModelCircle currentCircle) {
        mCurrentCircle = currentCircle;

        mBinding.btnMyCircles.setText(mCurrentCircle.getName());
        //Load shared recipes of current circle
        loadSharedRecipes();

        mDialogMyCircles.dismiss();
    }

    @Override
    public void onRecipeClick(int position) {
        ModelRecipe recipe = mRecipeList.get(position);

        Intent intent = new Intent(getActivity(), ActivityViewRecipe.class);
        intent.putExtra(ActivityViewRecipe.RECIPE, recipe);
        startActivity(intent);
    }

    private void setToast() {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(mContext, "Please join a circle fist", Toast.LENGTH_SHORT);
        mToast.show();
    }

    private View.OnClickListener myCircles = view -> {
        if (mCurrentCircle != null) mDialogMyCircles.show();
        else setToast();
    };

    private View.OnClickListener circleManagement = view -> {
        if (mCurrentCircle != null) {
            mDialogCircleManage.setCurrentCircle(mCurrentCircle);
            mDialogCircleManage.show();
        } else setToast();
    };

    private View.OnClickListener shareRecipe = view -> {
        if (mCurrentCircle != null) {
            mDialogShareRecipe.setCurrentCircle(mCurrentCircle);
            mDialogShareRecipe.mAdapter.notifyDataSetChanged();
            mDialogShareRecipe.show();
        } else setToast();
    };
}
