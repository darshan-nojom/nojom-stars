package com.nojom.ui.projects;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityCampDetailsBinding;
import com.nojom.model.CampList;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.balance.CampByIdVM;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CampaignDetailActivity2 extends BaseActivity implements View.OnClickListener {

    private ProjectDetailsActivityVM projectDetailsActivityVM;
    public CampList campList;
    private ActivityCampDetailsBinding binding;
    public int selectedTab = 0;
    private String campId;
    private CampByIdVM campByIdVM;
    private CampDetailFragment campDetailFragment;
    private CampFileFragment campFileFragment;
    private CampPayFragment campPayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camp_details);
//        projectDetailsActivityVM = new ProjectDetailsActivityVM(Task24Application.getInstance(), detailsBinding, this);
        campByIdVM = new CampByIdVM(Task24Application.getActivity(), this);

        binding.txtTabDetail.setOnClickListener(this);
        binding.txtTabStars.setOnClickListener(this);
        binding.txtTabPay.setOnClickListener(this);

        setupViewPager(binding.viewpager);

        if (getIntent() != null) {
            campList = (CampList) getIntent().getSerializableExtra(Constants.PROJECT);
            selectedTab = getIntent().getIntExtra("state", 0);

            campId = getIntent().getStringExtra("camp_id");

            if (campList == null && !TextUtils.isEmpty(campId)) {
                campList = new CampList();
                campList.campaignId = Integer.valueOf(campId);
                campByIdVM.getCampById(campList.campaignId);
            }
        }
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                setTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        binding.viewpager.setCurrentItem(0);

        binding.imgBack.setOnClickListener(view -> onBackPressed());

        campByIdVM.campListData.observe(this, campListData -> {
            campList.campaignTitle = campListData.title;
            campList.campaignBrief = campListData.brief;
            campList.campaignStatus = campListData.status;
            campList.client_profile_picture = campListData.client_profile_picture;
            campList.client_name = campListData.client_name;
            campList.star_details = campListData.star_details;
            campList.agency_fee_rate = campListData.agency_fee_rate;
            campList.tax_rate = campListData.tax_rate;
            campList.totalPrice = campListData.totalPrice;
            campList.progress = campListData.progress;

            if (campListData.attachment != null) {
                campList.campaignAttachmentUrl = campListData.attachment;
            }
            campList.campaignLaunchDate = campListData.launch_date;
            campList.timestamp = campListData.created_at;
            if (campListData.profiles != null) {
                campList.profiles = campListData.profiles;
            }
            if (campListData.socialPlatforms != null) {
                campList.socialPlatforms = campListData.socialPlatforms;
            }
            if (campListData.services != null) {
                campList.services = campListData.services;
            }
            if (campDetailFragment != null) {
                campDetailFragment.onResume();
            }
            if (campFileFragment != null) {
                campFileFragment.setData();
            }
            if (campPayFragment != null) {
                campPayFragment.getdata(campList);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        campDetailFragment = new CampDetailFragment();
        campFileFragment = new CampFileFragment();
        campPayFragment = new CampPayFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(campDetailFragment, getString(R.string.details));
        adapter.addFrag(campFileFragment, getString(R.string.files));
        adapter.addFrag(campPayFragment, getString(R.string.pay));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_tab_detail:
                binding.viewpager.setCurrentItem(0);
                setTab(0);
                break;
            case R.id.txt_tab_pay:
                binding.viewpager.setCurrentItem(2);
                setTab(2);
                break;
            case R.id.txt_tab_stars:
                binding.viewpager.setCurrentItem(1);
                setTab(1);
                break;
        }
    }

    private void setTab(int selTab) {
        switch (selTab) {
            case 0:
                //binding.viewpager.setCurrentItem(0);
                binding.txtViewDetail.setVisibility(View.VISIBLE);
                binding.txtViewPay.setVisibility(View.GONE);
                binding.txtViewStars.setVisibility(View.GONE);
                break;
            case 1:
                //binding.viewpager.setCurrentItem(1);
                binding.txtViewDetail.setVisibility(View.GONE);
                binding.txtViewPay.setVisibility(View.GONE);
                binding.txtViewStars.setVisibility(View.VISIBLE);
                break;
            case 2:
                //binding.viewpager.setCurrentItem(2);
                binding.txtViewDetail.setVisibility(View.GONE);
                binding.txtViewPay.setVisibility(View.VISIBLE);
                binding.txtViewStars.setVisibility(View.GONE);
                break;
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Fragment> mFragmentList = new ArrayList<>();

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
