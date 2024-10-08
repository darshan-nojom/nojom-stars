package com.nojom.ui.policy;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.Html;

import androidx.lifecycle.AndroidViewModel;

import com.nojom.R;
import com.nojom.databinding.ActivityNewPolicyBinding;
import com.nojom.ui.BaseActivity;

class NewPolicyActivityVM extends AndroidViewModel {
    private ActivityNewPolicyBinding binding;
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    NewPolicyActivityVM(Application application, ActivityNewPolicyBinding newPolicyBinding, BaseActivity newPolicyActivity) {
        super(application);
        binding = newPolicyBinding;
        activity = newPolicyActivity;
        initData();
    }

    private void initData() {
        binding.toolbar.tvTitle.setText(activity.getString(R.string.privacy_policy));
        String text = "<h2><strong>" + activity.getString(R.string.task_freelancing_app) + "</strong></h2>" +
                "<p><br /></p>\n" +
                "<p><strong>" + activity.getString(R.string.user_agreement) + ":&nbsp;</strong></p>\n" +
                "<p><span style=\"font-weight: 400;\">As a star of Nojom App,&nbsp; you have agreed </span><strong>NOT</strong><span style=\"font-weight: 400;\"> to do any of the following:</span></p>\n" +
                "<ul>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Fail to deliver/submit the completed work ON (or before)&nbsp; the deadline.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Fail to achieve the expected output of the Client or fail to follow the instructions provided by the Client. (Client has the right to request a redo or edit).&nbsp;&nbsp;</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Convince or inform the Client to Pay you directly on your account (Paypal, Bank, Credit/debit etc.) then instruct the Client to request a refund that has been deposited to the company&rsquo;s account.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Post your email address or any other personal contact information on the Chat, bid note etc, except in the \"email\" field of the signup form, at our request.&nbsp;</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Collect contact details or personal information of the Clients.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Post false, inaccurate, misleading, deceptive, defamatory or offensive content (including personal information).</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Manipulate or Bypass our billing process or fees.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;Provide false information during signing up.</span></li>\n" +
                "</ul>\n" +
                "<p><br /></p>\n" +
                "<p><strong>You are expected to comply with all 24Task USER AGREEMENT and the failure to do so can result in account holds. Account holds lead to account interruptions, and consequences could include the inability to continue working on 24Task and the loss of your hard-earned reputation.</strong></p>\n" +
                "<p><br /><br /></p>\n" +
                "<p><strong>COMMON VIOLATIONS TO AVOID:</strong></p>\n" +
                "<ol>\n" +
                "<li style=\"font-weight: 400;\"><strong><em> &nbsp;Receiving Payments off 24Task</em></strong><span style=\"font-weight: 400;\">: Decline or let us know immediately if Client proposes to pay you directly.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><strong><em> &nbsp;Contact Information Sharing: </em></strong><span style=\"font-weight: 400;\">Keep contact with potential Client through the App ONLY.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><strong><em> &nbsp;Failure to follow instructions given by Clients: </em></strong><span style=\"font-weight: 400;\">When you accept a project, you promise to complete the work and to deliver high-quality work. Failing to fulfill your responsibilities can result in low client satisfaction and payment disputes which will highly affect the company by all means.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><strong><em> &nbsp;False Identity</em></strong><span style=\"font-weight: 400;\">: Provide correct and verifiable information such as Payment details or personal information to avoid payment issues in the future.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><strong><em> &nbsp;Failure to submit during the deadline</em></strong><span style=\"font-weight: 400;\">: Every output is important to the Client so as their time and money! Contact the </span><em><span style=\"font-weight: 400;\">Customer Support</span></em><span style=\"font-weight: 400;\"> if you encountered an error while sending your output.&nbsp;</span></li>\n" +
                "</ol>\n" +
                "<p><br /><br /><br /></p>\n" +
                "<p><strong>FEES AND CHARGES IF YOU HAVE VIOLATION:</strong></p>\n" +
                "<ul>\n" +
                "<li><strong> &nbsp;FIRST OFFENSE: </strong><em><span style=\"font-weight: 400;\">Fees and charges will increase up to</span></em><strong><em> 9%-50%</em></strong><em><span style=\"font-weight: 400;\"> for the first 3 months. Then if you did well and did not violate any of the rules within that 3 months,fees and charges will be back from the regular.&nbsp;</span></em></li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "<li><strong> &nbsp;SECOND OFFENSE: </strong><em><span style=\"font-weight: 400;\">Bidding will be </span></em><strong><em>INACTIVE</em></strong><em><span style=\"font-weight: 400;\">.&nbsp;</span></em></li>\n" +
                "</ul>\n" +
                "<ul>\n" +
                "<li><strong> &nbsp;THIRD OFFENSE: </strong><em><span style=\"font-weight: 400;\">Account will be </span></em><strong><em>INACTIVE.</em></strong></li>\n" +
                "</ul>\n" +
                "<p><br /><br /><br /></p>\n" +
                "<p><strong><em>WITHDRAWALS:</em></strong></p>\n" +
                "<p><span style=\"font-weight: 400;\">Withdrawal requests are being&nbsp; reviewed after the request. We carefully review the jobs assigned to you before we release the payment.&nbsp;</span></p>\n" +
                "<p><span style=\"font-weight: 400;\">Release of payments will take up to a maximum of 10 business days.&nbsp;</span></p>\n" +
                "<p><br /><br /><br /></p>\n" +
                "<p><strong><em>SUSPENSION OF BIDDING OR LIMIT OF ACCESS ON YOUR ACCOUNT:</em></strong></p>\n" +
                "<ol>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;If you under-bid on any Project in an attempt to renegotiate the actual price privately, to attempt to avoid fees.</span></li>\n" +
                "<li style=\"font-weight: 400;\"><span style=\"font-weight: 400;\"> &nbsp;If you do not respond to the Clients messages after he/she hired you. (It&rsquo;s understood that Clients paid their deposit as soon as they accepted a stars bid, so communication must be continuous).</span></li>\n" +
                "</ol>\n" +
                "<p><br /><br /><br /><br /><br /></p>";
        binding.text.setText(Html.fromHtml(text));

        binding.toolbar.imgBack.setOnClickListener(view -> activity.onBackPressed());
    }
}
