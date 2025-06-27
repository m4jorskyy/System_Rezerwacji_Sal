package com.example.rezerwacje.data.api

import com.example.rezerwacje.data.model.AddReservationRequest
import com.example.rezerwacje.data.model.AddReservationResponse
import com.example.rezerwacje.data.model.AddRoomRequest
import com.example.rezerwacje.data.model.AddRoomResponse
import com.example.rezerwacje.data.model.LoginRequest
import com.example.rezerwacje.data.model.LoginResponse
import com.example.rezerwacje.data.model.RegisterRequest
import com.example.rezerwacje.data.model.RegisterResponse
import com.example.rezerwacje.data.model.Reservation
import com.example.rezerwacje.data.model.RoomDataModel
import com.example.rezerwacje.data.model.RoomFilterRequest


import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
}