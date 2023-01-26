package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.ItemDialogOptionBinding;
import com.neoniequellponce.kusinasyon.model.ModelCircle;

import java.util.List;

public class AdapterCircle extends RecyclerView.Adapter<AdapterCircle.ViewHolder> {

    private List<ModelCircle> mCircleList;
    private OnCircleClickListener mListener;

    public AdapterCircle(List<ModelCircle> circleList, OnCircleClickListener listener) {
        mCircleList = circleList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDialogOptionBinding binding = ItemDialogOptionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelCircle circle = mCircleList.get(position);

        holder.mBinding.icon.setImageResource(R.drawable.ic_people);
        holder.mBinding.tvName.setText(circle.getName());
    }

    @Override
    public int getItemCount() {
        return mCircleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemDialogOptionBinding mBinding;
        private OnCircleClickListener mListener;

        public ViewHolder(@NonNull ItemDialogOptionBinding binding, OnCircleClickListener listener) {
            super(binding.getRoot());
            mBinding = binding;
            mListener = listener;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onCircleClick(getAdapterPosition());
        }
    }

    public interface OnCircleClickListener {
        void onCircleClick(int position);
    }
}
