package com.nojom.ui.radiobutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.view.View;

import java.util.Arrays;

class RippleHelper {

    static void setSelectableItemBackground(Context context, View view) {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        BackgroundHelper.setBackground(view, drawableFromTheme);
    }

    static void setRipple(View view, int pressedColor, int radius) {
        setRipple(view, pressedColor, null, radius);
    }

    static void setRipple(View view, int pressedColor, Integer normalColor, int radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getRippleDrawable(pressedColor, normalColor, radius));
        } else {
            view.setBackgroundDrawable(getStateListDrawable(pressedColor, normalColor, radius));
        }
    }

    private static StateListDrawable getStateListDrawable(int pressedColor, Integer normalColor, int radius) {
        StateListDrawable states = new StateListDrawable();
        states.addState(
                new int[]{android.R.attr.state_pressed}
                , getDrawable(pressedColor, radius)
        );
        if (null != normalColor)
            states.addState(
                    new int[]{}
                    , getDrawable(normalColor, radius)
            );
        return states;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getRippleDrawable(int pressedColor, Integer normalColor, int radius) {
        ColorStateList colorStateList = getPressedColorSelector(pressedColor);
        Drawable content = null != normalColor ? new ColorDrawable(normalColor) : null;
        Drawable mask = getRippleMask(Color.WHITE, radius);

        return new RippleDrawable(colorStateList, content, mask);
    }

    private static ColorStateList getPressedColorSelector(int pressedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        pressedColor
                }
        );
    }

    private static Drawable getRippleMask(int color, int radius) {
        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, radius);
        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    private static Drawable getDrawable(Integer color, int radius) {
        if (color == null)
            return null;
        if (radius == 0)
            return new ColorDrawable(color);

        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, radius);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(outerRadii);
        shape.setColor(color);
        return shape;
    }

}
