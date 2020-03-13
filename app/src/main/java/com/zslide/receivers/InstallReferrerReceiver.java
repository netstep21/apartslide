package com.zslide.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zslide.activities.ATEventActivity;
import com.zslide.utils.EasySharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by chulwoo on 16. 8. 11..
 */
public class InstallReferrerReceiver extends BroadcastReceiver {

    private static final String CAMPAIGN_AT_EVENT_OLD = "at_event";
    private static final String CAMPAIGN_AT_EVENT = "atevent";

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
        try {
            Map<String, String> params = splitQuery(referrer);
            String campaign = params.get("utm_campaign");
            String source = params.get("utm_source");
            String medium = params.get("utm_medium");
            String content = params.get("utm_content");
            String term = params.get("utm_term");

            if (CAMPAIGN_AT_EVENT.equals(campaign) || CAMPAIGN_AT_EVENT_OLD.equals(campaign)) {
                setupRecruiterCode(context, content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecruiterCode(Context context, String content) {
        String[] parsedContent = content.split(",");
        String type = parsedContent[0];
        String code = parsedContent[1];
        EasySharedPreferences.with(context)
                .putString(ATEventActivity.KEY_AT_EVENT_TYPE, type);
        EasySharedPreferences.with(context)
                .putString(ATEventActivity.KEY_AT_EVENT_RECRUITER_CODE, code);
    }
}
