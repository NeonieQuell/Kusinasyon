package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemIngredientBinding;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;
import com.neoniequellponce.kusinasyon.model.ModelIngredient;

import java.util.List;

public class AdapterIngredient extends RecyclerView.Adapter<AdapterIngredient.ViewHolder> {

    private List<ModelIngredient> mIngredientList;
    private HolderIngredients mHolder;

    private ItemIngredientBinding mBinding;
    private OnQuantityChangeListener mListener;

    public AdapterIngredient(List<ModelIngredient> ingredientList,
                             OnQuantityChangeListener listener) {
        mIngredientList = ingredientList;
        mListener = listener;

        mHolder = HolderIngredients.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemIngredientBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelIngredient ingredient = mIngredientList.get(position);
        holder.mBinding.checkBox.setText(ingredient.getName());

        //Check if ingredient ingredient is checked
        holder.mBinding.checkBox.setOnCheckedChangeListener(null);
        holder.mBinding.checkBox.setChecked(ingredient.isChecked());
        holder.mBinding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> 
                mIngredientList.get(holder.getAdapterPosition()).setChecked(isChecked));

        //If ingredient is checked, add to checkedIngredientList
        holder.mBinding.checkBox.setOnClickListener(null);
        holder.mBinding.checkBox.setOnClickListener(v -> {
            if (ingredient.isChecked()) mHolder.getCheckedIngredientList().add(ingredient);
            else mHolder.getCheckedIngredientList().remove(ingredient);

            mListener.onQuantityChange(mHolder.getCheckedIngredientList());
        });
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    public void filterList(List<ModelIngredient> filteredList) {
        mIngredientList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemIngredientBinding mBinding;

        public ViewHolder(@NonNull ItemIngredientBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(List<ModelIngredient> ingredientList);
    }
}
