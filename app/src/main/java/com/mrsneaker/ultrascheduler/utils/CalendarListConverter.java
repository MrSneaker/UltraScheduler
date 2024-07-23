package com.mrsneaker.ultrascheduler.utils;

import androidx.room.TypeConverter;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class CalendarListConverter {

    @TypeConverter
    public static String fromCalendarList(List<Calendar> calendarList) {
        if (calendarList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Calendar>>() {}.getType();
        return gson.toJson(calendarList, type);
    }

    @TypeConverter
    public static List<Calendar> toCalendarList(String calendarListString) {
        if (calendarListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Calendar>>() {}.getType();
        return gson.fromJson(calendarListString, type);
    }
}
