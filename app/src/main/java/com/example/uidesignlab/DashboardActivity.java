package com.example.uidesignlab;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "LifecycleTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        Log.d(TAG, "Dashboard onCreate");
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setTitle(R.string.dashboard_title);

        String userName = getIntent().getStringExtra("USER_NAME");

        TextView txtUserName = findViewById(R.id.txtUserName);

        txtUserName.setText(getString(R.string.hello_user, userName));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dashboard_menu, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Dashboard onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Dashboard onDestroy");
    }
}