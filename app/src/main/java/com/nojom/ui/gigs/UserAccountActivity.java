package com.nojom.ui.gigs;

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
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ActivityMyGigsBinding;
import com.nojom.databinding.ActivityUserAccountBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.MyProfileActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class UserAccountActivity extends BaseActivity {
    private ActivityUserAccountBinding binding;
    private ProfileResponse profileData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account);

        initData();

    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        profileData = Preferences.getProfileData(this);

        if (profileData != null) {

            if (profileData.firstName != null && profileData.lastName != null) {
                binding.tvName.setTextColor(getColor(R.color.black));
                binding.tvName.setText(profileData.firstName + " " + profileData.lastName);
            }
            if (profileData.username != null) {
                binding.tvUserName.setTextColor(getColor(R.color.black));
                binding.tvUserName.setText("@" + profileData.username);
            }
            if (profileData.websites != null) {
                binding.tvLink.setTextColor(getColor(R.color.black));
                binding.tvLink.setText(profileData.websites);
            }

            Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(binding.imgProfile);
        }

        binding.tvEditProfile.setOnClickListener(v -> startActivity(new Intent(this, MyProfileActivity.class)));
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null)
            redirectTab(Constants.TAB_HOME);
        else
            super.onBackPressed();
    }

}
