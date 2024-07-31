package com.mrsneaker.ultrascheduler;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.mrsneaker.ultrascheduler.database.AppDatabase;
import com.mrsneaker.ultrascheduler.utils.NotificationHelper;
import com.mrsneaker.ultrascheduler.utils.PermissionHelper;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static AppDatabase database;
    private static Context appContext;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        permissionHelper = new PermissionHelper(this);
        permissionHelper.checkAndRequestPermissions(this);

        NotificationHelper.createNotificationChannel(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            Map<String, Integer> perms = new HashMap<>();

            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }
            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (perms.get(permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                Toast.makeText(this, "Some permissions were not allowed, please allow asked permissions.", Toast.LENGTH_SHORT).show();
                checkExactAlarmPermission();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkExactAlarmPermission();
    }

    public static AppDatabase getDatabase() {
        if (database == null) {
            synchronized (MainActivity.class) {
                if (database == null) {
                    database = Room.databaseBuilder(appContext,
                                    AppDatabase.class, "ultra-scheduler-db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return database;
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Missing permission: SCHEDULE_EXACT_ALARM");
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            } else {
                Log.d(TAG, "Permission granted: SCHEDULE_EXACT_ALARM");
            }
        }
    }
}