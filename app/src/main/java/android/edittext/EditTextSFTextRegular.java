package android.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class EditTextSFTextRegular extends androidx.appcompat.widget.AppCompatEditText {

    public EditTextSFTextRegular(Context context) {
        super(context);
        applyCustomFont();
    }

    public EditTextSFTextRegular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public EditTextSFTextRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
