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
import com.neoniequellponce.kusinasyon.databinding.FragmentSetRecipeKeyIngredientsBinding;
import com.neoniequellponce.kusinasyon.utility.UtilTextArea;

import java.util.List;

public class FragmentSetRecipeKeyIngredients extends Fragment {

    public static final String STEP = "step";
    public static final String FOR_EDIT = "for_edit";

    //On Edit
    public static final String KEY_INGREDIENTS = "key_ingredients";

    private String mStep;
    private boolean mForEdit;

    private FragmentSetRecipeKeyIngredientsBinding mBinding;
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
        mBinding = FragmentSetRecipeKeyIngredientsBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mBinding.tvStepIndicator.setText(mStep);

        forEditRecipe();

        return mBinding.getRoot();
    }

    public List<String> getKeyIngredientList() {
        String text = String.valueOf(mBinding.etKeyIngredients.getText()).toLowerCase().trim();
        return UtilTextArea.format(mContext, text);
    }

    public void requestFocusOnKeyIngredients() {
        mBinding.etKeyIngredients.requestFocus();
    }

    private void forEditRecipe() {
        if (mForEdit) {
            assert getArguments() != null;
            List<String> ingredientList = getArguments().getStringArrayList(KEY_INGREDIENTS);

            StringBuilder builder = new StringBuilder();

            String splitter = mContext.getString(R.string.splitter_sign);

            for (String ingredient : ingredientList) {
                builder.append(splitter).append(ingredient).append("\n");
            }

            mBinding.etKeyIngredients.setText(String.valueOf(builder));
        }
    }
}
