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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neoniequellponce.kusinasyon.activity.ActivityViewRecipe;
import com.neoniequellponce.kusinasyon.adapter.AdapterRecipe;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.FragmentPubliclySharedBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.List;

public class FragmentPubliclyShared extends Fragment implements AdapterRecipe.OnRecipeClickListener {

    private List<ModelRecipe> mUserRecipeList;

    private FragmentPubliclySharedBinding mBinding;
    private Context mContext;

    private AdapterRecipe mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPubliclySharedBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mUserRecipeList = new ArrayList<>();

        mBinding.tvIndicator.setVisibility(View.GONE);

        setRecyclerView();

        mBinding.swipeLayout.setOnRefreshListener(this::loadUserRecipes);

        return mBinding.getRoot();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mAdapter = new AdapterRecipe(mContext, mUserRecipeList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.swipeLayout.setRefreshing(true);
        loadUserRecipes();
    }

    private void loadUserRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        DbUsers dao = new DbUsers();
        dao.getUserRecipes(user.getUid(), "public",
                userRecipeList -> {
                    mUserRecipeList.clear();

                    if (!userRecipeList.isEmpty()) {
                        mUserRecipeList.addAll(userRecipeList);
                        mBinding.tvIndicator.setVisibility(View.GONE);
                    } else mBinding.tvIndicator.setVisibility(View.VISIBLE);

                    mAdapter.notifyDataSetChanged();
                    mBinding.swipeLayout.setRefreshing(false);
                });
    }

    @Override
    public void onRecipeClick(int position) {
        ModelRecipe recipe = mUserRecipeList.get(position);

        Intent intent = new Intent(getActivity(), ActivityViewRecipe.class);
        intent.putExtra(ActivityViewRecipe.RECIPE, recipe);
        startActivity(intent);
    }
}
