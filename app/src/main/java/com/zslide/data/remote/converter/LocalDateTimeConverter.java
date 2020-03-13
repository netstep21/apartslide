package com.zslide.data.remote.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class LocalDateTimeConverter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String localDateTimeString = json.getAsJsonPrimitive().getAsString();
        try {
            return LocalDateTime.ofInstant(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(localDateTimeString)), ZoneId.systemDefault());
        } catch (Exception e1) {
            try {
                return LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e2) {
                try {
                    return LocalDate.parse(localDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                } catch (Exception e3) {
                    // pass
                }
            }
        }

        return null;
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext
            context) {
        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // "yyyy-mm-dd"
    }
}
