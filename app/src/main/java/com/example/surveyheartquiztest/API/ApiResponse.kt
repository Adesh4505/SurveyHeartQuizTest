package com.example.surveyheartquiztest.API

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val response_code: Int,
    val results: List<Question>
)
