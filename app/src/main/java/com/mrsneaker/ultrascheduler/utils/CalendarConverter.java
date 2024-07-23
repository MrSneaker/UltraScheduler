package com.mrsneaker.ultrascheduler.utils;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class CalendarConverter {

    @TypeConverter
    public static Long fromCalendar(Calendar calendar) {
        return calendar == null ? null : calendar.getTimeInMillis();
    }

    @TypeConverter
    public static Calendar toCalendar(Long millis) {
        if (millis == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
