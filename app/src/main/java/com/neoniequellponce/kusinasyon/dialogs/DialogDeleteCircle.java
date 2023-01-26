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
import com.neoniequellponce.kusinasyon.databinding.DialogConfirmBinding;

public class DialogDeleteCircle extends Dialog {

    private String mCircleKey;

    private DialogConfirmBinding mBinding;
    private Context mContext;
    private OnCircleDeleteListener mListener;

    public DialogDeleteCircle(@NonNull Context context, String circleKey,
                              OnCircleDeleteListener listener) {
        super(context);
        mContext = context;
        mListener = listener;
        mCircleKey = circleKey;

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
        mBinding.tvTitle.setText("Delete this circle?");
        mBinding.tvMessage.setText("This action will permanently delete the circle.");
        mBinding.btnNegative.setText("Cancel");
        mBinding.btnPositive.setText("Delete");

        mBinding.btnNegative.setOnClickListener(v -> dismiss());
        mBinding.btnPositive.setOnClickListener(v -> deleteCircle());
    }

    private void deleteCircle() {
        dismiss();

        Dialog diaLoading = new DialogLoading(mContext);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            diaLoading.show();

            DbCircle dbCircle = new DbCircle();
            dbCircle.deleteCircle(mCircleKey)
                    .addOnSuccessListener(s -> handler.postDelayed(() -> {
                        diaLoading.dismiss();
                        mListener.onCircleDelete();
                    }, 1500))
                    .addOnFailureListener(e -> handler.postDelayed(() -> {
                        diaLoading.dismiss();
                        e.printStackTrace();
                    }, 1500));
        }, 200);
    }

    public interface OnCircleDeleteListener {
        void onCircleDelete();
    }
}
