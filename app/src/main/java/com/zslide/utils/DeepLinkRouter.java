package com.zslide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.zslide.IntentConstants;
import com.zslide.activities.ATEventActivity;
import com.zslide.activities.AccountLinkActivity;
import com.zslide.activities.EventsActivity;
import com.zslide.activities.FaqActivity;
import com.zslide.activities.InviteActivity;
import com.zslide.activities.NoticeActivity;
import com.zslide.activities.NoticesActivity;
import com.zslide.activities.OCBIntroActivity;
import com.zslide.activities.WebViewActivity;
import com.zslide.activities.ZmoneyActivity;
import com.zslide.activities.ZmoneyPaymentsActivity;
import com.zslide.activities.ZmoneyPaymentsHelpActivity;
import com.zslide.view.auth.AuthActivity;
import com.zslide.view.base.ActivityNavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by chulwoo on 16. 3. 30..
 * <p>
 * <p>
 * <p>
 * TODO: deprecated 함. {@link ActivityNavigator}에 흡수 할 것
 */

@Deprecated
public class DeepLinkRouter {

    private static final String[] HTTP_SCHEMES = new String[]{"http", "https"};
    private static final String APP_SCHEME = "zummaslide";
    private static final String HOST = "zummaslide.com";
    private static final String PREFIX_PATH = "/app/zslide";

    public static final String SIGNUP = "phoneAuth";
    public static final String MAIN = "main";
    public static final String INVITE = "invite";
    public static final String NOTICES = "notices";
    public static final String NOTICE = "notice";
    public static final String FAQ = "faq";
    public static final String EVENT = "event";
    public static final String ATEVENT = "atevent";
    public static final String SHINHAN = "shinhan";
    public static final String WEB = "web";
    public static final String ZMONEY = "zmoney";
    public static final String OCB = "ocb";
    public static final String ZMONEY_PAYMENTS = "zmoneypayments";
    public static final String ZMONEY_PAYMENTS_HELP = "zmoneypaymentshelp";
    public static final String LINK_ACCOUNT = "linkaccount";

    public static String internalBrowseUrl(String title, String path) {
        return "zummaslide://internal/web?web_title=" + title + "&web_url=" + path;
    }

    private static boolean isAppScheme(String scheme) {
        return APP_SCHEME.equals(scheme);
    }

    private static boolean isHttpScheme(String scheme) {
        for (String httpScheme : HTTP_SCHEMES) {
            if (httpScheme.equals(scheme)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAllowHost(String host) {
        return HOST.equals(host);
    }

    private static boolean isAllowPrefixPath(String path) {
        return path != null && path.startsWith(PREFIX_PATH);
    }

    /**
     * 전달받은 Deeplink를 분석해 액티비티를 실행한다.
     *
     * @param context
     * @param deepLink
     * @return 액티비티 실행에 성공했을 경우 true를 반환
     */
    public static boolean route(final Context context, final Uri deepLink, boolean isNewTask) {
        try {
            if (deepLink == null) {
                throw new IllegalArgumentException("Uri cannot be null");
            }

            DLog.d(context, "host: " + deepLink.getHost() + ", path: " + deepLink.getPath());

            if (isHttpScheme(deepLink.getScheme())) {
                if (!(isAllowHost(deepLink.getHost()) && isAllowPrefixPath(deepLink.getPath()))) {
                    Uri uri = Uri.parse(internalBrowseUrl("", deepLink.toString()));
                    route(context, uri, false);
                    return true;
                }
            } else {
                if (!isAppScheme(deepLink.getScheme())) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, deepLink));
                    return true;
                }
            }

            DLog.d(context, "parse deep link");

            String path = deepLink.getPath().replace(PREFIX_PATH, "");
            LinkedHashMap<String, HashMap<String, String>> data = new LinkedHashMap<>();
            for (String host : path.split("/")) {
                if (!TextUtils.isEmpty(host)) {
                    data.put(host, new HashMap<>());
                }
            }

            for (String key : deepLink.getQueryParameterNames()) {
                String[] parameterWithHost = key.split("_", 2);

                if (parameterWithHost.length == 2) {
                    String host = parameterWithHost[0];
                    String param = parameterWithHost[1];
                    HashMap<String, String> paramPair = data.get(host);
                    if (paramPair != null) {
                        paramPair.put(param, deepLink.getQueryParameter(key));
                    }
                }
            }

            if (isNewTask) {
                routeNewTask(context, data);
            } else {
                DLog.d(context, "deepLink: " + data);
                route(context, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean routeNewTask(Context context, HashMap<String, HashMap<String, String>> hostParams) {
        try {
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context.getApplicationContext());
            for (String host : hostParams.keySet()) {
                ZLog.i("host: " + host + ", params: " + hostParams.get(host).toString());
                Intent route = getRouteIntent(context, host, hostParams.get(host));

                if (route != null) {
                    taskStackBuilder.addNextIntent(route);
                }
            }

            if (taskStackBuilder.getIntentCount() == 0) {
                return false;
            }
            taskStackBuilder.startActivities();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean route(Context context, HashMap<String, HashMap<String, String>> hostParams) {
        try {
            List<Intent> intents = new ArrayList<>();
            for (String host : hostParams.keySet()) {
                Intent route = getRouteIntent(context, host, hostParams.get(host));

                if (route != null) {
                    intents.add(route);
                }
            }

            if (intents.size() == 0) {
                return false;
            }
            Intent[] intentArray = new Intent[intents.size()];
            intents.toArray(intentArray);
            context.startActivities(intentArray);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static Intent getRouteIntent(Context context, String host, HashMap<String, String> paramPair) {
        Intent route = null;

        if (SIGNUP.equals(host)) {
            route = new Intent(context, AuthActivity.class);
            route.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (MAIN.equals(host)) {
            route = new Intent(context, com.zslide.activities.MainActivity.class);
            route.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else if (INVITE.equals(host)) {
            route = new Intent(context, InviteActivity.class);
        } else if (NOTICES.equals(host)) {
            route = new Intent(context, NoticesActivity.class);
        } else if (NOTICE.equals(host)) {
            route = new Intent(context, NoticeActivity.class);
            if (paramPair.containsKey(IntentConstants.EXTRA_ID)) {
                route.putExtra(IntentConstants.EXTRA_ID, Long.parseLong(paramPair.get(IntentConstants.EXTRA_ID)));
            }
        } else if (FAQ.equals(host)) {
            route = new Intent(context, FaqActivity.class);
        } else if (EVENT.equals(host)) {
            route = new Intent(context, EventsActivity.class);
        } else if (ATEVENT.equals(host)) {
            route = new Intent(context, ATEventActivity.class);
            if (paramPair.containsKey(IntentConstants.EXTRA_TYPE)) {
                route.putExtra(IntentConstants.EXTRA_TYPE,
                        paramPair.get(IntentConstants.EXTRA_TYPE));
            }
            if (paramPair.containsKey(IntentConstants.EXTRA_RECRUITER_CODE)) {
                route.putExtra(IntentConstants.EXTRA_RECRUITER_CODE,
                        paramPair.get(IntentConstants.EXTRA_RECRUITER_CODE));
            }
        } else if (OCB.equals(host)) {
            route = new Intent(context, OCBIntroActivity.class);
        } else if (SHINHAN.equals(host)) {
            route = new Intent(context, ATEventActivity.class);
            route.putExtra(IntentConstants.EXTRA_TYPE, ATEventActivity.TYPE_SHINHAN);
            if (paramPair.containsKey(IntentConstants.EXTRA_RECRUITER_CODE)) {
                route.putExtra(IntentConstants.EXTRA_RECRUITER_CODE,
                        paramPair.get(IntentConstants.EXTRA_RECRUITER_CODE));
            }
        } else if (WEB.equals(host)) {
            route = new Intent(context, WebViewActivity.class);
            if (paramPair.containsKey(IntentConstants.EXTRA_TITLE)) {
                route.putExtra(IntentConstants.EXTRA_TITLE,
                        paramPair.get(IntentConstants.EXTRA_TITLE));
            }
            if (paramPair.containsKey(IntentConstants.EXTRA_URL)) {
                route.putExtra(IntentConstants.EXTRA_URL,
                        paramPair.get(IntentConstants.EXTRA_URL));
            }
        } else if (ZMONEY.equals(host)) {
            route = new Intent(context, ZmoneyActivity.class);
        } else if (ZMONEY_PAYMENTS.equals(host)) {
            route = new Intent(context, ZmoneyPaymentsActivity.class);
        } else if (ZMONEY_PAYMENTS_HELP.equals(host)) {
            route = new Intent(context, ZmoneyPaymentsHelpActivity.class);
            int page;
            try {
                page = Integer.parseInt(paramPair.get(IntentConstants.EXTRA_PAGE));
            } catch (Exception e) {
                page = ZmoneyPaymentsHelpActivity.PAGE_PAYMENTS;
            }
            route.putExtra(IntentConstants.EXTRA_PAGE, page);
        } else if (LINK_ACCOUNT.equals(host)) {
            route = new Intent(context, AccountLinkActivity.class);
        }

        return route;
    }
}
