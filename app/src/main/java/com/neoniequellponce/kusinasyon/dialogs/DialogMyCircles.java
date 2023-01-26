package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterCircle;
import com.neoniequellponce.kusinasyon.databinding.DialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelCircle;

import java.util.List;

public class DialogMyCircles extends Dialog {

    private List<ModelCircle> mCircleList;

    private DialogOptionBinding mBinding;
    private Context mContext;
    private OnCurrentCircleGetListener mListener;

    public AdapterCircle mAdapter;

    public DialogMyCircles(@NonNull Context context, List<ModelCircle> circleList,
                           OnCurrentCircleGetListener listener) {
        super(context);
        mContext = context;
        mCircleList = circleList;
        mListener = listener;

        setProperty();
        setRecyclerView();

        mBinding.tvTitle.setText("My Circles");
        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void setProperty() {
        mBinding = DialogOptionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mAdapter = new AdapterCircle(mCircleList, position ->
                mListener.getCurrentCircle(mCircleList.get(position)));

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public interface OnCurrentCircleGetListener {
        void getCurrentCircle(ModelCircle currentCircle);
    }
}
