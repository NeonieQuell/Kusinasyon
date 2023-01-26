package com.neoniequellponce.kusinasyon.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.databinding.FragmentPublicRecipesBinding;

public class FragmentPublicRecipes extends Fragment {

    private FragmentPublicRecipesBinding mBinding;
    private AdapterViewPager mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentPublicRecipesBinding.inflate(inflater, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbar);

        setAdapter();

        mBinding.pager.setAdapter(mAdapter);
        mBinding.tabLayout.addOnTabSelectedListener(tabSelection);
        mBinding.pager.registerOnPageChangeCallback(pageChange);

        return mBinding.getRoot();
    }

    private void setAdapter() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Lifecycle lifecycle = getActivity().getLifecycle();

        mAdapter = new AdapterViewPager(fragmentManager, lifecycle);

        mAdapter.addFragment(new FragmentLuzon());
        mAdapter.addFragment(new FragmentVisayas());
        mAdapter.addFragment(new FragmentMindanao());
        mAdapter.addFragment(new FragmentMore());
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
                    if (child instanceof RecyclerView)
                        child.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
            };
}
