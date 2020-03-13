package com.zslide.widget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zslide.utils.ZLog;

import java.net.URISyntaxException;

import rx.functions.Action0;

/**
 * Created by jdekim43 on 2016. 4. 21..
 */
public class CustomWebViewClient extends WebViewClient {

    private Activity activity;

    protected Action0 loadStartListener;
    protected Action0 loadFinishListener;

    public CustomWebViewClient(Activity activity, Action0 loadStartListener, Action0 loadFinishListener) {
        this.activity = activity;
        this.loadStartListener = loadStartListener;
        this.loadFinishListener = loadFinishListener;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (loadStartListener != null) {
            loadStartListener.call();
        }
        ZLog.i(this, "start loading : " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (loadFinishListener != null) {
            loadFinishListener.call();
        }
        ZLog.i(this, "end loading : " + url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        ZLog.e(this, "request : " + failingUrl +
                "\nerror code : " + errorCode +
                "\nerror description : " + description);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")) {
            activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
            return true;
        } else if (url.startsWith("mailto:")) {
            activity.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
            return true;
        } else if (url.startsWith("zummaslide:")) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return true;
        } else if (url.startsWith("market:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                Activity host = (Activity) view.getContext();
                host.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                // Google Play app is not installed, you may want to open the app store link
                Uri uri = Uri.parse(url);
                view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                return false;
            }
        } else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Uri uri = Uri.parse(intent.getDataString());

                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return true;
            } catch (URISyntaxException ex) {
                return false;
            } catch (ActivityNotFoundException e) {
                if (intent == null) return false;
                String packageName = intent.getPackage();
                if (packageName != null) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }

                return false;
            }
        }

        return false;
    }

        /* TODO: error handling
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
        */
}