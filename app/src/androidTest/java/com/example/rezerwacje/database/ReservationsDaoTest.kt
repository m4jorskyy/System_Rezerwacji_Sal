package com.example.rezerwacje.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rezerwacje.data.database.ReservationEntity
import com.example.rezerwacje.data.database.ReservationsDao
import com.example.rezerwacje.data.database.ReservationsDatabase
import com.example.rezerwacje.notification.AlarmState // Upewnij się, że importujesz swój enum
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class ReservationsDaoTest {

    private lateinit var database: ReservationsDatabase
    private lateinit var dao: ReservationsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Tworzymy bazę w pamięci (RAM)
        database = Room.inMemoryDatabaseBuilder(context, ReservationsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.reservationsDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetReservation() = runBlocking {
        // 1. GIVEN - Tworzymy rezerwację
        val reservation = ReservationEntity(
            id = 1,
            title = "Testowe Spotkanie",
            startTime = LocalDateTime.of(2024, 1, 1, 10, 0),
            endTime = LocalDateTime.of(2024, 1, 1, 11, 0),
            roomId = 10,
            // POPRAWKA: Używamy istniejącego stanu PENDING
            alarmState = AlarmState.PENDING,
            nextTriggerAt = 0L
        )

        // 2. WHEN - Zapisujemy do bazy
        dao.insertReservation(reservation)

        // 3. THEN - Odczytujemy i sprawdzamy
        val loaded = dao.getReservationById(1)

        assertTrue(loaded != null)
        assertEquals("Testowe Spotkanie", loaded?.title)
        assertEquals(10, loaded?.roomId)
        assertEquals(AlarmState.PENDING, loaded?.alarmState)
    }

    @Test
    fun deleteReservation() = runBlocking {
        // 1. GIVEN - Zapisz rezerwację
        val reservation = ReservationEntity(
            id = 2,
            title = "Do usunięcia",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusHours(1),
            roomId = 1,
            // POPRAWKA: Tutaj również używamy PENDING
            alarmState = AlarmState.PENDING,
            nextTriggerAt = 0L
        )
        dao.insertReservation(reservation)

        // 2. WHEN - Usuń ją
        dao.deleteReservation(2)

        // 3. THEN - Sprawdź czy zniknęła
        val loaded = dao.getReservationById(2)
        assertEquals(null, loaded)
    }
}
