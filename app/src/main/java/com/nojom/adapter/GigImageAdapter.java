package com.nojom.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemGigImgBinding;
import com.nojom.model.GigDetails;

import java.util.List;

public class GigImageAdapter extends PagerAdapter {

    private Context mContext;
    private List<GigDetails.GigImage> gigImages;
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private String gigImagesPath = "";

    public GigImageAdapter(Context mContext, List<GigDetails.GigImage> gigImages, String gigImagesPath) {
        this.mContext = mContext;
        this.gigImages = gigImages;
        this.gigImagesPath = gigImagesPath;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(collection.getContext());
        ItemGigImgBinding binding =
                ItemGigImgBinding.inflate(layoutInflater, collection, false);

        if (gigImages != null && gigImages.size() > 0 && !TextUtils.isEmpty(gigImages.get(position).imageName)) {
            try {
                String urlEncoded = Uri.encode(gigImages.get(position).imageName, ALLOWED_URI_CHARS);
                Glide.with(mContext).load(gigImagesPath + urlEncoded)
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
                        .into(binding.img);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
        return gigImages != null ? gigImages.size() : 0;
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
