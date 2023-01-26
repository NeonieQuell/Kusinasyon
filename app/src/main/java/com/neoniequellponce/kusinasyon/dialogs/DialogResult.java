package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.DialogResultBinding;

public class DialogResult extends Dialog {

    private DialogResultBinding mBinding;
    private OnResultClickListener mListener;

    public DialogResult(@NonNull Context context, OnResultClickListener listener) {
        super(context);
        mListener = listener;

        setProperty();

        mBinding.btnOk.setOnClickListener(v -> mListener.onResultClick());
    }

    private void setProperty() {
        mBinding = DialogResultBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        setCancelable(false);
    }

    public void setContent(String title, String message) {
        mBinding.tvTitle.setText(title);
        mBinding.tvMessage.setText(message);
    }

    public void setTitle(String title) {
        mBinding.tvTitle.setText(title);
    }

    public void setMessage(String message) {
        mBinding.tvMessage.setText(message);
    }

    public void hideTitle() {
        mBinding.tvTitle.setVisibility(View.GONE);
    }

    public void showTitle() {
        mBinding.tvTitle.setVisibility(View.VISIBLE);
    }

    public interface OnResultClickListener {
        void onResultClick();
    }
}
