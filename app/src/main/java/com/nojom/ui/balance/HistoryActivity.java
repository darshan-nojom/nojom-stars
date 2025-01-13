package com.nojom.ui.balance;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.WallectTxnAdapter;
import com.nojom.databinding.ActivityWalletBinding;
import com.nojom.model.WalletData;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

public class HistoryActivity extends BaseActivity {
    private ActivityWalletBinding binding;
    private double availableBalance;
    private CampByIdVM campByIdVM;
    private WalletData walletData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        campByIdVM = new CampByIdVM(Task24Application.getActivity(), this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        campByIdVM.getWalletBalance();
        campByIdVM.getWalletTransaction();
    }

    private void initData() {
        if (getCurrency().equals("SAR")) {
            binding.txtSign.setText(getString(R.string.sar));
        } else {
            binding.txtSign.setText(getString(R.string.dollar));
        }
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvShowDetails.setOnClickListener(v -> {
            if (binding.llShowDetails.getVisibility() == View.GONE) {

                binding.llShowDetails.setVisibility(View.VISIBLE);
                //binding.tvShowDetails.setText(getString(R.string.withdraw_from_balance));

                expand(binding.llShowDetails);

                binding.tvShowDetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            }
        });
        binding.imgArrowUp.setOnClickListener(v -> {

            //  binding.llShowDetails.setVisibility(View.GONE);
//            binding.tvShowDetails.setText(getString(R.string.show_details));
            binding.tvShowDetails.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);

            //collapse(binding.tvShowDetails, 2000, 0);
            collapse(binding.llShowDetails);

        });

        campByIdVM.mpWalletData.observe(this, walletData -> {
            WallectTxnAdapter wallectTxnAdapter = new WallectTxnAdapter(this, walletData);
            binding.viewpager.setAdapter(wallectTxnAdapter);
        });

        campByIdVM.mpWalletBalanceData.observe(this, walletData -> {
            this.walletData = walletData;
            setBalance(walletData.available_balance, walletData.hold_balance, walletData.balance);
            binding.txtSign.setText(walletData.currency);
        });

        binding.txtAddBalance.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddBalanceActivity.class);
            intent.putExtra("data", walletData);
            startActivity(intent);
        });
        binding.txtAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, MyAccountActivity.class);
            intent.putExtra("data", walletData);
            startActivity(intent);
        });
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

    public void setBalance(double availableBalance, double pendingBalance, double totalBalance) {
        this.availableBalance = availableBalance;
        if (getCurrency().equals("SAR")) {
            binding.tvPendingBalance.setText(String.format("%s", Utils.priceWithSAR(this, Utils.getDecimalValue("" + pendingBalance))));
            binding.tvTotalBalance.setText(String.format("%s", Utils.priceWithSAR(this, Utils.getDecimalValue("" + totalBalance))));
            binding.tvBalance.setText(Utils.priceWithSAR(this, Utils.getDecimalValue("" + availableBalance)).replace(getString(R.string.sar), ""));
        } else {
            binding.tvPendingBalance.setText(String.format("%s", Utils.priceWith$(Utils.getDecimalValue("" + pendingBalance), this)));
            binding.tvTotalBalance.setText(String.format("%s", Utils.priceWith$(Utils.getDecimalValue("" + totalBalance), this)));
            binding.tvBalance.setText(Utils.priceWith$(Utils.getDecimalValue("" + availableBalance), this).replace(getString(R.string.dollar), ""));
        }
    }
}
