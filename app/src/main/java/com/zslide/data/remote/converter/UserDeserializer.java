package com.zslide.data.remote.converter;

import com.zslide.data.model.User;
import com.zslide.utils.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.threeten.bp.LocalDateTime;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by chulwoo on 2018. 1. 9..
 */

public class UserDeserializer implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement jsonFamily = jsonObject.get("family");
            if (jsonFamily != null && jsonFamily.isJsonObject()) { // 구 User 모델
                long familyId;
                try {
                    familyId = jsonFamily.getAsJsonObject().get("id").getAsLong();
                } catch (Exception e) {
                    familyId = 0;
                }
                jsonObject.remove("family");
                jsonObject.addProperty("family", familyId);
            }

            // todo date deserializer 처리 다른 곳에서
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new GsonUtil.DateDeserializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                    .create();
            return gson.fromJson(jsonObject, User.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
