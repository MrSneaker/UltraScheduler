package com.mrsneaker.ultrascheduler.model.event;

import java.util.Calendar;

public class Event {
    private final String subject;
    private final Calendar startTime;
    private final Calendar endTime;

    public Event(String subject, Calendar startTime, Calendar endTime) {
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
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
}
