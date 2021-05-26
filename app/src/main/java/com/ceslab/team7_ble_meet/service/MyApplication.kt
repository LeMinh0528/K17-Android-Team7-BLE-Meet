package com.ceslab.team7_ble_meet.service

import android.app.Application
import android.content.Context
import com.ceslab.team7_ble_meet.db.UserDiscoveredDataBase
import com.ceslab.team7_ble_meet.repository.UserDiscoveredRepository

class MyApplication : Application() {
//    val database by lazy { UserDiscoveredDataBase.getDatabase(this) }
//    val repository by lazy { UserDiscoveredRepository(database.userDiscoveredDao()) }
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