package com.example.givetakeapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.givetakeapp.dao.UserDao
import com.example.givetakeapp.data.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}