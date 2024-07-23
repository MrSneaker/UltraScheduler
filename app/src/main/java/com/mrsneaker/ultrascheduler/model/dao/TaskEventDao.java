package com.mrsneaker.ultrascheduler.model.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Insert;

import com.mrsneaker.ultrascheduler.model.event.TaskEvent;

import java.util.List;

@Dao
public interface TaskEventDao {
    @Query("SELECT * FROM task_event")
    List<TaskEvent> getAll();

    @Insert
    void insert(TaskEvent event);

    @Delete
    void delete(TaskEvent event);
}
