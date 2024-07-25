package com.mrsneaker.ultrascheduler.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.mrsneaker.ultrascheduler.MainActivity;
import com.mrsneaker.ultrascheduler.database.AppDatabase;
import com.mrsneaker.ultrascheduler.model.dao.DetailedEventDao;
import com.mrsneaker.ultrascheduler.model.dao.TaskEventDao;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EventViewModel extends ViewModel {
    private DetailedEventDao detDao;
    private TaskEventDao taskDao;
    private final MutableLiveData<List<GenericEvent>> allEventList;
    private final MutableLiveData<GenericEvent> currentEvent;

    public EventViewModel() {
        this.allEventList = new MutableLiveData<>();
        this.currentEvent = new MutableLiveData<>();
        AppDatabase db = MainActivity.getDatabase();
        detDao = db.detailedEventDao();
        taskDao = db.taskEventDao();
        loadAllEvents();
    }

    public LiveData<List<GenericEvent>> getAllEventList() {
        return allEventList;
    }

    public LiveData<GenericEvent> getDetailedEventById(long id) {
        loadDetailedEventById(id);
        return currentEvent;
    }

    public void insertDetailedEvent(DetailedEvent e) {
        new Thread(() -> {
            detDao.insert(e);
            loadAllEvents();
        }).start();
    }

    public void insertTaskEvent(TaskEvent e)  {
        new Thread(() -> {
            taskDao.insert(e);
            loadAllEvents();
        }).start();
    }

    private void loadDetailedEventById(long id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            GenericEvent res = detDao.getDetailedEventById(id);
            currentEvent.postValue(res);
        });
    }

    private void loadAllEvents() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<GenericEvent> res = new ArrayList<>();
            res.addAll(detDao.getAll());
            res.addAll(taskDao.getAll());
            Log.d("EventViewModel", "res size:" + res.size());
            allEventList.postValue(res);
        });
    }

}
