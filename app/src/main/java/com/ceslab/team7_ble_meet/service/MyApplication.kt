package com.ceslab.team7_ble_meet.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MyApplication : Application() {
//    val database by lazy { UserDiscoveredDataBase.getDatabase(this) }
//    val repository by lazy { UserDiscoveredRepository(database.userDiscoveredDao()) }

    val CHANNEL_ID = "channel_ble"
    val CHANNEL_NAME = "Channel Bluetooth Low Energy"
    companion object {
        var context: Context? = null
        var instance: Application? = null
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

        createBleChannelNotification()
    }

    private fun createBleChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}