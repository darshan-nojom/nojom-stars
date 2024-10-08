package com.nojom.ui.gigs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.nojom.adapter.AllSectionAdapter;
import com.nojom.adapter.CustomAdapter;
import com.nojom.adapter.ProfileMenuAdapter;
import com.nojom.adapter.ProfilePartnersAdapter;
import com.nojom.adapter.ProfileProductsAdapter;
import com.nojom.adapter.ProfileStoreAdapter;
import com.nojom.adapter.ProfileYoutubeAdapter;
import com.nojom.adapter.SkillsListAdapter;
import com.nojom.adapter.SocialMediaAdapterProfile;
import com.nojom.adapter.WorkwithAdapter;
import com.nojom.databinding.ActivityProfileUpdateBinding;
import com.nojom.databinding.DialogAllSectionBinding;
import com.nojom.databinding.DialogReorderBinding;
import com.nojom.databinding.ViewAgencyBinding;
import com.nojom.databinding.ViewMyStoreBinding;
import com.nojom.databinding.ViewOverviewBinding;
import com.nojom.databinding.ViewPartnerBinding;
import com.nojom.databinding.ViewPortfolioBinding;
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
import com.nojom.model.Skill;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.AgencyInfoActivity;
import com.nojom.ui.workprofile.EditProfileActivity;
import com.nojom.ui.workprofile.EditProfileActivityVM;
import com.nojom.ui.workprofile.MyPartnerActivity;
import com.nojom.ui.workprofile.MyPartnerActivityVM;
import com.nojom.ui.workprofile.MyPlatformActivityVM;
import com.nojom.ui.workprofile.MyStoreActivityVM;
import com.nojom.ui.workprofile.MyStoresActivity;
import com.nojom.ui.workprofile.NewPortfolioActivity;
import com.nojom.ui.workprofile.NewPortfolioActivityVM;
import com.nojom.ui.workprofile.NewSocialMediaActivity;
import com.nojom.ui.workprofile.OverViewActivityNew;
import com.nojom.ui.workprofile.WorkWithActivity;
import com.nojom.ui.workprofile.WorkWithActivityVM;
import com.nojom.ui.workprofile.YoutubeActivity;
import com.nojom.ui.workprofile.YoutubeActivityVM;
import com.nojom.util.Constants;
import com.nojom.util.NumberTextWatcherForThousand;
import com.nojom.util.Preferences;
import com.nojom.util.ReOrderItemMoveCallback;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ProfileUpdateActivity extends BaseActivity implements AllSectionAdapter.OnClickHideShow, BaseActivity.OnProfileLoadListener, ProfileMenuAdapter.UpdateSwipeListener, ProfileMenuAdapter.OnClickMenuListener {
    private ActivityProfileUpdateBinding binding;
    private ProfileResponse profileData;
    private EditProfileActivityVM editProfileActivityVM;
    List<ProfileMenu> profileMenuListOrigin, copySections;
    private int selectedSectionPos;
    boolean isSelectPublic = true;
    boolean isEnglishLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLACK);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_update);

        binding.linHeader.setBackground(getResources().getDrawable(R.drawable.bottom_black));
        binding.linDesc.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
        binding.txtBrand.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
        binding.txtPublic.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
        binding.txtPublic.setTextColor(Color.BLACK);
        binding.txtBrand.setTextColor(Color.WHITE);
        DrawableCompat.setTint(binding.txtBrand.getBackground(), ContextCompat.getColor(this, R.color.C_3C3C43));
        DrawableCompat.setTint(binding.txtPublic.getBackground(), ContextCompat.getColor(this, R.color.white));

        editProfileActivityVM = ViewModelProviders.of(this).get(EditProfileActivityVM.class);
        editProfileActivityVM.init(this, null);
        setOnProfileLoadListener(this);

        if (language.equals("ar")) {
            setArFont(binding.txtPublic, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtBrand, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtInstruct, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtLinkCopy, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtCopy, Constants.FONT_AR_BOLD);
            setArFont(binding.txtPreview, Constants.FONT_AR_BOLD);
            setArFont(binding.imgLanguage, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtEdit, Constants.FONT_AR_REGULAR);
            setArFont(binding.tvName, Constants.FONT_AR_BOLD);
            setArFont(binding.tvUserName, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtOffer, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtAllSection, Constants.FONT_AR_REGULAR);
        }

        initData();

        binding.nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            View view = binding.nestedSV.getChildAt(binding.nestedSV.getChildCount() - 1);
            int topDetector = binding.nestedSV.getScrollY();
            int bottomDetector = view.getBottom() - (binding.nestedSV.getHeight() + binding.nestedSV.getScrollY());
            if (topDetector >= 600) {
                binding.linDesc.setVisibility(GONE);
                binding.imgShow.setRotation(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
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

        binding.linPreview.invalidate();
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
            txtView.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
            txtView.setTextColor(getResources().getColor(R.color.C_020814));
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
        if (language.equals("ar")) {
            setArFont(txtView, Constants.FONT_AR_REGULAR);
        }
        txtView.setText(getString(R.string.message));
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
//                    setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
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
//                    setMargins(txtView, 0, 0, (int) getResources().getDimension(R.dimen._5sdp), 0);
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

    private void updateUi() {
        profileData = Preferences.getProfileData(this);
        updateProfileData();
//        binding.txtAllSection.setText(getString(R.string.show_all_section));

        copySections = new ArrayList<>();
        profileMenuListOrigin = new ArrayList<>();
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.social_media), 1));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.overview), 2));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.portfolio), 3));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.work_with_1), 4));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.stores_products), 5));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.youtube), 6));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.partners), 7));
        profileMenuListOrigin.add(new ProfileMenu(getString(R.string.agency), 8));


        if (profileData != null) {

            if (profileData.is_verified != null && profileData.is_verified == 1) {
                binding.imgVerified.setVisibility(VISIBLE);
            } else {
                binding.imgVerified.setVisibility(GONE);
            }

            setPreview();
//            if (profileData.show_email == 1) {
//                binding.tvEmail.setVisibility(VISIBLE);
//            } else {
//                binding.tvEmail.setVisibility(GONE);
//            }
//            if (profileData.show_whatsapp == 1) {
//                binding.tvWhatsapp.setVisibility(VISIBLE);
//            } else {
//                binding.tvWhatsapp.setVisibility(GONE);
//            }
//            if (profileData.show_message_button == 1) {
//                binding.tvChat.setVisibility(VISIBLE);
//            } else {
//                binding.tvChat.setVisibility(GONE);
//            }
//            if (profileData.show_send_offer_button == 1) {
//                binding.tvSendOffer.setVisibility(VISIBLE);
//            } else {
//                binding.tvSendOffer.setVisibility(GONE);
//            }
            StringBuilder stringBuilder = new StringBuilder();
            if (profileData.lastName != null && language.equals("ar")) {
                binding.tvName.setText(profileData.lastName);
            } else if (profileData.firstName != null) {
                binding.tvName.setText(profileData.firstName);
            }

            if (profileData.username != null) {
//                binding.tvUserName.setTextColor(getColor(R.color.black));
                binding.tvUserName.setText(String.format(getString(R.string.nojom_com_s), profileData.username));
                binding.txtLinkCopy.setText(String.format(getString(R.string.nojom_com_s), profileData.username));
            }
            /*if (profileData.website != null) {
                binding.tvLink.setTextColor(getColor(R.color.black));
                binding.tvLink.setText(profileData.website);
            }*/

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

            binding.linearCustom.removeAllViews();

            if (profileData.settings_order != null) {
                List<String> list = new ArrayList<>(Arrays.asList(profileData.settings_order.split(",")));
                Set<String> set = new HashSet<>(list);
                List<String> newList = new ArrayList<>(set);
                for (String item : newList) {
//                    profileMenuList.add(profileMenuListOrigin.get(Integer.parseInt(item) - 1));
                    int pos = Integer.parseInt(item) - 1;
                    profileMenuListOrigin.get(pos).isShow = true;

                    switch (item) {
                        case "1":
                            addSocialMediaLayout(pos);
                            break;
                        case "2":
                            addOverviewLayout(pos);
                            break;
                        case "3":
                            addPortfolioLayout(pos);
                            break;
                        case "4":
                            addWorkWithLayout(pos);
                            break;
                        case "5":
                            addMyStoreLayout(pos);
                            break;
                        case "6":
                            addYoutubeLayout(pos);
                            break;
                        case "7":
                            addPartnerLayout(pos);
                            break;
                        case "8":
                            addAgencyLayout(pos);
                            break;
                    }

                }
            }
            binding.linearCustom.invalidate();


//            ProfileMenuUpdateAdapter adapter = new ProfileMenuUpdateAdapter(this, profileMenuList, null, null);
//            binding.rvMenu.setAdapter(adapter);
        }

        editProfileActivityVM.getUpdateProfileSuccess().observe(this, isSuccess -> {
            if (allSectionAdapter != null && selectedSectionPos != -1) {
                allSectionAdapter.updatePosition(selectedSectionPos, isSuccess);

                selectedSectionPos = -1;

                if (binding.linearCustom.getChildCount() == 8) {
                    binding.txtAllSection.setVisibility(GONE);
                    binding.txtAllSection.invalidate();
                } else {
                    binding.txtAllSection.setVisibility(VISIBLE);
                    binding.txtAllSection.invalidate();
                }
            }

        });

        binding.txtPublic.setOnClickListener(view -> {
            isSelectPublic = true;
            binding.txtInstruct.setText(getString(R.string.public_profile_is_visible_to_anyone_even_outside_nojom_use_this_link_as_your_bio_link_on_all_platforms));
            binding.txtBrand.setBackground(getResources().getDrawable(R.drawable.lightblack_button_bg));
            binding.txtPublic.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
            binding.txtPublic.setTextColor(Color.BLACK);
            binding.txtBrand.setTextColor(Color.WHITE);
            onResume();
        });
        binding.txtBrand.setOnClickListener(view -> {
            isSelectPublic = false;
            binding.txtInstruct.setText(getString(R.string.brand_profile_is_visible_to_anyone_even_outside_nojom_use_this_link_as_your_bio_link_on_all_platforms));
            binding.txtBrand.setBackground(getResources().getDrawable(R.drawable.white_button_bg));
            binding.txtPublic.setBackground(getResources().getDrawable(R.drawable.lightblack_button_bg));
            binding.txtBrand.setTextColor(Color.BLACK);
            binding.txtPublic.setTextColor(Color.WHITE);
            onResume();
        });

        binding.txtPreview.setOnClickListener(view -> {
            if (isSelectPublic) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = "https://nojom.com/" + profileData.username;
                i.setData(Uri.parse(url));
                startActivity(i);
            } else {
                redirectActivity(BrandViewActivity.class);
            }
        });

        if (language.equals("ar")) {
            binding.imgLanguage.setText("EN");
        } else {
            binding.imgLanguage.setText("ع");
        }

        binding.imgLanguage.setOnClickListener(view -> {
            Preferences.writeString(this, Constants.PREF_SELECTED_LANGUAGE, language.equals("ar") ? "en" : "ar");
            loadAppLanguage();
            gotoMainActivity(TAB_HOME);
        });

        if (binding.linearCustom.getChildCount() == 8) {
            binding.txtAllSection.setVisibility(GONE);
            binding.txtAllSection.invalidate();
        } else {
            binding.txtAllSection.setVisibility(VISIBLE);
            binding.txtAllSection.invalidate();
        }
    }

    private void copyMsg(String msg) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied", msg);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            toastMessage(getString(R.string.copy));
        }
    }

    private void initData() {

        binding.txtCopy.setOnClickListener(v -> copyMsg(binding.txtLinkCopy.getText().toString()));
        binding.txtPreview.setOnClickListener(v -> {

        });
        binding.txtAllSection.setOnClickListener(v -> {
            showAllSectionDialog();
        });

        boolean isFirstTime = Preferences.readBoolean(this, Constants.IS_SHOW_FIRST_TIME, false);
        if (isFirstTime) {
            Preferences.writeBoolean(this, Constants.IS_SHOW_FIRST_TIME, false);
            binding.linDesc.setVisibility(View.VISIBLE);
            binding.imgShow.setRotation(180);
        }
        binding.imgShow.setOnClickListener(v -> {
            if (binding.linDesc.isShown()) {
                binding.linDesc.setVisibility(View.GONE);
                binding.imgShow.setRotation(0);
            } else {
                binding.linDesc.setVisibility(View.VISIBLE);
                binding.imgShow.setRotation(180);
            }

        });

        binding.txtEdit.setOnClickListener(v -> {
//            startActivity(new Intent(this, MyProfileActivity.class));
            startActivity(new Intent(this, EditProfileActivity.class));
        });

//        binding.tvSendOffer.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.OFFER));
//        binding.tvChat.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.MESSAGE));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showOfferDialog(Utils.WindowScreen screen) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_profile_offer);
        dialog.setCancelable(true);
        RelativeLayout rlSave = dialog.findViewById(R.id.rel_save);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtDesc = dialog.findViewById(R.id.txt_desc);

        ImageView imgClose = dialog.findViewById(R.id.img_close);

        switch (screen) {
            case OFFER:
                break;
            case MESSAGE:
                txtTitle.setText(getString(R.string.accept_message));
                txtDesc.setText(getString(R.string.choose_who_can_send_you_message_nany_user_verified_users_or_verified_brands));
                break;

        }

        imgClose.setOnClickListener(v -> dialog.dismiss());
        rlSave.setOnClickListener(v -> {

            switch (screen) {
                case NAME:
                    break;
                case USERNAME:
                    break;
            }

            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private Dialog dialogShowSection;
    private Dialog dialogReOrder;
    private DialogAllSectionBinding allSectionBinding;
    private DialogReorderBinding reorderBinding;
    AllSectionAdapter allSectionAdapter;

    public void showAllSectionDialog() {
        dialogShowSection = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogShowSection.setTitle(null);
        allSectionBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_all_section, null, false);
        allSectionBinding.title.setText(getString(R.string.show_all_section));
        dialogShowSection.setContentView(allSectionBinding.getRoot());
        dialogShowSection.setCancelable(true);

        if (language.equals("ar")) {
            setArFont(allSectionBinding.title, Constants.FONT_AR_MEDIUM);
        }
        copySections = profileMenuListOrigin;
        ProfileResponse profileData = Preferences.getProfileData(this);
        if (profileData != null) {
            if (profileData.settings_order != null) {
                List<String> list = new ArrayList<>(Arrays.asList(profileData.settings_order.split(",")));

                Iterator<ProfileMenu> iterator = copySections.iterator();
                while (iterator.hasNext()) {
                    ProfileMenu next = iterator.next();
                    for (String selData : list) {
                        if (next.id == Integer.parseInt(selData)) {
                            iterator.remove();// remove the partner
                            break;
                        }
                    }
                }
            }
        }

        allSectionBinding.tvCancel.setOnClickListener(view -> dialogShowSection.dismiss());

        allSectionAdapter = new AllSectionAdapter(this, copySections, this);
        allSectionBinding.rMenu.setAdapter(allSectionAdapter);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogShowSection.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogShowSection.show();
        dialogShowSection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogShowSection.getWindow().setAttributes(lp);

        dialogShowSection.setOnDismissListener(dialogInterface -> onResume());
    }

    @Override
    public void onClickHideShow(ProfileMenu menu, int position) {
        hideShowMenu(menu.isShow, menu.id, menu.id);
    }

    @Override
    public void onClickAdd(ProfileMenu menu) {
        if (dialogShowSection != null && dialogShowSection.isShowing()) {
            dialogShowSection.dismiss();
        }

        clickToOpenModule(menu.id);
    }

    private void clickToOpenModule(int id) {
        switch (id) {
            case 1://social media
                startActivity(new Intent(this, NewSocialMediaActivity.class));
                break;
            case 2://overview
//                startActivity(new Intent(this, OverViewActivity.class));
                startActivity(new Intent(this, OverViewActivityNew.class));
                break;
            case 3://portfolio
                startActivity(new Intent(this, NewPortfolioActivity.class));
                break;
            case 4://work with
                startActivity(new Intent(this, WorkWithActivity.class));
                break;
            case 5://my store
                startActivity(new Intent(this, MyStoresActivity.class));
                break;
            case 6://youtube
                startActivity(new Intent(this, YoutubeActivity.class));
                break;
            case 7://partners
                startActivity(new Intent(this, MyPartnerActivity.class));
                break;
            case 8://agency
                redirectActivity(AgencyInfoActivity.class);
                break;
        }
    }

    public static String modifyString(String original, String remove, String add) {
        // Split the original string into an array
        List<String> list;
        if (!TextUtils.isEmpty(original)) {

            String[] array = original.split(",");

            // Convert the array to a list
            list = new ArrayList<>(Arrays.asList(array));
        } else {
            list = new ArrayList<>();
        }
        // Remove the specific number from the list
        if (!TextUtils.isEmpty(remove)) {
            for (String li : list) {
                if (li.equals(remove)) {
                    list.remove(remove);
                    return String.join(",", list);
                }
            }
        }
        // Add the new number to the list
        if (!TextUtils.isEmpty(add)) {
            list.add(add);
            return String.join(",", list);
        }

        // Convert the list back to a comma-separated string
        return String.join(",", list);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        profileData = Preferences.getProfileData(this);
    }

    private void addSocialMediaLayout(int pos) {
        ViewSocialMediaBinding socialMediaBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_social_media, null, false);
        if (language.equals("ar")) {
            setArFont(socialMediaBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(socialMediaBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(socialMediaBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }
        socialMediaBinding.txtName.setText(getString(R.string.social_media));
        socialMediaBinding.txtShow.setText(getString(R.string.hide));
        socialMediaBinding.txtAdd.setText(getString(R.string.add));
        MyPlatformActivityVM nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);

        nameActivityVM.getConnectedPlatform(this);

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {
            if (data != null && data.size() > 0) {
                socialMediaBinding.txtShow.setVisibility(GONE);
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
                socialMediaBinding.rvSocial.setVisibility(VISIBLE);
            } else {
                socialMediaBinding.rvSocial.setVisibility(GONE);
                socialMediaBinding.txtShow.setVisibility(VISIBLE);
            }
        });

        socialMediaBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(1));
        socialMediaBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(socialMediaBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 1, profileMenuListOrigin.get(pos).id);
        });
        socialMediaBinding.imgOrder.setOnClickListener(view -> reOrderDialog());

        binding.linearCustom.addView(socialMediaBinding.getRoot());
    }

    private void addOverviewLayout(int pos) {
        ViewOverviewBinding overviewBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_overview, null, false);
        overviewBinding.txtName.setText(getString(R.string.overview));
        overviewBinding.titleAbout.setText(getString(R.string.about_me));
        overviewBinding.txtCatTitle.setText(getString(R.string.category));
        overviewBinding.txtTagTitle.setText(getString(R.string.tags));
        overviewBinding.txtPriceTitle.setText(getString(R.string.price_range));
        overviewBinding.txtMawTitle.setText(getString(R.string.mawthooq));
        overviewBinding.txtGenTitle.setText(getString(R.string.gender));
        overviewBinding.txtAgeTitle.setText(getString(R.string.age_));
        overviewBinding.txtAdd.setText(getString(R.string.add));
        overviewBinding.txtShow.setText(getString(R.string.hide));
        overviewBinding.txtLocTitle.setText(getString(R.string.location));
        boolean isHide = false;

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
            setArFont(overviewBinding.tvLoc, Constants.FONT_AR_MEDIUM);
            setArFont(overviewBinding.txtLocTitle, Constants.FONT_AR_REGULAR);
        }

        overviewBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
        if (profileData != null) {
            overviewBinding.txtAdd.setText(getString(R.string.edit));

            if (isSelectPublic && profileData.about_me_public_status == 1) {
                overviewBinding.tvAboutme.setVisibility(VISIBLE);
                overviewBinding.titleAbout.setVisibility(VISIBLE);
                overviewBinding.viewAbout.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.about_me_public_status == 2) {
                overviewBinding.tvAboutme.setVisibility(VISIBLE);
                overviewBinding.titleAbout.setVisibility(VISIBLE);
                overviewBinding.viewAbout.setVisibility(VISIBLE);
            } else {
                overviewBinding.tvAboutme.setVisibility(GONE);
                overviewBinding.titleAbout.setVisibility(GONE);
                overviewBinding.viewAbout.setVisibility(GONE);
            }
            if (!TextUtils.isEmpty(profileData.about_me)) {
                isHide = true;
                overviewBinding.tvAboutme.setText(profileData.about_me);
            } else {
                overviewBinding.tvAboutme.setVisibility(GONE);
                overviewBinding.titleAbout.setVisibility(GONE);
                overviewBinding.viewAbout.setVisibility(GONE);
            }

            if (profileData.mawthooq_status != null) {
                if (profileData.mawthooq_status.data != null) {
                    overviewBinding.tvMawId.setText(String.format("%s", profileData.mawthooq_status.data));
                    isHide = true;
                } else {
                    overviewBinding.tvMawId.setText("");
                }

                if (isSelectPublic && profileData.mawthooq_status.public_status == 1) {
                    overviewBinding.tvMawId.setVisibility(VISIBLE);
                    overviewBinding.txtMawTitle.setVisibility(VISIBLE);
                    overviewBinding.viewMaw.setVisibility(VISIBLE);
                } else if (!isSelectPublic && profileData.mawthooq_status.public_status == 2) {
                    overviewBinding.tvMawId.setVisibility(VISIBLE);
                    overviewBinding.txtMawTitle.setVisibility(VISIBLE);
                    overviewBinding.viewMaw.setVisibility(VISIBLE);
                } else {
                    overviewBinding.tvMawId.setVisibility(GONE);
                    overviewBinding.txtMawTitle.setVisibility(GONE);
                    overviewBinding.viewMaw.setVisibility(GONE);
                }
            }

            if (profileData.gender != null) {
                if (profileData.gender == 1) {
                    overviewBinding.tvGender.setText(getString(R.string.male));
                    isHide = true;
                } else if (profileData.gender == 2) {
                    overviewBinding.tvGender.setText(getString(R.string.female));
                    isHide = true;
                } else if (profileData.gender == 3) {
                    overviewBinding.tvGender.setText(getString(R.string.others));
                    isHide = true;
                }
            }

            if (isSelectPublic && profileData.gender_public_status == 1) {
                overviewBinding.tvGender.setVisibility(VISIBLE);
                overviewBinding.txtGenTitle.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.gender_public_status == 2) {
                overviewBinding.txtGenTitle.setVisibility(VISIBLE);
                overviewBinding.tvGender.setVisibility(VISIBLE);
            } else {
                overviewBinding.txtGenTitle.setVisibility(GONE);
                overviewBinding.tvGender.setVisibility(GONE);
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

            if (isSelectPublic && profileData.location_public == 1) {
                overviewBinding.tvLoc.setVisibility(VISIBLE);
                overviewBinding.txtLocTitle.setVisibility(VISIBLE);
                overviewBinding.viewLoc.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.location_public == 2) {
                overviewBinding.txtLocTitle.setVisibility(VISIBLE);
                overviewBinding.tvLoc.setVisibility(VISIBLE);
                overviewBinding.viewLoc.setVisibility(VISIBLE);
            } else {
                overviewBinding.txtLocTitle.setVisibility(GONE);
                overviewBinding.tvLoc.setVisibility(GONE);
                overviewBinding.viewLoc.setVisibility(GONE);
            }


            if (profileData.price_range_public_status == 1 && isSelectPublic) {
                overviewBinding.tvPriceRange.setVisibility(VISIBLE);
                overviewBinding.txtPriceTitle.setVisibility(VISIBLE);
                overviewBinding.viewRange.setVisibility(VISIBLE);
                String minP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.minPrice));
                String maxP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.maxPrice));

                if (profileData.minPrice != null && profileData.maxPrice != null && profileData.minPrice > 0 && profileData.maxPrice > 0) {
                    isHide = true;
                    overviewBinding.tvPriceRange.setText(String.format("%s - %s %s", minP, maxP, getCurrency().equals("SAR") ? getString(R.string.sar) : getString(R.string.dollar)));
                } else {
                    overviewBinding.tvPriceRange.setText("");
                }
            } else if (profileData.price_range_public_status == 2 && !isSelectPublic) {
                overviewBinding.tvPriceRange.setVisibility(VISIBLE);
                overviewBinding.txtPriceTitle.setVisibility(VISIBLE);
                overviewBinding.viewRange.setVisibility(VISIBLE);
                String minP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.minPrice));
                String maxP = NumberTextWatcherForThousand.getDecimalFormat(formatValue(profileData.maxPrice));

                if (profileData.minPrice != null && profileData.maxPrice != null && profileData.minPrice > 0 && profileData.maxPrice > 0) {
                    isHide = true;
                    overviewBinding.tvPriceRange.setText(String.format("%s - %s %s", minP, maxP, getCurrency().equals("SAR") ? getString(R.string.sar) : getString(R.string.dollar)));
                } else {
                    overviewBinding.tvPriceRange.setText("");
                }
            } else {
                overviewBinding.tvPriceRange.setVisibility(GONE);
                overviewBinding.txtPriceTitle.setVisibility(GONE);
                overviewBinding.viewRange.setVisibility(GONE);
            }


            if (!TextUtils.isEmpty(profileData.birth_date)) {
                int age = Utils.calculateAge(profileData.birth_date.split("T")[0]);
                isHide = true;
                overviewBinding.tvAge.setText("" + age);
            } else {
                overviewBinding.tvAge.setText("-");
            }

            if (isSelectPublic && profileData.show_age == 1) {
                overviewBinding.linAge.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.show_age == 2) {
                overviewBinding.linAge.setVisibility(VISIBLE);
            } else {
                overviewBinding.linAge.setVisibility(GONE);
            }

            ArrayList<Skill> skillList = new ArrayList<>();
            if (profileData.category_lists != null && profileData.category_lists.size() > 0) {
                isHide = true;
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
                isHide = true;
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

            if (isSelectPublic && profileData.category_public_status != null && profileData.category_public_status == 1) {
                overviewBinding.chipView.setVisibility(VISIBLE);
                overviewBinding.chipViewTags.setVisibility(VISIBLE);
                overviewBinding.txtCatTitle.setVisibility(VISIBLE);
                overviewBinding.txtTagTitle.setVisibility(VISIBLE);
                overviewBinding.viewTags.setVisibility(VISIBLE);
                overviewBinding.viewCat.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.category_public_status != null && profileData.category_public_status == 2) {
                overviewBinding.chipView.setVisibility(VISIBLE);
                overviewBinding.chipViewTags.setVisibility(VISIBLE);
                overviewBinding.txtCatTitle.setVisibility(VISIBLE);
                overviewBinding.txtTagTitle.setVisibility(VISIBLE);
                overviewBinding.viewTags.setVisibility(VISIBLE);
                overviewBinding.viewCat.setVisibility(VISIBLE);
            } else {
                overviewBinding.chipView.setVisibility(GONE);
                overviewBinding.chipViewTags.setVisibility(GONE);
                overviewBinding.txtCatTitle.setVisibility(GONE);
                overviewBinding.txtTagTitle.setVisibility(GONE);
                overviewBinding.viewTags.setVisibility(GONE);
                overviewBinding.viewCat.setVisibility(GONE);
            }
        }

        if (isHide) {
            overviewBinding.txtShow.setVisibility(GONE);
        } else {
            overviewBinding.txtShow.setVisibility(VISIBLE);
        }
        overviewBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(2));

        overviewBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(overviewBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 2, profileMenuListOrigin.get(pos).id);
        });

        binding.linearCustom.addView(overviewBinding.getRoot());
    }

    private void addPortfolioLayout(int pos) {
        ViewPortfolioBinding portfolioBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_portfolio, null, false);
        portfolioBinding.txtName.setText(getString(R.string.portfolio));
        portfolioBinding.txtShow.setText(getString(R.string.hide));
        portfolioBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
        NewPortfolioActivityVM newPortfolioActivityVM = ViewModelProviders.of(this).get(NewPortfolioActivityVM.class);
        newPortfolioActivityVM.init(this);
        newPortfolioActivityVM.getMyPortfolios();
        portfolioBinding.txtAdd.setText(getString(R.string.add));

        if (language.equals("ar")) {
            setArFont(portfolioBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(portfolioBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(portfolioBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }

        newPortfolioActivityVM.getListMutableLiveData().observe(this, data -> {
            if (data != null && data.size() > 0) {
                portfolioBinding.txtShow.setVisibility(GONE);
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
                portfolioBinding.rvPortfolio.setVisibility(VISIBLE);
            } else {
                portfolioBinding.rvPortfolio.setVisibility(GONE);
                portfolioBinding.txtShow.setVisibility(VISIBLE);
            }
        });
        portfolioBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(3));

        portfolioBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(portfolioBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 3, profileMenuListOrigin.get(pos).id);
        });

        binding.linearCustom.addView(portfolioBinding.getRoot());
    }

    private void addWorkWithLayout(int pos) {
        ViewWorkwithBinding workwithBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_workwith, null, false);
        workwithBinding.txtName.setText(getString(R.string.work_with));
        workwithBinding.txtShow.setText(getString(R.string.hide));
        workwithBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
        WorkWithActivityVM workWithActivityVM = ViewModelProviders.of(this).get(WorkWithActivityVM.class);
        workWithActivityVM.init(this);
        workWithActivityVM.getAgentCompanies();
        workwithBinding.txtAdd.setText(getString(R.string.add));

        if (language.equals("ar")) {
            setArFont(workwithBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(workwithBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(workwithBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }

        AtomicReference<GridLayoutManager> gridLayoutManager = new AtomicReference<>(new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false));

        workWithActivityVM.getAgentCompanyData().observe(this, getAgentCompanies -> {
            if (getAgentCompanies.data != null && getAgentCompanies.data.size() > 0) {
                workwithBinding.txtShow.setVisibility(GONE);
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
                workwithBinding.rvPortfolio.setVisibility(VISIBLE);
            } else {
                workwithBinding.rvPortfolio.setVisibility(GONE);
                workwithBinding.txtShow.setVisibility(VISIBLE);
            }
        });
        workwithBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(4));
        workwithBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(workwithBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 4, profileMenuListOrigin.get(pos).id);
        });
        binding.linearCustom.addView(workwithBinding.getRoot());
    }

    private GetStores storeList;
    private GetProduct productList;

    private void addMyStoreLayout(int pos) {
        ViewMyStoreBinding myStoreBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_my_store, null, false);
        myStoreBinding.txtName.setText(getString(R.string.stores_products));
        myStoreBinding.txtStores.setText(getString(R.string.my_stores));
        myStoreBinding.txtProduct.setText(getString(R.string.my_product));
        myStoreBinding.txtShow.setText(getString(R.string.hide));
        myStoreBinding.txtAdd.setText(getString(R.string.add));
        myStoreBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
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
                myStoreBinding.txtShow.setVisibility(GONE);
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
                myStoreBinding.rvPortfolio.setVisibility(VISIBLE);
            } else {
                myStoreBinding.rvPortfolio.setVisibility(GONE);
                myStoreBinding.txtStores.setVisibility(GONE);
                myStoreBinding.txtShow.setVisibility(VISIBLE);
            }
        });

        myStoreActivityVM.getProductDataList().observe(this, getCompanies -> {
            if (getCompanies != null && getCompanies.data != null && getCompanies.data.size() > 0) {
                myStoreBinding.txtShow.setVisibility(GONE);
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
                myStoreBinding.rvProduct.setVisibility(VISIBLE);
            } else {
                myStoreBinding.rvProduct.setVisibility(GONE);
                myStoreBinding.txtShow.setVisibility(VISIBLE);
                myStoreBinding.txtProduct.setVisibility(GONE);
            }
        });
        myStoreBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(5));

        myStoreBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(myStoreBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 5, profileMenuListOrigin.get(pos).id);
        });

        binding.linearCustom.addView(myStoreBinding.getRoot());
    }

    private void addYoutubeLayout(int pos) {
        ViewYoutubeBinding youtubeBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_youtube, null, false);
        youtubeBinding.txtName.setText(getString(R.string.youtube));
        youtubeBinding.txtShow.setText(getString(R.string.hide));
        youtubeBinding.txtAdd.setText(getString(R.string.add));
        youtubeBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
        YoutubeActivityVM youtubeActivityVM = ViewModelProviders.of(this).get(YoutubeActivityVM.class);
        youtubeActivityVM.init(this);

        if (language.equals("ar")) {
            setArFont(youtubeBinding.txtName, Constants.FONT_AR_MEDIUM);
            setArFont(youtubeBinding.txtShow, Constants.FONT_AR_MEDIUM);
            setArFont(youtubeBinding.txtAdd, Constants.FONT_AR_MEDIUM);
        }


        youtubeActivityVM.getYoutubeListData().observe(this, youtubeData -> {
            if (youtubeData.data != null && youtubeData.data.size() > 0) {
                youtubeBinding.txtShow.setVisibility(GONE);
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
                youtubeBinding.rvPortfolio.setVisibility(VISIBLE);
            } else {
                youtubeBinding.rvPortfolio.setVisibility(GONE);
                youtubeBinding.txtShow.setVisibility(VISIBLE);
            }
        });
        youtubeBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(6));
        youtubeBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(youtubeBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 6, profileMenuListOrigin.get(pos).id);
        });
        binding.linearCustom.addView(youtubeBinding.getRoot());
    }

    private void addPartnerLayout(int pos) {
        ViewPartnerBinding partnerBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_partner, null, false);
        partnerBinding.txtName.setText(getString(R.string.partners));
        partnerBinding.txtShow.setText(getString(R.string.hide));
        partnerBinding.txtAdd.setText(getString(R.string.add));
        partnerBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
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
                partnerBinding.txtShow.setVisibility(GONE);
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
                partnerBinding.rvPortfolio.setVisibility(VISIBLE);
            } else {
                partnerBinding.rvPortfolio.setVisibility(GONE);
                partnerBinding.txtShow.setVisibility(VISIBLE);
            }
        });
        partnerBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(7));
        partnerBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(partnerBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 7, profileMenuListOrigin.get(pos).id);
        });
        binding.linearCustom.addView(partnerBinding.getRoot());
    }

    private void addAgencyLayout(int pos) {
        ViewAgencyBinding agencyBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.view_agency, null, false);
        agencyBinding.txtName.setText(getString(R.string.agency));
        agencyBinding.txtShow.setText(getString(R.string.hide));
        agencyBinding.txtAdd.setText(getString(R.string.add));
        boolean isHide = false;
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


        agencyBinding.imgOrder.setOnClickListener(view -> reOrderDialog());
        if (profileData != null && profileData.profile_agencies != null) {

            agencyBinding.txtAdd.setText(getString(R.string.edit));

            if (!TextUtils.isEmpty(profileData.profile_agencies.name)) {
                isHide = true;
                agencyBinding.txtAgencyName.setText(profileData.profile_agencies.name);
            }

//            if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
//                agencyBinding.txtAg.setText(profileData.profile_agencies.about);
//            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.phone)) {
                isHide = true;
                agencyBinding.txtAgencyNo.setText(profileData.profile_agencies.phone);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.email)) {
                agencyBinding.txtAgencyEmail.setText(profileData.profile_agencies.email);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.address)) {
                isHide = true;
                agencyBinding.txtAgencyLocation.setText(String.format("%s", profileData.profile_agencies.address));
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.website)) {
                isHide = true;
                agencyBinding.txtAgencyWbsite.setText(profileData.profile_agencies.website);
            }

            if (isSelectPublic && profileData.profile_agencies.email_public == 1) {
                agencyBinding.txtAgencyEmail.setVisibility(VISIBLE);
                agencyBinding.imgEmail.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.profile_agencies.email_public == 2) {
                agencyBinding.txtAgencyEmail.setVisibility(VISIBLE);
                agencyBinding.imgEmail.setVisibility(VISIBLE);
            } else {
                agencyBinding.txtAgencyEmail.setVisibility(GONE);
                agencyBinding.imgEmail.setVisibility(GONE);
            }

            if (isSelectPublic && profileData.profile_agencies.about_public == 1) {
                agencyBinding.txtAgencyAbout.setVisibility(VISIBLE);
                agencyBinding.imgInfo.setVisibility(VISIBLE);
            } else if (!isSelectPublic && profileData.profile_agencies.about_public == 2) {
                agencyBinding.txtAgencyAbout.setVisibility(VISIBLE);
                agencyBinding.imgInfo.setVisibility(VISIBLE);
            } else {
                agencyBinding.txtAgencyAbout.setVisibility(GONE);
                agencyBinding.imgInfo.setVisibility(GONE);
            }
            if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
                isHide = true;
                agencyBinding.txtAgencyAbout.setText(profileData.profile_agencies.about);
            } else {
                agencyBinding.txtAgencyAbout.setVisibility(GONE);
                agencyBinding.imgInfo.setVisibility(GONE);
            }
            agencyBinding.txtAgencyAbout.invalidate();

            Glide.with(this).load(profileData.filePaths.agency + profileData.profile_agencies.filename)
                    .error(R.mipmap.ic_launcher).into(agencyBinding.imgProfile);

        }
        if (isHide) {
            agencyBinding.txtShow.setVisibility(GONE);
        } else {
            agencyBinding.txtShow.setVisibility(VISIBLE);
        }

        agencyBinding.txtAdd.setOnClickListener(view -> clickToOpenModule(8));
        agencyBinding.txtShow.setOnClickListener(view -> {
            binding.linearCustom.removeView(agencyBinding.getRoot());
            profileMenuListOrigin.get(pos).isShow = false;
            hideShowMenu(true, 8, profileMenuListOrigin.get(pos).id);
        });
        binding.linearCustom.addView(agencyBinding.getRoot());
    }

    public void reOrderDialog() {
        dialogReOrder = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogReOrder.setTitle(null);
        reorderBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_reorder, null, false);
        reorderBinding.title.setText(getString(R.string.reorder_section));
        dialogReOrder.setContentView(reorderBinding.getRoot());
        dialogReOrder.setCancelable(true);
        if (language.equals("ar")) {
            setArFont(reorderBinding.title, Constants.FONT_AR_BOLD);
        }


        reorderBinding.tvCancel.setOnClickListener(view -> dialogReOrder.dismiss());


        ProfileResponse profileData = Preferences.getProfileData(this);

        List<ProfileMenu> profileMenuList = new ArrayList<>();

        if (profileData != null) {
            if (profileData.settings_order != null) {
                List<String> list = new ArrayList<>(Arrays.asList(profileData.settings_order.split(",")));
                for (String item : list) {
                    profileMenuList.add(profileMenuListOrigin.get(Integer.parseInt(item) - 1));
                }
            } else {
                profileMenuList.addAll(profileMenuListOrigin);
            }
        }

        ProfileMenuAdapter adapter = new ProfileMenuAdapter(this, profileMenuList, this, this);
        adapter.setReOrderScreen(true);
        ItemTouchHelper.Callback callback = new ReOrderItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(reorderBinding.rMenu);
        reorderBinding.rMenu.setAdapter(adapter);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogReOrder.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogReOrder.show();
        dialogReOrder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogReOrder.getWindow().setAttributes(lp);

        dialogReOrder.setOnDismissListener(dialogInterface -> onResume());
    }

    @Override
    public void onSwipeSuccess(List<ProfileMenu> mDatasetFiltered) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mDatasetFiltered.size(); i++) {
            stringBuilder.append(mDatasetFiltered.get(i).id);
            stringBuilder.append(",");
        }
        editProfileActivityVM.updateProfile(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
    }

    @Override
    public void onClickMenu(ProfileMenu menu) {

    }

    private void hideShowMenu(boolean isShow, int menuId, int position) {
        String modifiedString;
        String orijinalValue = "";
        if (profileData.settings_order != null) {
            orijinalValue = profileData.settings_order;
        }
        if (isShow) {
            //hide
            modifiedString = modifyString(orijinalValue, "" + menuId, "");

        } else {
            //show
            modifiedString = modifyString(orijinalValue, "", "" + menuId);
        }
        selectedSectionPos = position;
        editProfileActivityVM.updateProfile(modifiedString);
    }

    private void updateProfileData() {
        try {
            HashMap<String, String> accounts = Preferences.getMultipleAccounts(this);
            UserModel userModel = Preferences.getUserData(this);
            if (accounts != null && accounts.size() > 0 && userModel != null) {
//                if (!accounts.containsKey(userModel.username)) {
                Preferences.addMultipleAccounts(this, userModel.jwt, userModel.email);
//                }
            } else {
                if (userModel != null) {
                    userModel.jwt = getJWT();
                    Preferences.addMultipleAccounts(this, getJWT(), userModel.email);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
