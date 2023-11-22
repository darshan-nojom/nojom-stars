package com.nojom.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.adapter.SwitchAccountAdapter;
import com.nojom.databinding.ActivitySwitchAccountBinding;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginSignUpActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchAccountActivity extends BaseActivity {
    private ActivitySwitchAccountBinding binding;
    private List<UserModel> userModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_switch_account);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(view -> onBackPressed());
        binding.toolbar.tvTitle.setText(getString(R.string.switch_account));
        userModelList = new ArrayList<>();

        int userid = getUserID();
        HashMap<String, String> accounts = Preferences.getMultipleAccounts(this);

        for (Map.Entry<String, String> entry : accounts.entrySet()) {

            // if give value is equal to value from entry
            // print the corresponding key
            Log.e("username", "----- " + entry.getKey());
            Log.e("jwt", "---- " + entry.getValue());
            UserModel userModel = null;//get logged in user data from JWT token
            try {
                userModel = new Gson().fromJson(Utils.decode(entry.getValue()), UserModel.class);
                userModel.jwt = entry.getValue();
                userModelList.add(userModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        SwitchAccountAdapter accountAdapter = new SwitchAccountAdapter(this, userModelList, userid);
        binding.recyclerView.setAdapter(accountAdapter);

        binding.tvAddAccount.setOnClickListener(v -> {

            Intent i = new Intent(this, LoginSignUpActivity.class);
//            i.putExtra(Constants.FROM_LOGIN, isLogin);
//            i.putExtra(Constants.LOGIN_FINISH, isNeedToFinish);
            startActivity(i);
        });
    }
}
