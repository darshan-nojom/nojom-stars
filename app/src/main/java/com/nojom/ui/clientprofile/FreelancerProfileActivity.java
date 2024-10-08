package com.nojom.ui.clientprofile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.VerifiedAdapter;
import com.nojom.databinding.ActivityFreelancerProfileBinding;
import com.nojom.fragment.profile.AboutProfileFragment;
import com.nojom.fragment.profile.PortfolioFragment;
import com.nojom.fragment.profile.ReviewsProfileFragment;
import com.nojom.fragment.profile.SkillProfileFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class FreelancerProfileActivity extends BaseActivity {
    private ActivityFreelancerProfileBinding binding;
    private VerifiedAdapter mVerifiedAdapter;
    private boolean isFromPortfolio;
    private PortfolioFragment portfolioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_freelancer_profile);
        initData();
    }

    private void initData() {
//        getProfile();//TODO: un-comment if it will create problem
        ProfileResponse profileData = Preferences.getProfileData(this);

        if (getIntent() != null) {
            isFromPortfolio = getIntent().getBooleanExtra("isFromPortfolio", false);
        }

        if (profileData != null) {
            binding.rvVerified.setLayoutManager(new GridLayoutManager(this, 2));

            binding.tvUserName.setText(getProperName(profileData.firstName, profileData.lastName, profileData.username));

            try {
                if (profileData.averageRate != null) {
                    String rate = Utils.numberFormat(profileData.averageRate, 1);
                    binding.tvReviews.setText(String.format("(%s)", rate));
                    binding.ratingbar.setRating(Float.parseFloat(rate));
                    binding.ratingbar.setEnable(false);
                }
            } catch (NumberFormatException e) {
                binding.ratingbar.setRating(0);
            }

//            if (profileData.profilePic != null) {
            Glide.with(this).load(getImageUrl() + profileData.profilePic)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.imgProfile);
//            }

            ArrayList<ProfileResponse.VerifiedWith> verifiedList = new ArrayList<>();

            if (profileData.trustRate != null) {
                if (profileData.trustRate.email > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.email_address), profileData.trustRate.email));
                }
                if (profileData.trustRate.facebook > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.facebook), profileData.trustRate.facebook));
                }
                if (profileData.trustRate.payment > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.payment), profileData.trustRate.payment));
                }
                if (profileData.trustRate.phoneNumber > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.phonenumber), profileData.trustRate.phoneNumber));
                }
                if (profileData.trustRate.verifyId > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.government_id), profileData.trustRate.verifyId));
                }
            }
            setVerifiedAdapter(verifiedList);
        }

        setupPager();

        binding.imgBack.setOnClickListener(view -> onBackPressed());

        binding.imgShare.setOnClickListener(view -> {
            if (profileData != null && profileData.firebaseUrl != null) {
                shareProfile(getString(R.string.share_profile_content) + "\n\n" + profileData.firebaseUrl);
            }
        });
    }

    public void shareProfile(String profileUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, profileUrl);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via..."));
    }

    private void setVerifiedAdapter(ArrayList<ProfileResponse.VerifiedWith> verifiedList) {
        if (verifiedList != null && verifiedList.size() > 0) {
            binding.tvNoVerified.setVisibility(View.GONE);
            if (mVerifiedAdapter == null) {
                mVerifiedAdapter = new VerifiedAdapter();
            }
            mVerifiedAdapter.doRefresh(verifiedList);

            if (binding.rvVerified.getAdapter() == null) {
                binding.rvVerified.setAdapter(mVerifiedAdapter);
            }
        } else {
            binding.tvNoVerified.setVisibility(View.VISIBLE);
            if (mVerifiedAdapter != null) {
                mVerifiedAdapter.doRefresh(verifiedList);
            }
        }
    }

    private void setupPager() {
        setupViewPager(binding.viewpager);

        if (isFromPortfolio) {
            setTab(2);
            binding.viewpager.setCurrentItem(2);
        } else {
            setTab(0);
        }

        binding.segmentGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.viewpager.addOnPageChangeListener(onPageChangeListener);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AboutProfileFragment(), getString(R.string.about));
        adapter.addFrag(new SkillProfileFragment(), getString(R.string.skills));
        adapter.addFrag(portfolioFragment = new PortfolioFragment(), getString(R.string.my_portfolio));
        adapter.addFrag(new ReviewsProfileFragment(), getString(R.string.reviews));
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 121 && binding.viewpager.getCurrentItem() == 2) {
            if (portfolioFragment != null) {
                portfolioFragment.getPortfolio();
            }
        }
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

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
        RadioButton button = group.findViewById(checkedId);
        if (button.getTag().equals("about")) {
            binding.viewpager.setCurrentItem(0);
        } else if (button.getTag().equals("skills")) {
            binding.viewpager.setCurrentItem(1);
        } else if (button.getTag().equals("portfolio")) {
            binding.viewpager.setCurrentItem(2);
        } else if (button.getTag().equals("reviews")) {
            binding.viewpager.setCurrentItem(3);
        }
    };

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

    private void setTab(int selectedPos) {
        if (selectedPos == 0) {
            binding.tabAbout.setChecked(true);
        } else if (selectedPos == 1) {
            binding.tabSkills.setChecked(true);
        } else if (selectedPos == 2) {
            binding.tabPortfolio.setChecked(true);
        } else if (selectedPos == 3) {
            binding.tabReviews.setChecked(true);
        }
    }
}
