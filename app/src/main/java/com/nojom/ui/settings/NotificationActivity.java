package com.nojom.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityNotificationBinding;
import com.nojom.model.Notification;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.Objects;

public class NotificationActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityNotificationBinding binding;
    private NotificationActivityVM notificationActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        notificationActivityVM = ViewModelProviders.of(this).get(NotificationActivityVM.class);
        notificationActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(view -> onBackPressed());
        binding.toolbar.tvTitle.setText(getString(R.string.notifications));

        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvServiceNotifications.setLayoutManager(new LinearLayoutManager(this));

        if (Preferences.readBoolean(this, Constants.ALL_NOTIFICATION, true))
            binding.segmentGroupAll.setPosition(1);
        else
            binding.segmentGroupAll.setPosition(0);

        binding.segmentGroupAll.setOnPositionChangedListener(status -> {
            if (status == 0)
                Preferences.writeBoolean(this, Constants.ALL_NOTIFICATION, false);
            else
                Preferences.writeBoolean(this, Constants.ALL_NOTIFICATION, true);
        });

        notificationActivityVM.getNotificationList();

        Utils.trackFirebaseEvent(this, "Notification_Setting_Screen");

        addObserver();
    }

    private void addObserver() {
        notificationActivityVM.getListMutableLiveData().observe(this, data -> {
            RecyclerviewAdapter adapter = new RecyclerviewAdapter(data,
                    R.layout.item_notification, NotificationActivity.this);
            binding.rvNotifications.setAdapter(adapter);
        });

//        notificationActivityVM.getMutableTopicData().observe(this, data -> {
//            NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this);
//            adapter.doRefresh(data);
//            binding.rvServiceNotifications.setAdapter(adapter);
//        });

        notificationActivityVM.getIsShow().observe(this, isShow -> {
            if (isShow) {
                binding.shimmerLayout.startShimmer();
//                binding.shimmerLayoutNew.startShimmer();
//                binding.shimmerLayoutNew.setVisibility(View.VISIBLE);
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                binding.shimmerLayout.stopShimmer();
//                binding.shimmerLayoutNew.stopShimmer();
//                binding.shimmerLayoutNew.setVisibility(View.GONE);
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.rvNotifications.setVisibility(View.VISIBLE);
                binding.rvServiceNotifications.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void bindView(View view, int position) {
        Notification item = Objects.requireNonNull(notificationActivityVM.getListMutableLiveData().getValue()).get(position);

        SegmentedButtonGroup segmentGroup = view.findViewById(R.id.segmentGroup);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        tvTitle.setText(item.name);

        runOnUiThread(() -> {
            segmentGroup.setPosition(item.status.equals("1") ? 1 : 0);

            segmentGroup.setOnPositionChangedListener(status -> notificationActivityVM.addNotification(item.id, status));
        });

    }
}
