package com.zslide.utils;

import com.zslide.data.model.User;
import com.zslide.data.remote.converter.LocalDateTimeConverter;
import com.zslide.data.remote.converter.UserDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import org.threeten.bp.LocalDateTime;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GsonUtil {

    public static Gson customGson() {
        // TODO: 좀 더 알아보고 깔끔하게 써야 함
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    public static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String src = json.getAsJsonPrimitive().getAsString();
            int position = src.lastIndexOf(".");
            if (position != -1) {
                src = src.substring(0, position);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.KOREA);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                return format.parse(src);
            } catch (Exception e) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    return dateFormat.parse(src);
                } catch (Exception e1) {
                    //do nothing
                }
            }
            return null;
        }
    }
}
