package com.nojom.ui.projects;

import android.os.Bundle;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.nojom.R;
import com.nojom.databinding.ActivityMyProjectsBinding;
import com.nojom.fragment.projects.ProjectsListFragment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyProjectsActivity extends BaseActivity {
    private ActivityMyProjectsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_projects);
        initData();
    }

    private void initData() {
        setupTabs();
        Utils.trackFirebaseEvent(this, "Project_List_Screen");
    }

    private void setupTabs() {
        setupViewPager(binding.viewpager);

        binding.segmentedGroupTab.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton button = group.findViewById(checkedId);
            if (button.getTag().equals("openjobs")) {
                binding.viewpager.setCurrentItem(0);
            } else if (button.getTag().equals("closedjobs")) {
                binding.viewpager.setCurrentItem(1);
            }
        });

        binding.viewpager.addOnPageChangeListener(pageChangeListener);
        binding.viewpager.setCurrentItem(0);
        setTab(0);
    }

    OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            binding.viewpager.setCurrentItem(i);
            setTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private void setTab(int pos) {
        if (pos == 0) {
            binding.tabOpenJob.setChecked(true);
        } else if (pos == 1) {
            binding.tabClosedJob.setChecked(true);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(ProjectsListFragment.newInstance(true), Constants.WORK_IN_PROGRESS);
        adapter.addFrag(ProjectsListFragment.newInstance(false), Constants.PAST_PROJECTS);
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitleList = new ArrayList<>();

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

    public void showHideHorizontalProgress(int visibility) {
        binding.hProgressBar.setVisibility(visibility);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null)
            redirectTab(Constants.TAB_HOME);
        else
            super.onBackPressed();
    }
}
