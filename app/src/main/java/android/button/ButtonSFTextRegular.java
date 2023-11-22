package android.button;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class ButtonSFTextRegular extends androidx.appcompat.widget.AppCompatButton {

    public ButtonSFTextRegular(Context context) {
        super(context);
        applyCustomFont();
    }

    public ButtonSFTextRegular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public ButtonSFTextRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFTEXT_REGULAR)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFTEXT_REGULAR);
            setTypeface(tf);
        }
    }
}
