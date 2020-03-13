package com.zslide.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jdekim43 on 2016. 3. 15..
 */
public class Notification extends ZummaApiData {

    @SerializedName("id") private int id;
    @SerializedName("image_url") private String thumbnailUrl;
    @SerializedName("message") private String title;
    @SerializedName("pub_date") private Date date;
    @SerializedName("noti_type") private NotificationType notificationType;
    @SerializedName("link") private String detail;
    @SerializedName("active") private boolean isAlreadyRead;

    public static List<Notification> mockList() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(Notification.mock());
        notifications.add(Notification.mock2());
        notifications.add(Notification.mock3());
        return notifications;
    }

    public static Notification mock() {
        Notification notification = new Notification();
        notification.id = 1;
        notification.thumbnailUrl = "http://dev.zummaslide.com/media/images/ads/logo/%EC%99%84%EC%84%B1%EB%B3%B8%EC%84%AC_RpDs4Vi.jpg";
        notification.title = "TEST1";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -15);
        notification.date = calendar.getTime();
        notification.notificationType = NotificationType.EVENT;
        notification.detail = "http://zummaslide.com/";
        notification.isAlreadyRead = true;
        return notification;
    }

    public static Notification mock2() {
        Notification notification = new Notification();
        notification.id = 2;
        notification.thumbnailUrl = "http://dev.zummaslide.com/media/images/ads/logo/%EC%99%84%EC%84%B1%EB%B3%B8%EC%84%AC_RpDs4Vi.jpg";
        notification.title = "TEST2";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -5);
        notification.date = calendar.getTime();
        notification.notificationType = NotificationType.EVENT;
        notification.detail = "http://zummaslide.com/";
        notification.isAlreadyRead = false;
        return notification;
    }

    public static Notification mock3() {
        Notification notification = new Notification();
        notification.id = 3;
        notification.thumbnailUrl = "http://dev.zummaslide.com/media/images/ads/logo/%EC%99%84%EC%84%B1%EB%B3%B8%EC%84%AC_RpDs4Vi.jpg";
        notification.title = "TEST3";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -5);
        calendar.add(Calendar.HOUR, -1);
        notification.date = calendar.getTime();
        notification.notificationType = NotificationType.EVENT;
        notification.detail = "http://zummaslide.com/";
        notification.isAlreadyRead = false;
        return notification;
    }

    public int getId() {
        return id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isAlreadyRead() {
        return isAlreadyRead;
    }

    public void setRead() {
        isAlreadyRead = true;
    }
}
