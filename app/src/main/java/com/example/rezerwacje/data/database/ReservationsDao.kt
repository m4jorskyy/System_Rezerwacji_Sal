package com.example.rezerwacje.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReservationsDao {
    @Query("SELECT * FROM reservations")
    suspend fun getAllReservations(): List<ReservationEntity>

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: Int): ReservationEntity?

    @Query("SELECT * FROM reservations WHERE startTime > :nowEpoch ORDER BY startTime ASC")
    suspend fun getUpcomingReservations(nowEpoch: Long): List<ReservationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservation(id: Int)

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

}