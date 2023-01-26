package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemDialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelOption;

import java.util.List;

public class AdapterDialogOption extends
        RecyclerView.Adapter<AdapterDialogOption.ViewHolder> {

    private List<ModelOption> mOptionList;
    private OnOptionClickListener mListener;

    public AdapterDialogOption(List<ModelOption> optionList,
                               OnOptionClickListener listener) {
        mOptionList = optionList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        ItemDialogOptionBinding binding;
        binding = ItemDialogOptionBinding.inflate(inflater, parent, false);
        
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

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private ItemDialogOptionBinding mBinding;
        private OnOptionClickListener mListener;

        public ViewHolder(@NonNull ItemDialogOptionBinding binding,
                          OnOptionClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onOptionClick(getAdapterPosition());
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }
}
