package com.visnalize.capacitor.plugins.floatingweb;

import android.os.Message;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.net.MalformedURLException;
import java.net.URL;

@CapacitorPlugin(name = "FloatingWeb")
public class FloatingWebPlugin extends Plugin {
    private WebView webView;
    private int width;
    private int height;
    private float x;
    private float y;
    private static final String EVENT_PAGELOAD = "pageLoad";
    private static final String EVENT_NAVIGATE = "navigate";
    private static final String EVENT_PROGRESS = "progress";

    private final FloatingWeb implementation = new FloatingWeb(this.getActivity());

    private float getPixels(int value) {
        return value * getContext().getResources().getDisplayMetrics().density + 0.5f;
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

    @PluginMethod
    public void open(PluginCall call) {
        String userAgent = call.getString("userAgent");
        Boolean wideViewport = call.getBoolean("wideViewport");
        webView = implementation.initWebView(userAgent, wideViewport);

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

        String urlString = call.getString("url", "");

        if (urlString != null && urlString.isEmpty()) {
            call.reject("Must provide a URL to open");
            return;
        }


        width = (int) getPixels(call.getInt("width", 1));
        height = (int) getPixels(call.getInt("height", 1));
        x = getPixels(call.getInt("x", 0));
        y = getPixels(call.getInt("y", 0));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        params.width = width;
        params.height = height;
        webView.setX(x);
        webView.setY(y);
        webView.requestLayout();

        ((ViewGroup) getBridge().getWebView().getParent()).addView(webView);

        webView.loadUrl(urlString);
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
            call.resolve();
        });
    }

    @PluginMethod
    public void updateDimensions(PluginCall call) {
        getActivity().runOnUiThread(() -> {
            width = (int) getPixels(call.getInt("width", 1));
            height = (int) getPixels(call.getInt("height", 1));
            x = getPixels(call.getInt("x", 0));
            y = getPixels(call.getInt("y", 0));

            ViewGroup.LayoutParams params = webView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            webView.setX(0);
            webView.setY(0);
            webView.requestLayout();
        });
    }

    @PluginMethod
    public void goBack() {
        implementation.handleWebView(FloatingWeb.Event.BACK);
    }

    @PluginMethod
    public void goForward() {
        implementation.handleWebView(FloatingWeb.Event.FORWARD);
    }

    @PluginMethod
    public void reload() {
        implementation.handleWebView(FloatingWeb.Event.RELOAD);
    }

    @PluginMethod
    public void loadUrl(PluginCall call) {
        String url = call.getString("url");
        if (url != null) {
            implementation.handleWebView(FloatingWeb.Event.LOAD_URL, url);
        }
    }
}
