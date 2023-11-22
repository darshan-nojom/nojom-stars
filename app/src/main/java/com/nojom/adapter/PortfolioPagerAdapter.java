package com.nojom.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class PortfolioPagerAdapter extends PagerAdapter {
    private BaseActivity activity;
    private List<Portfolios.PortfolioFiles> portfolioFiles;
    private LayoutInflater layoutInflater;
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    public PortfolioPagerAdapter(BaseActivity context, List<Portfolios.PortfolioFiles> images) {
        this.activity = context;
        this.portfolioFiles = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return portfolioFiles.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_image_portfolio, container, false);

        ImageView imageView = itemView.findViewById(R.id.image);
        ProgressBar progressBar = itemView.findViewById(R.id.progressBar2);

        if (portfolioFiles != null && portfolioFiles.size() > 0 && !TextUtils.isEmpty(portfolioFiles.get(position).path)) {
            try {
                String urlEncoded = Uri.encode(portfolioFiles.get(position).path, ALLOWED_URI_CHARS);
                Log.e("PORTFOLIO URL ", "----------------- " + activity.getPortfolioUrl() + urlEncoded);
                Glide.with(activity).load(activity.getPortfolioUrl() + urlEncoded)
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        container.addView(itemView);

        return itemView;
    }

    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}