package com.nojom.ui.gigs;

import static com.nojom.ui.workprofile.ChooseOfferActivity.chooseOfferActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.nojom.R;
import com.nojom.adapter.GigViewAsImageAdapter;
import com.nojom.adapter.RequirementDeadlineAdapter;
import com.nojom.adapter.RequirementPriceViewAdapter;
import com.nojom.databinding.ActivityGigOfferViewBinding;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigDetails;
import com.nojom.model.ProfileResponse;
import com.nojom.model.RequiremetList;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class OfferGigViewActivity extends BaseActivity {
    private ActivityGigOfferViewBinding binding;
    private ArrayList<RequiremetList.Data> requirementByCatListBinding;
    private String title, desc, gigPrice, deadDesc;
    private ArrayList<ImageFile> fileList;
    private ArrayList<GigCategoryModel.Deadline> deadlineList;
    private String cUsername;
    private long cUserid;
    private GigDetails gigDetails;
    private CreateCustomGigsActivityVM createCustomGigsActivityVM;
    private SocialPlatformResponse.Data selectedPlatform = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_gig_offer_view);

        createCustomGigsActivityVM = new CreateCustomGigsActivityVM();
        createCustomGigsActivityVM.init(this);

        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        deadDesc = getIntent().getStringExtra("deadDesc");
        deadlineList = (ArrayList<GigCategoryModel.Deadline>) getIntent().getSerializableExtra("delivery");
        gigPrice = getIntent().getStringExtra("gigprice");
        fileList = getIntent().getParcelableArrayListExtra("files");
        requirementByCatListBinding = (ArrayList<RequiremetList.Data>) getIntent().getSerializableExtra("packages");
        cUsername = getIntent().getStringExtra("cUsername");
        cUserid = getIntent().getLongExtra("cUserId", 0);
        gigDetails = (GigDetails) getIntent().getSerializableExtra("gigDetail");

        if (gigDetails != null && gigDetails.socialPlatform.size() > 0) {
            selectedPlatform = new SocialPlatformResponse.Data();
            selectedPlatform.followers = gigDetails.socialPlatform.get(0).followers;
            selectedPlatform.id = gigDetails.socialPlatform.get(0).socialPlatformID;
            selectedPlatform.username = gigDetails.socialPlatform.get(0).username;
            selectedPlatform.name = gigDetails.socialPlatform.get(0).name;
            selectedPlatform.platformIcon = gigDetails.socialPlatform.get(0).platformIcon;
        }

        initData();
    }

    private void initData() {
        ProfileResponse profileData = Preferences.getProfileData(this);

        binding.imgBack.setOnClickListener(v -> onBackPressed());
//        binding.relContinue.setOnClickListener(v -> startActivity(new Intent(this, UpgradeGigsActivity.class)));

        binding.tvName.setText(getProperName(profileData.firstName, profileData.lastName, profileData.username));

        binding.tvGigPrice.setText("" + gigPrice);
        try {
            if (profileData.averageRate != null) {
                String rate = Utils.numberFormat(profileData.averageRate, 1);
                binding.tvRating.setText(String.format("(%s)", rate));
                binding.ratingbar.setRating(Float.parseFloat(rate));
            }
        } catch (NumberFormatException e) {
            binding.ratingbar.setRating(0);
        }

        Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.mipmap.ic_launcher).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                return false;
            }
        }).into(binding.imgProfile);

        binding.tvTitle.applyCustomFontBold();
        binding.tvTitle.setText(title);
        binding.tvGigDescription.setText(desc);

        if (fileList != null && fileList.size() > 0) {
            GigViewAsImageAdapter adapter = new GigViewAsImageAdapter(this, fileList);
            binding.viewPager.setAdapter(adapter);
            binding.indicator.setViewPager(binding.viewPager);
        }

        if (requirementByCatListBinding != null && requirementByCatListBinding.size() > 0) {
            setRequirement();
        }

        addDeadlineView();

        binding.txtSendOffer.setOnClickListener(v -> showConfDialog());

        calculatePrice();

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
            Preferences.saveCreateOffer(OfferGigViewActivity.this, createOfferResponse);
            finish();
        });

        if (gigDetails.parentCategoryID == 4352 && selectedPlatform != null) {//social influencer
            binding.linPlatform.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(selectedPlatform.getName(language))) {
                binding.txtPlatform.setText(selectedPlatform.getName(language));
            }
            if (!TextUtils.isEmpty(selectedPlatform.username)) {
                binding.tvSocial.setText(selectedPlatform.username);
            }

            binding.imgPlatform.setOnClickListener(v -> ViewTooltip.on(this, binding.imgPlatform).color(getResources().getColor(R.color.black)).textColor(getResources().getColor(R.color.white)).autoHide(true, 3000).corner(30).position(ViewTooltip.Position.BOTTOM).text(Utils.getPlatformTxt(selectedPlatform.followers, this) + " " + getString(R.string.followers)).show());
        }
    }

    double selectedDeadlinePrice;

    private void addDeadlineView() {
        if (deadlineList != null && deadlineList.size() > 0) {
            final int[] position = {0};

            if (deadlineList.size() == 1) {
                binding.imgPrev.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.imgPrev.setEnabled(false);
                binding.imgNext.setEnabled(false);
            } else {
                binding.imgPrev.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                binding.imgPrev.setEnabled(true);
                binding.imgNext.setEnabled(true);
            }

            final String[] value = {deadlineList.get(position[0]).value + " " + (deadlineList.get(position[0]).type == 2 ? "" + getString(R.string.days) : "" + getString(R.string.hours))};
            binding.tvDeadline.setText(value[0]);


            selectedDeadlinePrice = deadlineList.get(0).price;

            binding.imgPrev.setOnClickListener(view1 -> {
                if (position[0] == 0) {
                    return;
                }

                position[0] = position[0] - 1;
                value[0] = deadlineList.get(position[0]).value + " " + (deadlineList.get(position[0]).type == 2 ? "" + getString(R.string.days) : "" + getString(R.string.hours));
                binding.tvDeadline.setText(value[0]);

                if (position[0] == 0) {
                    binding.imgPrev.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
                    binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    binding.imgPrev.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                selectedDeadlinePrice = deadlineList.get(position[0]).price;
                calculatePrice();
            });

            binding.imgNext.setOnClickListener(view1 -> {

                if (position[0] != deadlineList.size() - 1) {
                    position[0] = position[0] + 1;
                }
                value[0] = deadlineList.get(position[0]).value + " " + (deadlineList.get(position[0]).type == 2 ? "" + getString(R.string.days) : "" + getString(R.string.hours));
                binding.tvDeadline.setText(value[0]);

                if (position[0] == deadlineList.size() - 1) {
                    binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    binding.imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                binding.imgPrev.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                selectedDeadlinePrice = deadlineList.get(position[0]).price;
                calculatePrice();
            });

            binding.imgInfo.setOnClickListener(view -> {
                priceDialogCustom(null, null, deadlineList, 1);
            });
        }
    }

    private void setRequirement() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ArrayList<RequiremetList.Data> sortedList = new ArrayList<>();

        for (RequiremetList.Data reqData : requirementByCatListBinding) {

            View view = null;
            if (reqData.inputType == 1 || reqData.gigOtherInputType == 1) {//number

                if (reqData.gigReqType == 1) {//fix
                    //plus minus
                    view = inflater.inflate(R.layout.item_custom_gig_view_number, null);
                    TextView tvReqTitle = view.findViewById(R.id.tv_req_name);
                    TextView etQty = view.findViewById(R.id.et_quantity);
                    ImageView imgMinus = view.findViewById(R.id.img_minus);
                    ImageView imgPlus = view.findViewById(R.id.img_plus);
                    ImageView imgInfo = view.findViewById(R.id.img_info);

                    tvReqTitle.setText(reqData.name);
                    //etQty.setText(reqData.dataValue);

                    reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, reqData.dataValue) : Utils.priceWithout$(reqData.dataValue));
                    reqData.selectedQty = 0;

                    imgInfo.setOnClickListener(view1 -> {
                        priceDialogCustom(reqData, null, null, 2);
                    });

                    imgPlus.setOnClickListener(v -> {
                        String currentValue = etQty.getText().toString().trim();

                        currentValue = String.valueOf(Integer.parseInt(currentValue) + 1);
                        etQty.setText(currentValue);

                        reqData.selectedQty = Double.parseDouble(currentValue);
                        calculatePrice();
                    });

                    imgMinus.setOnClickListener(v -> {
                        String currentValue = etQty.getText().toString().trim();

                        if (Integer.parseInt(currentValue) > 0) {
                            currentValue = String.valueOf(Integer.parseInt(currentValue) - 1);
                            etQty.setText(currentValue);
                            reqData.selectedQty = Double.parseDouble(currentValue);
                            calculatePrice();
                        }
                    });

                } else if (reqData.gigReqType == 3) {//custom
                    //choose option
                    view = customViewOptions(inflater, reqData);
                }

            } else if (reqData.inputType == 3 || reqData.gigOtherInputType == 3) {//text

                if (reqData.gigReqType == 1) {//fix
                    //radiobutton
                    sortedList.add(reqData);
                } else if (reqData.gigReqType == 3) {//custom
                    view = customViewOptions(inflater, reqData);
                }
            } else if (reqData.inputType == -1) {//other
                sortedList.add(reqData);
            }

            if (view != null) {
                binding.linRequirement.addView(view);
            }
        }

        for (RequiremetList.Data reqData : sortedList) {//without option view
            View view = null;
            if (reqData.inputType == 3 || reqData.gigOtherInputType == 3) {//text
                if (reqData.gigReqType == 1) {//fix
                    //radiobutton
                    view = inflater.inflate(R.layout.item_custom_gig_view_radio, null);
                    TextView tvReqTitle = view.findViewById(R.id.tv_req_name);
                    TextView tvAmnt = view.findViewById(R.id.tv_req_amount);
                    CheckBox imgCheck = view.findViewById(R.id.img_checked);
                    ImageView imgInfo = view.findViewById(R.id.img_info);

                    tvReqTitle.setText(reqData.name);

                    if (getCurrency().equals("SAR")) {
                        if (reqData.dataValue.endsWith(getString(R.string.sar))) {
                            tvAmnt.setText(reqData.dataValue);
                        } else {
                            tvAmnt.setText(Utils.getDecimalValue(reqData.dataValue) + " " + getString(R.string.sar));
                        }
                    } else {
                        if (reqData.dataValue.startsWith(getString(R.string.dollar))) {
                            tvAmnt.setText(reqData.dataValue);
                        } else {
                            tvAmnt.setText(getString(R.string.dollar) + Utils.getDecimalValue(reqData.dataValue));
                        }
                    }

                    imgInfo.setOnClickListener(view1 -> {
                        priceDialogCustom(reqData, null, null, 2);
                    });

                    imgCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            reqData.selectedQty = 1;
                            reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, reqData.dataValue) : Utils.priceWithout$(reqData.dataValue));
                        } else {
                            reqData.selectedPrice = 0;
                            reqData.selectedQty = 0;
                        }
                        calculatePrice();
                    });
                }
            } else if (reqData.inputType == -1) {//other
                view = inflater.inflate(R.layout.item_custom_gig_view, null);
                TextView tvReqTitle = view.findViewById(R.id.tv_req_name);
                TextView tvAmnt = view.findViewById(R.id.tv_req_amount);
                ImageView imgInfo = view.findViewById(R.id.img_info);
                tvReqTitle.setText(reqData.name);
                tvAmnt.setText("" + reqData.dataValue);

                reqData.selectedQty = 1;
                reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, reqData.dataValue) : Utils.priceWithout$(reqData.dataValue));

                imgInfo.setOnClickListener(view1 -> {
                    priceDialogCustom(reqData, null, null, 2);
                });

            }

            if (view != null) {
                binding.linRequirement.addView(view);
            }
        }
    }

    final int[] position = {0};
    private View customViewOptions(LayoutInflater inflater, RequiremetList.Data reqData) {
        View view = inflater.inflate(R.layout.item_custom_gig_view_option, null);
        TextView tvReqTitle = view.findViewById(R.id.tv_req_name);
        TextView tvAmnt = view.findViewById(R.id.tv_req_amount);
        ImageView imgBack = view.findViewById(R.id.img_back);
        ImageView imgNext = view.findViewById(R.id.img_next);
        ImageView imgInfo = view.findViewById(R.id.img_info);

        tvReqTitle.setText(reqData.name);

        ArrayList<RequiremetList.CustomData> customDataList = new ArrayList<>();
        RequiremetList.CustomData cuData = new RequiremetList.CustomData();
        cuData.dataReq = reqData.dataReq;
        cuData.dataValue = reqData.dataValue;
        cuData.id = reqData.id;
        customDataList.add(cuData);
        if (reqData.customData != null && reqData.customData.size() > 0) {
            customDataList.addAll(reqData.customData);
        }

        final String[] value = {customDataList.get(position[0]).dataReq};
        tvAmnt.setText(value[0]);

        reqData.selectedQty = 1;
        reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, reqData.dataValue) : Utils.priceWithout$(reqData.dataValue));

        if (customDataList.size() == 1) {
            imgBack.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
            imgNext.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
            imgBack.setEnabled(false);
            imgNext.setEnabled(false);
        } else {
            imgBack.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
            imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            imgBack.setEnabled(true);
            imgNext.setEnabled(true);
        }

        imgInfo.setOnClickListener(view1 -> priceDialogCustom(reqData, customDataList, null, 3));

        imgBack.setOnClickListener(view1 -> {
            if (position[0] == 0) {
                return;
            }

            position[0] = position[0] - 1;
            value[0] = customDataList.get(position[0]).dataReq;
            tvAmnt.setText(value[0]);

            if (position[0] == 0) {
                imgBack.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

            reqData.selectedQty = 1;
            reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, customDataList.get(position[0]).dataValue) : Utils.priceWithout$(customDataList.get(position[0]).dataValue));
            calculatePrice();
        });

        imgNext.setOnClickListener(view1 -> {

            if (position[0] != customDataList.size() - 1) {
                position[0] = position[0] + 1;
            }
            value[0] = customDataList.get(position[0]).dataReq;
            tvAmnt.setText(value[0]);

            if (position[0] == customDataList.size() - 1) {
                imgNext.setColorFilter(ContextCompat.getColor(this, R.color.placeholder_bg), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                imgNext.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);

            reqData.selectedQty = 1;
            reqData.selectedPrice = Double.parseDouble(getCurrency().equals("SAR") ? Utils.priceWithoutSAR(this, customDataList.get(position[0]).dataValue)
                    : Utils.priceWithout$(customDataList.get(position[0]).dataValue));

            calculatePrice();
        });
        return view;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void priceDialogCustom(RequiremetList.Data reqData, ArrayList<RequiremetList.CustomData> customDataList, ArrayList<GigCategoryModel.Deadline> deadlineList, int viewType) {

        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_custom_price_option);
        dialog.setCancelable(true);

        TextView txtName = dialog.findViewById(R.id.txt_name);
        RecyclerView rvCustom = dialog.findViewById(R.id.rv_custom);
        TextView description = dialog.findViewById(R.id.txt_description);
        TextView txtReq = dialog.findViewById(R.id.txt_req);
        TextView txtValue = dialog.findViewById(R.id.txt_price);
        LinearLayout linView = dialog.findViewById(R.id.linearView);

        LinearLayoutManager linearLayoutManagerCustomDesigner = new LinearLayoutManager(this);
        rvCustom.setLayoutManager(linearLayoutManagerCustomDesigner);

        if (viewType == 1) {//delivery time
            txtName.setText(getString(R.string.delivery_time));
            if (!TextUtils.isEmpty(deadDesc)) {
                description.setVisibility(View.VISIBLE);
                description.setText(deadDesc);
            }
        } else {
            txtName.setText(reqData.name);
        }

        if (reqData != null && !TextUtils.isEmpty(reqData.reqDescription)) {
            description.setVisibility(View.VISIBLE);
            description.setText(reqData.reqDescription);
        }

        if ((deadlineList != null && deadlineList.size() > 8) || (reqData != null && reqData.customData != null && reqData.customData.size() > 8)) {
            ViewGroup.LayoutParams params = rvCustom.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen._250sdp);
            rvCustom.setLayoutParams(params);
        } else {
            rvCustom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        if (viewType == 1) {//delivery time
            RequirementDeadlineAdapter customOptionGigAdapter = new RequirementDeadlineAdapter(this, deadlineList);
            rvCustom.setAdapter(customOptionGigAdapter);
        } else if (viewType == 2) {
            linView.setVisibility(View.VISIBLE);
            txtReq.setText("" + reqData.dataReq);

            if (reqData.dataValue.startsWith(getString(R.string.dollar)) || reqData.dataValue.endsWith(getString(R.string.sar))) {
                txtValue.setText(reqData.dataValue);
            } else {
                txtValue.setText(getCurrency().equals("SAR") ? reqData.dataValue + " " + getString(R.string.sar) : getString(R.string.dollar) + reqData.dataValue);
            }
        } else if (viewType == 3) {
            RequirementPriceViewAdapter customOptionGigAdapter = new RequirementPriceViewAdapter(this, customDataList);
            rvCustom.setAdapter(customOptionGigAdapter);
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
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

            JSONArray jsonArray = new JSONArray();
            if (gigDetails.customPackages != null && gigDetails.customPackages.size() > 0) {

                for (GigDetails.CustomRequirements data : gigDetails.customPackages) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("gig_requirment_id", data.id);
                        object.put("customPackageID", data.requirmentDetails.get(position[0]).id);
                        jsonArray.put(object);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            RequestBody reqBody = RequestBody.create(jsonArray.toString(), MediaType.parse("application/json"));

            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("gigID", gigID);
            map.put("clientID", clientProfileIdBody);
            map.put("requirments", reqBody);

            createCustomGigsActivityVM.createOfferGig(map);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void calculatePrice() {
        double amount = 0;
        for (RequiremetList.Data data : requirementByCatListBinding) {
            if (data.selectedPrice > 0 && data.selectedQty >= 0) {
                amount = amount + (data.selectedPrice * data.selectedQty);
            } else if (data.selectedPrice > 0) {
                amount = amount + data.selectedPrice;
            }
        }

        double finalAmount = amount + selectedDeadlinePrice;
        if (getCurrency().equals("SAR")) {
            binding.txtPrice.setText(getString(R.string.price) + " (" + Utils.priceWithSAR(this, Utils.getDecimalValue("" + finalAmount)) + ")");
        } else {
            binding.txtPrice.setText(getString(R.string.price) + " (" + Utils.priceWith$(Utils.getDecimalValue("" + finalAmount), this) + ")");
        }
        binding.txtPrice.setTag(finalAmount);
    }
}
