package com.nojom.ui.gigs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.nojom.R;
import com.nojom.adapter.GigImageAdapter;
import com.nojom.api.APIRequest;
import com.nojom.apis.GigDetailAPI;
import com.nojom.databinding.ActivityMyGigJobsBinding;
import com.nojom.model.GigDetails;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyGigJobsActivity extends BaseActivity implements APIRequest.JWTRequestResponseListener {
    private ActivityMyGigJobsBinding binding;
    private GigDetails gigDetails;
    private int selectedScreenTab;
    private GigDetailAPI gigDetailAPI;

    public GigDetails getGigDetails() {
        return gigDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_gig_jobs);
        gigDetails = (GigDetails) getIntent().getSerializableExtra(GIG_DETAILS);
        selectedScreenTab = getIntent().getIntExtra(Constants.SCREEN_TAB, 1);

        gigDetailAPI = new GigDetailAPI();
        gigDetailAPI.init(this);

        initData();
        Utils.trackFirebaseEvent(this, "Gig_Detail_With_Contract_Screen");
    }

    private void initData() {
        setupTabs();
        binding.imgBack.setOnClickListener(v -> finish());

        bindView();

        binding.imgEdit.setOnClickListener(v -> {
            Intent i;
            if (gigDetails.gigType.equalsIgnoreCase("1")) {//custom gig
                i = new Intent(this, CreateCustomGigsActivityCopy.class);
//                i = new Intent(this, CreateCustomGigsActivityCopy2.class);
            } else {//standard gig
                i = new Intent(this, CreateGigsActivity.class);
            }
//            Intent i = new Intent(this, CreateGigsActivity.class);
            i.putExtra(Constants.SCREEN_TAB, selectedScreenTab);
            i.putExtra(Constants.GIG_DETAILS, gigDetails);
            startActivityForResult(i, 121);
        });

        binding.imgShare.setOnClickListener(v -> generateDynamicLink());

        gigDetailAPI.getGigDetailsMutableLiveData().observe(this, gigDetails -> {
            MyGigJobsActivity.this.gigDetails = gigDetails;
            bindView();
        });
    }

    private void bindView() {
        if (gigDetails != null) {
            binding.tvGigTitle.applyCustomFontBold();
            binding.tvGigTitle.setText(gigDetails.gigTitle);

            if (gigDetails.gigImages != null && gigDetails.gigImages.size() > 0) {
                GigImageAdapter adapter = new GigImageAdapter(this, gigDetails.gigImages, gigDetails.gigImagesPath);
                binding.viewPager.setAdapter(adapter);
                binding.indicator.setViewPager(binding.viewPager);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
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
        adapter.addFrag(GigProjectListFragment.newInstance(true, gigDetails.gigID, gigDetails.gigType), Constants.GIG_IN_PROGRESS);
        adapter.addFrag(GigProjectListFragment.newInstance(false, gigDetails.gigID, gigDetails.gigType), Constants.GIG_PAST_PROJECT);
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
//        if (pageChangeListener != null)
//            binding.viewpager.removeOnPageChangeListener(pageChangeListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 121) {
            if (gigDetails.gigType.equalsIgnoreCase("1") || gigDetails.gigType.equals("3")) {//custom gig
                gigDetailAPI.getCustomGigDetails(gigDetails.gigID);
            } else {
                gigDetailAPI.getGigDetails(gigDetails.gigID);
            }
        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {

        GigDetails gigDetails = GigDetails.getGigDetails(responseBody);
        if (gigDetails != null) {
            this.gigDetails = gigDetails;
            bindView();
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {

    }

    private void generateDynamicLink() {
//        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
//                .setLink(Uri.parse("https://client.24task.com/"))
//                .setDomainUriPrefix("https://24taskgig.page.link")
//                // Open links with this app on Android
//                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
//                // Open links with com.example.ios on iOS
//                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
//                .buildDynamicLink();
//
//        Uri dynamicLinkUri = dynamicLink.getUri();
//        Log.e("Dynamic Link ", "" + dynamicLinkUri);

        if(!TextUtils.isEmpty(gigDetails.dynamicLink)) {
            shareGig(getString(R.string.share_gig_content)+"\n\n"+gigDetails.dynamicLink);
        }
    }

    public void shareGig(String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via..."));
    }
}
