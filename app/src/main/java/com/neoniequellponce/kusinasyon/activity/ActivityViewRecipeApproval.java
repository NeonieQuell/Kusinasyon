package com.neoniequellponce.kusinasyon.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.databinding.ActivityViewRecipeApprovalBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogManageRecipeVoter;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeDetails;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeIngredients;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeInstructions;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityViewRecipeApproval extends AppCompatActivity implements
        DialogManageRecipeVoter.OnRecipeVoteListener {

    //region Fields
    public static final String RECIPE = "recipe";

    private ModelRecipe mRecipe;

    private ActivityViewRecipeApprovalBinding mBinding;
    private Handler mHandler;

    private DialogManageRecipeVoter mDiaManage;
    private DialogResult mDiaResult;

    private AdapterViewPager mAdapter;
    private FragmentRecipeDetails mFragDetails;
    private FragmentRecipeIngredients mFragIngredients;
    private FragmentRecipeInstructions mFragInstructions;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityViewRecipeApprovalBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mRecipe = getIntent().getParcelableExtra(RECIPE);
        mHandler = new Handler(Looper.getMainLooper());

        //region Methods
        setStatusBarColor();
        setBottomSheet();
        setDialogs();
        createFragments();
        setAdapter();
        setFragmentBundles();
        //endregion

        mBinding.fabBack.setOnClickListener(v -> finish());
        Picasso.with(this).load(mRecipe.getImageUrl()).into(mBinding.recipeImg);
        mBinding.btmSheet.tabLayout.addOnTabSelectedListener(tabSelection);
        mBinding.btmSheet.pager.setAdapter(mAdapter);
        mBinding.btmSheet.pager.registerOnPageChangeCallback(pageChange);
    }

    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLACK);

        View view = window.getDecorView();
        view.setSystemUiVisibility(view.getSystemUiVisibility() &
                ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setBottomSheet() {
        BottomSheetBehavior<MaterialCardView> sheetBehavior;
        sheetBehavior = BottomSheetBehavior.from(mBinding.btmSheet.cvContainer);
        sheetBehavior.setHideable(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Set bottom sheet minimum height
        int sheetBehaviourMinHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.60d);
        sheetBehavior.setPeekHeight(sheetBehaviourMinHeight, true);

        mBinding.btmSheet.tvRecipeName.setText(mRecipe.getName());
        mBinding.btmSheet.btnManage.setOnClickListener(v -> mDiaManage.show());
    }

    private void setDialogs() {
        //Dialog manage recipe
        mDiaManage = new DialogManageRecipeVoter(this, this);
        mDiaManage.setRecipe(mRecipe);

        //Dialog result
        mDiaResult = new DialogResult(this, () -> {
            mDiaResult.dismiss();
            finish();
        });
        mDiaResult.hideTitle();
    }

    private void createFragments() {
        mFragDetails = new FragmentRecipeDetails();
        mFragIngredients = new FragmentRecipeIngredients();
        mFragInstructions = new FragmentRecipeInstructions();
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(mFragDetails);
        mAdapter.addFragment(mFragIngredients);
        mAdapter.addFragment(mFragInstructions);
    }

    private void setFragmentBundles() {
        Bundle bdDetails = new Bundle();
        bdDetails.putString(FragmentRecipeDetails.DESCRIPTION, mRecipe.getDescription());
        bdDetails.putString(FragmentRecipeDetails.AUTHOR, mRecipe.getAuthor());
        bdDetails.putString(FragmentRecipeDetails.ORIGIN, mRecipe.getOrigin());

        Bundle bdIngred = new Bundle();
        bdIngred.putStringArrayList(FragmentRecipeIngredients.INGREDIENTS,
                (ArrayList<String>) mRecipe.getIngredients());

        Bundle bdInstruct = new Bundle();
        bdInstruct.putStringArrayList(FragmentRecipeInstructions.INSTRUCTIONS,
                (ArrayList<String>) mRecipe.getInstructions());

        mFragDetails.setArguments(bdDetails);
        mFragIngredients.setArguments(bdIngred);
        mFragInstructions.setArguments(bdInstruct);
    }

    @Override
    public void onRecipeVote(boolean approved) {
        mDiaManage.dismiss();

        if (approved) {
            mDiaResult.setMessage("Thanks for voting! You have approved this recipe.");
            mHandler.postDelayed(() -> mDiaResult.show(), 200);
        } else {
            mDiaResult.setMessage(("Thanks for voting! You have rejected this recipe."));
            mHandler.postDelayed(() -> mDiaResult.show(), 200);
        }
    }

    private TabLayout.OnTabSelectedListener tabSelection = new
            TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mBinding.btmSheet.pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

    private ViewPager2.OnPageChangeCallback pageChange = new
            ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mBinding.btmSheet.tabLayout.getTabAt(position).select();

                    // Remove overscroll on horizontal swipe in viewpager
                    View child = mBinding.btmSheet.pager.getChildAt(position);
                    if (child instanceof RecyclerView) {
                        child.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                }
            };
}
