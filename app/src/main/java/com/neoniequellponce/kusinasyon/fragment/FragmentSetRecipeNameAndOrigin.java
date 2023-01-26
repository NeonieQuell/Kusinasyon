package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.FragmentSetRecipeNameAndOriginBinding;

public class FragmentSetRecipeNameAndOrigin extends Fragment implements
        AdapterView.OnItemSelectedListener {

    public static final String STEP = "step";
    public static final String FOR_EDIT = "for_edit";

    //On Edit
    public static final String NAME = "name";
    public static final String ORIGIN = "origin";

    private String mStep;
    private String mSelectedOrigin;
    private boolean mForEdit;

    private FragmentSetRecipeNameAndOriginBinding mBinding;
    private Context mContext;

    private ArrayAdapter<CharSequence> mSpinnerAdapter;

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
        mBinding = FragmentSetRecipeNameAndOriginBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mBinding.tvStepIndicator.setText(mStep);

        setSpinnerAdapter();
        forEditRecipe();

        return mBinding.getRoot();
    }

    public String getName() {
        return String.valueOf(mBinding.etName.getText()).trim();
    }

    public String getOrigin() {
        return mSelectedOrigin;
    }

    public void requestFocusOnName() {
        mBinding.etName.requestFocus();
    }

    private void setSpinnerAdapter() {
        mSpinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.origins,
                R.layout.layout_text_view_recipe_origin_spinner);

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.spinnerOrigin.setAdapter(mSpinnerAdapter);
        mBinding.spinnerOrigin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedOrigin = String.valueOf(parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void forEditRecipe() {
        if (mForEdit) {
            assert getArguments() != null;
            String recipeName = getArguments().getString(NAME);
            String recipeOrigin = getArguments().getString(ORIGIN);

            mBinding.etName.setText(recipeName);

            int spinnerPosition = mSpinnerAdapter.getPosition(recipeOrigin);
            mBinding.spinnerOrigin.setSelection(spinnerPosition);

            mBinding.spinnerOrigin.setEnabled(false);
            mBinding.spinnerOrigin.setBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.sonic_silver_300));
        }
    }
}
