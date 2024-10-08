package com.nojom.ui.balance;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nojom.R;
import com.nojom.databinding.ActivityBalanceBinding;
import com.nojom.fragment.balance.AccountFragment;
import com.nojom.fragment.balance.IncomeFragment;
import com.nojom.fragment.balance.WithdrawFragment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class BalanceActivity extends BaseActivity {
    private ActivityBalanceBinding binding;
    private int tabPosition = 0;
    private double availableBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_balance);
        initData();
    }

    private void initData() {
        if (getCurrency().equals("SAR")) {
            binding.txtSign.setText(getString(R.string.sar));
        } else {
            binding.txtSign.setText(getString(R.string.dollar));
        }
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvWithdraw.setOnClickListener(v -> {
            if (availableBalance > 0) {
                Intent i = new Intent(this, WithdrawActivity.class);
                i.putExtra(Constants.AVAILABLE_BALANCE, availableBalance);
                startActivity(i);
            } else {
                toastMessage(getString(R.string.your_available_balance_is_zero));
            }
        });
        binding.tvShowDetails.setOnClickListener(v -> {
            if (binding.llShowDetails.getVisibility() == View.GONE) {

                binding.llShowDetails.setVisibility(View.VISIBLE);
                binding.tvShowDetails.setText(getString(R.string.withdraw_from_balance));

                expand(binding.llShowDetails);

                binding.tvShowDetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            }
        });
        binding.imgArrowUp.setOnClickListener(v -> {

            //  binding.llShowDetails.setVisibility(View.GONE);
            binding.tvShowDetails.setText(getString(R.string.show_details));
            binding.tvShowDetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);

            //collapse(binding.tvShowDetails, 2000, 0);
            collapse(binding.llShowDetails);

        });

        if (getIntent() != null) {
            tabPosition = getIntent().getIntExtra(Constants.TAB_BALANCE, 0);
        }

        setupPager();
        Utils.trackFirebaseEvent(this, "Balance_List_Screen");
    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? LinearLayout.LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void setupPager() {
        setupViewPager(binding.viewpager);

        binding.segmentedGroupTab.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton button = group.findViewById(checkedId);
            if (button.getTag().equals("income")) {
                binding.viewpager.setCurrentItem(0);
            } else if (button.getTag().equals("withdraw")) {
                binding.viewpager.setCurrentItem(1);
            } else if (button.getTag().equals("account")) {
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
            binding.tabIncome.setChecked(true);
        } else if (pos == 1) {
            binding.tabWithdraw.setChecked(true);
        } else if (pos == 2) {
            binding.tabAccount.setChecked(true);
        }
    }

    public void setBalance(double availableBalance, double pendingBalance, double totalBalance) {
        this.availableBalance = availableBalance;
        if (getCurrency().equals("SAR")) {
            binding.tvAvailableBalance.setText(String.format("%s", Utils.priceWithSAR(this,Utils.getDecimalValue("" + availableBalance))));
            binding.tvPendingBalance.setText(String.format("%s", Utils.priceWithSAR(this,Utils.getDecimalValue("" + pendingBalance))));
            binding.tvTotalBalance.setText(String.format("%s", Utils.priceWithSAR(this,Utils.getDecimalValue("" + totalBalance))));
            binding.tvBalance.setText(Utils.priceWithSAR(this,Utils.getDecimalValue("" + availableBalance)).replace(getString(R.string.sar), ""));
        } else {
            binding.tvAvailableBalance.setText(String.format("%s", Utils.priceWith$(Utils.getDecimalValue("" + availableBalance),this)));
            binding.tvPendingBalance.setText(String.format("%s", Utils.priceWith$(Utils.getDecimalValue("" + pendingBalance),this)));
            binding.tvTotalBalance.setText(String.format("%s", Utils.priceWith$(Utils.getDecimalValue("" + totalBalance),this)));
            binding.tvBalance.setText(Utils.priceWith$(Utils.getDecimalValue("" + availableBalance),this).replace(getString(R.string.dollar), ""));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new IncomeFragment(), getString(R.string.income));
        adapter.addFrag(new WithdrawFragment(), getString(R.string.withdraw));
        adapter.addFrag(new AccountFragment(), getString(R.string.account));
        viewPager.setPageMargin(20);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);
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

    public void hideShowHorizontalProgressBar(int visibility) {
        binding.hProgressBar.setVisibility(visibility);
    }
}
