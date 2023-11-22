package com.nojom.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.nojom.R;

public class RadioButtonSFPro extends AppCompatRadioButton {

    public RadioButtonSFPro(Context context) {
        super(context);
    }

    public RadioButtonSFPro(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public RadioButtonSFPro(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewSFTextPro);
        String customFont = a.getString(R.styleable.TextViewSFTextPro_fonts);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
//
        }
        setTypeface(tf);
    }
}
