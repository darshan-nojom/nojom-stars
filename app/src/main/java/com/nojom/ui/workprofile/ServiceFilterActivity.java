package com.nojom.ui.workprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityServiceFilterBinding;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.List;

public class ServiceFilterActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityServiceFilterBinding binding;
    private ServiceFilterActivityVM serviceFilterActivityVM;
    private int selectedPosition = 0;
    private RecyclerviewAdapter mAdapter;
    int selectedServiceId;
    ArrayList<SocialPlatformResponse.Data> socialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_service_filter);
        serviceFilterActivityVM = ViewModelProviders.of(this).get(ServiceFilterActivityVM.class);
        socialList = new ArrayList<>();
        initData();
        selectedServiceId = Preferences.readInteger(this, Constants.FILTER_ID, 0);
        new Thread(() -> serviceFilterActivityVM.init(ServiceFilterActivity.this)).start();

    }

    private void initData() {
        binding.tvApply.setOnClickListener(v -> {
            try {
                List<SocialPlatformResponse.Data> servicesList = socialList;
                if (servicesList == null || servicesList.size() == 0) {
                    return;
                }
                Intent i = new Intent();
                i.putExtra(Constants.SERVICE_ID, selectedPosition == 0 ? 0 : servicesList.get(selectedPosition).id);
                i.putExtra(Constants.SERVICE_NAME, selectedPosition == 0 ? getString(R.string.all_platforms) : servicesList.get(selectedPosition).getName(language));
                setResult(RESULT_OK, i);

//                AsyncTask.execute(() -> Preferences.writeInteger(ServiceFilterActivity.this, Constants.FILTER_ID, servicesList.get(selectedPosition).id));

                new Thread(() -> Preferences.writeInteger(ServiceFilterActivity.this, Constants.FILTER_ID, selectedPosition == 0 ? 0 : servicesList.get(selectedPosition).id)).start();

                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvCancel.setOnClickListener(v -> onBackPressed());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvServices.setLayoutManager(linearLayoutManager);
//        serviceFilterActivityVM.getLisDataMutableLiveData().observe(this, this::setAdapter);

        serviceFilterActivityVM.getSelectedPos().observe(this, pos -> selectedPosition = pos);

        serviceFilterActivityVM.getSocialDataList().observe(this, data -> {
            socialList.add(new SocialPlatformResponse.Data());//default for all platform
            socialList.addAll(data);
            for (int i = 0; i < socialList.size(); i++) {
                if (socialList.get(i).id == selectedServiceId || selectedServiceId == 0) {
                    selectedPosition = i;
                    break;
                }
            }
            setAdapter(socialList);
        });
    }

    private void setAdapter(List<SocialPlatformResponse.Data> servicesList) {
        if (servicesList != null && servicesList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter((ArrayList<?>) servicesList, R.layout.item_select_service, this);
            }
            mAdapter.doRefresh((ArrayList<?>) servicesList);
            if (binding.rvServices.getAdapter() == null) {
                binding.rvServices.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void bindView(View view, int position) {
        List<SocialPlatformResponse.Data> servicesList = socialList;
        if (servicesList == null || servicesList.size() == 0) {
            return;
        }
        final SocialPlatformResponse.Data service = servicesList.get(position);
        RelativeLayout rlView = view.findViewById(R.id.rl_view);
        TextView tvService = view.findViewById(R.id.tv_service);
        TextView tvSubService = view.findViewById(R.id.tv_subservice);
        ImageView imgService = view.findViewById(R.id.img_service);
        tvSubService.setVisibility(View.GONE);

        if (position == 0) {
            tvService.setText(getString(R.string.all_platforms));
        } else {
            tvService.setText(service.getName(language));
            if (!TextUtils.isEmpty(service.platformIcon)) {
                Glide.with(this)
                        .load(Uri.parse(service.platformIcon))
                        .placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgService);
            }
        }


        if (selectedPosition == position) {
            rlView.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_rounded_corner_15));
//            imgService.setColorFilter(ContextCompat.getColor(this, R.color.white));
            imgService.setImageResource(R.drawable.ic_funnel);
            tvService.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvSubService.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            rlView.setBackground(ContextCompat.getDrawable(this, R.drawable.white_rounded_corner_15));
//            imgService.clearColorFilter();
            tvService.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvSubService.setTextColor(ContextCompat.getColor(this, R.color.textgrayAccent));


        }

        view.setOnClickListener(v -> {
            selectedPosition = position;
            mAdapter.notifyDataSetChanged();
        });
    }
}
