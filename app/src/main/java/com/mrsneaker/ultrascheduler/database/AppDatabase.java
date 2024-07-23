package com.mrsneaker.ultrascheduler.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mrsneaker.ultrascheduler.model.dao.DetailedEventDao;
import com.mrsneaker.ultrascheduler.model.dao.TaskEventDao;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;

@Database(entities = {DetailedEvent.class, TaskEvent.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DetailedEventDao detailedEventDao();
    public abstract TaskEventDao taskEventDao();
}
