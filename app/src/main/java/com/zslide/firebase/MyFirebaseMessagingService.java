package com.zslide.firebase;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.zslide.R;
import com.zslide.activities.BaseActivity;
import com.zslide.activities.DeepLinkActivity;
import com.zslide.activities.MainActivity;
import com.zslide.utils.ZLog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final int NOTIFICATION_REQUEST_CODE = 8001;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean canUsePlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                if (context instanceof BaseActivity) {
                    Observable.just(context)
                            .filter(c -> c instanceof BaseActivity)
                            .map(c -> (BaseActivity) c)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(activity -> apiAvailability
                                    .getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                                    .show(), ZLog::e);
                }
            } else {
                ZLog.i(MyFirebaseInstanceIDHandler.class, "지원하지 않는 디바이스입니다.");
                Observable.just(context)
                        .filter(c -> c instanceof BaseActivity)
                        .map(c -> (BaseActivity) c)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Activity::finish, ZLog::e);
            }

            return false;
        }

        return true;
    }

    /**
     * Notification 커스터마이징을 위해 오버라이딩
     * <p>
     * 참고:
     * http://stackoverflow.com/questions/37325051/notification-icon-with-the-new-firebase-cloud-messaging-system/37332514#37332514
     */
    /*
    @Override
    public void zzm(Intent intent) {
        ZLog.e(this, intent);
        ZLog.e(this, intent.getExtras());
    }
    */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationItem item = null;
        if (remoteMessage.getData().size() > 0) {
            ZLog.d(this, "Message data payload: " + remoteMessage.getData());
            item = new NotificationItem(remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            ZLog.d(this, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            item = new NotificationItem(remoteMessage.getNotification());
        }

        if (item != null) {
            sendNotification(item);
        }
    }

    private void sendNotification(NotificationItem notification) {
        Intent intent;

        if (notification.hasTarget()) {
            intent = new Intent(this, DeepLinkActivity.class);
            intent.setData(Uri.parse(notification.getTarget()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        notification.setIntent(intent);
        notify(notification);
    }

    public void notify(NotificationItem notification) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_REQUEST_CODE,
                notification.getIntent(), PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.base_channel_id))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.ic_noti_reward)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getMessage())
                .setContentIntent(pIntent);

        if (notification.hasTicker()) {
            notificationBuilder.setTicker(notification.getTicker());
        }

        if (notificationManager == null) {
            return;
        }

        if (notification.hasThumbnail()) {
            NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
            Observable.just(notification.getThumbnail())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(thumbnail ->
                            Glide.with(getApplicationContext())
                                    .load(thumbnail)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(512, 256) {
                                        @Override
                                        public void onResourceReady(final Bitmap resource, GlideAnimation glideAnimation) {
                                            Log.d(TAG, "Glide onResourceReady.. Bitmap resource");
                                            if (resource != null) {
                                                Log.d(TAG, "resource is not null..");
                                            }

                                            style.bigPicture(resource)
                                                    .setSummaryText(notification.getMessage())
                                                    .setBigContentTitle(notification.getTitle());
                                            //notificationBuilder.setContentText("두 손으로 아래로 당겨주세요. ∇");
                                            notificationBuilder.setLargeIcon(resource);
                                            notificationBuilder.setStyle(style);
                                            notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                                            notificationManager.notify(NOTIFICATION_REQUEST_CODE, notificationBuilder.build());
                                        }

                                        @Override
                                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                            Log.d(TAG, "onLoadFailed..");
                                        }
                                    }), ZLog::e);
        } else {
            notificationManager.notify(NOTIFICATION_REQUEST_CODE, notificationBuilder.build());
        }
    }

    private class NotificationItem {

        private String ticker;
        private String title;
        private String message;
        private String target;
        //private String icon;
        private String thumbnail;
        private Intent intent;

        public NotificationItem(Map<String, String> data) {
            ticker = data.get("ticker");
            title = data.get("title");
            message = data.get("message");
            target = data.get("target");
            thumbnail = data.get("thumbnail");
        }

        public NotificationItem(RemoteMessage.Notification notification) {
            ticker = notification.getTitle();
            title = notification.getTitle();
            message = notification.getBody();
            //icon = notification.getIcon();
        }

        public boolean hasTarget() {
            return !TextUtils.isEmpty(target);
        }

        public String getTarget() {
            return target;
        }

        /*
        public boolean hasIcon() {
            return !TextUtils.isEmpty(icon);
        }

        public String getIcon() {
            return icon;
        }
        */

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public String getTicker() {
            return ticker;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public Intent getIntent() {
            return intent;
        }

        public boolean hasTicker() {
            return !TextUtils.isEmpty(ticker);
        }

        public boolean hasThumbnail() {
            return !TextUtils.isEmpty(thumbnail);
        }
    }
}