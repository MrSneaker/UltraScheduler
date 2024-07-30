package com.mrsneaker.ultrascheduler.model.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.mrsneaker.ultrascheduler.model.event.TaskEvent;

import java.util.List;

@Dao
public interface TaskEventDao {
    @Query("SELECT * FROM task_event")
    List<TaskEvent> getAll();

    @Query("SELECT * FROM task_event WHERE id= :id")
    TaskEvent getTaskEventById(long id);

    @Insert
    void insert(TaskEvent event);

    @Update
    void update(TaskEvent event);

    @Delete
    void delete(TaskEvent event);
}
