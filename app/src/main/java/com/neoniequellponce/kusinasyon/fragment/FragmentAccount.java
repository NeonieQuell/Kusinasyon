package com.neoniequellponce.kusinasyon.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.activity.ActivityCreateCircle;
import com.neoniequellponce.kusinasyon.activity.ActivityJoinCircle;
import com.neoniequellponce.kusinasyon.activity.ActivityMyRecipes;
import com.neoniequellponce.kusinasyon.adapter.AdapterAccountOption;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.FragmentAccountBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogSignOut;
import com.neoniequellponce.kusinasyon.model.ModelOption;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FragmentAccount extends Fragment implements
        AdapterAccountOption.OnAccountOptionClickListener {

    private List<ModelOption> mOptionList;

    private FragmentAccountBinding mBinding;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentAccountBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mOptionList = new ArrayList<>();

        setAccountDetails();
        setModels();
        setRecyclerView();

        return mBinding.getRoot();
    }

    private void setAccountDetails() {
        DbUsers dbUsers = new DbUsers();
        dbUsers.getUserInfo(user -> {
            if (user != null) {
                mBinding.progressIndicator.setVisibility(View.GONE);
                Picasso.with(mContext).load(user.getPhotoUrl()).into(mBinding.accountImg);
                mBinding.tvAccountName.setText(user.getDisplayName());
                mBinding.tvAccountEmail.setText(user.getEmail());
            }
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        AdapterAccountOption adapter = new AdapterAccountOption(mOptionList, this);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }

    private void setModels() {
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("Create a Circle", R.drawable.ic_add_circle);
        hashMap.put("Join a Circle", R.drawable.ic_connect_without_contact);
        hashMap.put("My Recipes", R.drawable.ic_receipt_long);
        hashMap.put("Sign Out", R.drawable.ic_logout);

        for (String key : hashMap.keySet()) {
            ModelOption option = new ModelOption(hashMap.get(key), key);
            mOptionList.add(option);
        }
    }

    @Override
    public void onAccountOptionClick(int position) {
        ModelOption option = mOptionList.get(position);
        String name = option.getName().toLowerCase();
        
        switch (name) {
            case "create a circle":
                startActivity(new Intent(getActivity(), ActivityCreateCircle.class));
                break;
            case "join a circle":
                startActivity(new Intent(getActivity(), ActivityJoinCircle.class));
                break;
            case "my recipes":
                startActivity(new Intent(getActivity(), ActivityMyRecipes.class));
                break;
            case "sign out":
                DialogSignOut diaSignOut = new DialogSignOut(mContext, getActivity());
                diaSignOut.show();
                break;
        }
    }
}
