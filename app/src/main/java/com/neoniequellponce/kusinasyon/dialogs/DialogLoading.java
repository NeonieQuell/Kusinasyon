package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;

public class DialogLoading extends Dialog {

    public DialogLoading(@NonNull Context context) {
        super(context);
        setProperty();
    }

    private void setProperty() {
        setContentView(R.layout.dialog_loading);
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }
}
