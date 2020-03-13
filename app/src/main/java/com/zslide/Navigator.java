package com.zslide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.zslide.activities.ATEventActivity;
import com.zslide.activities.AccountLinkActivity;
import com.zslide.activities.AdPartnerHelpActivity;
import com.zslide.activities.AddressCompleteActivity;
import com.zslide.activities.AppSettingsActivity;
import com.zslide.activities.EventsActivity;
import com.zslide.activities.FamilyActivity;
import com.zslide.activities.FamilyMoveActivity;
import com.zslide.activities.FamilyRegistrationActivity;
import com.zslide.activities.FaqActivity;
import com.zslide.activities.ImageGuideActivity;
import com.zslide.activities.InviteActivity;
import com.zslide.activities.LeaveActivity;
import com.zslide.activities.LevelBenefitActivity;
import com.zslide.activities.LevelBenefitLogsActivity;
import com.zslide.activities.MainActivity;
import com.zslide.activities.ModifyBlockedMemberActivity;
import com.zslide.activities.MyAccountActivity;
import com.zslide.activities.NoticeActivity;
import com.zslide.activities.NoticesActivity;
import com.zslide.activities.OCBActivity;
import com.zslide.activities.OCBIntroActivity;
import com.zslide.activities.ServiceCenterActivity;
import com.zslide.activities.SignupCompleteActivity;
import com.zslide.activities.SuggestionActivity;
import com.zslide.activities.UploadImageCreateActivity;
import com.zslide.activities.WebViewActivity;
import com.zslide.activities.ZmoneyActivity;
import com.zslide.activities.ZmoneyPaymentsActivity;
import com.zslide.activities.ZmoneyPaymentsHelpActivity;
import com.zslide.data.model.AuthType;
import com.zslide.models.Notice;
import com.zslide.models.Sex;
import com.zslide.network.ApiConstants;
import com.zslide.utils.ZLog;
import com.zslide.view.auth.AuthActivity;

import java.util.List;

/**
 * Created by chulwoo on 16. 4. 18..
 * <p>
 * TODO: 한 번만 사용하는 것도 여기에 추가시켜야하는지,
 */
public class Navigator {

    public static void startApplication(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));

        for (ApplicationInfo packageInfo : packages) {
            if (packageName.equals(packageInfo.packageName)) {
                context.startActivity(new Intent(pm.getLaunchIntentForPackage(packageInfo.packageName)));
                return;
            }
        }

        context.startActivity(intent);
    }

    public static void startZmoneyApplicationStore(Context context, Long index) {
        final PackageManager pm = context.getPackageManager();
        final String targetUri = ApiConstants.BASE_URL + "info/link/";
        final String packageName = "com.mobitle.zummoney";
        Intent zmoneyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("zmoney://app/main?main_page=" + index.toString()));

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageName.equals(packageInfo.packageName)) {
                Toast.makeText(context, "줌머니 앱으로 이동합니다.", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                context.startActivity(zmoneyIntent);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUri));
        context.startActivity(intent);
    }

    public static void startZmoneyApplicationMall(Context context, Long index) {
        final PackageManager pm = context.getPackageManager();
        final String targetUri = ApiConstants.BASE_URL + "info/link/";
        final String packageName = "com.mobitle.zummoney";
        Intent zmoneyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("zmoney://internal/mall?mall_id=" + index.toString()));

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageName.equals(packageInfo.packageName)) {
                context.startActivity(zmoneyIntent);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUri));
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    private static Intent createATEventActivityIntent(Context context, String type, String code) {
        Intent intent = new Intent(context, ATEventActivity.class);
        intent.putExtra(IntentConstants.EXTRA_TYPE, type);
        intent.putExtra(IntentConstants.EXTRA_RECRUITER_CODE, code);
        return intent;
    }

    public static void startATEventActivity(Context context, String type, String code) {
        context.startActivity(createATEventActivityIntent(context, type, code));
    }

    public static void startAccountLinkActivity(Context context) {
        startActivity(context, AccountLinkActivity.class);
    }

    public static void startAdPartner(Context context) {
        startActivity(context, AdPartnerHelpActivity.class);
    }

    public static void startEventsActivity(Context context) {
        startActivity(context, EventsActivity.class);
    }

    public static void startFaqActivity(Context context) {
        startActivity(context, FaqActivity.class);
    }

    private static Intent createUploadImageCreateActivityIntent(Context context, int type) {
        return new Intent(context, UploadImageCreateActivity.class)
                .putExtra(IntentConstants.EXTRA_TYPE, type);
    }

    public static void startUploadImageCreateActivity(Activity activity, int requestCode, int type) {
        activity.startActivityForResult(createUploadImageCreateActivityIntent(activity, type), requestCode);
    }

    public static void startUploadImageCreateActivity(Fragment fragment, int requestCode, int type) {
        fragment.startActivityForResult(
                createUploadImageCreateActivityIntent(
                        fragment.getActivity(), type), requestCode);
    }

    public static void startLevelBenefitActivity(Context context) {
        startActivity(context, LevelBenefitActivity.class);
    }

    public static void startLevelBenefitLogsActivity(Context context) {
        startActivity(context, LevelBenefitLogsActivity.class);
    }

    public static void startAllLevelBenefitActivity(Context context) {
        startWebViewActivity(context,
                ApiConstants.BASE_STATIC_IMAGE_URL + "/img_zumma_level_advantage.png");
    }


    /*

        private static Intent createLockscreenActivityIntent(Context context) {
            return new Intent(context, LockScreenActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        public static void startLockScreenActivity(Context context) {
            context.startActivity(createLockscreenActivityIntent(context));
        }

    */
    private static Intent createSignupActivityIntent(Context context, AuthType authType, String uid,
                                                     String name, String nickname, Sex sex, int year,
                                                     int month, int day, String accessToken, String refreshToken) {
        ZLog.e(Navigator.class, "INTENT uid: " + uid);
        Intent intent = new Intent(context, AuthActivity.class);
        intent.putExtra(IntentConstants.EXTRA_AUTH_TYPE, authType);
        intent.putExtra(IntentConstants.EXTRA_ID, uid);
        intent.putExtra(IntentConstants.EXTRA_NAME, name == null ? "" : name);
        intent.putExtra(IntentConstants.EXTRA_NICKNAME, nickname == null ? "" : nickname);
        intent.putExtra(IntentConstants.EXTRA_SEX, sex == null ? Sex.NONE : sex);
        intent.putExtra(IntentConstants.EXTRA_YEAR, year);
        intent.putExtra(IntentConstants.EXTRA_MONTH, month);
        intent.putExtra(IntentConstants.EXTRA_DAY, day);
        intent.putExtra(IntentConstants.EXTRA_ACCESS_TOKEN, accessToken);
        intent.putExtra(IntentConstants.EXTRA_REFRESH_TOKEN, refreshToken);
        return intent;
    }

    public static void startServiceCenterActivity(Context context) {
        startActivity(context, ServiceCenterActivity.class);
    }

    public static void startSignupActivity(Context context, AuthType authType) {
        startSignupActivity(context, authType, "", "", "", Sex.NONE, -1, -1, -1, "", "");
    }

    public static void startSignupActivity(Context context, AuthType authType,
                                           String accessToken, String refreshToken) {
        startSignupActivity(context, authType, "", "", "", Sex.NONE, -1, -1, -1, accessToken, refreshToken);
    }

    public static void startSignupActivity(Context context, AuthType authType, String uid, String name,
                                           String nickname, Sex sex, int year, int month, int day,
                                           String accessToken, String refreshToken) {
        context.startActivity(createSignupActivityIntent(context, authType, uid, name, nickname,
                sex, year, month, day, accessToken, refreshToken));
    }


    public static void startSuggestionActivity(Context context) {
        startActivity(context, SuggestionActivity.class);
    }

    public static void startTempApartmentCompleteActivity(Context context) {
        startActivity(context, AddressCompleteActivity.class);
    }

    private static Intent withoutBackstack(Intent intent) {
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    private static Intent createMainActivityIntent(Context context, int page) {
        return withoutBackstack(
                new Intent(context, MainActivity.class)
                        .putExtra(IntentConstants.EXTRA_PAGE, page));
    }


    public static void startMainActivity(Context context, int page) {
        context.startActivity(createMainActivityIntent(context, page));
    }

    public static void startNoticesActivity(Context context) {
        startActivity(context, NoticesActivity.class);
    }

    public static void startOCBIntroActivity(Context context) {
        startActivity(context, OCBIntroActivity.class);
    }

    private static Intent createOCBActivityIntent(Context context, String type) {
        return new Intent(context, OCBActivity.class)
                .putExtra(IntentConstants.EXTRA_TYPE, type);
    }

    public static void startOCBActivity(Context context) {
        context.startActivity(createOCBActivityIntent(context, OCBActivity.TYPE_OCB));
    }

    public static void startOCBPlusActivity(Context context) {
        context.startActivity(createOCBActivityIntent(context, OCBActivity.TYPE_OCP));
    }

    public static void startFamilyActivity(Context context) {
        startActivity(context, FamilyActivity.class);
    }

    public static void startAppSettingsActivity(Context context) {
        startActivity(context, AppSettingsActivity.class);
    }

    public static void startFamilyMoveActivity(Context context) {
        startActivity(context, FamilyMoveActivity.class);
    }

    public static void startFamilyRegistrationActivity(Context context) {
        startActivity(context, FamilyRegistrationActivity.class);
    }

    public static void startInviteActivity(Context context, String type) {
        context.startActivity(
                new Intent(context, InviteActivity.class)
                        .putExtra(IntentConstants.EXTRA_TYPE, type));
    }

    private static Intent createWebViewActivityIntent(Context context, String title, String url) {
        return new Intent(context, WebViewActivity.class)
                .putExtra(IntentConstants.EXTRA_TITLE, title)
                .putExtra(IntentConstants.EXTRA_URL, url);
    }

    public static void startWebViewActivity(Context context, String url) {
        startWebViewActivity(context, "", url);
    }

    public static void startWebViewActivity(Context context, String title, String url) {
        context.startActivity(createWebViewActivityIntent(context, title, url));
    }

    public static void startPersonalHelp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://zummaslide.com/link/qna"));
        context.startActivity(intent);
    }

    public static void startZummaStoreHelpActivity(Context context) {
        startWebViewActivity(context,
                ApiConstants.BASE_STATIC_IMAGE_URL + "/img_howto_zummastore.png");
    }

    public static void startZummaShoppingHelpActivity(Context context) {
        startWebViewActivity(context,
                ApiConstants.BASE_STATIC_IMAGE_URL + "/img_howto_mall_coupon.png");
    }

    public static void startHouseHelpActivity(Context context) {
        startWebViewActivity(context, ApiConstants.BASE_URL + "/manual/house/");
    }

    public static void startZmoneyActivity(Context context) {
        startZmoneyActivity(context, ZmoneyActivity.PAGE_TODAY);
    }

    public static void startZmoneyActivity(Context context, int page) {
        Intent intent = new Intent(context, ZmoneyActivity.class);
        intent.putExtra(IntentConstants.EXTRA_PAGE, page);
        context.startActivity(intent);
    }

    public static void startZmoneyPaymentsActivity(Context context) {
        startActivity(context, ZmoneyPaymentsActivity.class);
    }

    public static void startBrowser(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @SuppressLint("MissingPermission")
    public static void startCall(Context context, String telNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + telNumber));
        context.startActivity(intent);
    }

    public static void startLocationSourceSettingsActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startMap(Context context, double lat, double lng, String label) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")"));
        try {
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        } catch (Exception e) {
            Intent.createChooser(intent, context.getString(R.string.label_map));
        }
    }

    public static void startMyAccountActivity(Context context) {
        startActivity(context, MyAccountActivity.class);
    }

    public static void startRecommendStoreActivity(Context context) {
        startWebViewActivity(context, ApiConstants.BASE_URL + "/ads/recommend/store/");
    }

    public static void startModifyBlockedMemberActivity(Context context) {
        startActivity(context, ModifyBlockedMemberActivity.class);
    }

    public static void startLeaveActivity(Context context) {
        startActivity(context, LeaveActivity.class);
    }

    public static void startAuthActivity(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        ComponentName componentName = intent.getComponent();
        Intent loginIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(loginIntent);
    }

    public static void startImageGuideActivity(Context context, String title, String target) {
        Intent intent = new Intent(context, ImageGuideActivity.class);
        intent.putExtra(IntentConstants.EXTRA_TITLE, title);
        intent.putExtra(IntentConstants.EXTRA_TARGET, target);
        context.startActivity(intent);
    }

    public static void startZmoneyPaymentsHelpActivity(Context context, int page) {
        Intent intent = new Intent(context, ZmoneyPaymentsHelpActivity.class);
        intent.putExtra(IntentConstants.EXTRA_PAGE, page);
        context.startActivity(intent);
    }

    public static void startZmoneyPaymentsHelpActivity(Context context) {
        Navigator.startZmoneyPaymentsHelpActivity(context, ZmoneyPaymentsHelpActivity.PAGE_SAVING);
    }

    public static void startSignupCompleteActivity(Context context) {
        startActivity(context, SignupCompleteActivity.class);
    }

    public static void startNoticeActivity(Context context, Notice notice) {
        Intent intent = new Intent(context, NoticeActivity.class);
        intent.putExtra(IntentConstants.EXTRA_ID, notice.getId());
        context.startActivity(intent);
    }

    public static void startZmoneyApplication(Context context) {
        // check do not ask again
        startApplication(context, "com.mobitle.zummoney");
    }


    public static void startWebViewActivity(Context context, String title, String url, Boolean canGoBack) {
        context.startActivity(createWebViewActivityIntent(context, title, url, canGoBack).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    private static Intent createWebViewActivityIntent(Context context, String title, String url, Boolean canGoBack) {
        return new Intent(context, WebViewActivity.class)
                .putExtra(IntentConstants.EXTRA_TITLE, title)
                .putExtra(IntentConstants.EXTRA_URL, url)
                .putExtra(IntentConstants.EXTRA_CAN_GO_BACK, canGoBack);
    }

    private static Intent createWebViewActivityIntent(Context context, String title, String url, Boolean canGoBack, Boolean noToolBar) {
        return new Intent(context, WebViewActivity.class)
                .putExtra(IntentConstants.EXTRA_TITLE, title)
                .putExtra(IntentConstants.EXTRA_URL, url)
                .putExtra(IntentConstants.EXTRA_CAN_GO_BACK, canGoBack)
                .putExtra(IntentConstants.EXTRA_NO_TOOLBAR, noToolBar);
    }

    public static void startLockerServiceActivity(Context context) {
        startWebViewActivity(context, "줌마택배",ApiConstants.BASE_URL + "/api/v1/deliverybox/ublogin/", true);
    }
}
