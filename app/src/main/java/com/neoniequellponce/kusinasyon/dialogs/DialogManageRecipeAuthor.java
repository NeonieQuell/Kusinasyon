package com.neoniequellponce.kusinasyon.dialogs;

import android.app.Activity;
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
import com.neoniequellponce.kusinasyon.activity.ActivityEditRecipePrivate;
import com.neoniequellponce.kusinasyon.activity.ActivityEditRecipePublic;
import com.neoniequellponce.kusinasyon.activity.ActivityViewRecipe;
import com.neoniequellponce.kusinasyon.adapter.AdapterDialogOption;
import com.neoniequellponce.kusinasyon.databinding.DialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelOption;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DialogManageRecipeAuthor extends Dialog implements
        AdapterDialogOption.OnOptionClickListener {

    private ModelRecipe mRecipe;
    private List<ModelOption> mOptionList;

    private DialogOptionBinding mBinding;
    private Context mContext;
    private Activity mActivity;
    private OnRecipeDeleteListener mListener;

    private Dialog mDiaDeleteRecipe;

    public DialogManageRecipeAuthor(@NonNull Context context, Activity activity,
                                    OnRecipeDeleteListener listener) {
        super(context);
        mContext = context;
        mActivity = activity;
        mListener = listener;

        setProperty();
        setModels();
        setRecyclerView();

        mBinding.tvTitle.setText("Manage Recipe");
        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    public void setProperty() {
        mBinding = DialogOptionBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setModels() {
        mOptionList = new ArrayList<>();

        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("Edit Contents", R.drawable.ic_edit_note);
        hashMap.put("Delete Recipe", R.drawable.ic_delete);

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

    public void setRecipe(ModelRecipe recipe) {
        mRecipe = recipe;
    }

    @Override
    public void onOptionClick(int position) {
        dismiss();

        ModelOption option = mOptionList.get(position);
        String name = option.getName().toLowerCase();

        /*Case 1: edit contents
         *Case 2: delete recipe*/
        if ("edit contents".equals(name)) {
            Intent intent;
            String privacy = mRecipe.getPrivacy().toLowerCase();

            if ("private".equals(privacy)) {
                intent = new Intent(mActivity, ActivityEditRecipePrivate.class);
                intent.putExtra(ActivityEditRecipePrivate.RECIPE, mRecipe);
            } else {
                intent = new Intent(mActivity, ActivityEditRecipePublic.class);
                intent.putExtra(ActivityEditRecipePublic.RECIPE, mRecipe);
            }

            mActivity.startActivityForResult(intent, ActivityViewRecipe.RQ_EDIT_CONTENT);
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                mDiaDeleteRecipe = new DialogDeleteRecipe(mContext, mRecipe, () -> {
                    mDiaDeleteRecipe.dismiss();
                    mListener.onRecipeDelete();
                });
                mDiaDeleteRecipe.show();
            }, 200);
        }
    }

    public interface OnRecipeDeleteListener {
        void onRecipeDelete();
    }
}
