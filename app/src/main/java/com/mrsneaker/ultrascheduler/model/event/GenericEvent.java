package com.mrsneaker.ultrascheduler.model.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GenericEvent {
    private String subject;
    private Calendar startTime;
    private Calendar endTime;
    private String description;
    private List<Calendar> notificationDate;
    private boolean isAllDay;

    public GenericEvent(String subject, Calendar startTime, Calendar endTime, String description, boolean isAllDay) {
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.isAllDay = isAllDay;
        notificationDate = new ArrayList<>();
        notificationDate.add(startTime);
    }

    public GenericEvent() {
        this.subject = "";
        this.startTime = Calendar.getInstance();
        this.endTime = Calendar.getInstance();
        this.description = "";
        this.isAllDay = false;
        notificationDate = new ArrayList<>();
        notificationDate.add(startTime);
    }

    public String getSubject() {
        return subject;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public List<Calendar> getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(List<Calendar> notificationDate) {
        this.notificationDate = notificationDate;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }
}
