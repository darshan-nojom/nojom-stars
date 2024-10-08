package com.nojom.ui.discount;

import android.app.Application;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.style.ClickableSpan;
import android.textview.TextViewSFTextPro;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.nojom.R;
import com.nojom.apis.GetSocialSurveyAPI;
import com.nojom.databinding.FragmentWinBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.SocialSurveyListModel;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.jetbrains.annotations.NotNull;

import static android.app.Activity.RESULT_OK;

class WinFragmentVM extends AndroidViewModel implements View.OnClickListener {
    private FragmentWinBinding binding;
    private BaseFragment fragment;
    private GetSocialSurveyAPI socialSurveyAPI;

    WinFragmentVM(Application application, FragmentWinBinding winBinding, BaseFragment winFragment) {
        super(application);
        binding = winBinding;
        fragment = winFragment;
        initData();
    }

    private void initData() {
        socialSurveyAPI = new GetSocialSurveyAPI();
        socialSurveyAPI.init(fragment.activity);
        socialSurveyAPI.getSocialSurveyAPI();

        binding.tvHowItWorks.setPaintFlags(binding.tvHowItWorks.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.tvHowItWorks.setOnClickListener(this);
        binding.txtStart.setOnClickListener(this);
        binding.relAppStore.setOnClickListener(this);
        binding.relGooglePlay.setOnClickListener(this);
        binding.relGoogle.setOnClickListener(this);
        binding.relFacebook.setOnClickListener(this);
        binding.relTrustpilot.setOnClickListener(this);
        binding.relSitejabber.setOnClickListener(this);
        binding.linStartSurvey.setVisibility(View.VISIBLE);
        binding.linSurvey.setVisibility(View.GONE);

        ClickableSpan tncClick = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                fragment.activity.redirectUsingCustomTab(Constants.TERMS_USE);
            }
        };

        if (fragment.activity.getCurrency().equals("SAR")) {
            binding.tvHereBlue.setText(fragment.activity.getString(R.string.answer_6_questions_and_get_2_plus_get_a_chance_to_win_100_gift_card_sar));
            binding.txtT3.setText(fragment.activity.getString(R.string.get_2_for_every_review_sar));
            binding.txtT3D.setText(fragment.activity.getString(R.string.you_will_get_2_for_one_review_on_social_platform_sar));
            binding.tvTermsOfUse.setText(fragment.activity.getString(R.string.earn_money_footer_sar));
        }

        Utils.makeLinks(binding.tvTermsOfUse, new String[]{fragment.getString(R.string.terms_of_use_)}, new ClickableSpan[]{tncClick});

        socialSurveyAPI.getSocialSurveyMutableLiveData().observe(fragment, surveyListModel -> {
            setStatus(0, binding.tvProfileComplete, null, null);
            setStatus(0, binding.txtAppstoreStatus, null, binding.relAppStore);
            setStatus(0, binding.txtGooglepayStatus, null, binding.relGooglePlay);
            setStatus(0, binding.txtGoogleStatus, null, binding.relGoogle);
            setStatus(0, binding.txtFacebookStatus, null, binding.relFacebook);
            setStatus(0, binding.txtTrustpilotStatus, null, binding.relTrustpilot);
            setStatus(0, binding.txtSitejabberStatus, null, binding.relSitejabber);

            if (surveyListModel != null && surveyListModel.data.size() > 0) {
                for (SocialSurveyListModel.Data data : surveyListModel.data) {
                    switch (data.id) {
                        case 1:
                            setStatus(data.surveyStatus, binding.txtAppstoreStatus, data.note, binding.relAppStore);
                            break;
                        case 2:
                            setStatus(data.surveyStatus, binding.txtGooglepayStatus, data.note, binding.relGooglePlay);
                            break;
                        case 3:
                            setStatus(data.surveyStatus, binding.txtGoogleStatus, data.note, binding.relGoogle);
                            break;
                        case 4:
                            setStatus(data.surveyStatus, binding.txtFacebookStatus, data.note, binding.relFacebook);
                            break;
                        case 5:
                            setStatus(data.surveyStatus, binding.txtTrustpilotStatus, data.note, binding.relTrustpilot);
                            break;
                        case 6:
                            setStatus(data.surveyStatus, binding.txtSitejabberStatus, data.note, binding.relSitejabber);
                            break;
                        case 7:
                            setStatus(data.surveyStatus, binding.tvProfileComplete, data.note, null);
                            break;
                    }
                }
            }
        });
    }

    void onResumeMethod() {
        if (fragment.activity.getProfileData() != null && fragment.activity.getProfileData().agentSurvey == 1) {
            binding.rlSurveyReview.setVisibility(View.VISIBLE);
            binding.viewDivider.setVisibility(View.VISIBLE);
            binding.relStartSurvey.setVisibility(View.GONE);
        } else {
            binding.rlSurveyReview.setVisibility(View.GONE);
            binding.viewDivider.setVisibility(View.GONE);
            binding.relStartSurvey.setVisibility(View.VISIBLE);
        }
    }


    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.txt_start:
                intent = new Intent(fragment.activity, AddSurveyActivity.class);
                fragment.startActivityForResult(intent, 125);
                break;
            case R.id.rel_app_store:
                if (binding.relAppStore.getTag() != null && !binding.relAppStore.getTag().equals("1")) {
                    openReSubmitSurveyActivity(1);
                }
                break;
            case R.id.rel_google_play:
                if (binding.relGooglePlay.getTag() != null && !binding.relGooglePlay.getTag().equals("1")) {
                    openReSubmitSurveyActivity(2);
                }
                break;
            case R.id.rel_google:
                if (binding.relGoogle.getTag() != null && !binding.relGoogle.getTag().equals("1")) {
                    openReSubmitSurveyActivity(3);
                }
                break;
            case R.id.rel_facebook:
                if (binding.relFacebook.getTag() != null && !binding.relFacebook.getTag().equals("1")) {
                    openReSubmitSurveyActivity(4);
                }
                break;
            case R.id.rel_trustpilot:
                if (binding.relTrustpilot.getTag() != null && !binding.relTrustpilot.getTag().equals("1")) {
                    openReSubmitSurveyActivity(5);
                }
                break;
            case R.id.rel_sitejabber:
                if (binding.relSitejabber.getTag() != null && !binding.relSitejabber.getTag().equals("1")) {
                    openReSubmitSurveyActivity(6);
                }
                break;
            case R.id.tv_how_it_works:
                binding.scrollview.post(() ->
                        scrollToView(binding.scrollview, binding.txtBlueLabel));
                break;
        }
    }

    private void openReSubmitSurveyActivity(int socialId) {
        Intent intent = new Intent(fragment.activity, ReSubmitSurveyActivity.class);
        intent.putExtra("social_id", socialId);
        switch (socialId) {
            case 1:
                intent.putExtra("note", "" + binding.txtAppstoreStatus.getTag());
                break;
            case 2:
                intent.putExtra("note", "" + binding.txtGooglepayStatus.getTag());
                break;
            case 3:
                intent.putExtra("note", "" + binding.txtGoogleStatus.getTag());
                break;
            case 4:
                intent.putExtra("note", "" + binding.txtFacebookStatus.getTag());
                break;
            case 5:
                intent.putExtra("note", "" + binding.txtTrustpilotStatus.getTag());
                break;
            case 6:
                intent.putExtra("note", "" + binding.txtSitejabberStatus.getTag());
                break;
        }
        fragment.startActivityForResult(intent, 125);
    }


    private void setStatus(int surveyStatus, TextViewSFTextPro txtStatus, String note, RelativeLayout relativeLayout) {
        if (relativeLayout != null) {
            relativeLayout.setTag("" + surveyStatus);
        }
        txtStatus.setTag(note);
        switch (surveyStatus) {
            case 1:
                txtStatus.setText(fragment.getString(R.string.completed));
//                txtStatus.setTag(fragment.getString(R.string.deposited));
                txtStatus.setBackground(fragment.getResources().getDrawable(R.drawable.green_rounded_corner_20));
                txtStatus.setTextColor(fragment.getResources().getColor(R.color.white));
                break;
            case 2:
                txtStatus.setText(fragment.getString(R.string.under_review));
//                txtStatus.setTag(fragment.getString(R.string.under_review));
                txtStatus.setBackground(fragment.getResources().getDrawable(R.drawable.orangelight_bg_20));
                txtStatus.setTextColor(fragment.getResources().getColor(R.color.white));
                break;
            case 3:
                txtStatus.setText(fragment.getString(R.string.rejected));
//                txtStatus.setTag(fragment.getString(R.string.rejected));
                txtStatus.setBackground(fragment.getResources().getDrawable(R.drawable.red_bg_20));
                txtStatus.setTextColor(fragment.getResources().getColor(R.color.white));
                break;
            default:
                txtStatus.setText(fragment.getString(R.string.not_started));
//                txtStatus.setTag(fragment.getString(R.string.not_started));
                txtStatus.setBackground(fragment.getResources().getDrawable(R.drawable.gray_rounded_corner_20));
                txtStatus.setTextColor(fragment.getResources().getColor(R.color.tab_gray));
        }
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 125 && resultCode == RESULT_OK) {
            binding.linStartSurvey.setVisibility(View.GONE);
            binding.linSurvey.setVisibility(View.VISIBLE);
            socialSurveyAPI.getSocialSurveyAPI();
            fragment.activity.getProfile();
        }
    }
}
