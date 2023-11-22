package android.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class EditTextSFDisplayRegular extends androidx.appcompat.widget.AppCompatEditText {

    public EditTextSFDisplayRegular(Context context) {
        super(context);
        applyCustomFont();
    }

    public EditTextSFDisplayRegular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public EditTextSFDisplayRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFDISPLAY_REGULAR)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFDISPLAY_REGULAR);
            setTypeface(tf);
        }
    }
}
