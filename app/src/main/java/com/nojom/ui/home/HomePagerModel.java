package com.nojom.ui.home;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class HomePagerModel {

    public String title;
    public String name;
    public int icon;
    public String details;

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, int icon) {
        Glide.with(view.getContext())
                .load(icon)
                .into(view);
    }
}
