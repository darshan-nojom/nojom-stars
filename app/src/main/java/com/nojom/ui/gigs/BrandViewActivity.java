package com.nojom.ui.gigs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Math.abs;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nojom.R;
import com.nojom.adapter.CustomAdapter;
import com.nojom.adapter.ProfilePartnersAdapter;
import com.nojom.adapter.ProfileProductsAdapter;
import com.nojom.adapter.ProfileStoreAdapter;
import com.nojom.adapter.ProfileYoutubeAdapter;
import com.nojom.adapter.SelectedServiceAdapter;
import com.nojom.adapter.SkillsListAdapter;
import com.nojom.adapter.SocialMediaAdapterProfile;
import com.nojom.adapter.WorkwithAdapter;
import com.nojom.databinding.ActivityProfileBrandBinding;
import com.nojom.databinding.ViewAgencyBinding;
import com.nojom.databinding.ViewMyStoreBinding;
import com.nojom.databinding.ViewOverviewBinding;
import com.nojom.databinding.ViewPartnerBinding;
import com.nojom.databinding.ViewPortfolioBinding;
import com.nojom.databinding.ViewServicesBinding;
import com.nojom.databinding.ViewSocialMediaBinding;
import com.nojom.databinding.ViewWorkwithBinding;
import com.nojom.databinding.ViewYoutubeBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.GetAgentCompanies;
import com.nojom.model.GetProduct;
import com.nojom.model.GetStores;
import com.nojom.model.GetYoutube;
import com.nojom.model.Portfolios;
import com.nojom.model.ProfileMenu;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Serv;
import com.nojom.model.Skill;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.EditProfileActivity;
import com.nojom.ui.workprofile.EditProfileActivityVM;
import com.nojom.ui.workprofile.GetServiceActivityVM;
import com.nojom.ui.workprofile.MyPartnerActivityVM;
import com.nojom.ui.workprofile.MyPlatformActivityVM;
import com.nojom.ui.workprofile.MyStoreActivityVM;
import com.nojom.ui.workprofile.NewPortfolioActivityVM;
import com.nojom.ui.workprofile.WorkWithActivityVM;
import com.nojom.ui.workprofile.YoutubeActivityVM;
import com.nojom.util.Constants;
import com.nojom.util.NumberTextWatcherForThousand;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BrandViewActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener {
    private ActivityProfileBrandBinding binding;
    private ProfileResponse profileData;
    private EditProfileActivityVM editProfileActivityVM;
    List<ProfileMenu> profileMenuListOrigin;
    boolean isSelectPublic = false;
    private GetServiceActivityVM serviceActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_brand);
        editProfileActivityVM = ViewModelProviders.of(this).get(EditProfileActivityVM.class);
        editProfileActivityVM.init(this, null);
        serviceActivityVM = ViewModelProviders.of(this).get(GetServiceActivityVM.class);
        serviceActivityVM.init(this);
        setOnProfileLoadListener(this);
        if (language.equals("ar")) {
            setArFont(binding.tvName, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tvUserName, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tvLink, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtOffer, Constants.FONT_AR_REGULAR);
            setArFont(binding.toolbarTitle, Constants.FONT_AR_MEDIUM);
            setArFont(binding.imgLanguage, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtEdit, Constants.FONT_AR_REGULAR);
        }
        initData();

    }

    private void setPreview() {
        if (getEmailStatus() == 3 && getWhatsappStatus() == 3 && getAcceptOfferStatus() == 3) {
            msg();
        } else if (getWhatsappStatus() != 3 && getAcceptOfferStatus() != 3 && getEmailStatus() != 3) {
            businessEmailWhatsapp(false);
        } else if (getEmailStatus() != 3 && getAcceptOfferStatus() != 3) {
            msgEmailOffer();
        } else if (getEmailStatus() == 3 && getAcceptOfferStatus() != 3 && getWhatsappStatus() != 3) {
            msgWhatsappOffer(true);
        } else if (getEmailStatus() != 3 && getAcceptOfferStatus() == 3 && getWhatsappStatus() != 3) {
            businessEmailWhatsapp(true);
        } else if (getEmailStatus() == 3 && getAcceptOfferStatus() == 3 && getWhatsappStatus() != 3) {
            msgWhatsapp(true);
        } else if (getWhatsappStatus() != 3 && getAcceptOfferStatus() != 3) {
            msgWhatsappOffer(false);
        } else if (getAcceptOfferStatus() != 3 && getEmailStatus() == 3 && getWhatsappStatus() == 3) {
            msgOffer();
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void businessEmailWhatsapp(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.whatsapp));
                    break;
                case 1:
                    txtView.setText(getString(R.string.email));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.message));
                    break;
            }
//            txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//            txtView.setTextColor(getResources().getColor(R.color.C_020814));
            binding.linPreview.addView(view);
        }
    }

    private void msgOffer() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);
        for (int i = 0; i < 2; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    if (language.equals("ar")) {
                        setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, 0, 0);
                    } else {
                        setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    }
                    break;
                case 1:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    DrawableCompat.setTint(txtView.getBackground(), ContextCompat.getColor(this, R.color.black));
                    txtView.setTextColor(getResources().getColor(R.color.white));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msg() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);

        View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
        TextView txtView = view.findViewById(R.id.txt_view1);
        txtView.setText(getString(R.string.message));
        if (language.equals("ar")) {
            setArFont(txtView, Constants.FONT_AR_REGULAR);
        }
//        txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//        txtView.setTextColor(getResources().getColor(R.color.C_020814));
        binding.linPreview.addView(view);
    }


    private void msgWhatsappOffer(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
//                    setMargins(txtView,0,0,(int) getResources().getDimension(R.dimen._5sdp),0);
                    break;
                case 1:
                    txtView.setText(getString(R.string.whatsapp));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    DrawableCompat.setTint(txtView.getBackground(), ContextCompat.getColor(this, R.color.black));
                    txtView.setTextColor(getResources().getColor(R.color.white));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msgEmailOffer() {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(View.GONE);
        for (int i = 0; i < 3; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
//                    setMargins(txtView,0,0,(int) getResources().getDimension(R.dimen._5sdp),0);
                    break;
                case 1:
                    txtView.setText(getString(R.string.email));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    break;
                case 2:
                    txtView.setText(getString(R.string.send_offer));
                    txtView.setBackground(getResources().getDrawable(R.drawable.light_black_bg_7));
                    DrawableCompat.setTint(txtView.getBackground(), ContextCompat.getColor(this, R.color.black));
                    txtView.setTextColor(getResources().getColor(R.color.white));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private void msgWhatsapp(boolean offerGone) {
        binding.linPreview.removeAllViews();
        binding.txtOffer.setVisibility(offerGone ? View.GONE : View.VISIBLE);
        for (int i = 0; i < 2; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_textview, binding.linPreview, false);
            TextView txtView = view.findViewById(R.id.txt_view1);
            if (language.equals("ar")) {
                setArFont(txtView, Constants.FONT_AR_REGULAR);
            }
            switch (i) {
                case 0:
                    txtView.setText(getString(R.string.message));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    if (language.equals("ar")) {
                        setMargins(txtView, (int) getResources().getDimension(R.dimen._5sdp), 0, 0, 0);
                    } else {
                        setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
                    }
                    break;
                case 1:
                    txtView.setText(getString(R.string.whatsapp));
//                    txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
//                    txtView.setTextColor(getResources().getColor(R.color.C_020814));
                    break;
            }

            binding.linPreview.addView(view);
        }
    }

    private int getAcceptOfferStatus() {
        return profileData.show_send_offer_button;
    }

    private int getEmailStatus() {
        return profileData.show_email;
    }

    private int getWhatsappStatus() {
        return profileData.show_whatsapp;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        profileData = Preferences.getProfileData(this);

        profileMenuListOrigin = new ArrayList<>();
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.social_media), 1));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.overview), 2));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.portfolio), 3));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.work_with_1), 4));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.stores_products), 5));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.youtube), 6));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.partners), 7));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.agency), 8));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.service), 9));

        if (profileData != null) {

            StringBuilder stringBuilder = new StringBuilder();
            if (profileData.firstName != null) {
//                binding.tvName.setTextColor(getColor(R.color.black));
                stringBuilder.append(profileData.firstName);

            }
            if (profileData.lastName != null) {
//                binding.tvName.setTextColor(getColor(R.color.black));
                stringBuilder.append(" ");
                stringBuilder.append(profileData.lastName);
            }
            binding.tvName.setText(stringBuilder.toString());
            binding.toolbarTitle.setText(stringBuilder.toString());

            if (profileData.username != null) {
//                binding.tvUserName.setTextColor(getColor(R.color.black));
                binding.tvUserName.setText(String.format(getString(R.string.nojom_com_s), profileData.username));
            }
            if (profileData.website != null) {
                binding.tvLink.setTextColor(getColor(R.color.black));
                binding.tvLink.setText(profileData.website);
            }

            Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(binding.imgProfile);

            setPreview();

            binding.linearCustom.removeAllViews();

            if (profileData.settings_order != null) {
                List<String> list = new ArrayList<>(Arrays.asList(profileData.settings_order.split(",")));
//                Set<String> set = new HashSet<>(list);
//                List<String> newList = new ArrayList<>(set);
                for (String item : list) {

                    switch (item) {
                        case "1":
                            addSocialMediaLayout();
                            break;
                        case "2":
                            addOverviewLayout();
                            break;
                        case "3":
                            addPortfolioLayout();
                            break;
                        case "4":
                            addWorkWithLayout();
                            break;
                        case "5":
                            addMyStoreLayout();
                            break;
                        case "6":
                            addYoutubeLayout();
                            break;
                        case "7":
                            addPartnerLayout();
                            break;
                        case "8":
                            addAgencyLayout();
                            break;
                        case "9":
                            addInfluencerServiceLayout();
                            break;
                    }

                }
            }
        }


        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            Log.e("lll", "" + (abs(verticalOffset) - appBarLayout.getTotalScrollRange()));
            if (abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                binding.toolbarTitle.setVisibility(VISIBLE);
                binding.imgProfileToolbar.setVisibility(VISIBLE);
//                binding.tvSendOffer1.setVisibility(VISIBLE);
            } else {
                //Expanded
                binding.toolbarTitle.setVisibility(View.INVISIBLE);
                binding.imgProfileToolbar.setVisibility(View.INVISIBLE);
//                binding.tvSendOffer1.setVisibility(GONE);
            }
        });


        binding.imgLanguage.setOnClickListener(view -> {
            Preferences.writeString(this, Constants.PREF_SELECTED_LANGUAGE, language.equals("ar") ? "en" : "ar");
            loadAppLanguage();
            gotoMainActivity(TAB_HOME);
        });
        binding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    private void initData() {
        if (language.equals("ar")) {
            binding.imgLanguage.setText("EN");
        } else {
            binding.imgLanguage.setText("ع");
        }

        binding.txtEdit.setOnClickListener(v -> {
//            startActivity(new Intent(this, MyProfileActivity.class));
            startActivity(new Intent(this, EditProfileActivity.class));
        });

//        binding.tvSendOffer.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.OFFER));
//        binding.tvChat.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.MESSAGE));
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null) redirectTab(Constants.TAB_HOME);
        else super.onBackPressed();
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        profileData = Preferences.getProfileData(this);
    }

    private void addSocialMediaLayout() {
        ViewSocialMediaBinding socialMediaBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_social_media, null, false);
        if (language.equals("ar")) {
            setArFont(socialMediaBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(socialMediaBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(socialMediaBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }
        socialMediaBinding.txtName.setText(getString(R.string.social_media));
        MyPlatformActivityVM nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);

        nameActivityVM.getConnectedPlatform(this);

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {
            if (data != null && data.size() > 0) {
                socialMediaBinding.txtAdd.setText(getString(R.string.edit));

                for (ConnectedSocialMedia.Data data1 : data) {
                    if (data1.social_platform_id == 12) {//whatsapp
                        data.remove(data1);
                        break;
                    }
                }

                List<ConnectedSocialMedia.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = data;
                }

                SocialMediaAdapterProfile adapter = new SocialMediaAdapterProfile();
                adapter.doRefresh(filteredList, this);
                socialMediaBinding.rvSocial.setAdapter(adapter);
            }
        });

        socialMediaBinding.txtAdd.setVisibility(GONE);
        socialMediaBinding.txtShow.setVisibility(GONE);
        socialMediaBinding.imgOrder.setVisibility(GONE);

        binding.linearCustom.addView(socialMediaBinding.getRoot());
    }

    private void addOverviewLayout() {
        ViewOverviewBinding overviewBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_overview, null, false);
        overviewBinding.txtName.setText(getString(R.string.overview));

        if (language.equals("ar")) {
            setArFont(overviewBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtAdd, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.titleAbout, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.tvAboutme, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtCatTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.txtTagTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.txtPriceTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.tvPriceRange, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtMawTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.tvMawId, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtGenTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.txtAgeTitle, Constants.FONT_AR_REGULAR);
            setArFont(overviewBinding.tvAge, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.tvGender, Constants.FONT_AR_MEDIUM);
        }

        overviewBinding.titleAbout.setText(getString(R.string.about_me));
        overviewBinding.txtCatTitle.setText(getString(R.string.category));
        overviewBinding.txtTagTitle.setText(getString(R.string.tags));
        overviewBinding.txtPriceTitle.setText(getString(R.string.price_range));
        overviewBinding.txtMawTitle.setText(getString(R.string.mawthooq));
        overviewBinding.txtGenTitle.setText(getString(R.string.gender));
        overviewBinding.txtAgeTitle.setText(getString(R.string.age));
        overviewBinding.txtLocTitle.setText(getString(R.string.location));

        overviewBinding.imgOrder.setVisibility(GONE);
        if (profileData != null) {
            overviewBinding.txtAdd.setText(getString(R.string.edit));

            overviewBinding.tvAboutme.setText(profileData.about_me);
            overviewBinding.tvMawId.setText("-");
            if (profileData.gender != null) {
                if (profileData.gender == 1) {
                    overviewBinding.tvGender.setText(getString(R.string.male));
                } else if (profileData.gender == 2) {
                    overviewBinding.tvGender.setText(getString(R.string.female));
                } else if (profileData.gender == 3) {
                    overviewBinding.tvGender.setText(getString(R.string.others));
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(profileData.getCountryName(language))) {
                stringBuilder.append(profileData.getCountryName(language));
            }
            if (!TextUtils.isEmpty(profileData.getCityName(language))) {
                stringBuilder.append(", ");
                stringBuilder.append(profileData.getCityName(language));
            }
            overviewBinding.tvLoc.setText(stringBuilder.toString());

            if (profileData.price_range_public_status == 1 || profileData.price_range_public_status == 2) {
                overviewBinding.tvPriceRange.setVisibility(VISIBLE);
                overviewBinding.txtPriceTitle.setVisibility(VISIBLE);

                if (profileData.minPrice != null && profileData.maxPrice != null && profileData.minPrice > 0 && profileData.maxPrice > 0) {
                    String minP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.minPrice));
                    String maxP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.maxPrice));
                    overviewBinding.tvPriceRange.setText(String.format("%s - %s %s", minP, maxP, getCurrency().equals("SAR") ? getString(R.string.sar) : getString(R.string.dollar)));
                } else {
                    overviewBinding.tvPriceRange.setText("");
                }
            } else {
                overviewBinding.tvPriceRange.setVisibility(GONE);
                overviewBinding.txtPriceTitle.setVisibility(GONE);
            }

            if (profileData.show_age != null && profileData.show_age == 2) {
                overviewBinding.linAge.setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(profileData.birth_date)) {
                    int age = Utils.calculateAge(profileData.birth_date.split("T")[0]);
                    overviewBinding.tvAge.setText("" + age);
                } else {
                    overviewBinding.tvAge.setText("-");
                }
            } else {
                overviewBinding.linAge.setVisibility(GONE);
            }

            ArrayList<Skill> skillList = new ArrayList<>();
            if (profileData.category_lists != null && profileData.category_lists.size() > 0) {
                for (ProfileResponse.Skill data : profileData.category_lists) {
                    skillList.add(new Skill(data.getName(language), Utils.getRatingLevel(1)));
                }
            }

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            overviewBinding.chipView.setLayoutManager(layoutManager);
            SkillsListAdapter skillsListAdapter = new SkillsListAdapter(this, skillList, true);
            overviewBinding.chipView.setAdapter(skillsListAdapter);

            ArrayList<Skill> tagList = new ArrayList<>();
            if (profileData.tags_lists != null && profileData.tags_lists.size() > 0) {
                for (ProfileResponse.Skill data : profileData.tags_lists) {
                    tagList.add(new Skill(data.getName(language), Utils.getRatingLevel(1)));
                }
            }

            layoutManager = new FlexboxLayoutManager(this);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            overviewBinding.chipViewTags.setLayoutManager(layoutManager);
            SkillsListAdapter skillsListAdapter1 = new SkillsListAdapter(this, tagList, false);
            overviewBinding.chipViewTags.setAdapter(skillsListAdapter1);
        }

        overviewBinding.txtAdd.setVisibility(GONE);
        overviewBinding.txtShow.setVisibility(GONE);

        binding.linearCustom.addView(overviewBinding.getRoot());
    }

    private void addPortfolioLayout() {
        ViewPortfolioBinding portfolioBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_portfolio, null, false);
        portfolioBinding.imgOrder.setVisibility(GONE);
        portfolioBinding.txtShow.setVisibility(GONE);
        if (language.equals("ar")) {
            setArFont(portfolioBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(portfolioBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(portfolioBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }
        portfolioBinding.txtName.setText(getString(R.string.portfolio));
        NewPortfolioActivityVM newPortfolioActivityVM = ViewModelProviders.of(this).get(NewPortfolioActivityVM.class);
        newPortfolioActivityVM.init(this);
        newPortfolioActivityVM.getMyPortfolios();

        newPortfolioActivityVM.getListMutableLiveData().observe(this, data -> {
            if (data != null) {
                portfolioBinding.txtAdd.setText(getString(R.string.edit));

                List<Portfolios> updatedList = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    try {
                        if (i % 3 == 0) {
                            updatedList.add(data.get(i));
                        } else {
                            int n = i;
                            List<Portfolios> twoList = new ArrayList<>();
                            twoList.add(data.get(i));
                            i++;
                            if (i <= data.size() - 1) {
                                twoList.add(data.get(i));
                            }
                            data.get(n).data = twoList;
                            updatedList.add(data.get(n));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                List<Portfolios> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = updatedList.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = data;
                }

                CustomAdapter adapter = new CustomAdapter(this, filteredList);
                portfolioBinding.rvPortfolio.setAdapter(adapter);

            } else {

            }
        });
        portfolioBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(portfolioBinding.getRoot());
    }

    private void addWorkWithLayout() {
        ViewWorkwithBinding workwithBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_workwith, null, false);
        workwithBinding.imgOrder.setVisibility(GONE);
        workwithBinding.txtShow.setVisibility(GONE);
        workwithBinding.txtName.setText(getString(R.string.work_with));
        WorkWithActivityVM workWithActivityVM = ViewModelProviders.of(this).get(WorkWithActivityVM.class);
        workWithActivityVM.init(this);
        workWithActivityVM.getAgentCompanies();
        if (language.equals("ar")) {
            setArFont(workwithBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(workwithBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(workwithBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }
        AtomicReference<GridLayoutManager> gridLayoutManager = new AtomicReference<>(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));

        workWithActivityVM.getAgentCompanyData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies.data != null && getAgentCompanies.data.size() > 0) {
                workwithBinding.txtAdd.setText(getString(R.string.edit));

                List<GetAgentCompanies.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = getAgentCompanies.data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = getAgentCompanies.data;
                }

                if (filteredList != null && filteredList.size() > 2) {
                    gridLayoutManager.set(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));
                    workwithBinding.rvPortfolio.setLayoutManager(gridLayoutManager.get());
                } else {
                    gridLayoutManager.set(new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false));
                    workwithBinding.rvPortfolio.setLayoutManager(gridLayoutManager.get());
                }

                WorkwithAdapter adapter = new WorkwithAdapter(this);
                adapter.doRefresh(filteredList);
                adapter.doRefresh(getAgentCompanies.path);
                workwithBinding.rvPortfolio.setAdapter(adapter);
            } else {

            }
        });
        workwithBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(workwithBinding.getRoot());
    }

    private GetStores storeList;
    private GetProduct productList;

    private void addMyStoreLayout() {
        ViewMyStoreBinding myStoreBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_my_store, null, false);
        myStoreBinding.imgOrder.setVisibility(GONE);
        myStoreBinding.txtShow.setVisibility(GONE);
        myStoreBinding.txtName.setText(getString(R.string.stores_products));
        myStoreBinding.txtStores.setText(getString(R.string.my_stores));
        myStoreBinding.txtProduct.setText(getString(R.string.my_product));
        MyStoreActivityVM myStoreActivityVM = ViewModelProviders.of(this).get(MyStoreActivityVM.class);
        myStoreActivityVM.init(this);
        myStoreActivityVM.getStores();
        myStoreActivityVM.getProduct();

        if (language.equals("ar")) {
            setArFont(myStoreBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(myStoreBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(myStoreBinding.txtAdd, Constants.FONT_AR_MEDIUM);
            setArFont(myStoreBinding.txtStores, Constants.FONT_AR_MEDIUM);
            setArFont(myStoreBinding.txtProduct, Constants.FONT_AR_MEDIUM);
        }

        myStoreActivityVM.getStoreDataList().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                storeList = getCompanies;

                myStoreBinding.txtAdd.setText(getString(R.string.edit));

                List<GetStores.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = getCompanies.data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = getCompanies.data;
                }
                if (filteredList.size() > 0) {
                    myStoreBinding.txtStores.setVisibility(VISIBLE);
                } else {
                    myStoreBinding.txtStores.setVisibility(GONE);
                }
                ProfileStoreAdapter adapter = new ProfileStoreAdapter(this);
                myStoreBinding.rvPortfolio.setAdapter(adapter);
                adapter.doRefresh(storeList.path);
                adapter.doRefresh(filteredList);
            } else {
                myStoreBinding.txtStores.setVisibility(GONE);
            }
        });

        myStoreActivityVM.getProductDataList().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                productList = getCompanies;

                myStoreBinding.txtAdd.setText(getString(R.string.edit));

                List<GetProduct.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = getCompanies.data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = getCompanies.data;
                }
                if (filteredList.size() > 0) {
                    myStoreBinding.txtProduct.setVisibility(VISIBLE);
                } else {
                    myStoreBinding.txtProduct.setVisibility(GONE);
                }

                ProfileProductsAdapter productAdapter = new ProfileProductsAdapter(this);
                productAdapter.doRefresh(productList.path);
                productAdapter.doRefresh(filteredList);
                myStoreBinding.rvProduct.setAdapter(productAdapter);
            } else {
                myStoreBinding.txtProduct.setVisibility(GONE);
            }
        });
        myStoreBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(myStoreBinding.getRoot());
    }

    private void addYoutubeLayout() {
        ViewYoutubeBinding youtubeBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_youtube, null, false);
        youtubeBinding.imgOrder.setVisibility(GONE);
        youtubeBinding.txtShow.setVisibility(GONE);
        youtubeBinding.txtName.setText(getString(R.string.youtube));
        YoutubeActivityVM youtubeActivityVM = ViewModelProviders.of(this).get(YoutubeActivityVM.class);
        youtubeActivityVM.init(this);

        if (language.equals("ar")) {
            setArFont(youtubeBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(youtubeBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(youtubeBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }

        youtubeActivityVM.getYoutubeListData().observe(this, youtubeData -> {
            if (youtubeData.data != null && youtubeData.data.size() > 0) {

                youtubeBinding.txtAdd.setText(getString(R.string.edit));

                List<GetYoutube.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = youtubeData.data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = youtubeData.data;
                }

                ProfileYoutubeAdapter adapter = new ProfileYoutubeAdapter(this);
                adapter.doRefresh(filteredList);
                youtubeBinding.rvPortfolio.setAdapter(adapter);

            } else {

            }
        });
        youtubeBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(youtubeBinding.getRoot());
    }

    private void addPartnerLayout() {
        ViewPartnerBinding partnerBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_partner, null, false);
        partnerBinding.imgOrder.setVisibility(GONE);
        partnerBinding.txtShow.setVisibility(GONE);
        partnerBinding.txtName.setText(getString(R.string.partners));
        MyPartnerActivityVM myPartnerActivityVM = ViewModelProviders.of(this).get(MyPartnerActivityVM.class);
        myPartnerActivityVM.init(this);
        myPartnerActivityVM.getPartners();
        if (language.equals("ar")) {
            setArFont(partnerBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(partnerBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(partnerBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }
        myPartnerActivityVM.getAgentCompanyData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies.data != null && getAgentCompanies.data.size() > 0) {

                partnerBinding.txtAdd.setText(getString(R.string.edit));

                List<GetAgentCompanies.Data> filteredList;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    filteredList = getAgentCompanies.data.stream().filter(obj -> isSelectPublic ? (obj.public_status == 1) : (obj.public_status != 3)).collect(Collectors.toList());
                } else {
                    filteredList = getAgentCompanies.data;
                }

                ProfilePartnersAdapter adapter = new ProfilePartnersAdapter(this);
                adapter.doRefresh(getAgentCompanies.path);
                adapter.doRefresh(filteredList);
                partnerBinding.rvPortfolio.setAdapter(adapter);
            } else {

            }
        });
        partnerBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(partnerBinding.getRoot());
    }

    private void addAgencyLayout() {
        ViewAgencyBinding agencyBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_agency, null, false);
        agencyBinding.imgOrder.setVisibility(GONE);
        agencyBinding.txtShow.setVisibility(GONE);
        agencyBinding.txtName.setText(getString(R.string.agency));
        if (language.equals("ar")) {
            setArFont(agencyBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(agencyBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(agencyBinding.txtAdd, Constants.FONT_AR_MEDIUM);
            setArFont(agencyBinding.txtAgencyName, Constants.FONT_AR_BOLD);
            setArFont(agencyBinding.txtAgencyNo, Constants.FONT_AR_BOLD);
            setArFont(agencyBinding.txtAgencyWbsite, Constants.FONT_AR_BOLD);
            setArFont(agencyBinding.txtAgencyLocation, Constants.FONT_AR_BOLD);
            setArFont(agencyBinding.txtAgencyAbout, Constants.FONT_AR_BOLD);
            setArFont(agencyBinding.txtAgencyEmail, Constants.FONT_AR_BOLD);
        }
        agencyBinding.txtAgencyAbout.applyCustomFontBold();
        if (profileData != null && profileData.profile_agencies != null) {

            agencyBinding.txtAdd.setText(getString(R.string.edit));

            if (!TextUtils.isEmpty(profileData.profile_agencies.name)) {
                agencyBinding.txtAgencyName.setText(profileData.profile_agencies.name);
            }

//            if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
//                agencyBinding.txtAg.setText(profileData.profile_agencies.about);
//            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.phone)) {
                agencyBinding.txtAgencyNo.setText(profileData.profile_agencies.phone);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.email)) {
                agencyBinding.txtAgencyEmail.setText(profileData.profile_agencies.email);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.address)) {
                agencyBinding.txtAgencyLocation.setText(String.format("%s", profileData.profile_agencies.address));
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.website)) {
                agencyBinding.txtAgencyWbsite.setText(profileData.profile_agencies.website);
            }

            if (profileData.profile_agencies.about_public == 2) {
                agencyBinding.txtAgencyAbout.setVisibility(VISIBLE);
                agencyBinding.imgInfo.setVisibility(VISIBLE);
            } else {
                agencyBinding.txtAgencyAbout.setVisibility(GONE);
                agencyBinding.imgInfo.setVisibility(GONE);
            }

            if (profileData.profile_agencies.email_public == 2) {
                agencyBinding.txtAgencyEmail.setVisibility(VISIBLE);
                agencyBinding.imgEmail.setVisibility(VISIBLE);
            } else {
                agencyBinding.txtAgencyEmail.setVisibility(GONE);
                agencyBinding.imgEmail.setVisibility(GONE);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
                agencyBinding.txtAgencyAbout.setText(profileData.profile_agencies.about);
            } else {
                agencyBinding.txtAgencyAbout.setVisibility(GONE);
                agencyBinding.imgInfo.setVisibility(GONE);
            }

            Glide.with(this).load(profileData.filePaths.agency + profileData.profile_agencies.filename).error(R.mipmap.ic_launcher).into(agencyBinding.imgProfile);

        }
        agencyBinding.txtAdd.setVisibility(GONE);
        binding.linearCustom.addView(agencyBinding.getRoot());
    }

    private void addInfluencerServiceLayout() {
        ViewServicesBinding socialMediaBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_services, null, false);
        if (language.equals("ar")) {
            setArFont(socialMediaBinding.txtName, Constants.FONT_AR_MEDIUM);
        }
        socialMediaBinding.imgOrder.setVisibility(GONE);
        socialMediaBinding.txtShow.setVisibility(GONE);
        socialMediaBinding.txtAdd.setVisibility(GONE);
        serviceActivityVM.getServices();

        socialMediaBinding.txtName.setText(getString(R.string.influencer_services));

        serviceActivityVM.serviceMutableLiveData.observe(this, data -> {
//            influencerServices = data;
            if (data != null && data.services != null && data.services.size() > 0) {
                ArrayList<Serv> servicesData = new ArrayList<>(data.services);

                /*for (Serv da : data.services) {
                    if (da.id == -1) {
                        if (da.price > 0) {
                            servicesData.add(new Serv(getString(R.string.all_social_media), da.price));
                        }
                        break;
                    }
                }*/

                SelectedServiceAdapter adapter = new SelectedServiceAdapter(servicesData, this);
                socialMediaBinding.rvService.setAdapter(adapter);
                socialMediaBinding.rvService.setVisibility(VISIBLE);
            } else {
                socialMediaBinding.rvService.setVisibility(GONE);
                socialMediaBinding.txtShow.setVisibility(VISIBLE);
            }
        });

        binding.linearCustom.addView(socialMediaBinding.getRoot());
    }
}
