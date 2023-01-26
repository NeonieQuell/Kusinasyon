package com.neoniequellponce.kusinasyon.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemCircleMemberBinding;
import com.neoniequellponce.kusinasyon.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCircleMember extends RecyclerView.Adapter<AdapterCircleMember.ViewHolder> {

    private List<ModelUser> mMemberList;
    
    private ItemCircleMemberBinding mBinding;
    private Context mContext;

    public AdapterCircleMember(Context context, List<ModelUser> circleMemberList) {
        mContext = context;
        mMemberList = circleMemberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemCircleMemberBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUser member = mMemberList.get(position);

        Uri photoUrl = Uri.parse(member.getPhotoUrl());
        Picasso.with(mContext).load(photoUrl).into(holder.mBinding.accountImg);
        holder.mBinding.tvAccountName.setText(member.getDisplayName());
    }

    @Override
    public int getItemCount() {
        return mMemberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCircleMemberBinding mBinding;

        public ViewHolder(@NonNull ItemCircleMemberBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
