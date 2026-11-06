package com.example.uidesignlab;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {UserRecord.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Access to UserDao
    public abstract UserDao userDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;

    // Background thread executor
    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    // Get database instance
    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "uidesignlab.db"
                    ).build();

                }
            }
        }

        return INSTANCE;
    }
}