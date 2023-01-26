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

import com.neoniequellponce.kusinasyon.adapter.AdapterRecipeInstruction;
import com.neoniequellponce.kusinasyon.databinding.FragmentRecipeInstructionsBinding;

import java.util.ArrayList;

public class FragmentRecipeInstructions extends Fragment {

    public static final String INSTRUCTIONS = "instructions";
    private ArrayList<String> mInstructionList;

    private FragmentRecipeInstructionsBinding mBinding;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInstructionList = getArguments().getStringArrayList(INSTRUCTIONS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentRecipeInstructionsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        setRecyclerView();

        return mBinding.getRoot();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        AdapterRecipeInstruction adapter = new AdapterRecipeInstruction(mInstructionList);

        mBinding.recyclerView.setHasFixedSize(false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }
}
