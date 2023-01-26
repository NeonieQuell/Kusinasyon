package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemRecipeInstructionBinding;

import java.util.List;

public class AdapterRecipeInstruction extends
        RecyclerView.Adapter<AdapterRecipeInstruction.ViewHolder> {

    private int mCounter = 1;

    private ItemRecipeInstructionBinding mBinding;
    private List<String> mInstructionList;

    public AdapterRecipeInstruction(List<String> instructionList) {
        mInstructionList = instructionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemRecipeInstructionBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String instruction = mInstructionList.get(position);

        if (mCounter <= mInstructionList.size()) {
            String counter = String.valueOf(mCounter);
            holder.mBinding.tvCounter.setText(counter);
            mCounter++;
        }

        holder.mBinding.tvInstruction.setText(instruction);
    }

    @Override
    public int getItemCount() {
        return mInstructionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRecipeInstructionBinding mBinding;

        public ViewHolder(@NonNull ItemRecipeInstructionBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
