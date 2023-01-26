package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.neoniequellponce.kusinasyon.databinding.FragmentSetRecipeImageBinding;
import com.neoniequellponce.kusinasyon.utility.UtilPermission;
import com.squareup.picasso.Picasso;

public class FragmentSetRecipeImage extends Fragment {

    private Uri mImageUri = null;

    public static final String STEP = "step";
    public static final String FOR_EDIT = "for_edit";

    //On Edit
    public static final String IMAGE = "image";

    private String mStep;
    private boolean mForEdit;

    private FragmentSetRecipeImageBinding mBinding;
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
        mBinding = FragmentSetRecipeImageBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        //Load image if image uri field is not null
        if (mImageUri != null) {
            Picasso.with(mContext).load(mImageUri).into(mBinding.recipeImg);
        }

        forEditRecipe();

        mBinding.tvStepIndicator.setText(mStep);
        mBinding.btnChooseImage.setOnClickListener(v -> chooseImage());

        return mBinding.getRoot();
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    private void forEditRecipe() {
        if (mForEdit) {
            assert getArguments() != null;
            String imageUrl = getArguments().getString(IMAGE);
            Picasso.with(mContext).load(imageUrl).into(mBinding.recipeImg);
            mBinding.btnChooseImage.setVisibility(View.GONE);
        }
    }

    private void chooseImage() {
        if (UtilPermission.hasStoragePermission(mContext)) {
            if (activityResult != null) activityResult.launch("image/*");
        } else UtilPermission.requestStoragePermission(requireActivity());
    }

    private ActivityResultLauncher<String> activityResult =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    mImageUri = result;
                    Picasso.with(mContext).load(mImageUri).into(mBinding.recipeImg);
                }
            });

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityResult = null;
    }
}
