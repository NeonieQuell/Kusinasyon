package com.neoniequellponce.kusinasyon.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.databinding.FragmentRecipeDetailsBinding;

import java.util.Objects;

public class FragmentRecipeDetails extends Fragment {

    public static final String DESCRIPTION = "description";
    public static final String AUTHOR_UID = "author_uid";
    public static final String AUTHOR = "author";
    public static final String ORIGIN = "origin";
    public static final String VISIBILITY = "visibility";

    private String mDescription;
    private String mAuthorUid;
    private String mAuthor;
    private String mOrigin;
    private String mVisibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDescription = getArguments().getString(DESCRIPTION);
            mAuthorUid = getArguments().getString(AUTHOR_UID);
            mAuthor = getArguments().getString(AUTHOR);
            mOrigin = getArguments().getString(ORIGIN);
            mVisibility = getArguments().getString(VISIBILITY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentRecipeDetailsBinding binding = FragmentRecipeDetailsBinding
                .inflate(inflater, container, false);

        binding.tvDescriptionContent.setText(mDescription);
        binding.tvAuthorContent.setText(mAuthor);
        binding.tvOriginContent.setText(mOrigin);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (Objects.equals(auth.getUid(), mAuthorUid)) {
            String visibility = mVisibility.substring(0, 1).toUpperCase() + mVisibility.substring(1);
            binding.tvVisibilityContent.setText(visibility);
        } else {
            binding.tvVisibilityTitle.setVisibility(View.GONE);
            binding.tvVisibilityContent.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}
