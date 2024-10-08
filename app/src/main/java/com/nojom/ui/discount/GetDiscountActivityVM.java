package com.nojom.ui.discount;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.nojom.R;
import com.nojom.databinding.ActivityGetDiscountBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class GetDiscountActivityVM extends AndroidViewModel {
    private ActivityGetDiscountBinding binding;
    private BaseActivity activity;
    private int tabPosition = 0;
    private String mInvitationUrl;
    private EarnMoneyFragment earnMoneyFragment;

    GetDiscountActivityVM(Application application, ActivityGetDiscountBinding getDiscountBinding, BaseActivity getDiscountActivity) {
        super(application);
        binding = getDiscountBinding;
        activity = getDiscountActivity;
        initData();
    }

    String getmInvitationUrl() {
        return mInvitationUrl;
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> activity.onBackPressed());
        if (activity.getIntent() != null) {
            tabPosition = activity.getIntent().getIntExtra(Constants.TAB_BALANCE, 0);
        }
        generateDynamicLink();
        setupPager();
    }

    private void setupPager() {
        if (activity.getCurrency().equals("SAR")) {
            binding.tabEarnMoney.setText(activity.getString(R.string.get_200_sar));
            binding.tabEasy.setText(activity.getString(R.string.easy_12_sar));
            binding.tabWin.setText(activity.getString(R.string.win_100_sar));
        }
        setupViewPager(binding.viewpager);

        binding.segmentedGroupTab.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton button = group.findViewById(checkedId);
            if (button.getTag().equals("earnmoney")) {
                binding.viewpager.setCurrentItem(0);
            } else if (button.getTag().equals("easy")) {
                binding.viewpager.setCurrentItem(1);
            } else if (button.getTag().equals("win")) {
                binding.viewpager.setCurrentItem(2);
            }
        });

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                binding.viewpager.setCurrentItem(i);
                setTab(i);
                if (i == 0 && earnMoneyFragment != null) {
                    earnMoneyFragment.setLink();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        binding.viewpager.setCurrentItem(tabPosition);
        setTab(tabPosition);
    }

    private void setTab(int pos) {
        if (pos == 0) {
            binding.tabEarnMoney.setChecked(true);
        } else if (pos == 1) {
            binding.tabEasy.setChecked(true);
        } else if (pos == 2) {
            binding.tabWin.setChecked(true);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
        adapter.addFrag(earnMoneyFragment = new EarnMoneyFragment(), activity.getString(R.string.get_200));
        adapter.addFrag(new EasyFragment(), activity.getString(R.string.easy_12));
        adapter.addFrag(new WinFragment(), activity.getString(R.string.win_100));
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(4);
    }

    private void generateDynamicLink() {
        String uid = "" + activity.getUserID();
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        String link = "https://24task.com/invite/?invitedby=" + uid + "&code=" + activity.getReferralCode();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://24taskpromo.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.nojom.brands")
                                .setMinimumVersion(1)
                                .build())
                .setIosParameters(
                        new DynamicLink.IosParameters.Builder("Task24.Task24")
                                .setAppStoreId("1397804027")
                                .setMinimumVersion("1.0")
                                .build())
                .buildShortDynamicLink()
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(shortDynamicLink -> {
                    mInvitationUrl = Objects.requireNonNull(shortDynamicLink.getShortLink()).toString();
                    if (earnMoneyFragment != null) {
                        earnMoneyFragment.setLink();
                    }
                });

        //short
        /*String link1 = "https://24task.com/freelancers/24taskagent";
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link1))
                .setDomainUriPrefix("https://task.bio/r/")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.task24.android.apps.employer.hire.freelancer")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();
                        String sl = shortLink.toString();
                        String fcl = flowchartLink.toString();
                        Log.e("TAG" + " short link:", sl);
                        Log.e("TAG" + " flow chat link:", fcl);
                    } else {
                        // Error
                        Log.e("TAG" + " short links:", "problem");
                        Exception exception = task.getException();
                        Log.e("TAG", "Short Dynamic link error", exception);
                    }
                });*/
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
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
