package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.DialogVisibilityBinding;

public class DialogVisibility extends Dialog {

    private DialogVisibilityBinding mBinding;
    private Context mContext;
    private OnVisibilityChangeListener mChangeListener;

    public DialogVisibility(@NonNull Context context,
                            OnVisibilityChangeListener changeListener,
                            OnResultClickListener clickListener) {
        super(context);
        mContext = context;
        mChangeListener = changeListener;

        setProperty();

        mBinding.switchMaterial.setOnClickListener(v -> changeVisibility());
        mBinding.btn.setOnClickListener(v -> clickListener.onResultClick());
    }

    private void setProperty() {
        mBinding = DialogVisibilityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void changeVisibility() {
        mChangeListener.onVisibilityChange(mBinding.switchMaterial.isChecked() ?
                "hidden" : "visible");
    }

    public void setVisibility(String visibility) {
        mBinding.switchMaterial.setChecked(!visibility.equalsIgnoreCase("visible"));
    }

    public interface OnVisibilityChangeListener {
        void onVisibilityChange(String visibility);
    }

    public interface OnResultClickListener {
        void onResultClick();
    }
}
