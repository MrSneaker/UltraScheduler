package com.mrsneaker.ultrascheduler.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;

import java.util.List;

@Dao
public interface DetailedEventDao {
    @Query("SELECT * FROM detailed_event")
    List<DetailedEvent> getAll();

    @Insert
    void insert(DetailedEvent event);

    @Delete
    void delete(DetailedEvent event);

}
