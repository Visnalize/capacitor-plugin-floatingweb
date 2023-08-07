package com.visnalize.capacitor.plugins.floatingweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;

public class FloatingWeb {
    private final Activity activity;
    private WebView webView;

    protected enum Event {
        BACK, FORWARD, RELOAD, LOAD_URL
    }

    protected FloatingWeb(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected WebView initWebView(@Nullable String userAgent, Boolean wideViewport) {
        webView = new WebView(activity.getApplicationContext());
        WebSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
//        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);

        if (userAgent != null && !userAgent.isEmpty()) {
            settings.setUserAgentString(String.format("%s %s", settings.getUserAgentString(), userAgent));
        }

        if (wideViewport) {
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
        }

        return webView;
    }

    protected void handleWebView(Event event, String eventData) {
        if (webView == null) return;

        this.activity.runOnUiThread(() -> {
            switch (event) {
                case BACK:
                    webView.goBack();
                case FORWARD:
                    webView.goForward();
                case RELOAD:
                    webView.reload();
                case LOAD_URL:
                    webView.loadUrl(eventData);
                default:
            }
        });
    }

    protected void handleWebView(Event event) {
        handleWebView(event, null);
    }
}
