package com.neoniequellponce.kusinasyon.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.databinding.ActivityMyRecipesBinding;
import com.neoniequellponce.kusinasyon.fragment.FragmentPrivatelyShared;
import com.neoniequellponce.kusinasyon.fragment.FragmentPubliclyShared;

public class ActivityMyRecipes extends AppCompatActivity {

    private ActivityMyRecipesBinding mBinding;
    private AdapterViewPager mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMyRecipesBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setActionBar();
        setAdapter();

        mBinding.pager.setAdapter(mAdapter);
        mBinding.tabLayout.addOnTabSelectedListener(tabSelection);
        mBinding.pager.registerOnPageChangeCallback(pageChange);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Recipes");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(new FragmentPubliclyShared());
        mAdapter.addFragment(new FragmentPrivatelyShared());
    }

    private TabLayout.OnTabSelectedListener tabSelection = new
            TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mBinding.pager.setCurrentItem(tab.getPosition());
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
                    mBinding.tabLayout.getTabAt(position).select();

                    // Remove overscroll on horizontal swipe in viewpager
                    View child = mBinding.pager.getChildAt(position);
                    if (child instanceof RecyclerView) {
                        child.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                }
            };
}
