package com.example.givetakeapp

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.room.Room
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class MainApp: Application() {
    companion object {
        lateinit var database: AppDatabase
        lateinit var executorService: ExecutorService
        lateinit var mainHandler: Handler
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database5"
        ).build()
        executorService = Executors.newFixedThreadPool(5)
        mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    }
}