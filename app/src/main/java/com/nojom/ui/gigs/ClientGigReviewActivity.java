package com.nojom.ui.gigs;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityClientGigReviewBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;


public class ClientGigReviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        ActivityClientGigReviewBinding clientReviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_client_gig_review);
        new ClientGigReviewActivityVM(Task24Application.getActivity(), clientReviewBinding, this);
        Utils.trackFirebaseEvent(this, "Gig_Client_Review_Screen");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
