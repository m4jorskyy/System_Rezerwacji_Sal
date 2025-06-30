package com.example.rezerwacje.data.database

import com.example.rezerwacje.notification.AlarmState
import com.example.rezerwacje.notification.NotificationScheduler
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationsRepository(
    private val scheduler: NotificationScheduler
) {

    private val dao = ReservationsApp.database.reservationsDao()


    suspend fun getAllReservations(): List<ReservationEntity> = dao.getAllReservations()

    suspend fun insertReservation(reservation: ReservationEntity) =
        dao.insertReservation(reservation)

    suspend fun deleteReservation(id: Int) = dao.deleteReservation(id)

    suspend fun getReservationById(id: Int): ReservationEntity? = dao.getReservationById(id)

    suspend fun getUpcomingReservations(nowEpoch: Long): List<ReservationEntity> =
        dao.getUpcomingReservations(nowEpoch)

    private fun autoNextTriggerAt(startTime: LocalDateTime): Long {

        return startTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 10 * 60 * 1000L
    }

    suspend fun scheduleForReservation(id: Int) {
        val reservation = getReservationById(id) ?: return
        val nextTrigger = autoNextTriggerAt(reservation.startTime)
        if (nextTrigger <= System.currentTimeMillis()) return

        val title = "Przypomnienie: ${reservation.name}"
        val text = "Zdarzenie zaraz się zacznie"
        scheduler.scheduleNotification(id, nextTrigger, title, text)

        val updated = reservation.copy(
            nextTriggerAt = nextTrigger,
            alarmState = AlarmState.SCHEDULED
        )
        dao.updateReservation(updated)
    }

}