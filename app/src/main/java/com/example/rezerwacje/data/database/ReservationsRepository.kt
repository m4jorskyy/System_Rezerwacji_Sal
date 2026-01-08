package com.example.rezerwacje.data.database

import android.util.Log
import com.example.rezerwacje.notification.AlarmState
import com.example.rezerwacje.notification.NotificationScheduler
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationsRepository(
    private val scheduler: NotificationScheduler
) {

    private val dao = ReservationsApp.database.reservationsDao()

    private fun autoNextTriggerAt(startTime: LocalDateTime): Long {
        return startTime.atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 10 * 60 * 1000L // 10 minut przed czasem
    }

    suspend fun updateReservation(reservation: ReservationEntity) = dao.updateReservation(reservation)

    suspend fun getAllReservations(): List<ReservationEntity> = dao.getAllReservations()

    suspend fun insertReservation(reservation: ReservationEntity) =
        dao.insertReservation(reservation)

    suspend fun deleteReservation(id: Int) = dao.deleteReservation(id)

    suspend fun getReservationById(id: Int): ReservationEntity? = dao.getReservationById(id)

    suspend fun getUpcomingReservations(nowEpoch: Long): List<ReservationEntity> =
        dao.getUpcomingReservations(nowEpoch)

    suspend fun scheduleForReservation(id: Int) {
        val reservation = getReservationById(id) ?: return

        val now = System.currentTimeMillis()
        val meetingTimeMillis = reservation.startTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Domyślny czas alarmu: 10 minut przed
        var nextTrigger = autoNextTriggerAt(reservation.startTime)

        // LOGIKA NAPRAWCZA DLA TESTÓW I REZERWACJI "LAST MINUTE":
        if (nextTrigger <= now) {
            // Jeśli czas "10 min przed" już minął...
            if (meetingTimeMillis > now) {
                // ...ale spotkanie jest w przyszłości, ustaw alarm na "za 5 sekund" od teraz
                Log.d("Repo", "Alarm '10 min przed' minął, ale spotkanie w przyszłości. Ustawiam na teraz.")
                nextTrigger = now + 5000 // +5 sekund opóźnienia
            } else {
                // ...a spotkanie już się zaczęło (lub skończyło), ignorujemy.
                Log.d("Repo", "Spotkanie już się zaczęło, nie ustawiam alarmu.")
                return
            }
        }

        val title = "Przypomnienie: ${reservation.title}" // Upewnij się, że pole nazywa się title lub name w Entity
        val text = "Spotkanie w sali nr ${reservation.roomId} zaczyna się o ${reservation.startTime.toLocalTime()}"

        // Wywołanie schedulera (zwróć uwagę na parametry zgodne z poprzednim krokiem)
        scheduler.scheduleNotification(id, nextTrigger, title, text)

        val updated = reservation.copy(
            nextTriggerAt = nextTrigger,
            alarmState = AlarmState.SCHEDULED
        )
        dao.updateReservation(updated)
    }

    fun cancelAlarm(id: Int) {
        scheduler.cancelNotification(id)
    }

}