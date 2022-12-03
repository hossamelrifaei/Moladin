package com.moladin.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PeopleService {
    @GET("api/users")
    suspend fun getPeople(@Query("page") page: Int): PeopleResponse
}