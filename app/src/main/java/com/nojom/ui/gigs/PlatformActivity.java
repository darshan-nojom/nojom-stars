package com.nojom.ui.gigs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.SocialChannelAdapter;
import com.nojom.databinding.ActivityPlatformBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.MyPlatformActivityVM;
import com.nojom.ui.workprofile.SocialMediaActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class PlatformActivity extends BaseActivity implements SocialChannelAdapter.OnClickPlatformListener {
    private ActivityPlatformBinding binding;
    private ConnectedSocialMedia.Data selectedPlatform = null;
    private SocialChannelAdapter mAdapter;
    private ArrayList<ConnectedSocialMedia.Data> socialList;
    private MyPlatformActivityVM nameActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_platform);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        if (getIntent() != null) {
            socialList = (ArrayList<ConnectedSocialMedia.Data>) getIntent().getSerializableExtra("data");
            selectedPlatform = (ConnectedSocialMedia.Data) getIntent().getSerializableExtra("platform");
        }

        setUI();


        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {

            if (data != null && data.size() > 0) {
                socialList = data;
                setUI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUI() {
        binding.progress.tvToolbarTitle.setText(getString(R.string.select_platform));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.rvCategory.setLayoutManager(new GridLayoutManager(this, 3));

        if (socialList != null && socialList.size() > 0) {
            binding.rvCategory.setVisibility(View.VISIBLE);
            binding.linPh.setVisibility(View.GONE);
            setAdapter(socialList);
            binding.progress.tvSave.setText(getString(R.string.add));
            binding.progress.tvSave.setVisibility(View.VISIBLE);
            binding.btnAdd.setText(getString(R.string.save));
        } else {
            binding.rvCategory.setVisibility(View.GONE);
            binding.linPh.setVisibility(View.VISIBLE);
            binding.progress.tvSave.setVisibility(View.GONE);
            String s = getString(R.string.add_new_platform);
            int[] colorList = {R.color.colorPrimary};
            String[] words = {getString(R.string.new_)};
            binding.txtAdd.setText(Utils.getBoldString(this, s, null, colorList, words));
            binding.btnAdd.setText(getString(R.string.add_platform));
        }

        binding.relSave.setOnClickListener(v -> {
            if (socialList == null || socialList.size() == 0) {
                Intent intent = new Intent(this, SocialMediaActivity.class);
                launchSomeActivity.launch(intent);
            } else {
                if (selectedPlatform == null) {
                    toastMessage(getString(R.string.select_your_channel));
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("channel", selectedPlatform);
                setResult(RESULT_OK, intent);
                finish();
            }

        });

        binding.progress.tvSave.setOnClickListener(v -> {
            Intent intent = new Intent(this, SocialMediaActivity.class);
            launchSomeActivity.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        nameActivityVM.getConnectedPlatform(PlatformActivity.this);
                    }
                }
            });

    private void setAdapter(ArrayList<ConnectedSocialMedia.Data> data) {
//        if (selectedPlatform != null) {
//            for (ConnectedSocialMedia.Data plt : data) {
//                if (plt.id == selectedPlatform.id) {
//                    plt.isSelected = true;
//                    plt.username = selectedPlatform.username;
//                    plt.followers = (selectedPlatform.followers);
//                }
//            }
//        }
        mAdapter = new SocialChannelAdapter(this, data, this);
        mAdapter.seTSelectedPlatform(selectedPlatform);
        binding.rvCategory.setAdapter(mAdapter);
    }

    @Override
    public void onClickPlatform(ConnectedSocialMedia.Data platform) {
        selectedPlatform = platform;
    }

}
