package android.button;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class ButtonSFTextSemiBold extends androidx.appcompat.widget.AppCompatButton {

    public ButtonSFTextSemiBold(Context context) {
        super(context);
        applyCustomFont();
    }

    public ButtonSFTextSemiBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public ButtonSFTextSemiBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFTEXT_SEMIBOLD)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFTEXT_SEMIBOLD);
            setTypeface(tf);
        }
    }
}
