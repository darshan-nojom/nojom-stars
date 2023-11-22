package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.adapter.SwitchAccountAdapter;
import com.nojom.apis.GetPartnerQuestion;
import com.nojom.databinding.ActivityWorkMoreBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginSignUpActivity;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.ui.clientprofile.FreelancerProfileActivity;
import com.nojom.ui.discount.GetDiscountActivity;
import com.nojom.ui.partner.PartnerActivity;
import com.nojom.ui.settings.NotificationActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorkMoreActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener {

    private ActivityWorkMoreBinding binding;
    private WorkMoreActivityVM workMoreActivityVM;
    private GetPartnerQuestion getPartnerQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_more);
        binding.setActivity(this);
        workMoreActivityVM = ViewModelProviders.of(this).get(WorkMoreActivityVM.class);
        workMoreActivityVM.init(this);
        getPartnerQuestion = new GetPartnerQuestion();
        getPartnerQuestion.init(this);
        initData();
        managedAccountList();
    }

    private void initData() {
        setOnProfileLoadListener(this);
        new Thread(this::getProfile).start();

        Utils.trackFirebaseEvent(this, "Setting_Screen");

        getPartnerQuestion.getListMutableLiveData().observe(this, data -> {
            Intent intent = new Intent(WorkMoreActivity.this, PartnerActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        });

        workMoreActivityVM.getIsShow().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.imgPartner.setVisibility(View.INVISIBLE);
                binding.progressPartner.setVisibility(View.VISIBLE);
            } else {
                binding.imgPartner.setVisibility(View.VISIBLE);
                binding.progressPartner.setVisibility(View.GONE);
            }
        });
    }

    public void onClickProfile() {
        redirectActivity(WorkProfileActivity.class);
    }

    public void onClickBalance() {
        redirectActivity(BalanceActivity.class);
    }

    public void onClickDiscount() {
        redirectActivity(GetDiscountActivity.class);
    }

    public void onClickNotification() {
        redirectActivity(NotificationActivity.class);
    }

    public void onClickPartnerWithUs() {
        getPartnerQuestion.getPartnerQuestions();
    }

    public void onClickFeedback() {
        showFeedbackDialog(0,getString(R.string.feedback));
    }

    public void onClickHireFreelancer() {
        redirectActivity(HireFreelancersActivity.class);
    }

    public void onClickSetting() {
        redirectActivity(WorkSettingActivity.class);
    }

    public void onClickPublicProfile() {
        showSwitchAccountDialog();
    }

    public void onClickFreelancerProfile() {
        redirectActivity(FreelancerProfileActivity.class);
    }

    public void onClickDeleteAccountRequest() {
        workMoreActivityVM.deleteAccountRequestDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            new Thread(() -> {
                ProfileResponse profileData = Preferences.getProfileData(WorkMoreActivity.this);
                if (profileData != null) {
                    onProfileLoad(profileData);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        runOnUiThread(() -> {

            try {
                binding.tvUsername.setText(String.format(getString(R.string.hi_), data.username));
                if (data.email != null)
                    binding.tvEmail.setText(data.email);
//            if (data.profilePic != null && !TextUtils.isEmpty(data.profilePic)) {
                Log.e("IMAGE URL ", "===== " + getImageUrl() + data.profilePic);

                Glide.with(WorkMoreActivity.this).load(getImageUrl() + data.profilePic)
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

                if (data.percentage != null) {
                    String profilePercentage = Math.round(data.percentage.totalPercentage) + "%";
                    binding.tvProfileComplete.setText(Utils.getColorString(WorkMoreActivity.this,
                            getString(R.string.percent_complete, profilePercentage), profilePercentage, R.color.red));
                } else {
                    binding.tvProfileComplete.setVisibility(View.INVISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null)
            redirectTab(Constants.TAB_HOME);
        else
            super.onBackPressed();
    }

    private void showSwitchAccountDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_switch_account);
        dialog.setCancelable(true);
        TextView txtAddAccount = dialog.findViewById(R.id.tv_add_account);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

        if (accountAdapter != null) {
            accountAdapter.setDialog(dialog);
            recyclerView.setAdapter(accountAdapter);
        }

        txtAddAccount.setOnClickListener(v -> {
            dialog.dismiss();
            Intent i = new Intent(this, LoginSignUpActivity.class);
            startActivity(i);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private SwitchAccountAdapter accountAdapter;

    private void managedAccountList() {
        List<UserModel> userModelList = new ArrayList<>();
        int userid = getUserID();
        HashMap<String, String> accounts = Preferences.getMultipleAccounts(this);
        if (accounts != null && accounts.size() > 0) {
            for (Map.Entry<String, String> entry : accounts.entrySet()) {

                Log.e("username", "----- " + entry.getKey());
                UserModel userModel = null;//get logged in user data from JWT token
                try {
                    userModel = new Gson().fromJson(Utils.decode(entry.getValue()), UserModel.class);
                    userModel.jwt = entry.getValue();
                    userModelList.add(userModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            accountAdapter = new SwitchAccountAdapter(this, userModelList, userid);
        }
    }
}
