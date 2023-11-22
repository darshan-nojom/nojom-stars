package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.MyPlatformAdapter;
import com.nojom.databinding.ActivityPlatformBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class MyPlatformActivity extends BaseActivity implements MyPlatformAdapter.OnClickPlatformListener {
    private ActivityPlatformBinding binding;
    private SocialPlatformResponse.Data selectedPlatform = null;
    private MyPlatformAdapter mAdapter;
    private ArrayList<SocialPlatformResponse.Data> socialList;
    private MyPlatformActivityVM nameActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_platform);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        nameActivityVM.getInfluencerPlatform(this);
//        nameActivityVM.getSocialPlatforms(this);

        setUI();
    }

    private void setUI() {
        binding.progress.tvToolbarTitle.setText(getString(R.string.social_platform));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.save));
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

        binding.progress.tvSave.setOnClickListener(v -> {
            selectedPlatform = mAdapter.getSelectedCategory();

            if (selectedPlatform == null) {
                toastMessage(getString(R.string.select_your_profession));
                return;
            }
            StringBuilder sbId = new StringBuilder();
            StringBuilder sbUrl = new StringBuilder();
            StringBuilder sbFollow = new StringBuilder();

            for (SocialPlatformResponse.Data plt : mAdapter.getData()) {
                if (plt.username != null && !plt.username.isEmpty()) {
                    sbId.append(plt.id);
                    sbId.append(",");
                    sbUrl.append(plt.username);
                    sbUrl.append(",");
                    sbFollow.append(plt.platformFollower);
                    sbFollow.append(",");
                }
            }

            if (TextUtils.isEmpty(selectedPlatform.username)) {
                toastMessage(getString(R.string.enter_username));
                return;
            }

            if (selectedPlatform.followers == -1 || selectedPlatform.followers == 0) {
                toastMessage(getString(R.string.select_followers));
                return;
            }

            nameActivityVM.addSocialPlatforms(this, sbId.deleteCharAt(sbId.length() - 1).toString(),
                    sbUrl.deleteCharAt(sbUrl.length() - 1).toString(), sbFollow.deleteCharAt(sbFollow.length() - 1).toString());
        });
    }

    private void setAdapter(ArrayList<SocialPlatformResponse.Data> data) {
        for (SocialPlatformResponse.Data plt : data) {
            for (ProfileResponse.SocialPlatform selPlt : getProfileData().profile_social_platform) {
                if (plt.id == selPlt.platform_id) {
                    plt.isSelected = true;
                    plt.username = selPlt.social_platform_url;
//                        plt.username = selPlt.getName(language);
                    plt.platformFollower = selPlt.followers;
                    break;
                }
            }
        }
        mAdapter = new MyPlatformAdapter(this, data, this);
        binding.rvCategory.setAdapter(mAdapter);
    }

    @Override
    public void onClickPlatform(SocialPlatformResponse.Data platform) {
        selectedPlatform = platform;
    }
}
