
package com.example.uidesignlab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundActivity extends AppCompatActivity {

    private static final String TAG = "BackgroundLab";

    // UI components
    private Button btnStartHeavy;
    private ProgressBar progressBar;
    private TextView txtStatus;

    // Background worker thread
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    // Handler to update the UI from the main thread
    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_background);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Connect XML components with Java
        btnStartHeavy = findViewById(R.id.btnStartHeavy);

        Button btnFreezeUi = findViewById(R.id.btnFreezeUi);

        Button btnStartService = findViewById(R.id.btnStartService);

        Button btnStopService = findViewById(R.id.btnStopService);

        Button btnStartLeaky = findViewById(R.id.btnStartLeaky);

        progressBar = findViewById(R.id.progressBar);

        txtStatus = findViewById(R.id.txtStatus);

        // Start Heavy Task button
        btnStartHeavy.setOnClickListener(v -> startHeavyTask());

        // Freeze UI button
        btnFreezeUi.setOnClickListener(v -> freezeTheUi());

        // Start Download Service button
        btnStartService.setOnClickListener(v -> startDownloadService());

        // Stop Download Service button
        btnStopService.setOnClickListener(v -> stopDownloadService());

        // Start Leaky Task button - now uses SafeTask
        btnStartLeaky.setOnClickListener(v -> startSafeTask());
    }

    // BAD EXAMPLE: Run slow work on the Main Thread
    private void freezeTheUi() {

        Log.d(TAG, "Freeze button thread: "
                + Thread.currentThread().getName());

        txtStatus.setText("Freezing UI...");

        try {
            Thread.sleep(8000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        txtStatus.setText("Done (the screen was frozen)");

        Log.d(TAG, "Freeze UI task completed");
    }

    // GOOD EXAMPLE: Run CPU-heavy work on a Background Thread
    // Lambda expressions + Prime Number Calculation
    private void startHeavyTask() {

        // Disable button while task is running
        btnStartHeavy.setEnabled(false);

        // Reset and show progress bar
        progressBar.setProgress(0);

        progressBar.setVisibility(View.VISIBLE);

        txtStatus.setText("Working...");

        // Execute task on background thread using Lambda
        executor.execute(() -> {

            Log.d(TAG, "Heavy task thread: "
                    + Thread.currentThread().getName());

            // Perform five steps
            for (int i = 1; i <= 5; i++) {

                // Calculate the sum of prime numbers below 400000
                long primeSum = 0;

                for (int n = 2; n < 400000; n++) {

                    boolean isPrime = true;

                    for (int d = 2; d * d <= n; d++) {

                        if (n % d == 0) {

                            isPrime = false;

                            break;
                        }
                    }

                    if (isPrime) {

                        primeSum += n;
                    }
                }

                // Log calculation result
                Log.d(TAG, "Step " + i
                        + " - Sum of prime numbers: " + primeSum);

                final int step = i;

                // Send UI update to Main Thread using Lambda
                mainHandler.post(() -> {

                    progressBar.setProgress(step);

                    txtStatus.setText(
                            "Working... " + step + "/5"
                    );
                });
            }

            // Update UI after task completion using Lambda
            mainHandler.post(() -> {

                txtStatus.setText(
                        "Task Completed Successfully!"
                );

                progressBar.setVisibility(View.GONE);

                btnStartHeavy.setEnabled(true);
            });
        });
    }

    // Start Download Service using an Explicit Intent
    private void startDownloadService() {

        Intent intent = new Intent(
                BackgroundActivity.this,
                DownloadService.class
        );

        startService(intent);

        Log.d(TAG, "Download Service start requested");
    }

    // Stop Download Service
    private void stopDownloadService() {

        Intent intent = new Intent(
                BackgroundActivity.this,
                DownloadService.class
        );

        boolean stopped = stopService(intent);

        if (stopped) {

            Log.d(TAG, "Stop Service request successful");

        } else {

            Log.d(TAG, "Download Service is not running");
        }
    }

    // LEAKY: An anonymous Runnable that uses BackgroundActivity.this
    private void startLeakyTask() {

        txtStatus.setText("Leaky task started");

        executor.execute(new Runnable() {

            @Override
            public void run() {

                for (int i = 1; i <= 120; i++) {

                    try {

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {

                        Log.d(TAG, "Leaky task was interrupted: stop");

                        return;
                    }

                    if (i % 10 == 0) {

                        // BackgroundActivity.this keeps the Activity reference
                        Log.d(TAG, "Leaky task alive, activity id = "
                                + System.identityHashCode(BackgroundActivity.this));
                    }
                }
            }
        });
    }

    // SAFE: Static class with WeakReference
    private static class SafeTask implements Runnable {

        private final WeakReference<BackgroundActivity> activityRef;

        SafeTask(BackgroundActivity activity) {

            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void run() {

            for (int i = 1; i <= 120; i++) {

                try {

                    Thread.sleep(1000);

                } catch (InterruptedException e) {

                    return;
                }

                BackgroundActivity activity = activityRef.get();

                if (activity == null || activity.isDestroyed()) {

                    Log.d(TAG, "Safe task: the screen is gone, stop work");

                    return;
                }

                if (i % 10 == 0) {

                    Log.d(TAG, "Safe task alive, activity id = "
                            + System.identityHashCode(activity));
                }
            }
        }
    }

    // Start Safe Task
    private void startSafeTask() {

        txtStatus.setText("Safe task started");

        executor.execute(new SafeTask(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop running and pending background tasks
        executor.shutdownNow();

        Log.d(TAG, "BackgroundActivity onDestroy");
    }
}
