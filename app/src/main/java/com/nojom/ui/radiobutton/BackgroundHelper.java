package com.nojom.ui.radiobutton;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

class BackgroundHelper {

    static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(drawable);
        }
    }
}
