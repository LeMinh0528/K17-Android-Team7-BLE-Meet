package com.ceslab.team7_ble_meet.rest

import com.ceslab.team7_ble_meet.model.UserResp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserDBService {

    @GET("person/popular")
    fun listUser(@Query("language") language:String, @Query("page") page: Int
    ): Call<UserResp>
}