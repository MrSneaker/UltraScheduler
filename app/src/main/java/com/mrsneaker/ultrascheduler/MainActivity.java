package com.mrsneaker.ultrascheduler;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.mrsneaker.ultrascheduler.database.AppDatabase;
import com.mrsneaker.ultrascheduler.model.dao.DetailedEventDao;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "ultra-scheduler-db")
                .fallbackToDestructiveMigration() // Optional: handle schema migrations
                .build();

//        DetailedEventDao detDao = db.detailedEventDao();
//        new Thread(() -> {
//            detDao.insert(new DetailedEvent("test", Calendar.getInstance(), Calendar.getInstance(), "lol", false));
//        }).start();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}