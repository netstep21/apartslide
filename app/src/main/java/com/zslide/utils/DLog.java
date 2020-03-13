package com.zslide.utils;

import android.text.TextUtils;
import android.util.Log;

import com.zslide.Config;
import com.zslide.network.RetrofitException;
import com.crashlytics.android.Crashlytics;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.LinkedList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by chulwoo on 16. 3. 24..
 */
public class DLog implements HttpLoggingInterceptor.Logger {

    public static final int LOG_MAX_LENGTH = 4000;
    public static final String DEFAULT_DELIMITER = " | ";

    public static void e(Object obj, Throwable e, Object... message) {
        DLog.e(obj.getClass(), e, message);
    }

    public static void e(Class cls, Throwable e, Object... message) {
        DLog.e(DEFAULT_DELIMITER, cls, e, message);
    }

    public static void e(String delimiter, Object obj, Throwable e, Object... message) {
        DLog.e(delimiter, obj.getClass(), e, message);
    }

    public static void e(String delimiter, Class cls, Throwable e, Object... message) {
        DLog.e(e);
        DLog.e(delimiter, cls, message);
    }

    public static void e(Throwable e) {
        if (Config.DEBUG) {
            e.printStackTrace();
        }
        if (e instanceof RetrofitException) {
            RetrofitException retrofitException = (RetrofitException) e;
            if (retrofitException.getKind().equals(RetrofitException.Kind.NETWORK) ||
                    retrofitException.getKind().equals(RetrofitException.Kind.HTTP)) {
                return;
            }
        }
        Crashlytics.logException(e);
    }

    public static void e(Object obj, Object... message) {
        DLog.e(DEFAULT_DELIMITER, obj, message);
    }

    public static void e(Class cls, Object... message) {
        DLog.e(DEFAULT_DELIMITER, cls, message);
    }

    public static void e(String delimiter, Object obj, Object... message) {
        DLog.e(delimiter, obj.getClass(), message);
    }

    public static void e(String delimiter, Class cls, Object... message) {
        if (Config.DEBUG) {
            for (String msg : convert(delimiter, message)) {
                Log.e(cls.getSimpleName(), msg);
            }
        } else {
            Crashlytics.log(Log.ERROR, cls.getSimpleName(), TextUtils.join(delimiter, message));
        }
    }

    public static void d(Object obj, Object... message) {
        DLog.d(DEFAULT_DELIMITER, obj, message);
    }

    public static void d(Class cls, Object... message) {
        DLog.d(DEFAULT_DELIMITER, cls, message);
    }

    public static void d(String delimiter, Object obj, Object... message) {
        DLog.d(delimiter, obj.getClass(), message);
    }

    public static void d(String delimiter, Class cls, Object... message) {
        if (Config.DEBUG) {
            for (String msg : convert(delimiter, message)) {
                Log.d(cls.getSimpleName(), msg);
            }
        }
    }

    public static void i(Object obj, Object... message) {
        DLog.i(DEFAULT_DELIMITER, obj, message);
    }

    public static void i(Class cls, Object... message) {
        DLog.i(DEFAULT_DELIMITER, cls, message);
    }

    public static void i(String delimiter, Object obj, Object... message) {
        DLog.i(delimiter, obj.getClass(), message);
    }

    public static void i(String delimiter, Class cls, Object... message) {
        if (Config.DEBUG) {
            for (String msg : convert(delimiter, message)) {
                Log.i(cls.getSimpleName(), msg);
            }
        }
    }

    public static void w(Object obj, Object... message) {
        DLog.w(DEFAULT_DELIMITER, obj, message);
    }

    public static void w(Class cls, Object... message) {
        DLog.w(DEFAULT_DELIMITER, cls, message);
    }

    public static void w(String delimiter, Object obj, Object... message) {
        DLog.w(delimiter, obj.getClass(), message);
    }

    public static void w(String delimiter, Class cls, Object... message) {
        if (Config.DEBUG) {
            for (String msg : convert(delimiter, message)) {
                Log.w(cls.getSimpleName(), msg);
            }
        }
    }

    public static void v(Object obj, Object... message) {
        DLog.v(DEFAULT_DELIMITER, obj, message);
    }

    public static void v(Class cls, Object... message) {
        DLog.v(DEFAULT_DELIMITER, cls, message);
    }

    public static void v(String delimiter, Object obj, Object... message) {
        DLog.v(delimiter, obj.getClass(), message);
    }

    public static void v(String delimiter, Class cls, Object... message) {
        if (Config.DEBUG) {
            for (String msg : convert(delimiter, message)) {
                Log.v(cls.getSimpleName(), msg);
            }
        }
    }

    protected static List<String> convert(CharSequence delimiter, Object... message) {
        List<String> logs = new LinkedList<>();

        String prettyMessage = convertPretty(delimiter, message);

        for (int i = 0, len = prettyMessage.length(); i < len; i += LOG_MAX_LENGTH) {
            int end = Math.min(len, i + LOG_MAX_LENGTH);
            logs.add(prettyMessage.substring(i, end));
        }

        return logs;
    }

    protected static String convertPretty(CharSequence delimiter, Object... message) {
        List<String> prettyStringList = new LinkedList<>();

        for (Object msg : message) {
            if (msg == null) {
                prettyStringList.add(convertPretty("null"));
            } else {
                prettyStringList.add(convertPretty(msg.toString()));
            }
        }

        return TextUtils.join(delimiter, prettyStringList);
    }

    private static String convertPretty(String src) {
        JsonElement json = convertJson(src);
        if (json != null) {
            return prettyJson(json);
        }

        Element djangoSummary = convertDjangoSummaryHtml(src);
        if (djangoSummary != null) {
            return prettyDjanogSummaryHtml(djangoSummary);
        }

        return src;
    }

    private static JsonElement convertJson(String src) {
        try {
            JsonElement result = new JsonParser().parse(src);
            if (result.isJsonNull()) {
                return null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private static Element convertDjangoSummaryHtml(String src) {
        return Jsoup.parse(src).getElementById("summary");
    }

    private static String prettyJson(JsonElement src) {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create()
                .toJson(src);
    }

    private static String prettyDjanogSummaryHtml(Element summaryHtml) {
        StringBuilder builder = new StringBuilder();

        Element errorTitle = summaryHtml.select("h1").first();
        builder.append(String.format("Request Error: %s\n",
                errorTitle == null ? "" : errorTitle.text()));

        Element errorInfo = summaryHtml.select("pre.exception_value").first();
        builder.append(String.format("Error Info: %s\n",
                errorInfo == null ? "" : errorInfo.text()));

        for (Element element : summaryHtml.select("table.meta tr")) {
            Element detailTitle = element.select("th").first();
            Element detailContent = element.select("td").first();
            builder.append(String.format("%s %s\n",
                    detailTitle == null ? "" : detailTitle.text(),
                    detailContent == null ? "" : detailContent.text()));
        }

        return builder.toString();
    }

    @Override
    public void log(String message) {
        DLog.d(OkHttpClient.class, convertPretty(message));
    }
}