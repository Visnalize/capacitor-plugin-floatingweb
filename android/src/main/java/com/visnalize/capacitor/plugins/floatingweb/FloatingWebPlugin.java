package com.visnalize.capacitor.plugins.floatingweb;

import android.annotation.SuppressLint;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginConfig;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("unused")
@CapacitorPlugin(name = "FloatingWeb")
public class FloatingWebPlugin extends Plugin {
    private WebView webView;
    private PluginConfig config;
    private static final String EVENT_PAGELOAD = "pageLoad";
    private static final String EVENT_NAVIGATE = "navigate";
    private static final String EVENT_PROGRESS = "progress";
    private final String ERROR_MISSING_URL = "err/required-url";

    private enum Action {
        SHOW, HIDE, BACK, FORWARD, RELOAD, LOAD_URL
    }

    @Override
    public void load() {
        super.load();
        config = bridge.getConfig().getPluginConfiguration("FloatingWeb");
    }

    @SuppressLint("SetJavaScriptEnabled")
    @PluginMethod
    public void open(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            webView = new WebView(getContext());
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setSupportMultipleWindows(true);

            String urlString = call.getString("url", "");
            if (urlString == null || urlString.isEmpty()) {
                call.reject(ERROR_MISSING_URL);
                return;
            }

            String userAgent = call.getString("userAgent");
            if (userAgent == null) {
                userAgent = config.getString("userAgent");
            }
            if (userAgent != null && !userAgent.isEmpty()) {
                settings.setUserAgentString(userAgent);
            }

            Boolean wideViewport = call.getBoolean("wideViewport");
            if (wideViewport != null && wideViewport) {
                settings.setUseWideViewPort(true);
                settings.setLoadWithOverviewMode(true);
            }

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    JSObject progressValue = new JSObject();
                    progressValue.put("value", progress / 100.0);
                    notifyListeners(EVENT_PROGRESS, progressValue);
                }

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    final WebView targetWebView = new WebView(getActivity());
                    targetWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onLoadResource(WebView view, String url) {
                            if (hasListeners("navigate")) {
                                handleNavigation(url, true);
                                JSObject progressValue = new JSObject();
                                progressValue.put("value", 0.1);
                                notifyListeners(EVENT_PROGRESS, progressValue);
                            } else {
                                webView.loadUrl(url);
                            }
                            targetWebView.removeAllViews();
                            targetWebView.destroy();
                        }
                    });
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(targetWebView);
                    resultMsg.sendToTarget();
                    return true;
                }
            });

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    notifyListeners(EVENT_PAGELOAD, new JSObject());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (hasListeners(EVENT_NAVIGATE)) {
                        handleNavigation(url, false);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            changeDimensions(call);
            ((ViewGroup) getBridge().getWebView().getParent()).addView(webView);
            webView.loadUrl(urlString);
        });
    }

    @PluginMethod
    public void close(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            if (webView != null) {
                ViewGroup rootGroup = ((ViewGroup) getBridge().getWebView().getParent());
                int count = rootGroup.getChildCount();
                if (count > 1) {
                    rootGroup.removeView(webView);
                    webView.destroyDrawingCache();
                    webView.destroy();
                    webView = null;
                }
            }
        });
    }

    @PluginMethod
    public void updateDimensions(PluginCall call) {
        getActivity().runOnUiThread(() -> changeDimensions(call));
    }

    @PluginMethod
    public void goBack(PluginCall call) {
        handleAction(Action.BACK);
    }

    @PluginMethod
    public void goForward(PluginCall call) {
        handleAction(Action.FORWARD);
    }

    @PluginMethod
    public void reload(PluginCall call) {
        handleAction(Action.RELOAD);
    }

    @PluginMethod
    public void loadUrl(PluginCall call) {
        String url = call.getString("url");
        if (url == null)
            call.reject(ERROR_MISSING_URL);
        else
            handleAction(Action.LOAD_URL, url);
    }

    @PluginMethod
    public void show(PluginCall call) {
        handleAction(Action.SHOW);
    }

    @PluginMethod
    public void hide(PluginCall call) {
        handleAction(Action.HIDE);
    }

    private float getPixels(float value) {
        return value * getContext().getResources().getDisplayMetrics().density;
    }

    private void changeDimensions(PluginCall call) {
        Float scale = call.getFloat("scale");
        Float width = call.getFloat("width");
        Float height = call.getFloat("height");
        Float x = call.getFloat("x");
        Float y = call.getFloat("y");

        if (scale == null) {
            scale = (float) config.getInt("scale", 1);
        }

        if (width == null || height == null || x == null || y == null) {
            call.reject("err/invalid-dimensions");
            return;
        }

        int scaledW = (int) (getPixels(width) * scale);
        int scaledH = (int) (getPixels(height) * scale);
        float scaledX = getPixels(x) * scale;
        float scaledY = getPixels(y) * scale;

        ViewGroup.LayoutParams params = webView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(scaledW, scaledH);
            webView.setLayoutParams(params);
        } else {
            params.width = scaledW;
            params.height = scaledH;
        }
        webView.setX(scaledX);
        webView.setY(scaledY);
        webView.requestLayout();
    }

    private void handleNavigation(String url, Boolean newWindow) {
        try {
            URL currentUrl = new URL(webView.getUrl());
            URL targetUrl = new URL(url);
            boolean sameHost = currentUrl.getHost().equals(targetUrl.getHost());

            JSObject navigationHandlerValue = new JSObject();
            navigationHandlerValue.put("url", url);
            navigationHandlerValue.put("newWindow", newWindow);
            navigationHandlerValue.put("sameHost", sameHost);

            notifyListeners(EVENT_NAVIGATE, navigationHandlerValue);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void handleAction(Action action, String eventData) {
        if (webView == null) return;

        getActivity().runOnUiThread(() -> {
            switch (action) {
                case HIDE:
                    webView.setVisibility(View.INVISIBLE);
                case SHOW:
                    webView.setVisibility(View.VISIBLE);
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

    private void handleAction(Action action) {
        handleAction(action, null);
    }
}
