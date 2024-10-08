package com.nojom.ui.discount;

import static com.nojom.util.Constants.API_GET_PROMOCODE_HISTORY;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.ReferralHistoryAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.FragmentEarnMoneyBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ReferralHistory;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


class EarnMoneyFragmentVM extends AndroidViewModel implements View.OnClickListener, APIRequest.APIRequestListener {
    private final FragmentEarnMoneyBinding binding;
    private final BaseFragment fragment;
    private String shareText;

    EarnMoneyFragmentVM(Application application, FragmentEarnMoneyBinding earnMoneyBinding, BaseFragment earnMoneyFragment) {
        super(application);
        binding = earnMoneyBinding;
        fragment = earnMoneyFragment;
        initData();
    }

    private void initData() {
        binding.txtCopy.setOnClickListener(this);
        binding.linEmail.setOnClickListener(this);
        binding.linMore.setOnClickListener(this);
        binding.linMsg.setOnClickListener(this);

        binding.tvHowItWorks.setPaintFlags(binding.tvHowItWorks.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (fragment.activity.getCurrency().equals("SAR")) {
            binding.tvHereBlue.setText(fragment.activity.getString(R.string.give_10_get_10_sar));
            binding.txtT1.setText(fragment.activity.getString(R.string.anyone_who_use_your_coupon_or_follows_your_link_gets_10_off_the_first_order_sar));
            binding.txtT2.setText(fragment.activity.getString(R.string.your_friend_s_order_must_be_completed_so_you_can_use_your_10_sar));
            binding.txtT3.setText(fragment.activity.getString(R.string.get_10_for_every_friend_sar));
            binding.txtT3D.setText(fragment.activity.getString(R.string.for_example_if_you_invited_100_friends_then_you_will_get_1_000_sar));
            binding.tvTermsOfUse.setText(fragment.activity.getString(R.string.earn_money_footer_sar));
        }

        binding.rvReferral.setLayoutManager(new LinearLayoutManager(fragment.activity));
        binding.rvReferral.addItemDecoration(new EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.VERTICAL));

        binding.tvHowItWorks.setOnClickListener(view -> binding.scrollview.post(() ->
                scrollToView(binding.scrollview, binding.txtBlueLabel)));


        ClickableSpan refCodeClick = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                copyMsg(fragment.activity.getReferralCode());
            }
        };

//        String language = Preferences.readString(fragment.activity, Constants.SELECTED_LANGUAGE, "");
        String textLine;
        if (fragment.activity.getCurrency().equals("SAR")) {
            textLine = "Give your friends 10% off their first order and get 20% of their first order up to 200 SAR for every friend who place their first order using your link or promo code (<u>" + fragment.activity.getReferralCode() + "</u>).";
            if (!TextUtils.isEmpty(fragment.activity.language)) {
                if (fragment.activity.language.equals("ar")) {
                    textLine = " قدّم لأصدقائك خصمًا بنسبة 10% على حملتهم الأولى واحصل على خصم بنسبة 20% يصل إلى 200 ريال، لكل صديق يقوم بإطلاق حملته الأولى باستخدام كود الخصم الخاص بك"+fragment.activity.getReferralCode();
                }
            }
        } else {
            textLine = "Give your friends 10% off their first order and get 20% of their first order up to $200 for every friend who place their first order using your link or promo code (<u>" + fragment.activity.getReferralCode() + "</u>).";
            if (!TextUtils.isEmpty(fragment.activity.language)) {
                if (fragment.activity.language.equals("ar")) {
                    textLine = fragment.activity.getReferralCode() + "قدّم لأصدقائك خصمًا بنسبة 10% على حملتهم الأولى واحصل على خصم بنسبة 20% يصل إلى 200 ريال، لكل صديق يقوم بإطلاق حملته الأولى باستخدام كود الخصم الخاص بك  ";
                }
            }
        }

        SpannableStringBuilder refCodeTxt = Utils.getColorString(fragment.activity, Html.fromHtml(textLine).toString(),
                fragment.activity.getReferralCode(), R.color.colorPrimary);
        binding.earnMoneyTitle.setText(refCodeTxt);
        Utils.makeLinks(binding.earnMoneyTitle, new String[]{fragment.activity.getReferralCode()}, new ClickableSpan[]{refCodeClick});

        ClickableSpan tncClick = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                fragment.activity.redirectUsingCustomTab(Constants.TERMS_USE);
            }
        };

        try {
            Utils.makeLinks(binding.tvTermsOfUse, new String[]{fragment.getString(R.string.terms_of_use_)}, new ClickableSpan[]{tncClick});
            setLink();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getReferralHistory();
    }

    void setLink() {
        try {
            if (fragment.activity != null) {
                String link = ((GetDiscountActivity) fragment.activity).getmInvitationUrl();
                binding.txtRefLink.setText(String.format(fragment.getString(R.string.task_promo_code) + " ", fragment.activity.getReferralCode()));
                if (fragment.activity.getCurrency().equals("SAR")) {
                    shareText = fragment.activity.getString(R.string.share_1_sar) + "\n\n1. "
                            + link + "\n" + fragment.activity.getString(R.string.share_2) + " " + fragment.activity.getReferralCode() + "\n" + fragment.activity.getString(R.string.share_3_sar);
                } else {
                    shareText = fragment.activity.getString(R.string.share_1) + "\n\n1. "
                            + link + "\n" + fragment.activity.getString(R.string.share_2) + " " + fragment.activity.getReferralCode() + "\n" + fragment.activity.getString(R.string.share_3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        switch (view.getId()) {
            case R.id.txt_copy:
//                copyMsg(binding.txtRefLink.getText().toString());
                copyMsg(fragment.activity.getReferralCode());
                break;
            case R.id.lin_msg:
                sendMsg();
                break;
            case R.id.lin_more:
                shareLink();
                break;
            case R.id.lin_email:
                sendEmail();
                break;
        }
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Referral link");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        fragment.startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void shareLink() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        fragment.startActivity(sendIntent);
    }

    private void sendMsg() {
        try {
            Uri uri = Uri.parse("smsto:");
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", shareText);
            fragment.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyMsg(String msg) {
        ClipboardManager clipboard = (ClipboardManager) fragment.activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied", msg);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            fragment.activity.toastMessage("Copied");
        }
    }

    private void getReferralHistory() {
        if (!fragment.activity.isNetworkConnected())
            return;

//        fragment.activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_GET_PROMOCODE_HISTORY, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_PROMOCODE_HISTORY)) {
            ReferralHistory model = ReferralHistory.getPromoCodeHistory(decryptedData);
            List<ReferralHistory.Data> historyData = new ArrayList<>();
            ReferralHistory.Data data = new ReferralHistory.Data();
            data.username = "Name of Friends";
            data.timestamp = "Date of first Order";
            historyData.add(data);

            if (model != null && model.data != null) {
                historyData.addAll(model.data);
            }
            if (historyData.size() > 1) {
                binding.relReferral.setVisibility(View.VISIBLE);
                binding.txtAmount.setText(fragment.activity.getCurrency().equals("SAR") ? Utils.priceWithSAR(fragment.activity, Utils.get2DecimalPlaces(Objects.requireNonNull(model).totalBalance))
                        : Utils.priceWith$(Utils.get2DecimalPlaces(Objects.requireNonNull(model).totalBalance),fragment.activity));
                binding.rvReferral.setVisibility(View.VISIBLE);
                ReferralHistoryAdapter adapter = new ReferralHistoryAdapter(fragment.activity, historyData);
                binding.rvReferral.setAdapter(adapter);
            }
        }
//        fragment.activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        fragment.activity.hideProgress();
    }
}
