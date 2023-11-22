package com.nojom.ui.startup;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nojom.R;
import com.nojom.adapter.HireItemsAdapter;
import com.nojom.databinding.ActivitySelectAccountBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginSignUpActivity;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Utils;

public class SelectAccountActivity extends BaseActivity {
    private ActivitySelectAccountBinding binding;
    private SelectAccountActivityVM selectAccountActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Foreground.get().isForeground()) {

//        }
        if (isLogin()) {
            redirectActivity(MainActivity.class);
            finish();
            return;
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_account);
        binding.setDataModel(this);
        selectAccountActivityVM = ViewModelProviders.of(this).get(SelectAccountActivityVM.class);
        initData();
        runOnUiThread(() -> selectAccountActivityVM.getList(SelectAccountActivity.this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkForMaintenance(this);
    }

    private void initData() {
        binding.rvHire.setLayoutManager(new GridLayoutManager(this, 4));
        binding.rvHire.addItemDecoration(new EqualSpacingItemDecoration(35));

        selectAccountActivityVM.getListMutableLiveData().observe(this, homePagerModels -> {
            HireItemsAdapter mHireAdapter = new HireItemsAdapter(homePagerModels, null);
            binding.rvHire.setAdapter(mHireAdapter);
        });
    }

    public void onClickHire() {
//        redirectUsingCustomTab("https://24taskclient.page.link/naxz");
        openClientAppOnPlaystore();
    }

    public void onClickWork() {
        redirectActivity(LoginSignUpActivity.class);
    }
}
