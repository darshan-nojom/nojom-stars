package com.nojom.ui.workprofile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.ProfileMenuAdapter;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityLanguagesBinding;
import com.nojom.databinding.ActivityNewProfileBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.Language;
import com.nojom.model.ProfileMenu;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Response;

public class NewProfileActivity extends BaseActivity implements ProfileMenuAdapter.OnClickMenuListener {
    private ActivityNewProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_profile);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        ProfileResponse profileData = Preferences.getProfileData(this);

        if (profileData != null) {

            if (profileData.firstName != null && profileData.lastName != null) {
                binding.txtName.setText(profileData.firstName + " " + profileData.lastName);
            }
            if (profileData.username != null) {
                binding.txtUname.setText(String.format(getString(R.string.nojom_com_s), profileData.username));
            }
            if (profileData.website != null) {
                binding.txtStatus.setText(getString(R.string.available_to_work));
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
        List<ProfileMenu> profileMenuList = new ArrayList<>();
        List<ProfileMenu> profileMenuListOrigin = new ArrayList<>();
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.social_media), 1));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.overview), 2));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.portfolio), 3));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.work_with_1), 4));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.stores_products), 5));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.youtube), 6));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.partners), 7));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.agency), 8));

        if (profileData != null) {
            if (profileData.settings_order != null) {
                List<String> list = new ArrayList<>(Arrays.asList(profileData.settings_order.split(",")));
                for (String item : list) {
//                    if (!item.equals("6")) {
                    profileMenuList.add(profileMenuListOrigin.get(Integer.parseInt(item) - 1));
//                    }
                }
            } else {
                //                    if (item.id != 6) {
                //                    }
                profileMenuList.addAll(profileMenuListOrigin);
            }
        }


        ProfileMenuAdapter adapter = new ProfileMenuAdapter(this, profileMenuList, this, null);
        binding.rMenu.setAdapter(adapter);

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.imgSorting.setOnClickListener(v -> startActivity(new Intent(this, ReOrderProfileActivity.class)));
        binding.imgEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
    }

    @Override
    public void onClickMenu(ProfileMenu menu) {
        switch (menu.id) {
            case 1://social media
                startActivity(new Intent(this, NewSocialMediaActivity.class));
                break;
            case 2://overview
//                startActivity(new Intent(this, OverViewActivity.class));
                startActivity(new Intent(this, OverViewActivityNew.class));
                break;
            case 3://portfolio
                startActivity(new Intent(this, NewPortfolioActivity.class));
                break;
            case 4://work with
                startActivity(new Intent(this, WorkWithActivity.class));
                break;
            case 5://my store
                startActivity(new Intent(this, MyStoresActivity.class));
                break;
            case 6://youtube
                startActivity(new Intent(this, YoutubeActivity.class));
                break;
            case 7://partners
                startActivity(new Intent(this, MyPartnerActivity.class));
                break;
            case 8://agency
                redirectActivity(AgencyInfoActivity.class);
                break;
        }
    }
}
