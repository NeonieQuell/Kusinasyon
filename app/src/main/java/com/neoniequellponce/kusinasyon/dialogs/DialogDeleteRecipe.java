package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.database.DbPublicRecipes;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.DialogConfirmBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

public class DialogDeleteRecipe extends Dialog {

    private ModelRecipe mRecipe;

    private DialogConfirmBinding mBinding;
    private Context mContext;
    private Handler mHandler;
    private OnRecipeDeleteListener mListener;

    private Dialog mDiaLoading;

    public DialogDeleteRecipe(@NonNull Context context, ModelRecipe recipe,
                              OnRecipeDeleteListener listener) {
        super(context);
        mContext = context;
        mRecipe = recipe;
        mListener = listener;

        mHandler = new Handler(Looper.getMainLooper());
        mDiaLoading = new DialogLoading(context);

        setProperty();
        setViews();
    }

    public void setProperty() {
        mBinding = DialogConfirmBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void setViews() {
        mBinding.tvTitle.setText("Delete this recipe?");
        mBinding.tvMessage.setText("This will permanently delete this recipe.");
        mBinding.btnNegative.setText("Cancel");
        mBinding.btnPositive.setText("Delete");

        mBinding.btnNegative.setOnClickListener(v -> dismiss());
        mBinding.btnPositive.setOnClickListener(v -> deleteRecipe());
    }

    private void deleteRecipe() {
        dismiss();

        mHandler.postDelayed(() -> {
            mDiaLoading.show();

            DbUsers dbUsers = new DbUsers();
            dbUsers.deleteRecipe(mRecipe)
                    .addOnSuccessListener(s -> {
                        String privacy = mRecipe.getPrivacy().toLowerCase();

                        //Check if recipe is private, public, or hidden
                        switch (privacy) {
                            case "private":
                                DbCircle dbCircle = new DbCircle();
                                dbCircle.deleteRecipe(mRecipe.getCircleKey(), mRecipe.getKey())
                                        .addOnSuccessListener(s1 -> deleteSuccess())
                                        .addOnFailureListener(Throwable::printStackTrace);
                                break;
                            case "public":
                                DbPublicRecipes dbPublicRecipes = new DbPublicRecipes();
                                dbPublicRecipes.deleteRecipe(mRecipe, deleted -> {
                                    if (deleted) deleteSuccess();
                                });
                                break;
                            default:
                                deleteSuccess();
                        }
                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }, 200);
    }

    private void deleteSuccess() {
        mHandler.postDelayed(() -> {
            mDiaLoading.dismiss();
            mListener.onRecipeDelete();
        }, 1500);
    }

    public interface OnRecipeDeleteListener {
        void onRecipeDelete();
    }
}
