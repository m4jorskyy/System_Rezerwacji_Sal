package com.example.rezerwacje.data.database

import android.app.Application
import androidx.room.Room

class ReservationsApp : Application() {

    companion object {
        lateinit var database: ReservationsDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            ReservationsDatabase::class.java,
            "reservations_database"
        ).fallbackToDestructiveMigration().build()
    }
}