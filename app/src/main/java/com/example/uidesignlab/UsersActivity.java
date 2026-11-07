
package com.example.uidesignlab;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.ContentValues;
import android.content.ContentUris;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Connect RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers);

        // Display users vertically
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter with click listener
        adapter = new UserAdapter(user -> {

            // Delete selected user in background thread
            AppDatabase.databaseExecutor.execute(() -> {

                AppDatabase db =
                        AppDatabase.getInstance(getApplicationContext());

                // Delete selected user
                db.userDao().delete(user);

                Log.d("RoomLab", "Deleted user: " + user.name
                        + ", id = " + user.id);

                // Refresh users list
                loadUsers();

            });

        });

        // Connect adapter with RecyclerView
        recyclerView.setAdapter(adapter);

        // Add John button
        Button btnAddJohn = findViewById(R.id.btnAddJohn);

        btnAddJohn.setOnClickListener(v -> {

            // Insert John in background thread
            AppDatabase.databaseExecutor.execute(() -> {

                AppDatabase db =
                        AppDatabase.getInstance(getApplicationContext());

                long insertedId =
                        db.userDao().insert(new UserRecord("John"));

                Log.d("RoomLab", "Added John, id = " + insertedId);

                // Refresh users list
                loadUsers();

            });

        });

        // Clear All button
        Button btnClearAll = findViewById(R.id.btnClearAll);

        btnClearAll.setOnClickListener(v -> {

            // Delete all users in background thread
            AppDatabase.databaseExecutor.execute(() -> {

                AppDatabase db =
                        AppDatabase.getInstance(getApplicationContext());

                // Delete all records
                int deletedCount = db.userDao().deleteAll();

                Log.d("RoomLab", "Deleted " + deletedCount + " users");

                // Refresh RecyclerView
                loadUsers();

            });

        });


// Read Provider button
        Button btnReadProvider = findViewById(R.id.btnReadProvider);

        btnReadProvider.setOnClickListener(v -> {

            // Read data through ContentResolver in background thread
            AppDatabase.databaseExecutor.execute(() -> {

                Uri usersUri = UserProvider.CONTENT_URI;

                try (android.database.Cursor cursor =
                             getContentResolver().query(
                                     usersUri,
                                     null,
                                     null,
                                     null,
                                     null
                             )) {

                    if (cursor != null) {

                        Log.d("RoomLab", "Provider read successful");

                        while (cursor.moveToNext()) {

                            int id = cursor.getInt(
                                    cursor.getColumnIndexOrThrow("id"));

                            String name = cursor.getString(
                                    cursor.getColumnIndexOrThrow("name"));

                            Log.d("RoomLab",
                                    "Provider User: id = " + id
                                            + ", name = " + name);
                        }

                    } else {

                        Log.d("RoomLab", "Provider returned null cursor");
                    }

                } catch (Exception e) {

                    Log.e("RoomLab", "Provider read failed", e);
                }

            });

        });


        // Test Update button
        Button btnTestUpdate = findViewById(R.id.btnTestUpdate);

        btnTestUpdate.setOnClickListener(v -> {

            // Update user using ContentResolver in background thread
            AppDatabase.databaseExecutor.execute(() -> {

                // Create URI for user with ID 32
                Uri userUri =
                        ContentUris.withAppendedId(UserProvider.CONTENT_URI, 32);

                // Create ContentValues
                ContentValues values = new ContentValues();

                // Set the new user name
                values.put("name", "Alice Perera");

                // Update user through ContentResolver
                int updatedRows =
                        getContentResolver().update(
                                userUri,
                                values,
                                null,
                                null
                        );

                Log.d("RoomLab", "Updated rows = " + updatedRows);

                // Refresh RecyclerView
                loadUsers();

            });

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh users list whenever this screen opens
        loadUsers();
    }

    private void loadUsers() {

        // Read users from database in background thread
        AppDatabase.databaseExecutor.execute(() -> {

            // Get users from Room Database
            List<UserRecord> users =
                    AppDatabase.getInstance(getApplicationContext())
                            .userDao()
                            .getAll();

            // Update RecyclerView on main thread
            runOnUiThread(() -> {

                adapter.setUsers(users);

                Log.d("RoomLab", "Loaded " + users.size() + " users");

            });

        });
    }
}
