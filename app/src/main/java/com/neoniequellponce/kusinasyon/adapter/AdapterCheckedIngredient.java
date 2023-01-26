package com.neoniequellponce.kusinasyon.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neoniequellponce.kusinasyon.databinding.ItemCheckedIngredientBinding;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;
import com.neoniequellponce.kusinasyon.model.ModelIngredient;

import java.util.List;

public class AdapterCheckedIngredient extends
        RecyclerView.Adapter<AdapterCheckedIngredient.ViewHolder> {

    private HolderIngredients mHolder;
    private List<ModelIngredient> mIngredientList;

    private ItemCheckedIngredientBinding mBinding;
    private OnCheckedIngredientDeleteListener mListener;

    public AdapterCheckedIngredient(List<ModelIngredient> ingredientList,
                                    OnCheckedIngredientDeleteListener listener) {
        mIngredientList = ingredientList;
        mListener = listener;

        mHolder = HolderIngredients.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mBinding = ItemCheckedIngredientBinding.inflate(inflater, parent, false);
        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelIngredient ingredient = mIngredientList.get(position);

        holder.mBinding.tvName.setText(ingredient.getName());
        holder.mBinding.btnRemove.setOnClickListener(v -> {
            unCheckIngredient(ingredient);
            mIngredientList.remove(ingredient);
            notifyDataSetChanged();
            mListener.onCheckedIngredientDelete(true);
        });
    }

    private void unCheckIngredient(ModelIngredient modelIngredient) {
        String group = modelIngredient.getGroup();

        switch (group) {
            case "Baking Products":
                for (ModelIngredient ingredient : mHolder.getBakingProductsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Dairy and Eggs":
                for (ModelIngredient ingredient : mHolder.getDairyAndEggsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Bread and Salty Snacks":
                for (ModelIngredient ingredient : mHolder.getBreadAndSaltySnacksList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Condiments and Relishes":
                for (ModelIngredient ingredient : mHolder.getCondimentsAndRelishesList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Fruits":
                for (ModelIngredient ingredient : mHolder.getFruitsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Grains and Cereals":
                for (ModelIngredient ingredient : mHolder.getGrainsAndCerealsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Herbs and Spices":
                for (ModelIngredient ingredient : mHolder.getHerbsAndSpicesList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Meats":
                for (ModelIngredient ingredient : mHolder.getMeatsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Oils and Fats":
                for (ModelIngredient ingredient : mHolder.getOilsAndFatsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Pasta":
                for (ModelIngredient ingredient : mHolder.getPastaList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Seeds and Nuts":
                for (ModelIngredient ingredient : mHolder.getSeedsAndNutsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Vegetables and Greens":
                for (ModelIngredient ingredient : mHolder.getVegetablesList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Seafoods and Seaweeds":
                for (ModelIngredient ingredient : mHolder.getSeafoodsAndSeaweedsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Sugar and Sugar Products":
                for (ModelIngredient ingredient : mHolder.getSugarAndSugarProductsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Wines, Beers, and Spirits":
                for (ModelIngredient ingredient : mHolder.getWinesBeersAndSpiritsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
            case "Add Ons":
                for (ModelIngredient ingredient : mHolder.getAddOnsList()) {
                    if (ingredient.getName().equals(modelIngredient.getName())) {
                        ingredient.setChecked(false);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        mHolder.getCheckedIngredientList().remove(ingredient);
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemCheckedIngredientBinding mBinding;

        public ViewHolder(@NonNull ItemCheckedIngredientBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }

    public interface OnCheckedIngredientDeleteListener {
        void onCheckedIngredientDelete(boolean deleted);
    }
}
