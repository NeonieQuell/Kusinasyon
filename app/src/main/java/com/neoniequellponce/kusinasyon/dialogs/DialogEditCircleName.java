package com.neoniequellponce.kusinasyon.dialogs;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.DialogEditCircleNameBinding;

public class DialogEditCircleName extends Dialog {

    private String mCircleKey;

    private DialogEditCircleNameBinding mBinding;
    private Context mContext;
    private OnCircleEditNameListener mListener;

    public DialogEditCircleName(@NonNull Context context, String circleKey,
                                String oldName, OnCircleEditNameListener listener) {
        super(context);
        mContext = context;
        mCircleKey = circleKey;
        mListener = listener;

        setProperty();

        mBinding.etCircleName.setText(oldName);
        mBinding.btnNegative.setOnClickListener(v -> dismiss());
        mBinding.btnPositive.setOnClickListener(v -> save());
    }

    public void setProperty() {
        mBinding = DialogEditCircleNameBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void save() {
        InputMethodManager imm = (InputMethodManager)
                mContext.getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        Handler handler = new Handler(Looper.getMainLooper());
        Dialog diaLoading = new DialogLoading(mContext);

        dismiss();

        handler.postDelayed(() -> {
            diaLoading.show();

            String newName = String.valueOf(mBinding.etCircleName.getText());

            DbCircle dbCircle = new DbCircle();
            dbCircle.updateCircleName(mCircleKey, newName)
                    .addOnSuccessListener(s -> handler.postDelayed(() -> {
                        diaLoading.dismiss();
                        mListener.onCircleEditName(newName);
                    }, 1500))
                    .addOnFailureListener(e -> handler.postDelayed(() -> {
                        diaLoading.dismiss();
                        e.printStackTrace();
                    }, 1500));
        }, 200);
    }

    public interface OnCircleEditNameListener {
        void onCircleEditName(String newName);
    }
}
