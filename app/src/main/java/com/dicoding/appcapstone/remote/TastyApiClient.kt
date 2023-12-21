package com.dicoding.appcapstone.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import os api_key = os.getenv("API_KEY2")
class TastyApiClient {
    private val BASE_URL = "https://tasty.p.rapidapi.com/"
    private val RAPID_API_KEY = "API_KEY2"
    private val RAPID_API_HOST = "tasty.p.rapidapi.com"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-RapidAPI-Key", RAPID_API_KEY)
                .addHeader("X-RapidAPI-Host", RAPID_API_HOST)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: TastyApiService = retrofit.create(TastyApiService::class.java)
}