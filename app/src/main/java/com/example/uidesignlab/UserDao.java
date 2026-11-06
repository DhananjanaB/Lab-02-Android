package com.example.uidesignlab;

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
}