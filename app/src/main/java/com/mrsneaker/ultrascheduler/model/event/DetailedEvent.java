package com.mrsneaker.ultrascheduler.model.event;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.mrsneaker.ultrascheduler.utils.CalendarConverter;
import com.mrsneaker.ultrascheduler.utils.CalendarListConverter;

import java.util.Calendar;

@Entity(tableName = "detailed_event")
@TypeConverters({CalendarConverter.class, CalendarListConverter.class})
public class DetailedEvent extends GenericEvent {

    @PrimaryKey
    private long id;

    public DetailedEvent(String subject, Calendar startTime, Calendar endTime, String description, boolean isAllDay) {
        super(subject, startTime, endTime, description, isAllDay);
        this.id = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
