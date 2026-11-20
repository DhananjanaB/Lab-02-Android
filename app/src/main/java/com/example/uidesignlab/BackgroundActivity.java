
package com.example.uidesignlab;

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

        progressBar = findViewById(R.id.progressBar);

        txtStatus = findViewById(R.id.txtStatus);

        // Start Heavy Task button
        btnStartHeavy.setOnClickListener(v -> startHeavyTask());

        // Freeze UI button
        btnFreezeUi.setOnClickListener(v -> freezeTheUi());
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

    // GOOD EXAMPLE: Run slow work on a Background Thread
    private void startHeavyTask() {

        // Disable button while task is running
        btnStartHeavy.setEnabled(false);

        // Reset and show progress bar
        progressBar.setProgress(0);

        progressBar.setVisibility(View.VISIBLE);

        txtStatus.setText("Working...");

        // Execute task on background thread
        executor.execute(new Runnable() {

            @Override
            public void run() {

                // This code runs on the worker thread
                Log.d(TAG, "Heavy task thread: "
                        + Thread.currentThread().getName());

                // Perform five steps
                for (int i = 1; i <= 5; i++) {

                    try {

                        Thread.sleep(1000);

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();

                        return;
                    }

                    // Copy loop value for inner Runnable
                    final int step = i;

                    // Send UI update to Main Thread
                    mainHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            progressBar.setProgress(step);

                            txtStatus.setText(
                                    "Working... " + step + "/5"
                            );
                        }
                    });
                }

                // Update UI after task completion
                mainHandler.post(new Runnable() {

                    @Override
                    public void run() {

                        txtStatus.setText(
                                "Task Completed Successfully!"
                        );

                        progressBar.setVisibility(View.GONE);

                        btnStartHeavy.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop accepting new tasks
        executor.shutdown();

        Log.d(TAG, "BackgroundActivity onDestroy");
    }
}
