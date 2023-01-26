package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.adapter.AdapterRecipeIngredient;
import com.neoniequellponce.kusinasyon.databinding.FragmentRecipeIngredientsBinding;

import java.util.ArrayList;

public class FragmentRecipeIngredients extends Fragment {

    public static final String INGREDIENTS = "ingredients";
    private ArrayList<String> mIngredientList;

    private FragmentRecipeIngredientsBinding mBinding;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIngredientList = getArguments().getStringArrayList(INGREDIENTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRecipeIngredientsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        setRecyclerView();
        return mBinding.getRoot();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        AdapterRecipeIngredient adapter = new AdapterRecipeIngredient(mIngredientList);

        mBinding.recyclerView.setHasFixedSize(false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }
}
