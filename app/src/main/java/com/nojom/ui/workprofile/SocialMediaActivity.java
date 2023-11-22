package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.MyPlatformAdapter;
import com.nojom.adapter.SelectedPlatformAdapter;
import com.nojom.adapter.SocialMediaAdapter;
import com.nojom.adapter.SocialPlatformAdapter;
import com.nojom.databinding.ActivityPlatformBinding;
import com.nojom.databinding.ActivitySocialMediaBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.ItemMoveCallback;

import java.util.ArrayList;
import java.util.List;

public class SocialMediaActivity extends BaseActivity implements SocialMediaAdapter.OnClickPlatformListener {
    private ActivitySocialMediaBinding binding;
    private SocialPlatformResponse.Data selectedPlatform = null;
    private SocialMediaAdapter mAdapter;
    private ArrayList<SocialPlatformResponse.Data> socialList;
    private ArrayList<SocialMedia> socialMediaList;
    private MyPlatformActivityVM nameActivityVM;
    List<SocialMedia.SocialPlatform> socialPlatformList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_social_media);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        nameActivityVM.getInfluencerPlatform(this);
        socialMediaList = new ArrayList<>();
        socialPlatformList = new ArrayList<>();
//        nameActivityVM.getSocialPlatforms(this);

        setUI();
    }

    private void setUI() {
        binding.progress.tvTitle.setText(getString(R.string.social_platform));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        nameActivityVM.getSocialDataList().observe(this, data -> {
            socialList = data;

            if (socialList != null && socialList.size() > 0) {
                setAdapter(socialList);
            }
        });

        nameActivityVM.getIsHideProgress().observe(this, integer -> {
            binding.progressBar.setVisibility(View.GONE);
        });

        nameActivityVM.getIsShowProgress().observe(this, integer -> {
            binding.progressBar.setVisibility(View.VISIBLE);
        });


    }

    private void setAdapter(ArrayList<SocialPlatformResponse.Data> data) {

        for (SocialPlatformResponse.Data plt : data) {

            socialPlatformList.add(new SocialMedia.SocialPlatform(plt.platformIcon, plt.name));

            /*for (ProfileResponse.SocialPlatform selPlt : getProfileData().profile_social_platform) {
                if (plt.id == selPlt.platform_id) {
                    plt.isSelected = true;
                    plt.username = selPlt.social_platform_url;
//                        plt.username = selPlt.getName(language);
                    plt.platformFollower = selPlt.followers;
                    break;
                }
            }*/
        }

        socialMediaList.add(new SocialMedia("Social", socialPlatformList));
        socialMediaList.add(new SocialMedia("Business", socialPlatformList));
        socialMediaList.add(new SocialMedia("Music", socialPlatformList));
        socialMediaList.add(new SocialMedia("Payment", socialPlatformList));
        socialMediaList.add(new SocialMedia("Entertainment", socialPlatformList));
        socialMediaList.add(new SocialMedia("Lifestyle", socialPlatformList));
        socialMediaList.add(new SocialMedia("Other", socialPlatformList));

        mAdapter = new SocialMediaAdapter(this, socialMediaList, this);
        binding.rvCategory.setAdapter(mAdapter);

        SelectedPlatformAdapter mAdapter = new SelectedPlatformAdapter(this, socialMediaList, null);
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvSelection);
        binding.rvSelection.setAdapter(mAdapter);
    }

    @Override
    public void onClickPlatform(SocialPlatformResponse.Data platform) {
        selectedPlatform = platform;
    }
}
