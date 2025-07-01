package com.example.rezerwacje.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ReservationEntity::class],
    version = 2
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class ReservationsDatabase : RoomDatabase() {
    abstract fun reservationsDao(): ReservationsDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE reservations ADD COLUMN nextTriggerAt INTEGER")
        db.execSQL("ALTER TABLE reservations ADD COLUMN alarmState INTEGER NOT NULL DEFAULT 0")
    }
}