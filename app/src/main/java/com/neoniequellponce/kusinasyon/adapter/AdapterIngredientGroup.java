package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemIngredientsGroupBinding;
import com.neoniequellponce.kusinasyon.model.ModelOption;

import java.util.List;

public class AdapterIngredientGroup extends RecyclerView.Adapter<AdapterIngredientGroup.ViewHolder> {

    private List<ModelOption> mOptionList;

    private ItemIngredientsGroupBinding mBinding;
    private OnIngredientGroupClickListener mListener;

    public AdapterIngredientGroup(List<ModelOption> optionList,
                                  OnIngredientGroupClickListener listener) {
        mOptionList = optionList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemIngredientsGroupBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding, mListener);
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

        private ItemIngredientsGroupBinding mBinding;
        private OnIngredientGroupClickListener mListener;

        public ViewHolder(@NonNull ItemIngredientsGroupBinding binding,
                          OnIngredientGroupClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onIngredientGroupClick(getAdapterPosition());
        }
    }

    public interface OnIngredientGroupClickListener {
        void onIngredientGroupClick(int position);
    }
}
