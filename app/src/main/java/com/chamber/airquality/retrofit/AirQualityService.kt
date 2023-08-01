package com.chamber.airquality.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityService {
    @GET("nearest_city")
    fun getAirQualityData(
        @Query("lat") lat : String,
        @Query("lon") log : String,
        @Query("key") key : String
    ): Call<AirQualityResponse>
}