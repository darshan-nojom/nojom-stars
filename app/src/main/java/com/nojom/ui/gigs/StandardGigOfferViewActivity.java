package com.nojom.ui.gigs;

import static com.nojom.ui.workprofile.ChooseOfferActivity.chooseOfferActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.GigAdapter;
import com.nojom.adapter.GigViewAsImageAdapter;
import com.nojom.databinding.ActivityStandardgigOffferViewBinding;
import com.nojom.model.GigDetails;
import com.nojom.model.GigPackages;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StandardGigOfferViewActivity extends BaseActivity {
    private ActivityStandardgigOffferViewBinding binding;
    private int selectedPackagePosition;
    private ArrayList<GigPackages.Data> packages;
    private ArrayList<TextView> tvList = new ArrayList<>();
    private String title, desc;
    private ArrayList<ImageFile> fileList;
    private GigDetails gigDetails;
    private String cUsername;
    private long cUserid;
    private CreateCustomGigsActivityVM createCustomGigsActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_standardgig_offfer_view);

        createCustomGigsActivityVM = new CreateCustomGigsActivityVM();
        createCustomGigsActivityVM.init(this);

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        fileList = getIntent().getParcelableArrayListExtra("files");
        packages = (ArrayList<GigPackages.Data>) getIntent().getSerializableExtra("packages");
        gigDetails = (GigDetails) getIntent().getSerializableExtra("gigDetail");
        cUsername = getIntent().getStringExtra("cUsername");
        cUserid = getIntent().getLongExtra("cUserId", 0);

        initData();
        Utils.trackFirebaseEvent(this, "Gig_View_As_Public_Screen");
    }

    private void initData() {
        ProfileResponse profileData = Preferences.getProfileData(this);

        binding.rvRequirements.setLayoutManager(new LinearLayoutManager(this));
        binding.imgBack.setOnClickListener(v -> onBackPressed());
//        binding.relContinue.setOnClickListener(v -> startActivity(new Intent(this, UpgradeGigsActivity.class)));

        binding.tvName.setText(getProperName(profileData.firstName, profileData.lastName, profileData.username));
        try {
            if (profileData.averageRate != null) {
                String rate = Utils.numberFormat(profileData.averageRate, 1);
                binding.tvRating.setText(String.format("(%s)", rate));
                binding.ratingbar.setRating(Float.parseFloat(rate));
            }
        } catch (NumberFormatException e) {
            binding.ratingbar.setRating(0);
        }

        Glide.with(this).load(getImageUrl() + profileData.profilePic)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(binding.imgProfile);

        binding.tvTitle.applyCustomFontBold();
        binding.tvTitle.setText(title);
        binding.tvGigDescription.setText(desc);

        GigViewAsImageAdapter adapter = new GigViewAsImageAdapter(this, fileList);
        binding.viewPager.setAdapter(adapter);
        binding.indicator.setViewPager(binding.viewPager);

        if (packages != null && packages.size() > 0) {
            addTabs(packages);
            updatePackageWiseData(packages.get(0));
        }

        binding.txtSendOffer.setOnClickListener(v -> showConfDialog());

        createCustomGigsActivityVM.getIsShowProgressCreateOffer().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.txtSendOffer.setVisibility(View.INVISIBLE);
                binding.progressBarView.setVisibility(View.VISIBLE);
            } else {
                binding.txtSendOffer.setVisibility(View.VISIBLE);
                binding.progressBarView.setVisibility(View.GONE);
            }
        });

        createCustomGigsActivityVM.getCreateOfferSuccess().observe(this, createOfferResponse -> {
            chooseOfferActivity.finish();
            Preferences.saveCreateOffer(StandardGigOfferViewActivity.this, createOfferResponse);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addTabs(List<GigPackages.Data> packages) {
        for (int listPosition = 0; listPosition < packages.size(); listPosition++) {
            if (listPosition == 0) {
                selectedPackagePosition = listPosition;
            }
            TextView textViewName = new TextView(this);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "font/SF-Pro-Text-Medium.otf");
            textViewName.setTypeface(face);
            textViewName.setTextSize(14);
            if (listPosition == 0) { //left border black
                textViewName.setBackground(getResources().getDrawable(R.drawable.left_bottom_black));
                textViewName.setTextColor(getResources().getColor(R.color.white));
            } else if (packages.size() - 1 == listPosition) {
                textViewName.setBackground(getResources().getDrawable(R.drawable.right_bottom_white));
                textViewName.setTextColor(getResources().getColor(R.color.tab_gray));
            } else {
                textViewName.setBackgroundColor(getResources().getColor(R.color.white));
                textViewName.setTextColor(getResources().getColor(R.color.tab_gray));
            }

            textViewName.setText(getCurrency().equals("SAR") ? Utils.getDecimalValue(String.valueOf(packages.get(listPosition).price)) + " "+getString(R.string.sar)
                    : getString(R.string.dollar) + Utils.getDecimalValue(String.valueOf(packages.get(listPosition).price)));

            textViewName.setTag(packages.get(listPosition).id);

            if (packages.size() == 3 || packages.size() == 2) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1);
                textViewName.setLayoutParams(param);
            }
            textViewName.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    1,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            View view = new View(this);
            view.setPadding(0,
                    (int) getResources().getDimension(R.dimen._5sdp),
                    0,
                    (int) getResources().getDimension(R.dimen._5sdp));
            view.setBackgroundColor(getResources().getColor(R.color.white));
            view.setLayoutParams(param);

            tvList.add(textViewName);
            binding.llSubMain.addView(textViewName);
            if (packages.size() - 1 != listPosition) {
                binding.llSubMain.addView(view);
            }

            final int finalListPosition = listPosition;
            textViewName.setOnClickListener(v -> {
                selectedPackagePosition = finalListPosition;
                changeSelection(finalListPosition, packages);
                updatePackageWiseData(packages.get(finalListPosition));
            });
        }
    }

    private void changeSelection(int position, List<GigPackages.Data> packages) {
        for (int listPosition = 0; listPosition < packages.size(); listPosition++) {
            if (listPosition == 0) { //left border black
                if (position == listPosition) {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.left_bottom_black));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
                } else {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.left_bottom_white));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
                }
            } else if (packages.size() - 1 == listPosition) {
                if (position == listPosition) {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.right_bottom_black));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
                } else {
                    tvList.get(listPosition).setBackground(getResources().getDrawable(R.drawable.right_bottom_white));
                    tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
                }
            } else if (position == listPosition) {
                tvList.get(listPosition).setBackgroundColor(getResources().getColor(R.color.black));
                tvList.get(listPosition).setTextColor(getResources().getColor(R.color.white));
            } else {
                tvList.get(listPosition).setBackgroundColor(getResources().getColor(R.color.white));
                tvList.get(listPosition).setTextColor(getResources().getColor(R.color.tab_gray));
            }
        }
    }

    private void updatePackageWiseData(GigPackages.Data data) {
        try {
            binding.tvRevisions.setText(data.revisions != 0 ? "" + data.revisions : "");
            binding.tvPackageDesc.setText(TextUtils.isEmpty(data.packageDescription) ? "" : data.packageDescription);
            binding.tvPackageTitle.setText(TextUtils.isEmpty(data.name) ? "" : data.name);
            binding.tvDeliveryDays.setText(TextUtils.isEmpty(data.deliveryTime) ? "" : data.deliveryTime + "");

            if (data.requirements != null && data.requirements.size() > 0) {
                binding.rvRequirements.setVisibility(View.VISIBLE);
                GigAdapter adapter = new GigAdapter(this, data.requirements);
                binding.rvRequirements.setAdapter(adapter);
            } else {
                binding.rvRequirements.setVisibility(View.GONE);
            }

            binding.txtPrice.setText(getString(R.string.price) + " (" + Utils.getDecimalValue(String.valueOf(data.price)) + ")");

//            if (searchTags != null && data.searchTags.size() > 0) {
//                binding.txtTags.setText("" + data.searchTags.toString().replace("[", "").replace("]", ""));
//            } else {
//                binding.txtTags.setText("");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void showConfDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_conf_offer);
        dialog.setCancelable(true);

        TextView txtOffer = dialog.findViewById(R.id.txt_offer);
        TextView tvConfirm = dialog.findViewById(R.id.tv_confirm);
        TextView tvNo = dialog.findViewById(R.id.tv_no);

        txtOffer.setText(getString(R.string.do_you_want_to_send_an_offer_anant05, cUsername));

        tvNo.setOnClickListener(v -> dialog.dismiss());

        tvConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            RequestBody gigID = RequestBody.create("" + gigDetails.gigID, MultipartBody.FORM);
            RequestBody clientProfileIdBody = RequestBody.create("" + cUserid, MultipartBody.FORM);

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("gigID", gigID);
            map.put("clientID", clientProfileIdBody);

            createCustomGigsActivityVM.createOfferGig(map);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}
