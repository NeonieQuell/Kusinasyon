package com.neoniequellponce.kusinasyon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemRecipeBinding;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterRecipe extends RecyclerView.Adapter<AdapterRecipe.ViewHolder> {

    private List<ModelRecipe> mRecipeList;

    private ItemRecipeBinding mBinding;
    private Context mContext;
    private OnRecipeClickListener mListener;

    public AdapterRecipe(Context context, List<ModelRecipe> recipeList,
                         OnRecipeClickListener listener) {
        mContext = context;
        mRecipeList = recipeList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemRecipeBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecipe.ViewHolder holder, int position) {
        ModelRecipe recipe = mRecipeList.get(position);

        Picasso.with(mContext).load(recipe.getImageUrl()).into(holder.mBinding.recipeImg);
        holder.mBinding.tvName.setText(recipe.getName());
        holder.mBinding.tvDescription.setText(recipe.getDescription());
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemRecipeBinding mBinding;
        private OnRecipeClickListener mListener;

        public ViewHolder(@NonNull ItemRecipeBinding binding, OnRecipeClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onRecipeClick(getAdapterPosition());
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(int position);
    }
}
