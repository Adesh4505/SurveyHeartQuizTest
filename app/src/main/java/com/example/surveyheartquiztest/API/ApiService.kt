package com.example.surveyheartquiztest.API

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api.php") /* else use this api_config as endpoint */
    fun getQuestions(
        @Query("amount") amount: Int
    ): Call<ApiResponse>
}
