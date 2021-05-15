package com.ceslab.team7_ble_meet.rest

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthenticationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var url: HttpUrl = chain.request().url()
            .newBuilder()
            .addQueryParameter("api_key","7519cb3f829ecd53bd9b7007076dbe23")
            .build()
        var request: Request = chain.request().newBuilder().url(url).build()
        return chain.proceed(request)
    }
}