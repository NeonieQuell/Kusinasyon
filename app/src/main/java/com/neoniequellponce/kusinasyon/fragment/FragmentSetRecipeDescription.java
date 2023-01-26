package com.neoniequellponce.kusinasyon.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.neoniequellponce.kusinasyon.databinding.FragmentSetRecipeDescriptionBinding;

public class FragmentSetRecipeDescription extends Fragment {

    public static final String STEP = "step";
    public static final String FOR_EDIT = "for_edit";

    //On Edit
    public static final String DESCRIPTION = "description";

    private String mStep;
    private boolean mForEdit;

    private FragmentSetRecipeDescriptionBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStep = getArguments().getString(STEP);
            mForEdit = getArguments().getBoolean(FOR_EDIT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSetRecipeDescriptionBinding.inflate(inflater, container, false);

        mBinding.tvStepIndicator.setText(mStep);

        forEditRecipe();

        return mBinding.getRoot();
    }

    public String getDescription() {
        return String.valueOf(mBinding.etDescription.getText()).trim();
    }

    public void requestFocusOnDescription() {
        mBinding.etDescription.requestFocus();
    }

    private void forEditRecipe() {
        if (mForEdit) {
            assert getArguments() != null;
            String description = getArguments().getString(DESCRIPTION);
            mBinding.etDescription.setText(description);
        }
    }
}
