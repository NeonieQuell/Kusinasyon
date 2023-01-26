package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemRecipeIngredientBinding;

import java.util.List;

public class AdapterRecipeIngredient extends
        RecyclerView.Adapter<AdapterRecipeIngredient.ViewHolder> {

    private ItemRecipeIngredientBinding mBinding;
    private List<String> mIngredientList;

    public AdapterRecipeIngredient(List<String> ingredientList) {
        mIngredientList = ingredientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemRecipeIngredientBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mBinding.tvIngredient.setText(mIngredientList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRecipeIngredientBinding mBinding;

        public ViewHolder(@NonNull ItemRecipeIngredientBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
