package com.example.rezerwacje.data.database

import android.app.Application
import androidx.room.Room
import kotlin.jvm.java

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
            "reservations-db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    }
}