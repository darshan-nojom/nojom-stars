package com.nojom.ui.workprofile;

import static com.nojom.util.NumberTextWatcherForThousand.getDecimalFormat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.adapter.PlatformAdapter;
import com.nojom.adapter.ServiceAdapter;
import com.nojom.databinding.ActivityInfServiceBinding;
import com.nojom.databinding.DialogPlatformBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Serv;
import com.nojom.model.Services;
import com.nojom.model.SocialMediaResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfluencerServiceActivity extends BaseActivity implements ServiceAdapter.PlatformClickListener, PlatformAdapter.PlatformListener, BaseActivity.OnProfileLoadListener {

    private ActivityInfServiceBinding binding;
    private ServiceActivityVM serviceActivityVM;
    private MutableLiveData<Boolean> isAnyChanges = new MutableLiveData<>();
    private List<SocialMediaResponse.PlatformPrice> itemList;
    private ServiceAdapter adapter;
    private MyPlatformActivityVM myPlatformActivityVM;
    ArrayList<SocialMediaResponse.SocialPlatform> platformList;
    private Services influencerServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inf_service);
        serviceActivityVM = ViewModelProviders.of(this).get(ServiceActivityVM.class);
        serviceActivityVM.init(this);
        myPlatformActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        myPlatformActivityVM.getInfluencerPlatform(this);
        influencerServices = (Services) getIntent().getSerializableExtra("data");
        isAnyChanges.postValue(false);
        if (language.equals("ar")) {
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
        }

        String s = getString(R.string.you_will_offer_a_20_discount_across_all_platforms_when_a_brand_selects_them_all_and_the_brand_can_view_this_discount);
        int[] colorList = {R.color.red};
        String[] words = {"20%"};
        String[] fonts = {Constants.SFTEXT_BOLD};
        binding.tv11.setText(Utils.getBoldString(this, s, fonts, colorList, words));

        initData();

    }

    private void initData() {
        setOnProfileLoadListener(this);
        itemList = new ArrayList<>();

        if (influencerServices != null) {
            binding.etDesc.setText(influencerServices.service_description);
            if (influencerServices.services != null && influencerServices.services.size() > 0) {
                for (Serv selData : influencerServices.services) {

                    if (selData.id == -1) {
                        if (selData.price > 0) {
                            String formattedNumber = getDecimalFormat(enFormatValue(selData.price));
                            binding.etName.setText(formattedNumber);
                        }
                    } else {
                        itemList.add(new SocialMediaResponse.PlatformPrice(selData.name, selData.filename, selData.name_ar, selData.price + "", selData.id));
                    }
                }
            } else {
                defaultList();
            }
        } else {
            defaultList();
        }


        platformList = new ArrayList<>();

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.relSave.setOnClickListener(v -> {
            List<SocialMediaResponse.Price> finalData = new ArrayList<>();
            if (adapter != null && adapter.getData().size() == 0) {
                toastMessage(getString(R.string.select_platform));
                return;
            }
            if (adapter != null && adapter.getData().size() > 0) {
                for (SocialMediaResponse.PlatformPrice data : adapter.getData()) {
                    /*if (!TextUtils.isEmpty(data.price) && Double.parseDouble(data.price.replaceAll(",", "")) > 2147483647) {
                        toastMessage(getString(R.string.price_must_be_less_than_2147483647));
                        return;
                    }*/
                    if (TextUtils.isEmpty(data.getName(language))) {
                        toastMessage(getString(R.string.select_platform));
                        return;
                    }
                    if (!TextUtils.isEmpty(data.getName(language)) && TextUtils.isEmpty(data.price)) {
                        toastMessage(getString(R.string.please_enter_price_of) + " " + data.getName(language));
                        return;
                    }

                    if (!TextUtils.isEmpty(data.getName(language)) && !TextUtils.isEmpty(data.price) && data.id != -1) {
                        finalData.add(new SocialMediaResponse.Price(Double.parseDouble(data.price.replaceAll(",", "")), data.id));
                    }
                }
            }
//            if (!TextUtils.isEmpty(binding.etName.getText().toString()) && Double.parseDouble(binding.etName.getText().toString().replaceAll(",", "")) > 2147483647) {
//                toastMessage(getString(R.string.price_must_be_less_than_2147483647));
//                return;
//            }
            if (!TextUtils.isEmpty(binding.etName.getText().toString())) {
                finalData.add(new SocialMediaResponse.Price(Double.parseDouble(binding.etName.getText().toString().replaceAll(",", "")), -1));
            }

            serviceActivityVM.addServices(finalData, binding.etDesc.getText().toString().trim());
            //save APIs
        });

        serviceActivityVM.getIsShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        binding.tvAddPlatform.setOnClickListener(v -> {
            itemList.add(new SocialMediaResponse.PlatformPrice());
            adapter.doRefresh(itemList.size() - 1, itemList);
        });

        adapter = new ServiceAdapter(this, this, isAnyChanges);
        binding.rMenu.setAdapter(adapter);
        adapter.doRefresh(-1, itemList);

        myPlatformActivityVM.getSocialMediaDataList().observe(this, data -> {
            for (SocialMediaResponse.Data value : data) {
                if (value.social_platforms != null) {
                    platformList.addAll(value.social_platforms);
                }
            }
        });

        isAnyChanges.observe(this, aBoolean -> {
            DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
            binding.tvSave.setTextColor(getResources().getColor(R.color.white));
        });

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isAnyChanges.postValue(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    binding.etName.removeTextChangedListener(this);
                    String value = binding.etName.getText().toString();


                    if (!value.equals("")) {

                        if (value.startsWith(".")) { //adds "0." when only "." is pressed on begining of writting
                            binding.etName.setText("0.");
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            binding.etName.setText(""); //Prevents "0" while starting but not "0."

                        }


                        String str = binding.etName.getText().toString().replaceAll(",", "");
                        binding.etName.setText(getDecimalFormat(str));
                        binding.etName.setSelection(binding.etName.getText().toString().length());
                    }
                    binding.etName.addTextChangedListener(this);
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    binding.etName.addTextChangedListener(this);
                }
            }
        });
    }

    private void defaultList() {
        itemList.add(new SocialMediaResponse.PlatformPrice());
        itemList.add(new SocialMediaResponse.PlatformPrice());
        itemList.add(new SocialMediaResponse.PlatformPrice());
    }

    int selectedPos;
    SocialMediaResponse.PlatformPrice selectedPosData;

    @Override
    public void onPlatformClick(int pos, SocialMediaResponse.PlatformPrice data) {
        //open dialog
        if (adapter != null && adapter.getItemCount() > 0) {
            for (SocialMediaResponse.SocialPlatform mainPlat : platformList) {
                for (SocialMediaResponse.PlatformPrice selPlt : adapter.getSelectedPlatform()) {
                    if (mainPlat.id == selPlt.id) {
                        mainPlat.isSelect = true;
                        break;
                    } else {
                        mainPlat.isSelect = false;
                    }
                }
            }
        }
        selectedPos = pos;
        selectedPosData = data;
        platformDialog(pos, data);
    }

    Dialog dialogPlatform;
    DialogPlatformBinding platformBinding;
    PlatformAdapter platformAdapter;

    public void platformDialog(int pos, SocialMediaResponse.PlatformPrice data) {
        dialogPlatform = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogPlatform.setTitle(null);
        platformBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_platform, null, false);
        platformBinding.title.setText(getString(R.string.select_platform));
        dialogPlatform.setContentView(platformBinding.getRoot());
        dialogPlatform.setCancelable(true);
        if (language.equals("ar")) {
            setArFont(platformBinding.title, Constants.FONT_AR_BOLD);
        }

        platformBinding.tvCancel.setOnClickListener(view -> dialogPlatform.dismiss());

        platformBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (platformAdapter != null) {
                    platformAdapter.getFilter().filter("" + charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        platformAdapter = new PlatformAdapter(this, this);
        platformBinding.rMenu.setAdapter(platformAdapter);
        platformAdapter.doRefresh(platformList);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogPlatform.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogPlatform.show();
        dialogPlatform.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogPlatform.getWindow().setAttributes(lp);

        dialogPlatform.setOnDismissListener(dialogInterface -> {
            selectedPos = -1;
            selectedPosData = null;
        });
    }

    @Override
    public void onPlatformSelect(int pos, SocialMediaResponse.SocialPlatform data) {
        if (selectedPos != -1) {
            adapter.itemChanged(selectedPos, data.name, data.nameAr, data.id, data.filename);
            selectedPos = -1;

            if (dialogPlatform != null) {
                dialogPlatform.dismiss();
            }

            DrawableCompat.setTint(binding.relSave.getBackground(), ContextCompat.getColor(this, R.color.black));
            binding.tvSave.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        serviceActivityVM.getIsShowProgress().postValue(false);
        finish();
        finishToRight();
    }
}
