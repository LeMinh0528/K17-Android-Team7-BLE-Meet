package com.ceslab.team7_ble_meet.service

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
    }

    companion object {
        var context: Context? = null
        var instance: Application? = null
    }
}