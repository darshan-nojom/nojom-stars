package android.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.nojom.R;

public class TextViewSFTextPro extends AppCompatTextView {

    public TextViewSFTextPro(Context context) {
        this(context, null);
    }

    public TextViewSFTextPro(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        setCustomFont(context, attrs);
    }

    public TextViewSFTextPro(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewSFTextPro);
        String customFont = a.getString(R.styleable.TextViewSFTextPro_fonts);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
//            Log.e(TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
