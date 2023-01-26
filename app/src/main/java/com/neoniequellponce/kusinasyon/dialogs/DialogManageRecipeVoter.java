package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterDialogOption;
import com.neoniequellponce.kusinasyon.database.DbPublicRecipes;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.DialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelOption;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class DialogManageRecipeVoter extends Dialog implements
        AdapterDialogOption.OnOptionClickListener {

    private FirebaseAuth mAuth;
    private DbUsers mDbUsers;

    private ModelRecipe mRecipe;
    private List<ModelOption> mOptionList;

    private DialogOptionBinding mBinding;
    private Context mContext;
    private Handler mHandler;
    private OnRecipeVoteListener mListener;

    private Dialog mDiaLoading;

    public DialogManageRecipeVoter(@NonNull Context context, OnRecipeVoteListener listener) {
        super(context);
        mContext = context;
        mListener = listener;

        mAuth = FirebaseAuth.getInstance();
        mDbUsers = new DbUsers();
        mHandler = new Handler(Looper.getMainLooper());
        mDiaLoading = new DialogLoading(context);

        setProperty();
        setModels();
        setRecyclerView();

        mBinding.tvTitle.setText("Manage Recipe");
        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    public void setProperty() {
        mBinding = DialogOptionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setModels() {
        mOptionList = new ArrayList<>();

        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("Approve", R.drawable.ic_verified);
        hashMap.put("Reject", R.drawable.ic_backspace);

        for (String key : hashMap.keySet()) {
            ModelOption option = new ModelOption(hashMap.get(key), key);
            mOptionList.add(option);
        }
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        AdapterDialogOption adapter = new AdapterDialogOption(mOptionList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }

    public void setRecipe(ModelRecipe recipe) {
        mRecipe = recipe;
    }

    @Override
    public void onOptionClick(int position) {
        dismiss();
        mHandler.postDelayed(() -> {
            mDiaLoading.show();

            String name = mOptionList.get(position).getName().toLowerCase();

            if (name.equals("approve")) approveRecipe();
            else rejectRecipe();
        }, 200);
    }

    private void approveRecipe() {
        List<String> newApproverList = new ArrayList<>();
        mDbUsers.getApproversAndRejectors(mRecipe, (approverList, rejectorList) -> {
            newApproverList.clear();
            newApproverList.addAll(approverList);
            newApproverList.add(mAuth.getUid());

            mRecipe.setApprovers(newApproverList);

            //Update approvers
            mDbUsers.voteRecipe(mRecipe)
                    .addOnSuccessListener(s ->
                            mDbUsers.getVoters(mRecipe, voterList -> mHandler.postDelayed(() -> {
                                DbPublicRecipes dbPubRec = new DbPublicRecipes();

                                if (Objects.equals(newApproverList.size(), voterList.size())) {
                                    //Add recipe to public
                                    dbPubRec.addApprovedRecipe(mRecipe);
                                } else {
                                    //Get sum of approvers and rejectors
                                    long total = newApproverList.size() + rejectorList.size();

                                    //Check if sum is equal to voters
                                    if (voterList.size() == total) {
                                        if (newApproverList.size() > rejectorList.size()) {
                                            //Add recipe to public
                                            dbPubRec.addApprovedRecipe(mRecipe);
                                        } else {
                                            //Change status of recipe to rejected
                                            mDbUsers.rejectRecipe(mRecipe);
                                        }
                                    }
                                }
                                mDiaLoading.dismiss();
                                mListener.onRecipeVote(true);
                            }, 1500)))
                    .addOnFailureListener(Throwable::printStackTrace);
        });
    }

    private void rejectRecipe() {
        List<String> newRejectorList = new ArrayList<>();
        mDbUsers.getApproversAndRejectors(mRecipe, (approverList, rejectorList) -> {
            newRejectorList.clear();
            newRejectorList.addAll(rejectorList);
            newRejectorList.add(mAuth.getUid());

            mRecipe.setRejectors(newRejectorList);

            //Update rejectors
            mDbUsers.voteRecipe(mRecipe)
                    .addOnSuccessListener(s ->
                            mDbUsers.getVoters(mRecipe, voterList -> mHandler.postDelayed(() -> {
                                if (Objects.equals(newRejectorList.size(), voterList.size())) {
                                    //Change status of recipe to rejected
                                    mDbUsers.rejectRecipe(mRecipe);
                                } else {
                                    //Get sum of approvers and rejectors
                                    long total = newRejectorList.size() + approverList.size();

                                    //Check if sum is equal to voters
                                    if (voterList.size() == total) {
                                        if (newRejectorList.size() > approverList.size()) {
                                            //Change status of recipe to rejected
                                            mDbUsers.rejectRecipe(mRecipe);
                                        } else {
                                            //Add recipe to public
                                            DbPublicRecipes dbPublicRecipes = new DbPublicRecipes();
                                            dbPublicRecipes.addApprovedRecipe(mRecipe);
                                        }
                                    }
                                }
                                mDiaLoading.dismiss();
                                mListener.onRecipeVote(false);
                            }, 1500)))
                    .addOnFailureListener(Throwable::printStackTrace);
        });
    }

    public interface OnRecipeVoteListener {
        void onRecipeVote(boolean approved);
    }
}
