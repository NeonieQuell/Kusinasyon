package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.DialogConfirmBinding;

import java.util.Objects;

public class DialogLeaveCircle extends Dialog {

    private String mCircleKey;
    private String mAdminUid;

    private DialogConfirmBinding mBinding;
    private OnCircleLeaveListener mListener;

    private Dialog mDiaLoading;

    public DialogLeaveCircle(@NonNull Context context, String circleKey, String adminUid,
                             OnCircleLeaveListener listener) {
        super(context);
        mListener = listener;
        mCircleKey = circleKey;
        mAdminUid = adminUid;

        mDiaLoading = new DialogLoading(context);

        setProperty();
        setViews();
    }

    private void setProperty() {
        mBinding = DialogConfirmBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void setViews() {
        mBinding.tvTitle.setText("Leave this circle?");
        mBinding.tvMessage.setText("Are you sure you want to leave the selected circle?");
        mBinding.btnNegative.setText("No");
        mBinding.btnPositive.setText("Yes");

        mBinding.btnNegative.setOnClickListener(v -> dismiss());
        mBinding.btnPositive.setOnClickListener(v -> leaveCircle());
    }

    private void leaveCircle() {
        dismiss();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            mDiaLoading.show();

            DbCircle dao = new DbCircle();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            /*Check if user is circle admin
             *If true, delete the circle when the user leaves
             *Else, remove only the user from the circle*/
            if (Objects.equals(auth.getUid(), mAdminUid)) {
                dao.deleteCircle(mCircleKey)
                        .addOnSuccessListener(s -> handler.postDelayed(() -> {
                            mDiaLoading.dismiss();
                            mListener.onCircleLeave();
                        }, 1500))
                        .addOnFailureListener(e -> handler.postDelayed(() -> {
                            mDiaLoading.dismiss();
                            mListener.onCircleLeave();
                            e.printStackTrace();
                        }, 1500));
            } else {
                dao.leaveCircle(mCircleKey, auth.getUid())
                        .addOnSuccessListener(s -> handler.postDelayed(() -> {
                            mDiaLoading.dismiss();
                            mListener.onCircleLeave();
                        }, 1500))
                        .addOnFailureListener(e -> handler.postDelayed(() -> {
                            mDiaLoading.dismiss();
                            mListener.onCircleLeave();
                            e.printStackTrace();
                        }, 1500));
            }
        }, 200);
    }

    public interface OnCircleLeaveListener {
        void onCircleLeave();
    }
}
