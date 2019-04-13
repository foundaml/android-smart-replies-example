package org.foundaml.exampleapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SmartReplyApi {
    @POST("/predictions")
    fun postPrediction(@Body command: PostPredictionRequest): Call<PostPredictionResponse>

    @POST("/examples")
    fun postExample(@Query("predictionId") predictionId: String, @Query("label") label: String, @Query("isCorrect") isCorrect: Boolean): Call<Any>
}