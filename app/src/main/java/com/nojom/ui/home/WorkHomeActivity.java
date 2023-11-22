package com.nojom.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.apis.GetServiceCategoryAPI;
import com.nojom.databinding.ActivityWorkHomeBinding;
import com.nojom.fragment.jobs.JobsListFragment;
import com.nojom.model.GigCatCharges;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.PrivateInfoActivity;
import com.nojom.ui.workprofile.SelectSkillsActivity;
import com.nojom.ui.workprofile.ServiceFilterActivity;
import com.nojom.ui.workprofile.UpdateLocationActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WorkHomeActivity extends BaseActivity implements APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {

    public ActivityWorkHomeBinding binding;
    private LayoutBinderHelper layoutBinderHelper;
    private static final int REQ_FILTER = 101;
    private int serviceId, SCREEN_TAB_ = -1;//used when user place BID on project at that time redirect to Bidding TAB using this FLAG
    private JobsListFragment jobsListFragment;
    private boolean isRefresh = false;
    public boolean isFilterCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getProfile();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_home);
        binding.setHomeAct(this);
        layoutBinderHelper = new LayoutBinderHelper();
        binding.setLayoutBinder(layoutBinderHelper);

        initData();

        serviceId = Preferences.readInteger(this, Constants.FILTER_ID, 0);
//        if (serviceId == 10) {
//            serviceId = 0;
//        }
        if (serviceId == 0) {//all service
            binding.imgFilter.setImageResource(R.drawable.ic_filter);
        } else {//any other services
            binding.imgFilter.setImageResource(R.drawable.ic_filter_blue);
        }
        updateProfileData();

        binding.segmentedGroupTab.setOnCheckedChangeListener(onCheckedChangeCan);
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeCan = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("available")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("bidding")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("offer")) {
            binding.viewpager.setCurrentItem(2);
        }
    };

    private void initData() {

        if (getIntent() != null && getIntent().hasExtra(SCREEN_TAB)) {//used when redirect to Bidding TAB
            SCREEN_TAB_ = getIntent().getIntExtra(SCREEN_TAB, -1);
        }

        boolean bannerClosed = Preferences.readBoolean(this, CLIENT_BANNER, false);//display TOP banner using this FLAG
        layoutBinderHelper.setIsBanner(bannerClosed);

        setupTabs();

        Utils.trackFirebaseEvent(this, "Home_Screen");

        //check if service category found, then no need to call API, if not found then do API call
//        List<ServicesModel.Data> mData = Preferences.getTopServices(this);
//        if (mData == null || mData.size() == 0) {
        GetServiceCategoryAPI getServiceCategoryAPI = new GetServiceCategoryAPI();
        getServiceCategoryAPI.init(this);
        getServiceCategoryAPI.getServiceCategories();
//        }

        getGigCatCharges();

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (jobsListFragment != null) {
                    jobsListFragment.getPostBySearch();
                    Utils.hideSoftKeyboard(this);
                }
                return true;
            }
            return false;
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    new Handler().postDelayed(() -> {
                        if (jobsListFragment != null) {
                            jobsListFragment.refresh();
                        }
                    }, 700);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void getGigCatCharges() {
        if (!isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(WorkHomeActivity.this, this, API_GIG_CAT_CHARGES, false, null);
    }

    private void checkProfileBannerView() {
        if (getProfileData() != null
                && getProfileData().profilePic != null
                && !TextUtils.isEmpty(getImageUrl() + getProfileData().profilePic)) {
            layoutBinderHelper.setIsBannerProfile(true);//if profile is set then no need to show profile banner
        } else {
            layoutBinderHelper.setIsBannerProfile(false);//if profile is not set then display banner at top [if client banner is gone]
        }
    }

    private void setupTabs() {
        setupViewPager(binding.viewpager);

        if (SCREEN_TAB_ != -1) {
            setTab(SCREEN_TAB_);
            binding.viewpager.setCurrentItem(SCREEN_TAB_);
        } else {
            setTab(0);
        }

        binding.viewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(jobsListFragment = JobsListFragment.newInstance(false, false), Constants.AVAILABLE_JOBS);
        adapter.addFrag(JobsListFragment.newInstance(true, false), getString(R.string.bidding));
        adapter.addFrag(JobsListFragment.newInstance(false, true), Constants.OFFER);
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);
    }

    private void setTab(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabAvailable.setChecked(true);
            hideShowSearchView(true);
        } else if (selectedPos == 1) {
            hideShowSearchView(false);
            binding.tabBidding.setChecked(true);
        } else if (selectedPos == 2) {
            hideShowSearchView(false);
            binding.tabOffer.setChecked(true);
        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GIG_CAT_CHARGES)) {
            GigCatCharges gigCatCharges = GigCatCharges.getGigCatCharges(responseBody);
            if (gigCatCharges != null && gigCatCharges.data != null && gigCatCharges.data.size() > 0) {
                Preferences.saveCategoryCharges(this, gigCatCharges.data);
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_FILTER) {
            if (data != null) {
                setTab(0);
                binding.viewpager.setCurrentItem(0);

                serviceId = data.getIntExtra(Constants.SERVICE_ID, 0);
//                if (serviceId == 10) {
//                    serviceId = 0;
//                }
                setRefresh(true);
                isFilterCall = true;
                if (jobsListFragment != null) {
                    jobsListFragment.reset();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
//            if (getProfileData() != null && getProfileData().serviceCategoryId != null
//                    && getProfileData().serviceCategoryId == 5) {//writing category
//                binding.imgFilter.setVisibility(View.GONE);//if select writing then hide filter option otherwise not
//            } else {
//                binding.imgFilter.setVisibility(View.VISIBLE);
//            }

            boolean isRefreshJobs = Preferences.readBoolean(this, IS_REFRESH_JOB, false);
            if (isRefreshJobs) {//only refresh in case of user update his/her Expertise
                Preferences.writeBoolean(this, IS_REFRESH_JOB, false);
                if (!isFilterCall && jobsListFragment != null) {
                    jobsListFragment.refresh();
                }
            }
            SCREEN_TAB_ = -1;//reset this flag
            checkProfileBannerView();

            //this below code is call when user don't have any location or any skills. At that time we need to update their data forcefully
            if (getProfileData() != null) {
                if (TextUtils.isEmpty(getProfileData().countryName)
                        || TextUtils.isEmpty(getProfileData().stateName)
                        || TextUtils.isEmpty(getProfileData().cityName)) {
                    showLocationSkillDialog(true);
                } else if (getProfileData().skills == null || getProfileData().skills.size() == 0) {
                    showLocationSkillDialog(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public int getServiceId() {
        serviceId = Preferences.readInteger(this, Constants.FILTER_ID, 0);
//        if (serviceId == 10) {
//            serviceId = 0;
//        }

        if (serviceId == 0) {//all service
            binding.imgFilter.setImageResource(R.drawable.ic_filter);
        } else {//any other services
            binding.imgFilter.setImageResource(R.drawable.ic_filter_blue);
        }

        return serviceId;
    }

    public void setFilterResult(boolean result) {
        isFilterCall = result;
    }

    public void onClickFilter() {
        Intent i = new Intent(this, ServiceFilterActivity.class);
        i.putExtra(Constants.SERVICE_ID, serviceId);
        startActivityForResult(i, REQ_FILTER);
    }

    public void onClickSearch() {
        if (binding.linSearch.isShown()) {
            Utils.hideSoftKeyboard(this);
            binding.linSearch.setVisibility(View.GONE);
        } else {
            binding.linSearch.setVisibility(View.VISIBLE);
            binding.etSearch.requestFocus();
            Utils.openSoftKeyboard(this, binding.etSearch);
        }
    }

    public void onClickSearchCancel() {
        Utils.hideSoftKeyboard(this);
        binding.linSearch.setVisibility(View.GONE);
        binding.etSearch.setText("");
        if (jobsListFragment != null) {
            jobsListFragment.refresh();
        }
    }

    public void hideShowSearchView(boolean isShow) {
        if (isShow) {
            binding.imgSearch.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(binding.etSearch.getText().toString().trim())) {
                binding.linSearch.setVisibility(View.VISIBLE);
            }
        } else {
            binding.imgSearch.setVisibility(View.GONE);
//            binding.etSearch.setText("");
            binding.linSearch.setVisibility(View.GONE);
        }
    }

    public void onClickCloseBanner() {
        Preferences.writeBoolean(this, CLIENT_BANNER, true);
        layoutBinderHelper.setIsBanner(true);
        checkProfileBannerView();
    }

    public void onClickBanner() {
        openClientAppOnPlaystore();
    }

    public void onClickBannerProfile() {
        redirectActivity(PrivateInfoActivity.class);
    }

    public static class LayoutBinderHelper extends BaseObservable {
        private Boolean isBanner, isBannerProfile;
        private int availableCount, biddingCount, offerCount;

        public LayoutBinderHelper() {
            this.isBanner = false;
            this.isBannerProfile = false;
            availableCount = 0;
            biddingCount = 0;
            offerCount = 0;
        }

        public void setIsBanner(boolean presentationElementsVisible) {
            this.isBanner = presentationElementsVisible;
            notifyChange();
        }

        public void setIsBannerProfile(boolean presentationElementsVisible) {
            this.isBannerProfile = presentationElementsVisible;
            notifyChange();
        }

        public void setAvailableCount(int availableCount) {
            this.availableCount = availableCount;
            notifyChange();
        }

        public void setOfferCount(int offerCount) {
            this.offerCount = offerCount;
            notifyChange();
        }

        public void setBiddingCount(int biddingCount) {
            this.biddingCount = biddingCount;
            notifyChange();
        }

        @Bindable
        public Boolean getIsBanner() {
            return isBanner;
        }

        @Bindable
        public Boolean getIsBannerProfile() {
            return isBannerProfile;
        }

        @Bindable
        public int getAvailableCount() {
            return availableCount;
        }

        @Bindable
        public int getBiddingCount() {
            return biddingCount;
        }

        @Bindable
        public int getOfferCount() {
            return offerCount;
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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

    public LayoutBinderHelper getLayoutBinderHelper() {
        return layoutBinderHelper;
    }

    public void hideShowHorizontalProgressBar(int visibility) {
        binding.hProgressBar.setVisibility(visibility);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onPageChangeListener != null)
            binding.viewpager.removeOnPageChangeListener(onPageChangeListener);
    }

    void showLocationSkillDialog(boolean isShowLocation) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_location_skill);
        dialog.setCancelable(false);

        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvUpdate = dialog.findViewById(R.id.tv_update);

        if (isShowLocation) {
            tvTitle.setText(getString(R.string.please_update_location));
            tvMessage.setText(getString(R.string.update_loc_desc));
        } else {
            tvTitle.setText(getString(R.string.please_update_your_skill));
            tvMessage.setText(getString(R.string.update_skill_desc));
        }

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
//            finish();
//            System.exit(0);
        });

        tvUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            if (isShowLocation) {
                Intent intent = new Intent(this, UpdateLocationActivity.class);
                intent.putExtra("flag", true);
                startActivity(intent);
            } else {
                Intent i = new Intent(this, SelectSkillsActivity.class);
                i.putExtra("flag", true);
                startActivity(i);
                openToLeft();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void updateProfileData() {
        try {
            HashMap<String, String> accounts = Preferences.getMultipleAccounts(this);
            UserModel userModel = Preferences.getUserData(this);
            if (accounts != null && accounts.size() > 0 && userModel != null) {
                if (!accounts.containsKey(userModel.username)) {
                    Preferences.addMultipleAccounts(this, userModel.jwt, userModel.username);
                }
            } else {
                if (userModel != null) {
                    userModel.jwt = getJWT();
                    Preferences.addMultipleAccounts(this, getJWT(), userModel.username);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
