package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.activity.ActivitySignIn;
import com.neoniequellponce.kusinasyon.databinding.DialogConfirmBinding;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;

public class DialogSignOut extends Dialog {

    private DialogConfirmBinding mBinding;
    private Context mContext;
    private Activity mActivity;

    public DialogSignOut(@NonNull Context context, Activity activity) {
        super(context);
        mBinding = DialogConfirmBinding.inflate(getLayoutInflater());
        mContext = context;
        mActivity = activity;

        setProperty();
        setViews();
    }

    public void setProperty() {
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void setViews() {
        mBinding.tvTitle.setText("Sign Out");
        mBinding.tvMessage.setText("Are you sure you want to sign out on this account?");
        mBinding.btnNegative.setText("No");
        mBinding.btnPositive.setText("Yes");

        mBinding.btnNegative.setOnClickListener(v -> dismiss());
        mBinding.btnPositive.setOnClickListener(v -> signOut());
    }

    private void signOut() {
        //Delete instance of the ingredient group object holder
        HolderIngredients.setInstanceToNull();

        FirebaseAuth.getInstance().signOut();
        signOutGoogleClient();

        dismiss();

        mContext.startActivity(new Intent(mActivity, ActivitySignIn.class));
        mActivity.finish();

        System.gc();
    }

    private void signOutGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient gsc = GoogleSignIn.getClient(mContext, gso);
        gsc.signOut();
    }
}
