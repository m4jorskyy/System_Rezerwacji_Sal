package com.example.rezerwacje.data.api

import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.AddReservationResponse
import com.example.rezerwacje.data.model.AddRoomRequest
import com.example.rezerwacje.data.model.AddRoomResponse
import com.example.rezerwacje.data.model.GlobalStats
import com.example.rezerwacje.data.model.LoginRequest
import com.example.rezerwacje.data.model.LoginResponse
import com.example.rezerwacje.data.model.RegisterRequest
import com.example.rezerwacje.data.model.RegisterResponse
import com.example.rezerwacje.data.model.Reservation
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest
import com.example.rezerwacje.data.model.RoomStats
import com.example.rezerwacje.data.model.UserStats


import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/reservations/user/{userId}")
    suspend fun getUserReservations(
        @Path("userId") userId: Int,
        @Header("Authorization") token: String
    ): List<Reservation>

    @Headers("Content-Type: application/json")
    @POST("api/rooms/admin")
    suspend fun addRoom(
        @Body request: AddRoomRequest,
        @Header("Authorization") token: String
    ): AddRoomResponse

    @Headers("Content-Type: application/json")
    @POST("api/rooms/filter")
    suspend fun filterRooms(
        @Body request: RoomFilterRequest,
        @Header("Authorization") token: String
    ): List<RoomDataModel>

    @Headers("Content-Type: application/json")
    @DELETE("api/rooms/admin/{roomId}")
    suspend fun deleteRoom(
        @Path("roomId") roomId: Int,
        @Header("Authorization") token: String
    ): retrofit2.Response<Unit>

    @Headers("Content-Type: application/json")
    @PUT("api/rooms/admin/{roomId}")
    suspend fun editRoom(
        @Path("roomId") roomId: Int,
        @Body request: AddRoomRequest,
        @Header("Authorization") token: String
    ): AddRoomResponse

    @GET("api/rooms/{id}")
    suspend fun getRoomById(
        @Path("id") roomId: Int,
        @Header("Authorization") token: String
    ): RoomDataModel

    @Headers("Content-Type: application/json")
    @POST("api/reservations")
    suspend fun addReservation(
        @Body request: AddReservationRequest,
        @Header("Authorization") token: String
    ): AddReservationResponse

    @GET("api/reservations/room/{roomId}")
    suspend fun getReservationsByRoomId(
        @Path("roomId") roomId: Int,
        @Header("Authorization") token: String
    ): List<Reservation>

    @DELETE("api/reservations/{reservationId}")
    suspend fun deleteReservation(
        @Path("reservationId") reservationId: Int,
        @Header("Authorization") token: String
    ): retrofit2.Response<Unit>

    @PUT("api/reservations/{reservationId}")
    suspend fun editReservation(
        @Path("reservationId") reservationId: Int,
        @Body request: AddReservationRequest,
        @Header("Authorization") token: String
    ): AddReservationResponse

    @GET("api/stats/global")
    suspend fun getGlobalStats(
        @Query("weekStart") weekStart: String,
        @Header("Authorization") token: String
    ): GlobalStats

    // --- STATYSTYKI POKOI (ROOMS) ---

    // 1. Wszystkie pokoje w danym tygodniu (odpowiednik: getAllRoomsStatsByWeek)
    @GET("api/stats/rooms")
    suspend fun getAllRoomsStatsByWeek(
        @Query("weekStart") weekStart: String,
        @Header("Authorization") token: String
    ): List<RoomStats>

    // 2. Konkretny pokój w danym tygodniu (odpowiednik: getRoomStatsByWeek)
    @GET("api/stats/rooms/{id}")
    suspend fun getRoomStatsByWeek(
        @Path("id") roomId: Int,
        @Query("weekStart") weekStart: String,
        @Header("Authorization") token: String
    ): RoomStats

    // 3. Historia statystyk dla konkretnego pokoju (odpowiednik: getRoomStatsById)
    @GET("api/stats/rooms/{id}/weeks")
    suspend fun getRoomStatsById(
        @Path("id") roomId: Int,
        @Header("Authorization") token: String
    ): List<RoomStats>

    // --- STATYSTYKI UŻYTKOWNIKÓW (USERS) ---

    // 1. Wszyscy użytkownicy w danym tygodniu (odpowiednik: getAllUsersStatsByWeek)
    @GET("api/stats/users")
    suspend fun getAllUsersStatsByWeek(
        @Query("weekStart") weekStart: String,
        @Header("Authorization") token: String
    ): List<UserStats>

    // 2. Konkretny użytkownik w danym tygodniu (odpowiednik: getUsersStatsByWeek)
    @GET("api/stats/users/{id}")
    suspend fun getUsersStatsByWeek(
        @Path("id") userId: Int,
        @Query("weekStart") weekStart: String,
        @Header("Authorization") token: String
    ): UserStats

    // 3. Historia statystyk dla konkretnego użytkownika (odpowiednik: getUsersStatsById)
    @GET("api/stats/users/{id}/weeks")
    suspend fun getUsersStatsById(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): List<UserStats>
}