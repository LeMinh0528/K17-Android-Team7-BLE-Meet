package com.ceslab.team7_ble_meet.rest

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {
    private var api : UserDBService
    val API: UserDBService
        get() = api
    init{
        api =createUserDBSerevice()
    }
    companion object{
        private var mInstance: RestClient? = null
        fun getInstance() = mInstance ?: synchronized(this){
            mInstance ?: RestClient().also { mInstance = it}
        }
    }
    private fun createUserDBSerevice(): UserDBService{
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        return retrofit.create(UserDBService::class.java)
    }
}