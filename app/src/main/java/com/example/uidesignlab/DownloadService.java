
package com.example.uidesignlab;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService extends Service {

    private static final String TAG = "BackgroundLab";

    // Handler to show Toast messages on the Main Thread
    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    // ExecutorService to manage download tasks
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    // Flag to control download tasks
    private volatile boolean running = false;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "DownloadService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "DownloadService onStartCommand, startId = " + startId);

        // Allow download tasks to run
        running = true;

        // Show Download started Toast on Main Thread
        mainHandler.post(() ->
                Toast.makeText(
                        getApplicationContext(),
                        "Download started",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Execute download task using ExecutorService
        executor.execute(() -> {

            Log.d(TAG, "Download task started on: "
                    + Thread.currentThread().getName());

            // Simulate download progress
            for (int i = 1; i <= 5 && running; i++) {

                try {

                    Thread.sleep(1000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    Log.d(TAG, "Download thread interrupted");
                    Log.d(TAG, "Download stopped");

                    return;
                }

                // Check whether the service is still running
                if (!running) {

                    Log.d(TAG, "Download stopped");

                    return;
                }

                int progress = i * 20;

                Log.d(TAG, "Download progress: " + progress + "%");
            }

            // Show finished message only if download completed
            if (running) {

                mainHandler.post(() ->
                        Toast.makeText(
                                getApplicationContext(),
                                "Download finished",
                                Toast.LENGTH_SHORT
                        ).show()
                );

                // Stop the service after completing this start request
                stopSelf(startId);
            }

        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        // Tell download tasks to stop
        running = false;

        // Stop running and queued tasks
        executor.shutdownNow();

        Log.d(TAG, "DownloadService onDestroy");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        // This is a started Service, so binding is not required
        return null;
    }
}
