package android.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class EditTextSFTextBold extends androidx.appcompat.widget.AppCompatEditText {

    public EditTextSFTextBold(Context context) {
        super(context);
        applyCustomFont();
    }

    public EditTextSFTextBold(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
    }

    public EditTextSFTextBold(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFTEXT_BOLD)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFTEXT_BOLD);
            setTypeface(tf);
        }
    }
}
