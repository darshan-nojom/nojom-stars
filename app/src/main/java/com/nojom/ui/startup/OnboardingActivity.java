package com.nojom.ui.startup;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.adapter.OnBoardingAdapter;
import com.nojom.databinding.ActivityOnboardingBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginActivity;
import com.nojom.ui.workprofile.WorkProfileActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

public class OnboardingActivity extends BaseActivity {
    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        initData();
    }

    private void initData() {
        if (language.equals("ar")) {
            binding.tvLang.setText("English");
            binding.imgText.setImageResource(R.drawable.logo_txt_splash_ar);
            setArFont(binding.tvLang, Constants.FONT_AR_MEDIUM);
            setArFont(binding.btnSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.btnNext, Constants.FONT_AR_MEDIUM);
        } else {
            binding.tvLang.setText("عربي");
            binding.imgText.setImageResource(R.drawable.nojom_txt_splash);
        }
        OnBoardingAdapter adapter = new OnBoardingAdapter(this);
        binding.viewPager.setAdapter(adapter);
        createIndicators(0);

        binding.btnNext.setOnClickListener(view -> {
            int pos = binding.viewPager.getCurrentItem();
            if (pos == 0 || pos == 1) {
                onClickNext();
            } else {
                redirectActivity(LoginActivity.class);
//                finish();
            }
        });
        binding.btnSkip.setOnClickListener(view -> {
            redirectActivity(LoginActivity.class);
//            redirectActivity(SocialActivity.class);
        });
        binding.tvLang.setOnClickListener(view -> {
            Preferences.writeString(this, Constants.PREF_SELECTED_LANGUAGE, language.equals("ar") ? "en" : "ar");
            loadAppLanguage();
            recreate();
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setActiveIndicator(position);
                switch (position) {
                    case 1:
                        binding.tvTitle.setText(getString(R.string.exceptional_experience));
                        binding.tvDesciption.setText(getString(R.string.enjoy_an_outstanding_user_experience_and_exclusive_features_just_for_celebrities));
                        binding.v1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        binding.v1.setVisibility(View.GONE);
                        binding.tvTitle.setText(getString(R.string.new_opportunities));
                        binding.tvDesciption.setText(getString(R.string.join_us_and_discover_collaboration_opportunities_with_prestigious_brands_and_global_companies));
                        break;
                    default:
                        binding.v1.setVisibility(View.GONE);
                        binding.tvTitle.setText(getString(R.string.shine_with_the_stars));
                        binding.tvDesciption.setText(getString(R.string.join_our_platform_to_be_part_of_an_exclusive_community_of_celebrities_and_influencers));
                        break;
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

    private ImageView[] indicators;

    private void createIndicators(int currentPosition) {
        indicators = new ImageView[3];
        binding.indicatorLayout.removeAllViews();

        for (int i = 0; i < 3; i++) {
            indicators[i] = new ImageView(this);
            if (i == currentPosition) {
                indicators[i].setImageResource(R.drawable.blue_circle_round);
            } else {
                indicators[i].setImageResource(R.drawable.gray_circle_round);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int space = (int) getResources().getDimension(R.dimen._10sdp);
            params.setMargins(space, 0, space, 0);
            binding.indicatorLayout.addView(indicators[i], params);
        }
    }

    private void setActiveIndicator(int position) {
        for (int i = 0; i < indicators.length; i++) {
            if (i == position) {
                indicators[i].setImageResource(R.drawable.blue_circle_round);
            } else {
                indicators[i].setImageResource(R.drawable.gray_circle_round);
            }
        }
    }
}
