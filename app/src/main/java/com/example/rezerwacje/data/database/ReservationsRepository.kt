package com.example.rezerwacje.data.database

class ReservationsRepository {

    private val dao = ReservationsApp.database.reservationsDao()

    suspend fun getAllReservations(): List<ReservationEntity> = dao.getAllReservations()

    suspend fun insertReservation(reservation: ReservationEntity) = dao.insertReservation(reservation)

    suspend fun deleteReservation(id: Int) = dao.deleteReservation(id)

    suspend fun getReservationById(id: Int): ReservationEntity? = dao.getReservationById(id)

    suspend fun getUpcomingReservations(nowEpoch: Long): List<ReservationEntity> = dao.getUpcomingReservations(nowEpoch)

}