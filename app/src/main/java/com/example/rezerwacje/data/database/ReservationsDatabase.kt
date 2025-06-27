package com.example.rezerwacje.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ReservationEntity::class],
    version = 1
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class ReservationsDatabase : RoomDatabase() {
    abstract fun reservationsDao(): ReservationsDao
}