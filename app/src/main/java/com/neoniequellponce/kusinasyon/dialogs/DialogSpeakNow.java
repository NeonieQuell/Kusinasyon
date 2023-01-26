package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.DialogSpeakNowBinding;

public class DialogSpeakNow extends Dialog {

    private DialogSpeakNowBinding mBinding;

    public DialogSpeakNow(@NonNull Context context) {
        super(context);
        mBinding = DialogSpeakNowBinding.inflate(getLayoutInflater());
        setProperty();
    }

    public void setProperty() {
        setContentView(mBinding.getRoot());
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
    }
}
