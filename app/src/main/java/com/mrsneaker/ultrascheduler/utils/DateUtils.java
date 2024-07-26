package com.mrsneaker.ultrascheduler.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtils {
    private static Calendar selectedDate = Calendar.getInstance();
    // Array to map Calendar.DAY_OF_WEEK constants to day names
    private static final List<String> DAYS_OF_WEEK = Arrays.asList(
            "NULL", // Calendar.DAY_OF_WEEK starts at 1
            "Sun.",
            "Mon.",
            "Tue.",
            "Wed.",
            "Thu.",
            "Fri.",
            "Sat."
    );

    // Array to map Calendar.MONTH constants to month names
    private static final List<String> MONTHS_OF_YEAR = Arrays.asList(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    );

    public static String formattedDate(Calendar date) {
        DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        return formatter.format(date.getTime());
    }

    public static String formattedTime(Calendar time) {
        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        return formatter.format(time.getTime());
    }

    public static String monthYearFromDate(Calendar date) {
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy");
        return formatter.format(date.getTime());
    }

    public static ArrayList<Calendar> daysInMonthArray(Calendar date) {
        ArrayList<Calendar> daysInMonthArray = new ArrayList<>();
        Calendar yearMonth = (Calendar) date.clone();

        int daysInMonth = yearMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar firstOfMonth = (Calendar) selectedDate.clone();
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = firstOfMonth.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(null);
            } else {
                Calendar day = new GregorianCalendar(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), i - dayOfWeek);
                daysInMonthArray.add(day);
            }
        }
        return daysInMonthArray;
    }

    public static ArrayList<Calendar> daysInWeekArray(Calendar selectedDate) {
        ArrayList<Calendar> days = new ArrayList<>();
        Calendar current = mondayForDate(selectedDate);
        Log.d("DateUtils", current.toString());
        assert current != null;
        Calendar endDate = (Calendar) current.clone();
        endDate.add(Calendar.WEEK_OF_YEAR, 1);

        while (current.before(endDate)) {
            days.add((Calendar) current.clone());
            current.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

     public static boolean isInCurrentWeek(Calendar date) {
        Calendar startOfWeek = getStartOfWeek(selectedDate);
        Calendar endOfWeek = getEndOfWeek(selectedDate);

        return date.after(startOfWeek) && date.before(endOfWeek);
    }

    private static Calendar getStartOfWeek(Calendar date) {
        Calendar start = (Calendar) date.clone();
        start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start;
    }

    private static Calendar getEndOfWeek(Calendar date) {
        Calendar end = (Calendar) date.clone();
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek() + 6);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return end;
    }

    private static Calendar mondayForDate(Calendar current) {
        Calendar oneWeekAgo = (Calendar) current.clone();
        oneWeekAgo.add(Calendar.WEEK_OF_YEAR, -1);

        while (current.after(oneWeekAgo)) {
            if (current.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                return current;
            }
            current.add(Calendar.DAY_OF_MONTH, -1);
        }
        return null;
    }

    public static Calendar getNextWeek() {
        selectedDate.add(Calendar.WEEK_OF_YEAR, 1);
        return selectedDate;
    }

    public static Calendar getLastWeek() {
        selectedDate.add(Calendar.WEEK_OF_YEAR, -1);
        return selectedDate;
    }

    public static Calendar getSelectedDate() {
        return (Calendar) selectedDate.clone();
    }

    public static void setSelectedDate(Calendar selectedDate) {
        DateUtils.selectedDate = (Calendar) selectedDate.clone();
    }

    public static String getDayOfWeekName(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        return DAYS_OF_WEEK.get(dayOfWeek);
    }

    public static String getMonthName(Calendar date) {
        int month = date.get(Calendar.MONTH);
        return MONTHS_OF_YEAR.get(month);
    }
}