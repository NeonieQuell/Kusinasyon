package com.neoniequellponce.kusinasyon.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterIngredientGroup;
import com.neoniequellponce.kusinasyon.database.DbIngredients;
import com.neoniequellponce.kusinasyon.databinding.FragmentHomeBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogIngredients;
import com.neoniequellponce.kusinasyon.holder.HolderIngredients;
import com.neoniequellponce.kusinasyon.model.ModelIngredient;
import com.neoniequellponce.kusinasyon.model.ModelOption;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FragmentHome extends Fragment implements
        AdapterIngredientGroup.OnIngredientGroupClickListener {

    private List<ModelOption> mIngredGroupList;

    private FragmentHomeBinding mBinding;
    private Context mContext;

    private OnScrollDirectionListener mListener;

    private DialogIngredients mDiaIngred;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Avoid recreation of objects every time the fragment is recreated
        if (mIngredGroupList == null) {
            mIngredGroupList = new ArrayList<>();
            getIngredients();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mContext = mBinding.getRoot().getContext();

        mDiaIngred = new DialogIngredients(mContext);

        setHeaderSpan();
        checkViewScrollDirection();
        setModelsIngredGroup();
        setRecyclerView();

        return mBinding.getRoot();
    }

    private void getIngredients() {
        StringBuilder builder = new StringBuilder();
        DbIngredients dbIngredients = new DbIngredients();
        HolderIngredients holder = HolderIngredients.getInstance();

        dbIngredients.getIngredients(ingredientGroupList -> {
            for (int i = 0; i < ingredientGroupList.size(); i++) {
                builder.delete(0, builder.length());
                builder.append(ingredientGroupList.get(i).getName());

                String group = String.valueOf(builder);

                switch (group) {
                    case "Baking Products":
                        ArrayList<ModelIngredient> bakingProductsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            bakingProductsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setBakingProductsList(bakingProductsList);
                        break;
                    case "Dairy and Eggs":
                        ArrayList<ModelIngredient> dairyAndEggsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            dairyAndEggsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setDairyAndEggsList(dairyAndEggsList);
                        break;
                    case "Bread and Salty Snacks":
                        ArrayList<ModelIngredient> breadAndSaltySnacks = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            breadAndSaltySnacks.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setBreadAndSaltySnacksList(breadAndSaltySnacks);
                        break;
                    case "Condiments and Relishes":
                        ArrayList<ModelIngredient> condimentsAndRelishes = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            condimentsAndRelishes.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setCondimentsAndRelishesList(condimentsAndRelishes);
                        break;
                    case "Fruits":
                        ArrayList<ModelIngredient> fruitsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            fruitsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setFruitsList(fruitsList);
                        break;
                    case "Grains and Cereals":
                        ArrayList<ModelIngredient> grainsAndCereals = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            grainsAndCereals.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setGrainsAndCerealsList(grainsAndCereals);
                        break;
                    case "Herbs and Spices":
                        ArrayList<ModelIngredient> herbsAndSpices = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            herbsAndSpices.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setHerbsAndSpicesList(herbsAndSpices);
                        break;
                    case "Meats":
                        ArrayList<ModelIngredient> meatsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            meatsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setMeatsList(meatsList);
                        break;
                    case "Oils and Fats":
                        ArrayList<ModelIngredient> oilsAndFats = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            oilsAndFats.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setOilsAndFatsList(oilsAndFats);
                        break;
                    case "Pasta":
                        ArrayList<ModelIngredient> pastaList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            pastaList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setPastaList(pastaList);
                        break;
                    case "Seeds and Nuts":
                        ArrayList<ModelIngredient> seedsAndNuts = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            seedsAndNuts.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setSeedsAndNutsList(seedsAndNuts);
                        break;
                    case "Vegetables and Greens":
                        ArrayList<ModelIngredient> vegetablesAndGreensList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            vegetablesAndGreensList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setVegetablesAndGreensList(vegetablesAndGreensList);
                        break;
                    case "Seafoods and Seaweeds":
                        ArrayList<ModelIngredient> seafoodsAndSeaweedsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            seafoodsAndSeaweedsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setSeafoodsAndSeaweedsList(seafoodsAndSeaweedsList);
                        break;
                    case "Sugar and Sugar Products":
                        ArrayList<ModelIngredient> sugarAndSugarProducts = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            sugarAndSugarProducts.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setSugarAndSugarProductsList(sugarAndSugarProducts);
                        break;
                    case "Wines, Beers, and Spirits":
                        ArrayList<ModelIngredient> winesBeersAndSpiritsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            winesBeersAndSpiritsList.add(new ModelIngredient(ingredientGroupList
                                    .get(i).getName(), member));
                        }
                        holder.setWinesBeersAndSpiritsList(winesBeersAndSpiritsList);
                        break;
                    case "Add Ons":
                        ArrayList<ModelIngredient> addOnsList = new ArrayList<>();
                        for (String member : ingredientGroupList.get(i).getMembers()) {
                            addOnsList.add(new ModelIngredient(ingredientGroupList.get(i).getName(),
                                    member));
                        }
                        holder.setAddOnsList(addOnsList);
                }
            }
        });
    }

    private void setHeaderSpan() {
        Spannable spanTxt = new SpannableString(mBinding.tvHeader.getText());
        ForegroundColorSpan span = new ForegroundColorSpan(
                ContextCompat.getColor(mBinding.getRoot()
                        .getContext(), R.color.magic_potion_500)
        );

        spanTxt.setSpan(span, 18, spanTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvHeader.setText(spanTxt);
    }

    private void checkViewScrollDirection() {
        mBinding.nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) mListener.onScrollDirection("up");
                    else mListener.onScrollDirection("down");
                });
    }

    private void setRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2,
                GridLayoutManager.VERTICAL, false);

        AdapterIngredientGroup adapter = new AdapterIngredientGroup(mIngredGroupList, this);

        mBinding.recyclerView.setHasFixedSize(false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(adapter);
    }

    private void setModelsIngredGroup() {
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();

        hashMap.put("Baking Products", R.drawable.img_baking_products);
        hashMap.put("Dairy and Eggs", R.drawable.img_dairy_and_eggs);
        hashMap.put("Bread and Salty Snacks", R.drawable.img_bread_and_salty_snacks);
        hashMap.put("Condiments and Relishes", R.drawable.img_condiments_and_relishes);
        hashMap.put("Fruits", R.drawable.img_fruits);
        hashMap.put("Grains and Cereals", R.drawable.img_grains_and_cereals);
        hashMap.put("Herbs and Spices", R.drawable.img_herbs_and_spices);
        hashMap.put("Meats", R.drawable.img_meats);
        hashMap.put("Oils and Fats", R.drawable.img_oils_and_fats);
        hashMap.put("Pasta", R.drawable.img_pasta);
        hashMap.put("Seeds and Nuts", R.drawable.img_seeds_and_nuts);
        hashMap.put("Vegetables and Greens", R.drawable.img_vegetables);
        hashMap.put("Seafoods and Seaweeds", R.drawable.img_seafoods_and_seaweeds);
        hashMap.put("Sugar and Sugar Products", R.drawable.img_sugar_and_sugar_products);
        hashMap.put("Wines, Beers, and Spirits", R.drawable.img_wines_beers_and_spirits);
        hashMap.put("Add Ons", R.drawable.img_add_ons);

        //Avoid duplication of categories
        mIngredGroupList.clear();

        for (String key : hashMap.keySet()) {
            ModelOption option = new ModelOption(hashMap.get(key), key);
            mIngredGroupList.add(option);
        }
    }

    @Override
    public void onIngredientGroupClick(int position) {
        ModelOption ingredientGroup = mIngredGroupList.get(position);

        mDiaIngred.setGroup(ingredientGroup.getName());
        mDiaIngred.createContent();
        DialogIngredients.sAdapterIngredient.notifyDataSetChanged();
        mDiaIngred.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            mListener = (OnScrollDirectionListener) activity;
        } catch (RuntimeException e) {
            throw new RuntimeException(activity + " methods must be implemented.");
        }
    }

    public interface OnScrollDirectionListener {
        void onScrollDirection(String direction);
    }
}
