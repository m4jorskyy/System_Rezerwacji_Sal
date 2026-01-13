package com.example.rezerwacje.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class ReservationMappersTest {

    @Test
    fun `toEntity maps AddReservationResponse to ReservationEntity correctly`() {
        // 1. GIVEN - Przygotowanie danych wejściowych
        // Poprawka: Używamy LocalDateTime.parse(), aby zamienić String na obiekt LocalDateTime
        val response = AddReservationResponse(
            id = 101,
            title = "Spotkanie projektowe",
            startTime = LocalDateTime.parse("2023-10-05T12:00:00"), // Tu była zmiana
            endTime = LocalDateTime.parse("2023-10-05T14:00:00"),   // I tutaj
            roomId = 5,
            userId = 1,
        )

        // 2. WHEN - Wykonanie konwersji
        val entity = response.toEntity()

        // 3. THEN - Sprawdzenie wyników
        assertEquals(101, entity.id)
        assertEquals("Spotkanie projektowe", entity.title)
        assertEquals(5, entity.roomId)

        // Sprawdzenie poprawności parsowania daty
        val expectedStart = LocalDateTime.of(2023, 10, 5, 12, 0, 0)
        assertEquals(expectedStart, entity.startTime)

        val expectedEnd = LocalDateTime.of(2023, 10, 5, 14, 0, 0)
        assertEquals(expectedEnd, entity.endTime)
    }
}