package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.GigDeliveryTimeAdapter;
import com.nojom.databinding.ActivityCategoryBinding;
import com.nojom.databinding.ActivityDeliveryTimeBinding;
import com.nojom.model.GigDeliveryTimeModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class GigDeliveryActivity extends BaseActivity implements GigDeliveryTimeAdapter.OnClickDeliveryTimeListener {
    private ActivityDeliveryTimeBinding binding;
    private GigDeliveryTimeModel.Data selectedCategory = null;
    private GigDeliveryTimeAdapter mAdapter;
    private ArrayList<GigDeliveryTimeModel.Data> deliveryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delivery_time);
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.shimmerLayout.stopShimmer();
        if (getIntent() != null) {
            selectedCategory = (GigDeliveryTimeModel.Data) getIntent().getSerializableExtra("data");
            deliveryList = (ArrayList<GigDeliveryTimeModel.Data>) getIntent().getSerializableExtra("list");
        }

        setUI();
    }

    private void setUI() {
        binding.progress.tvTitle.setText(getString(R.string.delivery_time));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        if (deliveryList != null && deliveryList.size() > 0) {

            for (GigDeliveryTimeModel.Data cat : deliveryList) {
                if (selectedCategory != null && cat.id == selectedCategory.id) {
                    cat.isSelected = true;
                    break;
                }
            }

            mAdapter = new GigDeliveryTimeAdapter(this, deliveryList, this);
            binding.rvCategory.setAdapter(mAdapter);

        }

        binding.tvSave.setOnClickListener(v -> {
            if (selectedCategory == null) {
                toastMessage(getString(R.string.select_your_delivery));
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("delivery", selectedCategory);
            setResult(RESULT_OK, intent);
            finish();
        });

    }

    @Override
    public void onClickDeliveryTime(GigDeliveryTimeModel.Data delTime) {
        selectedCategory = delTime;
    }
}
