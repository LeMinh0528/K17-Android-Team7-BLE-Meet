package com.ceslab.team7_ble_meet.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.MyApplication.Companion.context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if(KeyValueDB.getUserShortId() != ""){
            updateToken(token)
        }
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("MessagingService", "receive message: $message")
        if(message.data.isNotEmpty()){
            val map: Map<String, String> = message.data
            val title = map["title"]
            val content = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]
            Log.d("MessagingService", "receive message: ${map["message"]}")
            Log.d("MessagingService", "receive title: ${map["title"]}")
            Log.d("MessagingService", "receive id: ${map["hisId"]}")
            Log.d("MessagingService", "receive img: ${map["hisImage"]}")
            if(!KeyValueDB.isChat()){
                sendNotification(title!!,content!!,hisId!!,hisImage!!)
            }

        }else{
            Log.d("MessagingService", "not  message: $message")
        }

    }

    companion object {
        fun updateToken(newToken:String){
            UsersFireStoreHandler().getUserToken { tokens ->
                if(tokens.isEmpty()){
                    tokens.add(newToken)
                    KeyValueDB.setUserToken(newToken)
                    UsersFireStoreHandler().setUserToken(tokens)
                }else{
                    if(tokens.contains(newToken)){
                        return@getUserToken
                    }else{
                        tokens.add(newToken)
                        KeyValueDB.setUserToken(newToken)
                        UsersFireStoreHandler().setUserToken(tokens)
                    }
                }
            }
        }

    }

    private fun sendNotification(title: String
                                 , message: String
                                 , hisId: String
                                 , hisImage: String){
        val intent = Intent(this, DashBoardActivity::class.java).apply {
            putExtra(AppConstants.USER_ID,hisId)
            putExtra(AppConstants.AVATAR,hisImage)
            putExtra(AppConstants.USER_NAME,title)
            putExtra("isOpenChat",true)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d("MessagingService", "over oreo ${Build.VERSION.SDK_INT}")
            val builder = NotificationCompat.Builder(this, "channel_message")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_layout)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(uri)
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                with(NotificationManagerCompat.from(this)){
                    notify(hisId.toInt(), builder.build())
                }
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                Log.d("MessagingService", "over o ${Build.VERSION.SDK_INT}")
//                startForeground(hisId.toInt(), builder.build())
                val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(hisId.toInt(),builder.build())
            }

        }else{
            Log.d("MessagingService", "below")
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            val builder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_layout)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(uri)
            with(NotificationManagerCompat.from(this)){
                notify(hisId.toInt(), builder.build())
            }
        }




    }
}