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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.activity.ActivityShareToCircle;
import com.neoniequellponce.kusinasyon.activity.ActivityShareToPublic;
import com.neoniequellponce.kusinasyon.adapter.AdapterDialogOption;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.DialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelOption;
import com.neoniequellponce.kusinasyon.model.ModelUser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DialogShareRecipe extends Dialog implements
        AdapterDialogOption.OnOptionClickListener {

    private ModelCircle mCurrentCircle;
    private List<ModelOption> mOptionList;

    private DialogOptionBinding mBinding;
    private Context mContext;
    private Handler mHandler;

    private DialogLoading mDiaLoading;
    private DialogResult mDiaResult;

    public AdapterDialogOption mAdapter;

    public DialogShareRecipe(@NonNull Context context) {
        super(context);
        mContext = context;

        mOptionList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());

        mDiaLoading = new DialogLoading(context);

        mDiaResult = new DialogResult(context, () -> mDiaResult.dismiss());
        mDiaResult.hideTitle();

        setProperty();
        setRecyclerView();

        mBinding.tvTitle.setText("Share Recipe");
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
        mAdapter = new AdapterDialogOption(mOptionList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    public void setCurrentCircle(ModelCircle currentCircle) {
        mCurrentCircle = currentCircle;
        setModels();
    }

    private void setModels() {
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("To " + mCurrentCircle.getName(), R.drawable.ic_people);
        hashMap.put("Share Recipe to Public", R.drawable.ic_public_fill_0);

        mOptionList.clear();

        for (String key : hashMap.keySet()) {
            ModelOption option = new ModelOption(hashMap.get(key), key);
            mOptionList.add(option);
        }
    }

    @Override
    public void onOptionClick(int position) {
        dismiss();

        ModelOption option = mOptionList.get(position);
        String name = option.getName().toLowerCase();

        if (name.equals("share recipe to public")) shareToPublic();
        else {
            Intent intent = new Intent(mContext, ActivityShareToCircle.class);
            intent.putExtra(ActivityShareToCircle.KEY, mCurrentCircle.getKey());
            intent.putExtra(ActivityShareToCircle.NAME, mCurrentCircle.getName());
            mContext.startActivity(intent);
        }
    }

    private void shareToPublic() {
        mDiaLoading.show();

        DbCircle dbCircle = new DbCircle();
        dbCircle.getCircleMembers(mCurrentCircle.getKey(), memberList -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;

            //Remove current user from list
            for (ModelUser member : memberList) {
                if (user.getUid().equals(member.getUid())) {
                    memberList.remove(member);
                    break;
                }
            }

            //Get 75% of members
            long size = Math.round(memberList.size() * 0.75);
            mDiaLoading.dismiss();

            if (size >= 2) {
                if (size % 2 != 0) {
                    Intent intent = new Intent(mContext, ActivityShareToPublic.class);
                    intent.putExtra(ActivityShareToPublic.CIRCLE_KEY, mCurrentCircle.getKey());
                    mContext.startActivity(intent);
                } else {
                    String message = "Possible circle voters are " + size + "." +
                            "\nIt must be in odd number.";

                    mDiaResult.setMessage(message);
                    mHandler.postDelayed(() -> mDiaResult.show(), 200);
                }
            } else {
                mDiaResult.setMessage("Circle members (excluding you) must be at least " +
                        "2 or more for approval of public recipe.");
                
                mHandler.postDelayed(() -> mDiaResult.show(), 200);
            }
        });
    }
}
