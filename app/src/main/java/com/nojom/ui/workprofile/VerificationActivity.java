package com.nojom.ui.workprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.facebook.CallbackManager;
import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityVerificationBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.TrustPoint;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class VerificationActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner, BaseActivity.OnProfileLoadListener {


    private VerificationActivityVM verificationActivityVM;
    private ActivityVerificationBinding binding;
    private ProfileResponse profileData;
    private CallbackManager callbackManager;
    private static final int REQ_PHONE_VERIFICATION = 101;
    private static final int REQ_EMAIL_VERIFICATION = 102;
    private static final int REQ_ID_VERIFICATION = 103;
    private static final int REQ_PAYMENT_VERIFICATION = 104;
    private RecyclerviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification);
        verificationActivityVM = ViewModelProviders.of(this).get(VerificationActivityVM.class);
        verificationActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(view -> onBackPressed());

        callbackManager = CallbackManager.Factory.create();
        verificationActivityVM.initFacebook(callbackManager);

        binding.toolbar.tvTitle.setText(getString(R.string.trust_verification));
        String s = getString(R.string.need_points_text, "70");
        int[] colorList = {R.color.red_dark};
        String[] words = {getString(R.string.pro)};
        String[] fonts = {Constants.SFTEXT_BOLD};
//        binding.tvNeedPoints.setText(Utils.getBoldString(this, s, fonts, colorList, words));

        binding.rvVerify.setLayoutManager(new LinearLayoutManager(this));

        setOnProfileLoadListener(this);

        Utils.trackFirebaseEvent(this, "Verification_Screen");

        verificationActivityVM.getListMutableLiveData().observe(this, this::setAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profileData = Preferences.getProfileData(this);
        if (profileData != null) {
            onProfileLoad(profileData);
        }
    }

    private void setAdapter(ArrayList<TrustPoint> trustPoints) {
        if (trustPoints != null && trustPoints.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(trustPoints, R.layout.item_verification, this);
            }
            mAdapter.doRefresh(trustPoints);
            if (binding.rvVerify.getAdapter() == null) {
                binding.rvVerify.setAdapter(mAdapter);
            }
        } else {
            binding.linIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindView(View view, final int position) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvPoints = view.findViewById(R.id.tv_points);
        TextView tvVerified = view.findViewById(R.id.tv_verified);

        ArrayList<TrustPoint> arrayList = verificationActivityVM.getListMutableLiveData().getValue();
        if (arrayList == null || arrayList.size() == 0) {
            return;
        }

        tvTitle.setText(arrayList.get(position).title);
        if (language.equals("ar")) {
            String score = getString(R.string._10_100_points);
            if (arrayList.get(position).totalPoint == 10) {
                score = getString(R.string._10_points);
            } else if (arrayList.get(position).totalPoint > 10 && arrayList.get(position).totalPoint <= 100) {
                score = getString(R.string._10_100_points);
            } else if (arrayList.get(position).totalPoint >= 100) {
                score = getString(R.string.above_100_points);
            }
            tvPoints.setText(String.format(score, Utils.nFormate(arrayList.get(position).totalPoint)));
        } else {
            tvPoints.setText(String.format(getString(R.string._points), Utils.nFormate(arrayList.get(position).totalPoint)));
        }

        if (arrayList.get(position).point == 0) {
            tvVerified.setVisibility(View.INVISIBLE);
            tvPoints.setBackground(ContextCompat.getDrawable(this, R.drawable.red_rounded_corner));
        } else {
            tvVerified.setVisibility(View.VISIBLE);
            tvPoints.setBackground(ContextCompat.getDrawable(this, R.drawable.green_rounded_corner));
        }

        LinearLayout llMain = view.findViewById(R.id.ll_main);
        llMain.setOnClickListener(view1 -> {
            String item = arrayList.get(position).title;
            if (item.equals(getString(R.string.email))) {
                if (arrayList.get(position).point == 0) {
                    if (!isEmpty(profileData.email)) {
                        Intent i = new Intent(this, VerifyEmailActivity.class);
                        startActivityForResult(i, REQ_EMAIL_VERIFICATION);
                    } else {
                        toastMessage(getString(R.string.please_enter_your_email_in_profile));
                    }
                } else {
                    toastMessage(getString(R.string.you_already_verify_email));
                }
            } else if (item.equals(getString(R.string.phonenumber))) {
                if (arrayList.get(position).point == 0) {
                    Intent i = new Intent(this, VerifyPhoneNumberActivity.class);
                    startActivityForResult(i, REQ_PHONE_VERIFICATION);
                } else {
                    toastMessage(getString(R.string.you_already_verify_phone_number));
                }
            } else if (item.equals(getString(R.string.facebook))) {
                if (arrayList.get(position).point == 0) {
                    verificationActivityVM.showDialog();
                } else {
                    toastMessage(getString(R.string.you_already_verify_facebook));
                }
            } else if (item.equals(getString(R.string.payment))) {
                if (arrayList.get(position).point == 0) {
                    Intent i = new Intent(this, VerifyPaymentActivity.class);
                    startActivityForResult(i, REQ_PAYMENT_VERIFICATION);
                } else {
                    toastMessage(getString(R.string.you_already_verify_payment));
                }
            } else if (item.equals(getString(R.string.verify_id))) {
                if (arrayList.get(position).point == 0) {
                    Intent i = new Intent(this, VerifyIDActivity.class);
                    startActivityForResult(i, REQ_ID_VERIFICATION);
                } else {
                    toastMessage(getString(R.string.you_already_done_verifyid));
                }
            } else if (item.equals(getString(R.string.mawthooq))) {
                if (arrayList.get(position).point == 0) {
                    Intent i = new Intent(this, VerifyIDActivity.class);
                    i.putExtra("screen", "maw");
                    startActivityForResult(i, REQ_ID_VERIFICATION);
                } else {
                    toastMessage(getString(R.string.you_already_done_verifyid));
                }
            }
        });
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        if (data.trustRate != null) {
            binding.imgTriangle1.setVisibility(View.INVISIBLE);
            binding.imgTriangle2.setVisibility(View.INVISIBLE);
            binding.imgTriangle3.setVisibility(View.INVISIBLE);
            binding.imgTriangle4.setVisibility(View.INVISIBLE);
            binding.tvVeryLow.setVisibility(View.INVISIBLE);
            binding.tvLow.setVisibility(View.INVISIBLE);
            binding.tvGood.setVisibility(View.INVISIBLE);
            binding.tvVeryGood.setVisibility(View.INVISIBLE);

            verificationActivityVM.getList(data.trustRate);

            int trustScore = 0;
            if (data.trustRate.email != 0) {
                trustScore = trustScore + 5;
            }
            if (data.trustRate.phoneNumber != 0) {
                trustScore = trustScore + 5;
            }
            if (data.trustRate.google != 0) {
                trustScore = trustScore + 10;
            }
            if (data.trustRate.payment != 0) {
                trustScore = trustScore + 20;
            }
            if (data.trustRate.mawthooq != 0) {
                trustScore = trustScore + 30;
            }
            if (data.trustRate.verifyId != 0) {
                trustScore = trustScore + 30;
            }

            binding.tvCurrentTrustScore.setText(String.format(getString(R.string.current_trust_score), Utils.nFormate(trustScore)));

            if (trustScore > 80) {
                binding.imgTriangle4.setVisibility(View.VISIBLE);
                binding.tvVeryGood.setVisibility(View.VISIBLE);
            } else if (trustScore > 55) {
                binding.imgTriangle3.setVisibility(View.VISIBLE);
                binding.tvGood.setVisibility(View.VISIBLE);
            } else if (trustScore > 27) {
                binding.imgTriangle2.setVisibility(View.VISIBLE);
                binding.tvLow.setVisibility(View.VISIBLE);
            } else {
                binding.imgTriangle1.setVisibility(View.VISIBLE);
                binding.tvVeryLow.setVisibility(View.VISIBLE);
            }
        } else {
            verificationActivityVM.getList(null);
            binding.linIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_PHONE_VERIFICATION:
            case REQ_EMAIL_VERIFICATION:
            case REQ_ID_VERIFICATION:
            case REQ_PAYMENT_VERIFICATION:
                getProfile();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
