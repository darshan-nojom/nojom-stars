package com.nojom.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nojom.R;
import com.nojom.databinding.ItemOnboardingBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class OnBoardingAdapter extends PagerAdapter {

    private BaseActivity mContext;

    public OnBoardingAdapter(BaseActivity mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(collection.getContext());
        ItemOnboardingBinding binding = ItemOnboardingBinding.inflate(layoutInflater, collection, false);

        if (position == 0) {
            if (mContext.language.equals("ar")) {
                binding.img.setImageResource(R.drawable.board_1_ar);
            } else {
                binding.img.setImageResource(R.drawable.board_1);
            }
            binding.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            binding.tvTitle.setText(mContext.getString(R.string.shine_with_the_stars));
//            binding.tvDesciption.setText(mContext.getString(R.string.join_our_platform_to_be_part_of_an_exclusive_community_of_celebrities_and_influencers));
        } else if (position == 1) {
            if (mContext.language.equals("ar")) {
                binding.img.setImageResource(R.drawable.board_2_ar);
            } else {
                binding.img.setImageResource(R.drawable.board_2);
            }
            binding.img.setScaleType(ImageView.ScaleType.FIT_XY);
//            binding.tvTitle.setText(mContext.getString(R.string.exceptional_experience));
//            binding.tvDesciption.setText(mContext.getString(R.string.enjoy_an_outstanding_user_experience_and_exclusive_features_just_for_celebrities));
        } else if (position == 2) {
            if (mContext.language.equals("ar")) {
                binding.img.setImageResource(R.drawable.board_3_ar);
            } else {
                binding.img.setImageResource(R.drawable.board_3);
            }
            binding.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            binding.tvTitle.setText(mContext.getString(R.string.new_opportunities));
//            binding.tvDesciption.setText(mContext.getString(R.string.join_us_and_discover_collaboration_opportunities_with_prestigious_brands_and_global_companies));
        }
//        if (mContext.language.equals("ar")) {
//            mContext.setArFont(binding.tvTitle, Constants.FONT_AR_BOLD);
//            mContext.setArFont(binding.tvDesciption, Constants.FONT_AR_REGULAR);
//        }
        collection.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
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
