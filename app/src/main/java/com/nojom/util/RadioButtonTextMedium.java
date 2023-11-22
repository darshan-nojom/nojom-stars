package com.nojom.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

public class RadioButtonTextMedium extends AppCompatRadioButton {

    public RadioButtonTextMedium(Context context) {
        super(context);
        applyCustomFont();
    }

    public RadioButtonTextMedium(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public RadioButtonTextMedium(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFTEXT_MEDIUM)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFTEXT_MEDIUM);
            setTypeface(tf);
        }
    }
}
