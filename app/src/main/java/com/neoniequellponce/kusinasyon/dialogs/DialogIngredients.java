package com.neoniequellponce.kusinasyon.dialogs;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterIngredient;
import com.neoniequellponce.kusinasyon.databinding.DialogIngredientsBinding;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;
import com.neoniequellponce.kusinasyon.model.ModelIngredient;

import java.util.ArrayList;
import java.util.List;

public class DialogIngredients extends Dialog {

    private StringBuilder mBuilder;
    private List<ModelIngredient> mIngredientList;
    private HolderIngredients mHolder;

    private DialogIngredientsBinding mBinding;
    private Context mContext;

    public static AdapterIngredient sAdapterIngredient;

    public DialogIngredients(@NonNull Context context) {
        super(context);
        mBinding = DialogIngredientsBinding.inflate(getLayoutInflater());
        mContext = context;

        mBuilder = new StringBuilder();
        mHolder = HolderIngredients.getInstance();

        setProperty();

        mBinding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void setProperty() {
        setContentView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setWindowAnimations(R.style.CustomDialogAnimation);

        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.90d);
        getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, height);
    }

    public void createContent() {
        setRecyclerView();
        setSearch();
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false);

        setIngredientList();

        sAdapterIngredient = new AdapterIngredient(mIngredientList, ingredientList ->
                mHolder.setCheckedIngredientList(new ArrayList<>(ingredientList)));

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(sAdapterIngredient);

        //Hide progress indicator if content is loaded
        if (mIngredientList.size() > 0) mBinding.progressIndicator.setVisibility(View.GONE);
    }

    private void setSearch() {
        mBinding.etSearch.getRoot().clearFocus();
        mBinding.etSearch.getRoot().setText("");
        mBinding.etSearch.getRoot().setHint("Search an ingredient");
        mBinding.etSearch.getRoot().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterRecyclerView(s.toString());
            }
        });
    }

    private void filterRecyclerView(String text) {
        List<ModelIngredient> filteredIngredientList = new ArrayList<>();

        for (ModelIngredient ingredient : mIngredientList) {
            String ingredientName = ingredient.getName().toLowerCase();
            if (ingredientName.contains(text.toLowerCase())) filteredIngredientList.add(ingredient);
        }

        sAdapterIngredient.filterList(filteredIngredientList);
    }

    private void setIngredientList() {
        mIngredientList = new ArrayList<>();

        String group = String.valueOf(mBuilder);

        switch (group) {
            case "Baking Products":
                mIngredientList = mHolder.getBakingProductsList();
                break;
            case "Dairy and Eggs":
                mIngredientList = mHolder.getDairyAndEggsList();
                break;
            case "Bread and Salty Snacks":
                mIngredientList = mHolder.getBreadAndSaltySnacksList();
                break;
            case "Condiments and Relishes":
                mIngredientList = mHolder.getCondimentsAndRelishesList();
                break;
            case "Fruits":
                mIngredientList = mHolder.getFruitsList();
                break;
            case "Grains and Cereals":
                mIngredientList = mHolder.getGrainsAndCerealsList();
                break;
            case "Herbs and Spices":
                mIngredientList = mHolder.getHerbsAndSpicesList();
                break;
            case "Meats":
                mIngredientList = mHolder.getMeatsList();
                break;
            case "Oils and Fats":
                mIngredientList = mHolder.getOilsAndFatsList();
                break;
            case "Pasta":
                mIngredientList = mHolder.getPastaList();
                break;
            case "Seeds and Nuts":
                mIngredientList = mHolder.getSeedsAndNutsList();
                break;
            case "Vegetables and Greens":
                mIngredientList = mHolder.getVegetablesList();
                break;
            case "Seafoods and Seaweeds":
                mIngredientList = mHolder.getSeafoodsAndSeaweedsList();
                break;
            case "Sugar and Sugar Products":
                mIngredientList = mHolder.getSugarAndSugarProductsList();
                break;
            case "Wines, Beers, and Spirits":
                mIngredientList = mHolder.getWinesBeersAndSpiritsList();
                break;
            case "Add Ons":
                mIngredientList = mHolder.getAddOnsList();
                break;
        }
    }

    public void setGroup(String group) {
        mBuilder.delete(0, mBuilder.length());
        mBuilder.append(group);

        mBinding.tvName.setText(group);
    }
}
