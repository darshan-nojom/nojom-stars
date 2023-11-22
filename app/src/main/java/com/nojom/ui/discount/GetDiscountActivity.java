package com.nojom.ui.discount;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityGetDiscountBinding;
import com.nojom.ui.BaseActivity;


public class GetDiscountActivity extends BaseActivity {
    private GetDiscountActivityVM getDiscount;
    private int tabPosition = 0;
    private String mInvitationUrl;
    private EarnMoneyFragment earnMoneyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGetDiscountBinding getDiscountBinding = DataBindingUtil.setContentView(this, R.layout.activity_get_discount);
        getDiscount = new GetDiscountActivityVM(Task24Application.getActivity(), getDiscountBinding, this);
    }

    public String getmInvitationUrl() {
        return getDiscount.getmInvitationUrl();
    }
}
