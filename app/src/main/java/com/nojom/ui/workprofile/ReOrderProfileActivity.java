package com.nojom.ui.workprofile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.ProfileMenuAdapter;
import com.nojom.databinding.ActivityNewProfileBinding;
import com.nojom.databinding.ActivityReorderProfileBinding;
import com.nojom.model.ProfileMenu;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.ItemMoveCallback;
import com.nojom.util.Preferences;
import com.nojom.util.ReOrderItemMoveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReOrderProfileActivity extends BaseActivity implements ProfileMenuAdapter.OnClickMenuListener, ProfileMenuAdapter.UpdateSwipeListener {
    private ActivityReorderProfileBinding binding;
    private EditProfileActivityVM editProfileActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reorder_profile);
        editProfileActivityVM = ViewModelProviders.of(this).get(EditProfileActivityVM.class);
        editProfileActivityVM.init(this, null);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.imgSorting.setOnClickListener(v -> {
            editProfileActivityVM.updateProfile("1,2,3,4,5,7,8");
            Preferences.getProfileData(this).settings_order = "1,2,3,4,5,7,8";
            updateOrder();
        });
        binding.relSave.setOnClickListener(v -> {
            //save locally or globally
            onBackPressed();
        });
        updateOrder();
    }

    private void updateOrder() {
        ProfileResponse profileData = Preferences.getProfileData(this);

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
                Set<String> set = new HashSet<>(list);
                List<String> newList = new ArrayList<>(set);
                for (String item : newList) {
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

        ProfileMenuAdapter adapter = new ProfileMenuAdapter(this, profileMenuList, this, this);
        adapter.setReOrderScreen(true);
        ItemTouchHelper.Callback callback = new ReOrderItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rMenu);
        binding.rMenu.setAdapter(adapter);
    }

    @Override
    public void onClickMenu(ProfileMenu menu) {

    }

    @Override
    public void onSwipeSuccess(List<ProfileMenu> mDatasetFiltered) {
//        JSONArray jsonArray = new JSONArray();
//        JSONObject mainObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mDatasetFiltered.size(); i++) {
//            JSONObject jsonObject = new JSONObject();
            stringBuilder.append(mDatasetFiltered.get(i).id);
            stringBuilder.append(",");
//                jsonObject.put("id", mDatasetFiltered.get(i).id);
//                jsonObject.put("display_order", i + 1);
//                jsonArray.put(jsonObject);
        }
        //            mainObj.put("settings_order", jsonArray);
        editProfileActivityVM.updateProfile(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
    }
}
