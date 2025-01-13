package com.nojom.ui.projects;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.databinding.ActivityMyCampaignBinding;
import com.nojom.fragment.projects.CampaignListFragment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyCampaignActivity extends BaseActivity implements View.OnClickListener {
//    private MyProjectsActivityVM myProjectsActivityVM;
    private ActivityMyCampaignBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_campaign);

        binding.txtInProgress.setOnClickListener(this);
        binding.txtPending.setOnClickListener(this);
        binding.txtHistory.setOnClickListener(this);

        setupViewPager(binding.viewpager);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_in_progress:
                binding.viewpager.setCurrentItem(0);
                binding.txtInProgress.setBackground(getResources().getDrawable(R.drawable.blue_button_bg_10));
                binding.txtPending.setBackground(null);
                binding.txtHistory.setBackground(null);
                binding.txtInProgress.setTextColor(getResources().getColor(R.color.white));
                binding.txtPending.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                binding.txtHistory.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                break;
            case R.id.txt_pending:
                binding.viewpager.setCurrentItem(1);
                binding.txtPending.setBackground(getResources().getDrawable(R.drawable.blue_button_bg_10));
                binding.txtInProgress.setBackground(null);
                binding.txtHistory.setBackground(null);
                binding.txtPending.setTextColor(getResources().getColor(R.color.white));
                binding.txtInProgress.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                binding.txtHistory.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                break;
            case R.id.txt_history:
                binding.viewpager.setCurrentItem(2);
                binding.txtHistory.setBackground(getResources().getDrawable(R.drawable.blue_button_bg_10));
                binding.txtInProgress.setBackground(null);
                binding.txtPending.setBackground(null);
                binding.txtHistory.setTextColor(getResources().getColor(R.color.white));
                binding.txtInProgress.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                binding.txtPending.setTextColor(getResources().getColor(R.color.c_3C3C4399));
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(CampaignListFragment.newInstance(0), Constants.WORK_IN_PROGRESS);
        adapter.addFrag(CampaignListFragment.newInstance(1), Constants.PAST_PROJECTS);
        adapter.addFrag(CampaignListFragment.newInstance(2), Constants.PAST_PROJECTS);
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NotNull
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
}
