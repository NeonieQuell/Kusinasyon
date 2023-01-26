package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.FragmentSetRecipeInstructionsBinding;
import com.neoniequellponce.kusinasyon.utility.UtilTextArea;

import java.util.List;

public class FragmentSetRecipeInstructions extends Fragment {

    public static final String STEP = "step";
    public static final String FOR_EDIT = "for_edit";
    
    //On Edit
    public static final String INSTRUCTIONS = "instructions";

    private String mStep;
    private boolean mForEdit;

    private FragmentSetRecipeInstructionsBinding mBinding;
    private Context mContext;

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
        mBinding = FragmentSetRecipeInstructionsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mBinding.tvStepIndicator.setText(mStep);

        forEditRecipe();

        return mBinding.getRoot();
    }

    public List<String> getInstructionList() {
        String text = String.valueOf(mBinding.etInstructions.getText()).trim();
        return UtilTextArea.format(mContext, text);
    }

    public void requestFocusOnInstructions() {
        mBinding.etInstructions.requestFocus();
    }

    private void forEditRecipe() {
        if (mForEdit) {
            assert getArguments() != null;
            List<String> instructionList = getArguments().getStringArrayList(INSTRUCTIONS);

            StringBuilder builder = new StringBuilder();

            String splitter = mContext.getString(R.string.splitter_sign);

            for (String instruction : instructionList) {
                builder.append(splitter).append(instruction).append("\n");
            }

            mBinding.etInstructions.setText(String.valueOf(builder));
        }
    }
}
