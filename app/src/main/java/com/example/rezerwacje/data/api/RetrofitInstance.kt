package com.example.rezerwacje.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // Konfiguracja Moshi (bez zmian)
    private val moshi = Moshi
        .Builder()
        .add(LocalDateTimeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    // 1. Konfiguracja loggera - to pozwoli zobaczyć requesty w Logcat
    private val logging = HttpLoggingInterceptor().apply {
        // Level.BODY pokaże URL, nagłówki (token!) i treść odpowiedzi
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Klient HTTP z dodanym loggerem i timeoutami
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) // Warto dodać, żeby nie zrywało połączenia zbyt szybko
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://reservationsystem-7emb.onrender.com/")
            .client(client) // <--- KLUCZOWE: Dodajemy klienta z loggerem
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}