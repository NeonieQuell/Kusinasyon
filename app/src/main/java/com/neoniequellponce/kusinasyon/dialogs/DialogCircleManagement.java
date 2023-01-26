package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.activity.ActivityPublicRecipeApproval;
import com.neoniequellponce.kusinasyon.activity.ActivityViewCircle;
import com.neoniequellponce.kusinasyon.adapter.AdapterDialogOption;
import com.neoniequellponce.kusinasyon.databinding.DialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelOption;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DialogCircleManagement extends Dialog implements
        AdapterDialogOption.OnOptionClickListener {

    private List<ModelOption> mOptionList;
    private ModelCircle mCurrentCircle;

    private DialogOptionBinding mBinding;
    private Context mContext;
    private Handler mHandler;
    private OnCircleLeaveListener mListener;

    private Dialog mDialogLeaveCircle;

    public DialogCircleManagement(@NonNull Context context,
                                  OnCircleLeaveListener listener) {
        super(context);
        mContext = context;
        mListener = listener;

        mHandler = new Handler(Looper.getMainLooper());

        setProperty();
        setModels();
        setRecyclerView();

        mBinding.tvTitle.setText("Circle Management");
        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void setProperty() {
        mBinding = DialogOptionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setModels() {
        mOptionList = new ArrayList<>();

        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("View Circle Info", R.drawable.ic_info);
        hashMap.put("Public Recipes for Approval", R.drawable.ic_approval);
        hashMap.put("Leave Current Circle", R.drawable.ic_logout);

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

    public void setCurrentCircle(ModelCircle currentCircle) {
        mCurrentCircle = currentCircle;
    }

    @Override
    public void onOptionClick(int position) {
        dismiss();

        ModelOption option = mOptionList.get(position);
        String name = option.getName().toLowerCase();

        switch (name) {
            case "view circle info":
                Intent viewCircleInfoIntent = new Intent(mContext, ActivityViewCircle.class);
                viewCircleInfoIntent.putExtra(ActivityViewCircle.CIRCLE, mCurrentCircle);
                mContext.startActivity(viewCircleInfoIntent);
                break;
            case "public recipes for approval":
                Intent publicRecForAppIntent = new Intent(mContext, ActivityPublicRecipeApproval.class);
                mContext.startActivity(publicRecForAppIntent);
                break;
            default:
                leaveCircle();
        }
    }

    private void leaveCircle() {
        mDialogLeaveCircle = new DialogLeaveCircle(mContext, mCurrentCircle.getKey(),
                mCurrentCircle.getAdmin(), () -> {
            mListener.onCircleLeave();
            mDialogLeaveCircle.dismiss();
        });

        mHandler.postDelayed(() -> mDialogLeaveCircle.show(), 200);
    }

    public interface OnCircleLeaveListener {
        void onCircleLeave();
    }
}
