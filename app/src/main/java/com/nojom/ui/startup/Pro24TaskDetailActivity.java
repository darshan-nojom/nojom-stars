package com.nojom.ui.startup;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.adapter.Pro24TaskPagerAdapter;
import com.nojom.databinding.ActivityPro24TaskDetailBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.WorkProfileActivity;
import com.nojom.util.Preferences;

public class Pro24TaskDetailActivity extends BaseActivity {
    private ActivityPro24TaskDetailBinding binding;
    private Pro24TaskPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pro24_task_detail);
        binding.setActvity(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() == 0) {
                onBackPressed();
            } else {
                int pos = binding.viewPager.getCurrentItem();
                binding.viewPager.setCurrentItem(pos - 1);
            }
        });
        binding.toolbar.tvCancel.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.toolbar.progress.setVisibility(View.GONE);

        adapter = new Pro24TaskPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.indicator.setViewPager(binding.viewPager);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == adapter.getCount() - 1) {
                    binding.btnNext.setVisibility(View.GONE);
                    if (Preferences.getProfileData(Pro24TaskDetailActivity.this).percentage != null && Preferences.getProfileData(Pro24TaskDetailActivity.this).percentage.totalPercentage == 100) {
                        binding.btnCompleteProfile.setText(getString(R.string.check_your_profile));
                    } else {
                        binding.btnCompleteProfile.setText(getString(R.string.complete_your_profile));
                    }

                    binding.btnCompleteProfile.setVisibility(View.VISIBLE);
                } else {
                    binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnCompleteProfile.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void onClickNext() {
        int pos = binding.viewPager.getCurrentItem();
        binding.viewPager.setCurrentItem(pos + 1);
    }

    public void onClickCompleteProfile() {
        redirectActivity(WorkProfileActivity.class);
        finish();
    }
}
