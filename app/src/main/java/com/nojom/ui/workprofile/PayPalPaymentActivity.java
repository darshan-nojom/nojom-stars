package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityPaymentBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class PayPalPaymentActivity extends BaseActivity {
    private ActivityPaymentBinding binding;
    private String url;
    private float m_downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(view -> onBackPressed());
        if (getIntent() != null) {
            url = getIntent().getStringExtra(Constants.PAYPAL_URL);
        }

        binding.tvToolbarTitle.setText(getString(R.string.payment));

        initWebView();

        if (!isNetworkConnected())
            return;

        binding.webView.loadUrl(url);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initWebView() {
        binding.webView.setWebChromeClient(new MyWebChromeClient(this));
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("Page Start URL", url);
//                showProgress();
                if (url.contains("success")) {
                    new Handler().postDelayed(() -> {
                        Intent i = new Intent();
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    }, 700);
                } else if (url.contains("failed")) {
                    toastMessage(getString(R.string.make_sure_account));
                    onBackPressed();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", url);
                binding.webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                hideProgress();
                toastMessage(getString(R.string.error_paypal));
                finish();
            }
        });
        binding.webView.clearCache(true);
        binding.webView.clearHistory();
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(false);
        binding.webView.setOnTouchListener((v, event) -> {

            if (event.getPointerCount() > 1) {
                //Multi touch detected
                return true;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    // save the x
                    m_downX = event.getX();
                }
                break;

                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    // set x so that it doesn't move
                    event.setLocation(m_downX, event.getY());
                }
                break;
            }

            return false;
        });
    }

    private static class MyWebChromeClient extends WebChromeClient {
        Context context;

        MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    @Override
    protected void onDestroy() {
//        hideProgress();
        super.onDestroy();
    }
}
