package com.dicoding.appcapstone.remote

import com.dicoding.appcapstone.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET

interface TastyApiService {
    @GET("recipes/list?from=0&size=10")
    fun getRecipes(): Call<ArticleResponse>
}

