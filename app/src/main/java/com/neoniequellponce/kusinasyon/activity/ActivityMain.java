package com.neoniequellponce.kusinasyon.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.databinding.ActivityMainBinding;
import com.neoniequellponce.kusinasyon.fragment.FragmentAccount;
import com.neoniequellponce.kusinasyon.fragment.FragmentHome;
import com.neoniequellponce.kusinasyon.fragment.FragmentPrivateRecipes;
import com.neoniequellponce.kusinasyon.fragment.FragmentPublicRecipes;
import com.neoniequellponce.kusinasyon.service.AppService;

public class ActivityMain extends AppCompatActivity implements
        FragmentHome.OnScrollDirectionListener,
        NavigationBarView.OnItemSelectedListener {

    private boolean mIsFabExtended = true;

    private ActivityMainBinding mBinding;

    private ValueAnimator mFabAnimator;
    private AdapterViewPager mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        startService(new Intent(getApplicationContext(), AppService.class));
        setAdapter();

        mBinding.pager.setUserInputEnabled(false);
        mBinding.pager.setAdapter(mAdapter);

        mBinding.fab.setOnClickListener(v -> goToIngredBag());
        mBinding.btmNavView.setOnItemSelectedListener(this);
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(new FragmentHome());
        mAdapter.addFragment(new FragmentPublicRecipes());
        mAdapter.addFragment(new FragmentPrivateRecipes());
        mAdapter.addFragment(new FragmentAccount());
    }

    @Override
    public void onScrollDirection(String direction) {
        //Collapse FAB based on Fragment Home
        if (mFabAnimator != null) {
            if (direction.equalsIgnoreCase("up") && mIsFabExtended) {
                mFabAnimator.start();
                mIsFabExtended = false;
            } else if (direction.equalsIgnoreCase("down") && !mIsFabExtended) {
                mFabAnimator.reverse();
                mIsFabExtended = true;
            }
        } else {
            int initSize = mBinding.fabTitle.getMeasuredWidth();
            mFabAnimator = ValueAnimator.ofInt(initSize, 0);
            mFabAnimator.setDuration(100);

            mFabAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.fabTitle.getLayoutParams();
                layoutParams.width = value;
                mBinding.fabTitle.requestLayout();
            });
        }
    }

    private void goToIngredBag() {
        Intent intent = new Intent(ActivityMain.this, ActivityIngredientsBag.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_act_main_btm_nav_view_home:
                mBinding.pager.setCurrentItem(0, false);
                mBinding.fab.setVisibility(View.VISIBLE);
                mBinding.btmNavView.getMenu().findItem(R.id.menu_act_main_btm_nav_view_home)
                        .setChecked(true);
                break;
            case R.id.menu_act_main_btm_nav_view_public_recipes:
                mBinding.pager.setCurrentItem(1, false);
                mBinding.fab.setVisibility(View.GONE);
                mBinding.btmNavView.getMenu().findItem(R.id.menu_act_main_btm_nav_view_public_recipes)
                        .setChecked(true);
                break;
            case R.id.menu_act_main_btm_nav_view_private_recipes:
                mBinding.pager.setCurrentItem(2, false);
                mBinding.fab.setVisibility(View.GONE);
                mBinding.btmNavView.getMenu().findItem(R.id
                        .menu_act_main_btm_nav_view_private_recipes).setChecked(true);
                break;
            case R.id.menu_act_main_btm_nav_view_account:
                mBinding.pager.setCurrentItem(3, false);
                mBinding.fab.setVisibility(View.GONE);
                mBinding.btmNavView.getMenu().findItem(R.id.menu_act_main_btm_nav_view_account)
                        .setChecked(true);
                break;
        }

        return false;
    }
}
