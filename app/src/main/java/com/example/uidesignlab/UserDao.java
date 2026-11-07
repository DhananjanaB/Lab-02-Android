package com.example.uidesignlab;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    // Insert a new user
    @Insert
    long insert(UserRecord user);

    // Update an existing user
    @Update
    int update(UserRecord user);

    // Delete a user
    @Delete
    int delete(UserRecord user);

    // Get all users, newest first
    @Query("SELECT * FROM UserRecord ORDER BY id DESC")
    List<UserRecord> getAll();

    // Delete all users
    @Query("DELETE FROM UserRecord")
    int deleteAll();

    // Get all users as Cursor
    @Query("SELECT * FROM UserRecord ORDER BY id DESC")
    Cursor getAllCursor();

    // Get one user by ID as Cursor
    @Query("SELECT * FROM UserRecord WHERE id = :id")
    Cursor getByIdCursor(long id);

    // Update user name using ID
    @Query("UPDATE UserRecord SET name = :name WHERE id = :id")
    int updateName(long id, String name);

    // Delete one user using ID
    @Query("DELETE FROM UserRecord WHERE id = :id")
    int deleteById(long id);

    // Count users with the given name
    @Query("SELECT COUNT(*) FROM UserRecord WHERE name = :name")
    int countByName(String name);
}