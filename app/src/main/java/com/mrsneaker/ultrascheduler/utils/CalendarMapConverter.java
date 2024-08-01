package com.mrsneaker.ultrascheduler.utils;

import android.util.Log;

import androidx.room.TypeConverter;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CalendarMapConverter {

    @TypeConverter
    public static String fromCalendarMap(Map<Long, Calendar> calendarMap) {
        if (calendarMap == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Long, Calendar>>() {}.getType();
        return gson.toJson(calendarMap, type);
    }

    @TypeConverter
    public static Map<Long, Calendar> toCalendarMap(String calendarMapString) {
        if (calendarMapString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Long, Calendar>>() {}.getType();
        Log.d("CalendarMapConverter", "String JSON = " + calendarMapString);
        return gson.fromJson(calendarMapString, type);
    }
}
