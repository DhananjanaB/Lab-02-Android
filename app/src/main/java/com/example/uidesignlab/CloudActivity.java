package com.example.uidesignlab;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CloudActivity extends AppCompatActivity {

    private static final String TAG = "CloudLab";

    // UI components
    private EditText editUserId;
    private EditText editName;
    private EditText editRole;
    private EditText editEmail;

    private Button btnSaveUser;
    private Button btnUpdateRole;
    private Button btnDeleteUser;

    private TextView txtCloudData;

    // Firebase Database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cloud);

        // Connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://uidesignlabcloud-e54b3-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        databaseReference = database.getReference("users");

        // Connect UI components
        editUserId = findViewById(R.id.editUserId);
        editName = findViewById(R.id.editName);
        editRole = findViewById(R.id.editRole);
        editEmail = findViewById(R.id.editEmail);

        btnSaveUser = findViewById(R.id.btnSaveUser);
        btnUpdateRole = findViewById(R.id.btnUpdateRole);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);

        txtCloudData = findViewById(R.id.txtCloudData);

        // Save button
        btnSaveUser.setOnClickListener(v -> saveUser());

        // Update Role button
        btnUpdateRole.setOnClickListener(v -> updateRole());

        // Delete button
        btnDeleteUser.setOnClickListener(v -> deleteUser());

        // Read users from Firebase
        readUsers();
    }

    // Save user with automatically generated ID
    private void saveUser() {

        String name = editName.getText().toString().trim();
        String role = editRole.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (name.isEmpty() || role.isEmpty() || email.isEmpty()) {

            Toast.makeText(this,
                    "Please fill Name, Role and Email",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Create User object
        User user = new User(name, role, email);

        // Generate a unique user ID
        String newUserId = databaseReference.push().getKey();

        if (newUserId == null) {

            Toast.makeText(this,
                    "Failed to generate User ID",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Save user to Firebase using generated ID
        databaseReference.child(newUserId).setValue(user)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Saved to the cloud! ID: " + newUserId,
                                Toast.LENGTH_LONG).show();

                        // Display generated ID
                        editUserId.setText(newUserId);

                        editName.setText("");
                        editRole.setText("");
                        editEmail.setText("");

                    } else {

                        Toast.makeText(this,
                                "Failed to save user: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Read users from Firebase
    private void readUsers() {

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                StringBuilder data = new StringBuilder();

                if (snapshot.exists()) {

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                        String userId = userSnapshot.getKey();

                        User user = userSnapshot.getValue(User.class);

                        if (user != null) {

                            data.append("ID: ")
                                    .append(userId)
                                    .append("\n");

                            data.append("Name: ")
                                    .append(user.getName())
                                    .append("\n");

                            data.append("Role: ")
                                    .append(user.getRole())
                                    .append("\n");

                            data.append("Email: ")
                                    .append(user.getEmail())
                                    .append("\n\n");
                        }
                    }

                    txtCloudData.setText(data.toString());

                } else {

                    txtCloudData.setText("No users in the cloud yet");
                }

                Log.d(TAG, "Data changed in Firebase");
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(CloudActivity.this,
                        "Failed to read data: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();

                Log.w(TAG, "Read failed", error.toException());
            }
        });
    }

    // Update only the user's role
    private void updateRole() {

        String userId = editUserId.getText().toString().trim();
        String role = editRole.getText().toString().trim();

        if (userId.isEmpty() || role.isEmpty()) {

            Toast.makeText(this,
                    "Please enter User ID and new Role",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Check whether the user exists in Firebase
        databaseReference.child(userId).get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {

                        // User exists - update only the role
                        Map<String, Object> updateMap = new HashMap<>();

                        updateMap.put("role", role);

                        databaseReference.child(userId)
                                .updateChildren(updateMap)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Role updated successfully!",
                                            Toast.LENGTH_SHORT).show();

                                    editRole.setText("");
                                })
                                .addOnFailureListener(e -> {

                                    Log.w(TAG, "Role update failed", e);

                                    Toast.makeText(this,
                                            "Failed to update role: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {

                        Toast.makeText(this,
                                "User not found!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Log.w(TAG, "User check failed", e);

                    Toast.makeText(this,
                            "Failed to check user: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // Delete user with confirmation dialog
    private void deleteUser() {

        String userId = editUserId.getText().toString().trim();

        if (userId.isEmpty()) {

            Toast.makeText(this,
                    "Please enter User ID",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("Do you really want to delete this user?")

                // Yes button
                .setPositiveButton("Yes", (dialog, which) -> {

                    // Remove user from Firebase
                    databaseReference.child(userId).removeValue()
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(this,
                                        "User deleted successfully!",
                                        Toast.LENGTH_SHORT).show();

                                editUserId.setText("");
                            })
                            .addOnFailureListener(e -> {

                                Log.w(TAG, "Delete failed", e);

                                Toast.makeText(this,
                                        "Failed to delete user: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })

                // No button
                .setNegativeButton("No", (dialog, which) -> {

                    dialog.dismiss();
                })

                .show();
    }
}
