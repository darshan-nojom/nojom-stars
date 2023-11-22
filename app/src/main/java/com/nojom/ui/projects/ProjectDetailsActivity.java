package com.nojom.ui.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.apis.JobDetailAPI;
import com.nojom.databinding.ActivityProjectDetailsBinding;
import com.nojom.fragment.projects.ProjectDetailsFragment;
import com.nojom.fragment.projects.ProjectFilesFragment;
import com.nojom.fragment.projects.ProjectPaymentFragment;
import com.nojom.fragment.projects.ProjectRateFragment;
import com.nojom.fragment.projects.ProjectStatusFragment;
import com.nojom.fragment.projects.ProjectSubmitFragment;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends BaseActivity {

    private ProjectDetailsActivityVM projectDetailsActivityVM;
    private ActivityProjectDetailsBinding binding;
    private ProjectByID projectData;
    private ProjectRateFragment projectRateFragment;
    private ProjectSubmitFragment projectSubmitFragment;
    private ProjectStatusFragment projectStatusFragment;
    private boolean isRefresh = false;
    private JobDetailAPI jobDetailAPI;
    private boolean isNeedToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_project_details);
        projectDetailsActivityVM = ViewModelProviders.of(this).get(ProjectDetailsActivityVM.class);
        projectDetailsActivityVM.init(this);
        jobDetailAPI = new JobDetailAPI();
        jobDetailAPI.init(this);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvCloseProject.setOnClickListener(v -> {
            if (projectData.jobPostBids != null)
                projectDetailsActivityVM.showCancelBidDialog();
        });

        if (getIntent() != null) {
            projectData = (ProjectByID) getIntent().getSerializableExtra(Constants.PROJECT);
        }

        if (projectData != null) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            projectDetailsActivityVM.setProjectData(projectData);
            runOnUiThread(() -> {
                updateTab(projectData.jobPostStateId);
                setupPager();
            });
        }

        Preferences.writeInteger(this, Constants.EDIT_BID_ID, 0);
        observer();
//        projectDetailsActivityVM.getProjectById(false, projectId);
        Utils.trackFirebaseEvent(this, "Project_Detail_Screen");

        jobDetailAPI.getProjectById().observe(this, projectByID -> {
            if (projectByID != null) {
                binding.viewpager.setVisibility(View.VISIBLE);
                binding.tvNoData.setVisibility(View.GONE);

                projectData = projectByID;
                projectDetailsActivityVM.getMutableProjectData().postValue(projectData);

                runOnUiThread(() -> {
                    updateTab(projectData.jobPostStateId);
                    if (isNeedToRefresh) {
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
            } else {
                isRefresh = true;
                binding.viewpager.setVisibility(View.GONE);
                binding.tvNoData.setVisibility(View.VISIBLE);
            }

        });

        jobDetailAPI.getIsShowProgress().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isShow) {
                if (isShow) {

                } else {

                }
            }
        });
    }

    private void observer() {

        projectDetailsActivityVM.getMutableProjectData().observe(this, data -> projectData = data);

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
                binding.tvCloseProject.setVisibility(View.VISIBLE);
                setTabBid(0);
                break;
            case Constants.WAITING_FOR_ACCEPTANCE:
                binding.segmentGroupWaiting.setVisibility(View.VISIBLE);
                binding.tvCloseProject.setVisibility(View.VISIBLE);
                setTabWai(0);
                break;
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
                if (projectData.review == 0) {
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
//                setSegmentPosition(i, binding.segmentGroupCanceled, binding.segmentGroupBidding, binding.segmentGroupComplete, binding.segmentGroupProgress, binding.segmentGroupWaiting);
                binding.viewpager.setCurrentItem(position);
                switch (projectData.jobPostStateId) {
                    case Constants.BIDDING:
                        setTabBid(position);
                        break;
                    case Constants.WAITING_FOR_ACCEPTANCE:
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
        adapter.addFrag(projectStatusFragment = new ProjectStatusFragment(), getString(R.string.proposals));
        switch (projectData.jobPostStateId) {
            case Constants.BIDDING:
                adapter.addFrag(new ProjectDetailsFragment(), getString(R.string.details));
                adapter.addFrag(new ProjectFilesFragment(), getString(R.string.files));
                break;
            case Constants.WAITING_FOR_ACCEPTANCE:
            case Constants.WAITING_FOR_DEPOSIT:
            case Constants.CANCELLED:
            case Constants.REFUNDED:
                adapter.addFrag(new ProjectDetailsFragment(), getString(R.string.details));
                break;
            case Constants.IN_PROGRESS:
            case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                adapter.addFrag(projectSubmitFragment = new ProjectSubmitFragment(), getString(R.string.submit));
                adapter.addFrag(new ProjectDetailsFragment(), getString(R.string.details));
                break;
            case Constants.COMPLETED:
                adapter.addFrag(projectRateFragment = new ProjectRateFragment(), getString(R.string.rate));
                adapter.addFrag(projectSubmitFragment = new ProjectSubmitFragment(), getString(R.string.submit));
                adapter.addFrag(new ProjectDetailsFragment(), getString(R.string.details));
                break;
        }
        adapter.addFrag(new ProjectPaymentFragment(), getString(R.string.payment));
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
            isNeedToRefresh = false;
            jobDetailAPI.getProjectById(projectData.id);
        }

        int bidId = Preferences.readInteger(this, Constants.EDIT_BID_ID, 0);
        if (bidId != 0) {
            Preferences.writeInteger(this, Constants.EDIT_BID_ID, 0);
            isNeedToRefresh = true;
            jobDetailAPI.getProjectById(projectData.id);
        }

        boolean isFileSubmit = Preferences.readBoolean(this, Constants.SUBMIT_FILE_DONE, false);
        if (isFileSubmit) {
            Preferences.writeBoolean(this, Constants.SUBMIT_FILE_DONE, false);
            isNeedToRefresh = true;
            jobDetailAPI.getProjectById(projectData.id);
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

    public ProjectByID getProjectData() {
        return projectDetailsActivityVM.getProjectData();
    }

    public void getProjectById(boolean isNeedToRefresh) {
        this.isNeedToRefresh = isNeedToRefresh;
        jobDetailAPI.getProjectById(projectData.id);
    }


    public static class LayoutBinderHelper extends BaseObservable {
        private Integer isSate;

        public LayoutBinderHelper() {
            this.isSate = 0;
        }

        public void setIsSate(Integer state) {
            this.isSate = state;
            notifyChange();
        }

        @Bindable
        public Integer getIsSate() {
            return isSate;
        }
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
