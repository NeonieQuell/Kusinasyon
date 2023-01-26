package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemAccountOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelOption;

import java.util.List;

public class AdapterAccountOption extends RecyclerView.Adapter<AdapterAccountOption.ViewHolder> {

    private List<ModelOption> mOptionList;
    private OnAccountOptionClickListener mListener;

    public AdapterAccountOption(List<ModelOption> optionList,
                                OnAccountOptionClickListener listener) {
        mOptionList = optionList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        ItemAccountOptionBinding binding;
        binding = ItemAccountOptionBinding.inflate(inflater, parent, false);

        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelOption option = mOptionList.get(position);

        holder.mBinding.icon.setImageResource(option.getIcon());
        holder.mBinding.tvName.setText(option.getName());
    }

    @Override
    public int getItemCount() {
        return mOptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private ItemAccountOptionBinding mBinding;
        private OnAccountOptionClickListener mListener;

        public ViewHolder(@NonNull ItemAccountOptionBinding binding,
                          OnAccountOptionClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onAccountOptionClick(getAdapterPosition());
        }
    }

    public interface OnAccountOptionClickListener {
        void onAccountOptionClick(int position);
    }
}
