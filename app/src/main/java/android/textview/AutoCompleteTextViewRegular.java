package android.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.nojom.util.Constants;

public class AutoCompleteTextViewRegular extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    public AutoCompleteTextViewRegular(Context context) {
        this(context, null);
    }

    public AutoCompleteTextViewRegular(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoCompleteTextViewRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
