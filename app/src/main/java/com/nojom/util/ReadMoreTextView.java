package com.nojom.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.nojom.R;

public class ReadMoreTextView extends AppCompatTextView {

    private static final int TRIM_MODE_LINES = 0;
    private static final int TRIM_MODE_LENGTH = 1;
    private static final int DEFAULT_TRIM_LENGTH = 240;
    private static final int DEFAULT_TRIM_LINES = 2;
    private static final int INVALID_END_INDEX = -1;
    private static final boolean DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true;
    private static final String ELLIPSIZE = "... ";

    private CharSequence text;
    private BufferType bufferType;
    private boolean readMore = true;
    private int trimLength;
    private CharSequence trimCollapsedText;
    private CharSequence trimExpandedText;
    private ReadMoreClickableSpan viewMoreSpan;
    private int readMoreTextColor;
    private int readLessTextColor;
    private boolean showTrimExpandedText;

    private int trimMode;
    private int lineEndIndex;
    private int trimLines;

    public ReadMoreTextView(Context context) {
        this(context, null);
    }

    public ReadMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView);
        this.trimLength = typedArray.getInt(R.styleable.ReadMoreTextView_trimLength, DEFAULT_TRIM_LENGTH);
        int resourceIdTrimCollapsedText =
                typedArray.getResourceId(R.styleable.ReadMoreTextView_trimCollapsedText, R.string.read_more_small);
        int resourceIdTrimExpandedText =
                typedArray.getResourceId(R.styleable.ReadMoreTextView_trimExpandedText, R.string.read_less_small);
        this.trimCollapsedText = getResources().getString(resourceIdTrimCollapsedText);
        this.trimExpandedText = getResources().getString(resourceIdTrimExpandedText);
        this.trimLines = typedArray.getInt(R.styleable.ReadMoreTextView_trimLines, DEFAULT_TRIM_LINES);
        this.readMoreTextColor = typedArray.getColor(R.styleable.ReadMoreTextView_readMoreTextColor,
                ContextCompat.getColor(context, R.color.colorPrimary));
        this.readLessTextColor = typedArray.getColor(R.styleable.ReadMoreTextView_readMoreTextColor,
                ContextCompat.getColor(context, R.color.orange_light));
        this.showTrimExpandedText =
                typedArray.getBoolean(R.styleable.ReadMoreTextView_showTrimExpandedText, DEFAULT_SHOW_TRIM_EXPANDED_TEXT);
        this.trimMode = typedArray.getInt(R.styleable.ReadMoreTextView_trimMode, TRIM_MODE_LINES);
        typedArray.recycle();
        viewMoreSpan = new ReadMoreClickableSpan();
        onGlobalLayoutLineEndIndex();
        setText();
    }

    private void applyCustomFont() {
        if (!TextUtils.isEmpty(Constants.SFTEXT_REGULAR)) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), Constants.SFTEXT_REGULAR);
            setTypeface(tf);
        }
    }

    public void applyCustomFontBold() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/SF-Pro-Text-Bold.otf");
        setTypeface(tf);
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
        setMovementMethod(LinkMovementMethod.getInstance());
        setHighlightColor(Color.TRANSPARENT);
    }

    private CharSequence getDisplayableText() {
        return getTrimmedText(text);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (trimMode == TRIM_MODE_LENGTH) {
            if (text != null && text.length() > trimLength) {
                if (readMore) {
                    return updateCollapsedText();
                } else {
                    return updateExpandedText();
                }
            }
        }
        if (trimMode == TRIM_MODE_LINES) {
            if (text != null && lineEndIndex > 0) {
                if (readMore) {
                    if (getLayout().getLineCount() > trimLines) {
                        return updateCollapsedText();
                    }
                } else {
                    return updateExpandedText();
                }
            }
        }
        return text;
    }

    private CharSequence updateCollapsedText() {
        try {

            int trimEndIndex = text.length();
            switch (trimMode) {
                case TRIM_MODE_LINES:
                    trimEndIndex = lineEndIndex - (ELLIPSIZE.length() + trimCollapsedText.length() + 1);
                    if (trimEndIndex < 0) {
                        trimEndIndex = trimLength + 1;
                    }
                    break;
                case TRIM_MODE_LENGTH:
                    trimEndIndex = trimLength + 1;
                    break;
            }
            SpannableStringBuilder s = new SpannableStringBuilder(text, 0, trimEndIndex)
                    .append(ELLIPSIZE)
                    .append(trimCollapsedText);
            return addClickableSpan(s, trimCollapsedText);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return text;
        }
    }

    private CharSequence updateExpandedText() {
        if (showTrimExpandedText) {
            SpannableStringBuilder s = new SpannableStringBuilder(text, 0, text.length()).append(trimExpandedText);
            return addClickableSpan(s, trimExpandedText);
        }
        return text;
    }

    private CharSequence addClickableSpan(SpannableStringBuilder s, CharSequence trimText) {
        s.setSpan(viewMoreSpan, s.length() - trimText.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        setText();
    }

    public void setReadMoreTextColor(int readMoreTextColor) {
        this.readMoreTextColor = readMoreTextColor;
    }

    public void setTrimCollapsedText(CharSequence trimCollapsedText) {
        this.trimCollapsedText = trimCollapsedText;
    }

    public void setTrimExpandedText(CharSequence trimExpandedText) {
        this.trimExpandedText = trimExpandedText;
    }

    public void setTrimMode(int trimMode) {
        this.trimMode = trimMode;
    }

    public void setTrimLines(int trimLines) {
        this.trimLines = trimLines;
    }

    private class ReadMoreClickableSpan extends ClickableSpan {
        @Override
        public void onClick(View widget) {
            readMore = !readMore;
            setText();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(readMore ? readMoreTextColor : readLessTextColor);
            ds.setUnderlineText(true);
        }
    }

    private void onGlobalLayoutLineEndIndex() {
        if (trimMode == TRIM_MODE_LINES) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = getViewTreeObserver();
                    obs.removeOnGlobalLayoutListener(this);
                    refreshLineEndIndex();
                    setText();
                }
            });
        }
    }

    private void refreshLineEndIndex() {
        try {
            if (trimLines == 0) {
                lineEndIndex = getLayout().getLineEnd(0);
            } else if (trimLines > 0 && getLineCount() >= trimLines) {
                lineEndIndex = getLayout().getLineEnd(trimLines - 1);
            } else {
                lineEndIndex = INVALID_END_INDEX;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
