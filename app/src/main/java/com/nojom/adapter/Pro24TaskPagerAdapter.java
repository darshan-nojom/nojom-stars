package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nojom.R;
import com.nojom.databinding.Item24taskProBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

public class Pro24TaskPagerAdapter extends PagerAdapter {

    private BaseActivity mContext;

    public Pro24TaskPagerAdapter(BaseActivity mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(collection.getContext());
        Item24taskProBinding binding =
                Item24taskProBinding.inflate(layoutInflater, collection, false);

        if (position == 0) {
            binding.tvProfileComplete.setVisibility(View.GONE);
            String s = mContext.getString(R.string.what_s_24task_pro);
            int[] colorList = {R.color.red};
            String[] words = {"PRO"};
            binding.tvTitle.setText(Utils.getBoldString(mContext, s, null, colorList, words));
            binding.tvDesciption.setText(mContext.getString(R.string.what_pro_text));
        } else if (position == 1) {
            binding.tvProfileComplete.setVisibility(View.GONE);
            String s = mContext.getString(R.string.how_it_works);
            int[] colorList = {R.color.colorPrimary};
            String[] words = {"works"};
            binding.tvTitle.setText(Utils.getBoldString(mContext, s, null, colorList, words));
            binding.tvDesciption.setText(mContext.getString(R.string.how_it_works_text));
        } else if (position == 2) {
            binding.tvProfileComplete.setVisibility(View.GONE);
            String s = mContext.getString(R.string.guarantee_full_time);
            int[] colorList = {R.color.lightgreen};
            String[] words = {"Full-time"};
            binding.tvTitle.setText(Utils.getBoldString(mContext, s, null, colorList, words));
            binding.tvDesciption.setText(mContext.getString(R.string.guarantee_job_text));
        } else if (position == 3) {
            binding.tvProfileComplete.setVisibility(View.GONE);
            String s = mContext.getString(R.string.guarantee_payment);
            int[] colorList = {R.color.skin};
            String[] words = {"payment"};
            binding.tvTitle.setText(Utils.getBoldString(mContext, s, null, colorList, words));
            binding.tvDesciption.setText(mContext.getString(R.string.guarantee_payment_text));
        } else if (position == 4) {
            binding.tvProfileComplete.setVisibility(View.GONE);
            String s = mContext.getString(R.string.how_much_do_we_pay);
            int[] colorList = {R.color.colorPrimary};
            String[] words = {"pay"};
            binding.tvTitle.setText(Utils.getBoldString(mContext, s, null, colorList, words));
            binding.tvDesciption.setText(mContext.getCurrency().equals("SAR") ?mContext.getString(R.string.how_much_pay_text_sar):mContext.getString(R.string.how_much_pay_text));
        } else if (position == 5) {
            if (Preferences.getProfileData(mContext) != null && Preferences.getProfileData(mContext).percentage != null) {
                try {
                    String profilePercentage = Math.round(Preferences.getProfileData(mContext).percentage.totalPercentage) + "%";
                    binding.tvProfileComplete.setText(Utils.getColorString(mContext,
                            mContext.getString(R.string.your_profile_is, profilePercentage), profilePercentage, R.color.red));
                    binding.tvProfileComplete.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                binding.tvProfileComplete.setText(mContext.getString(R.string.your_profile_is_not_complete));
            }
            binding.tvTitle.setText(mContext.getString(R.string.how_to_apply));
            String s = mContext.getString(R.string.how_to_apply_text);
            int[] colorList = {R.color.lightgreen};
            String[] words = {"100%"};
            binding.tvDesciption.setText(Utils.getBoldString(mContext, s, null, colorList, words));
        }
        collection.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
