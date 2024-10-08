package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.databinding.ActivityMyGigsBinding;
import com.nojom.databinding.DialogAddGigBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyGigsActivity extends BaseActivity {
    private ActivityMyGigsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_gigs);

        initData();

        Utils.trackFirebaseEvent(this, "My_Gig_Screen");
    }

    private void initData() {
        setupViewPager();
        setTab(0);
        binding.viewpager.setCurrentItem(0);
        binding.viewpager.addOnPageChangeListener(pageChangeListener);
        binding.segmentedGroupTab.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.imgAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MyGigsActivity.this, CreateCustomGigsActivityCopy.class);
            startActivityForResult(intent, 1212);
        });
    }

    private boolean isSelectStandard = true;

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton button = group.findViewById(checkedId);
            if (button.getTag().equals("Active")) {
                binding.viewpager.setCurrentItem(0);
            } else if (button.getTag().equals("Draft")) {
                binding.viewpager.setCurrentItem(1);
            } else if (button.getTag().equals("Paused")) {
                binding.viewpager.setCurrentItem(2);
            }
        }
    };

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            binding.viewpager.setCurrentItem(position);
            setTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setTab(int pos) {
        if (pos == 0) {
            binding.tabActive.setChecked(true);
        } else if (pos == 1) {
            binding.tabDraft.setChecked(true);
        } else if (pos == 2) {
            binding.tabPaused.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ActiveGigsFragment.newInstance(1), getString(R.string.active));
        adapter.addFrag(ActiveGigsFragment.newInstance(2), getString(R.string.draft));
        adapter.addFrag(ActiveGigsFragment.newInstance(3), getString(R.string.paused));
        binding.viewpager.setPageMargin(20);
        binding.viewpager.setAdapter(adapter);

        binding.viewpager.setOffscreenPageLimit(3);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
