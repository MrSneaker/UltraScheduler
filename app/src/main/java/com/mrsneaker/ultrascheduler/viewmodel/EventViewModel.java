package com.mrsneaker.ultrascheduler.viewmodel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.mrsneaker.ultrascheduler.MainActivity;
import com.mrsneaker.ultrascheduler.database.AppDatabase;
import com.mrsneaker.ultrascheduler.model.dao.DetailedEventDao;
import com.mrsneaker.ultrascheduler.model.dao.TaskEventDao;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class EventViewModel extends ViewModel {
    private final DetailedEventDao detDao;
    private final TaskEventDao taskDao;
    private final MutableLiveData<List<GenericEvent>> allEventList;
    private final MutableLiveData<GenericEvent> currentEvent;
    private static EventViewModel evm;
    private static final int BASE_NOTIFICATION = -30;

    private EventViewModel() {
        this.allEventList = new MutableLiveData<>();
        this.currentEvent = new MutableLiveData<>();
        AppDatabase db = MainActivity.getDatabase();
        detDao = db.detailedEventDao();
        taskDao = db.taskEventDao();
        loadAllEvents();
    }

    public static EventViewModel getInstance() {
        if(evm == null) {
            evm = new EventViewModel();
        }
        return evm;
    }

    public LiveData<List<GenericEvent>> getAllEventList() {
        return allEventList;
    }

    public LiveData<GenericEvent> getEventById(long id) {

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            loadDetailedEventById(id);
            if (currentEvent.getValue() == null) {
                loadTaskEventById(id);
            }
            latch.countDown();
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return currentEvent;
    }

    public void insertDetailedEvent(DetailedEvent e, Context context) {
        new Thread(() -> {
            Calendar baseNotif = (Calendar) e.getStartTime().clone();
            baseNotif.add(Calendar.MINUTE,  BASE_NOTIFICATION);
            e.addNotification(baseNotif);
            initNotificationsSendEvent(context, e);
            detDao.insert(e);
            loadAllEvents();
        }).start();
    }

    public void insertTaskEvent(TaskEvent e, Context context)  {
        new Thread(() -> {
            Calendar baseNotif = (Calendar) e.getStartTime().clone();
            baseNotif.add(Calendar.MINUTE,  BASE_NOTIFICATION);
            e.addNotification(baseNotif);
            initNotificationsSendEvent(context, e);
            taskDao.insert(e);
            loadAllEvents();
        }).start();
    }

    public void updateEvent(GenericEvent e, Context context) {
        new Thread(() -> {
            if(e instanceof DetailedEvent) {
                detDao.update((DetailedEvent) e);
            } else {
                taskDao.update((TaskEvent) e);
            }
            updateAllNotifications(context, e);
            loadAllEvents();
        }).start();
    }

    public void deleteEvent(GenericEvent e, Context context) {
        new Thread(() -> {
            if(e instanceof DetailedEvent) {
                detDao.delete((DetailedEvent) e);
            } else  {
                taskDao.delete((TaskEvent) e);
            }
            cancelAllNotifications(context, e);
            loadAllEvents();
        }).start();
    }

    public void addNotification(long id, Calendar notif, Context context) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(() -> {
            LiveData<GenericEvent> liveEvent = getEventById(id);
            Observer<GenericEvent> observer = new Observer<GenericEvent>() {
                @Override
                public void onChanged(GenericEvent event) {
                    if (event != null) {
                        new Thread(() -> {
                            event.addNotification(notif);
                            initNotificationSendEvent(context, event, notif);
                            updateEvent(event, context);
                            loadAllEvents();
                        }).start();
                        liveEvent.removeObserver(this);
                    } else {
                        Log.e("EventViewModel", "Event not found with id: " + id);
                    }
                }
            };
            liveEvent.observeForever(observer);
        });
    }

    private void loadDetailedEventById(long id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            GenericEvent res = detDao.getDetailedEventById(id);
            currentEvent.postValue(res);
        });
    }

    private void loadTaskEventById(long id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            GenericEvent res = taskDao.getTaskEventById(id);
            currentEvent.postValue(res);
        });
    }

    private void initNotificationsSendEvent(Context context, GenericEvent e) {
        String subject = e.getSubject();
        String description = e.getDescription();
        Log.d("EventViewModel", "1 subject = " + subject + " and desc is : " + description);
        Calendar startTime = e.getStartTime();
        e.addNotification(startTime);
        Map<Long, Calendar> notifications = e.getNotificationDate();

        for (Map.Entry<Long, Calendar> notifEntry : notifications.entrySet()) {
            Calendar notification = notifEntry.getValue();
            NotificationHelper.scheduleNotification(context, notification.getTimeInMillis(), subject, description, notifEntry.getKey());
        }
    }

    private void initNotificationSendEvent(Context context, GenericEvent e, Calendar notif) {
        String subject = e.getSubject();
        String description = e.getDescription();
        Log.d("EventViewModel", "2 subject = " + subject + " and desc is : " + description);
        NotificationHelper.scheduleNotification(context, notif.getTimeInMillis(), subject, description, notif.getTimeInMillis());
    }

    private void cancelAllNotifications(Context context, GenericEvent e) {
        Map<Long, Calendar> notifications = e.getNotificationDate();

        for(Map.Entry<Long, Calendar> entry : notifications.entrySet()) {
            NotificationHelper.cancelNotification(context, entry.getKey());
        }
    }

    private void updateAllNotifications(Context context, GenericEvent e) {
        Map<Long, Calendar> notifications = e.getNotificationDate();

        for(Map.Entry<Long, Calendar> entry : notifications.entrySet()) {
            NotificationHelper.updateNotification(context, entry.getValue().getTimeInMillis(), e.getSubject(), e.getSubject(), entry.getKey());
        }
    }


    public void loadAllEvents() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<GenericEvent> res = new ArrayList<>();
            res.addAll(detDao.getAll());
            res.addAll(taskDao.getAll());

            List<GenericEvent> filteredEvents = new ArrayList<>();
            for(GenericEvent e : res) {
                if(DateUtils.isInCurrentWeek(e.getStartTime())) {
                    filteredEvents.add(e);
                }
            }

            Log.d("EventViewModel", "filteredEvents size:" + filteredEvents.size());
            allEventList.postValue(filteredEvents);
        });
    }

}
