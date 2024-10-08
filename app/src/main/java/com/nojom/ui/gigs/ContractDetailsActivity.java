package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.databinding.ActivityProjectDetailsBinding;
import com.nojom.model.ContractDetails;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContractDetailsActivity extends BaseActivity {

    private ContractDetailsActivityVM projectDetailsActivityVM;
    private ActivityProjectDetailsBinding binding;
    private ContractDetails projectData;
    private ContractRateFragment projectRateFragment;
    private ContractSubmitFragment projectSubmitFragment;
    private ContractStatusFragment projectStatusFragment;
    private boolean isRefresh = false;
    private String gigType = "2";
    public Long selectedMessageId = null;

    public String getGigType() {
        return gigType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_project_details);
        projectDetailsActivityVM = ViewModelProviders.of(this).get(ContractDetailsActivityVM.class);
        projectDetailsActivityVM.init(this);
        initData();
        Utils.trackFirebaseEvent(this, "Gig_Contract_Detail_Screen");
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvCloseProject.setVisibility(View.GONE);
//        binding.tvCloseProject.setOnClickListener(v -> {
//            if (projectData.jobPostBids != null)
//            projectDetailsActivityVM.showCancelBidDialog(projectData.id);
//        });

        if (getIntent() != null) {
            projectData = (ContractDetails) getIntent().getSerializableExtra(Constants.PROJECT);
            gigType = getIntent().getStringExtra("gigtype");
            selectedMessageId = getIntent().getLongExtra("messageid", 0);
        }

        if (projectData != null) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            projectDetailsActivityVM.setProjectData(projectData);
            runOnUiThread(() -> {
                updateTab(projectData.gigStateID);
                setupPager();
            });
        }

        Preferences.writeInteger(this, Constants.EDIT_BID_ID, 0);
        observer();
//        projectDetailsActivityVM.getProjectById(false, projectId);
    }

    private void observer() {

        projectDetailsActivityVM.getMutableProjectData().observe(this, data -> projectData = data);

        projectDetailsActivityVM.getNeedToRefresh().observe(this, isRefresh -> {
            if (isRefresh) {//need to refresh rate fragment after give rating to client
                if (projectRateFragment != null) {
                    projectRateFragment.refreshPage();
                }
                if (projectSubmitFragment != null) {
                    projectSubmitFragment.refreshPage();
                }
                if (projectStatusFragment != null) {
                    projectStatusFragment.refreshBidPrice();
                }

            }
        });

        projectDetailsActivityVM.getUpdatePager().observe(this, isShowPager -> {
            if (isShowPager) {
                binding.viewpager.setVisibility(View.VISIBLE);
                binding.tvNoData.setVisibility(View.GONE);
            } else {
                binding.viewpager.setVisibility(View.GONE);
                binding.tvNoData.setVisibility(View.VISIBLE);
            }
        });

        projectDetailsActivityVM.getUpdateTab().observe(this, this::updateTab);

        projectDetailsActivityVM.getRefresh().observe(this, refresh -> isRefresh = refresh);

        projectDetailsActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
        });
    }

    private void updateTab(Integer state) {
        switch (state) {
            case Constants.BIDDING:
                binding.segmentGroupBidding.setVisibility(View.VISIBLE);
                binding.tvCloseProject.setVisibility(View.GONE);
                setTabBid(0);
                break;
            case 7:
            case Constants.WAITING_FOR_ACCEPTANCE:
                binding.segmentGroupWaiting.setVisibility(View.VISIBLE);
                binding.tvCloseProject.setVisibility(View.GONE);
                setTabWai(0);
                break;
            case Constants.BANK_TRANSFER_REVIEW:
            case Constants.WAITING_FOR_DEPOSIT:
                binding.segmentGroupWaiting.setVisibility(View.VISIBLE);
                setTabWai(0);
                break;
            case Constants.IN_PROGRESS:
            case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                binding.segmentGroupProgress.setVisibility(View.VISIBLE);
                setTabPro(0);
                break;
            case Constants.COMPLETED:
                binding.segmentGroupComplete.setVisibility(View.VISIBLE);
                setTabCom(0);
                if (projectData.isClientReview == 0) {
                    projectDetailsActivityVM.giveRatingDialog();
                }
                break;
            case Constants.CANCELLED:
            case Constants.REFUNDED:
                binding.segmentGroupCanceled.setVisibility(View.VISIBLE);
                setTabCan(0);
                break;
        }
    }

    private void setupPager() {
        setupViewPager(binding.viewpager);

        binding.segmentGroupCanceled.setOnCheckedChangeListener(onCheckedChangeCan);
        binding.segmentGroupBidding.setOnCheckedChangeListener(onCheckedChangeBid);
        binding.segmentGroupComplete.setOnCheckedChangeListener(onCheckedChangeCom);
        binding.segmentGroupProgress.setOnCheckedChangeListener(onCheckedChangePro);
        binding.segmentGroupWaiting.setOnCheckedChangeListener(onCheckedChangeWai);

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.viewpager.setCurrentItem(position);
                switch (projectData.gigStateID) {
                    case Constants.BIDDING:
                        setTabBid(position);
                        break;
                    case 7:
                    case Constants.WAITING_FOR_ACCEPTANCE:
                    case Constants.BANK_TRANSFER_REVIEW:
                    case Constants.WAITING_FOR_DEPOSIT:
                        setTabWai(position);
                        break;
                    case Constants.IN_PROGRESS:
                    case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                        setTabPro(position);
                        break;
                    case Constants.COMPLETED:
                        setTabCom(position);
                        break;
                    case Constants.CANCELLED:
                    case Constants.REFUNDED:
                        setTabCan(position);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(projectStatusFragment = new ContractStatusFragment(), getString(R.string.proposals));
        switch (projectData.gigStateID) {
            case 7:
            case Constants.WAITING_FOR_ACCEPTANCE:
            case Constants.WAITING_FOR_DEPOSIT:
            case Constants.BANK_TRANSFER_REVIEW:
            case Constants.CANCELLED:
            case Constants.REFUNDED:
                adapter.addFrag(new ContractDetailsFragment(), getString(R.string.details));
                break;
            case Constants.IN_PROGRESS:
            case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                adapter.addFrag(projectSubmitFragment = new ContractSubmitFragment(), getString(R.string.submit));
                adapter.addFrag(new ContractDetailsFragment(), getString(R.string.details));
                break;
            case Constants.COMPLETED:
                adapter.addFrag(projectRateFragment = new ContractRateFragment(), getString(R.string.rate));
                adapter.addFrag(projectSubmitFragment = new ContractSubmitFragment(), getString(R.string.submit));
                adapter.addFrag(new ContractDetailsFragment(), getString(R.string.details));
                break;
        }
        adapter.addFrag(new ContractPaymentFragment(), getString(R.string.payment));
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            projectDetailsActivityVM.getProjectById(false, projectData.id);
        }

        int bidId = Preferences.readInteger(this, Constants.EDIT_BID_ID, 0);
        if (bidId != 0) {
            Preferences.writeInteger(this, Constants.EDIT_BID_ID, 0);
            projectDetailsActivityVM.getProjectById(true, projectData.id);
        }

        boolean isFileSubmit = Preferences.readBoolean(this, Constants.SUBMIT_FILE_DONE, false);
        if (isFileSubmit) {
            Preferences.writeBoolean(this, Constants.SUBMIT_FILE_DONE, false);
            projectDetailsActivityVM.getProjectById(true, projectData.id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int RC_RATING = 10101;
            if (requestCode == RC_RATING) {
                getProjectById(true);
            }
        }
    }

    public ContractDetails getProjectData() {
        return projectDetailsActivityVM.getProjectData();
    }

    public void getProjectById(boolean isNeedToRefresh) {
        projectDetailsActivityVM.getProjectById(isNeedToRefresh, projectData.id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    RadioGroup.OnCheckedChangeListener onCheckedChangeCan = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("status")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("details")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("payment")) {
            binding.viewpager.setCurrentItem(2);
        }
    };
    RadioGroup.OnCheckedChangeListener onCheckedChangeWai = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("employer")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("details")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("payment")) {
            binding.viewpager.setCurrentItem(2);
        }
    };

    RadioGroup.OnCheckedChangeListener onCheckedChangePro = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("job")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("submit")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("details")) {
            binding.viewpager.setCurrentItem(2);
        } else if (button.getTag().equals("payment")) {
            binding.viewpager.setCurrentItem(3);
        }
    };

    RadioGroup.OnCheckedChangeListener onCheckedChangeCom = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("status")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("rate")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("submit")) {
            binding.viewpager.setCurrentItem(2);
        } else if (button.getTag().equals("job")) {
            binding.viewpager.setCurrentItem(3);
        } else if (button.getTag().equals("pay")) {
            binding.viewpager.setCurrentItem(4);
        }
    };

    RadioGroup.OnCheckedChangeListener onCheckedChangeBid = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("employer")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("details")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("files")) {
            binding.viewpager.setCurrentItem(2);
        } else if (button.getTag().equals("payment")) {
            binding.viewpager.setCurrentItem(3);
        }
    };

    private void setTabCan(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabStaCan.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabDetCan.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabPayCan.setChecked(true);
        }
    }

    private void setTabWai(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabEmpWai.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabDetWai.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabPayWai.setChecked(true);
        }
    }


    private void setTabPro(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabJobPro.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabSubPro.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabDetPro.setChecked(true);
        } else if (selectedPos == 3) {
            binding.tabPayPro.setChecked(true);
        }
    }

    private void setTabCom(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabStaCom.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabRatCom.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabSubCom.setChecked(true);
        } else if (selectedPos == 3) {
            binding.tabJobCom.setChecked(true);
        } else if (selectedPos == 4) {
            binding.tabPayCom.setChecked(true);
        }
    }

    private void setTabBid(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabEmpBid.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabDetBid.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabFilBid.setChecked(true);
        } else if (selectedPos == 3) {
            binding.tabPayBid.setChecked(true);
        }
    }
}
