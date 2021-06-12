package com.shadhinlab.reminder.db;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shadhinlab.reminder.models.MData;
import com.shadhinlab.reminder.models.MDate;
import com.shadhinlab.reminder.models.MTime;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyConverters {
    private static Gson gson = new Gson();
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value == null) return null;
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        String d = sdf.format(date);
        return date == null ? null : d;
    }

    @TypeConverter
    public Uri fromString(String value) {
        if (value == null)
            return null;
        else return Uri.parse(value);
    }

    @TypeConverter
    public String toString(Uri uri) {
        return uri.toString();
    }


    @TypeConverter
    public static MDate stringToDateObject(String data) {

        Type objectType = new TypeToken<MDate>() {
        }.getType();

        return gson.fromJson(data, objectType);
    }

    @TypeConverter
    public static String dateToStringObject(MDate someObjects) {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static MTime stringToTimeObject(String data) {

        Type objectType = new TypeToken<MTime>() {
        }.getType();

        return gson.fromJson(data, objectType);
    }

    @TypeConverter
    public static String timeToStringObject(MTime someObjects) {
        return gson.toJson(someObjects);
    }

    @TypeConverter
    public static List<MData> stringToList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<MData>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ListToString(List<MData> someObjects) {
        return gson.toJson(someObjects);
    }

}
