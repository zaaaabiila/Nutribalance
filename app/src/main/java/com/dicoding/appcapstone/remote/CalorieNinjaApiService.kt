package com.dicoding.appcapstone.remote

import com.dicoding.appcapstone.response.CalorieNinjaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CalorieNinjaApiService {
    @GET("v1/nutrition")
    suspend fun getNutritionInfo(@Query("query") query: String): Response<CalorieNinjaResponse>
}


