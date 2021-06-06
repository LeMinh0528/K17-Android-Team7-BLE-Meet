package com.ceslab.team7_ble_meet.service

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

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
        createMessagingChannelNotification()
    }

    private fun createBleChannelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun createMessagingChannelNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MessagingService", "OOO")
            val id = "channel_message"
            val descriptionText = "Notification Messaging"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, descriptionText, importance).apply {
                description = descriptionText
                enableVibration(true)
                lightColor = Color.BLUE
                enableLights(true)
                setShowBadge(false) //open size
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}