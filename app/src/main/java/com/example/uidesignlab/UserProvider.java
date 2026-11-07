
package com.example.uidesignlab;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserProvider extends ContentProvider {

    // Content Provider Authority
    public static final String AUTHORITY =
            "com.example.uidesignlab.provider";

    // Content URI
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/users");

    // URI matching codes
    private static final int USERS = 1;
    private static final int USER_ID = 2;

    // Create URI Matcher
    private static final UriMatcher URI_MATCHER =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "users", USERS);
        URI_MATCHER.addURI(AUTHORITY, "users/#", USER_ID);
    }

    // Room Database DAO
    private UserDao dao;

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String s,
                      @Nullable String[] strings) {

        // Only allow deleting a single user
        int match = URI_MATCHER.match(uri);

        if (match != USER_ID) {
            throw new IllegalArgumentException(
                    "Invalid URI for delete: " + uri
            );
        }

        // Get user ID from URI
        long id = ContentUris.parseId(uri);

        // Delete user from Room Database
        int rows = dao.deleteById(id);

        // Notify observers about data change
        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        // Return number of deleted rows
        return rows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        // Check the requested URI
        int match = URI_MATCHER.match(uri);

        switch (match) {

            case USERS:

                // MIME type for multiple users
                return "vnd.android.cursor.dir/vnd."
                        + AUTHORITY + ".users";

            case USER_ID:

                // MIME type for a single user
                return "vnd.android.cursor.item/vnd."
                        + AUTHORITY + ".users";

            default:

                throw new IllegalArgumentException(
                        "Unknown URI: " + uri
                );
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues) {

        // Only allow inserting into the users URI
        int match = URI_MATCHER.match(uri);

        if (match != USERS) {
            throw new IllegalArgumentException(
                    "Invalid URI for insert: " + uri
            );
        }

        // Get user name from ContentValues
        String name = contentValues != null
                ? contentValues.getAsString("name")
                : null;

        // Validate user name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "User name cannot be empty"
            );
        }

        // Insert user into Room Database
        long id = dao.insert(new UserRecord(name.trim()));

        // Notify observers about data change
        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        // Return URI of the newly inserted user
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public boolean onCreate() {

        // Connect to Room Database
        dao = AppDatabase.getInstance(getContext()).userDao();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] strings,
                        @Nullable String s,
                        @Nullable String[] strings1,
                        @Nullable String s1) {

        Cursor cursor;

        // Check the requested URI
        int match = URI_MATCHER.match(uri);

        switch (match) {

            case USERS:

                // Get all users
                cursor = dao.getAllCursor();
                break;

            case USER_ID:

                // Get one user by ID
                long id = ContentUris.parseId(uri);

                cursor = dao.getByIdCursor(id);
                break;

            default:

                throw new IllegalArgumentException(
                        "Unknown URI: " + uri
                );
        }

        // Notify when data changes
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri
        );

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String s,
                      @Nullable String[] strings) {

        // Only allow updating a single user
        int match = URI_MATCHER.match(uri);

        if (match != USER_ID) {
            throw new IllegalArgumentException(
                    "Invalid URI for update: " + uri
            );
        }

        // Get user name from ContentValues
        String name = contentValues != null
                ? contentValues.getAsString("name")
                : null;

        // Validate user name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "User name cannot be empty"
            );
        }

        // Get user ID from URI
        long id = ContentUris.parseId(uri);

        // Update user name in Room Database
        int rows = dao.updateName(id, name.trim());

        // Notify observers about data change
        getContext().getContentResolver().notifyChange(
                CONTENT_URI, null
        );

        // Return number of updated rows
        return rows;
    }
}
