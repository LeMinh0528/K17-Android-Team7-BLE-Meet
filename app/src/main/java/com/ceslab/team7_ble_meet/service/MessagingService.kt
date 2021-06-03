package com.ceslab.team7_ble_meet.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MessagingService", "token: $token")
        if(KeyValueDB.getUserShortId() != ""){
            updateToken(token)
        }

    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("MessagingService","message  ${message.data}")
        if(message.data.isNotEmpty()){
            val map: Map<String, String> = message.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title!!, message!!, hisId!!, hisImage!!)
            else createNormalNotification(title!!, message!!, hisId!!, hisImage!!)
        }
    }

    companion object {
        fun updateToken(newToken:String){
            UsersFireStoreHandler().getUserToken { tokens ->
                if(tokens.isEmpty()){
                    tokens.add(newToken)
                    UsersFireStoreHandler().setUserToken(tokens)
                }else{
                    if(tokens.contains(newToken)){
                        return@getUserToken
                    }else{
                        tokens.add(newToken)
                        UsersFireStoreHandler().setUserToken(tokens)
                    }
                }

            }
        }

    }
    private fun createNormalNotification(title: String
    ,message: String
    ,hisId: String
    ,hisImage: String){
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this,AppConstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_layout)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources,R.color.colorblue100,null))
            .setSound(uri)
        val intent = Intent(this,ChatActivity::class.java)
//        intent.putExtra(AppConstants.USER_NAME,item.userName)
        intent.putExtra(AppConstants.USER_ID,hisId)
        intent.putExtra(AppConstants.AVATAR,hisImage)
        intent.putExtra(AppConstants.USER_NAME,title)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val penDingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(penDingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65),builder.build())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(title: String
                                       , message: String
                                       , hisId: String
                                       , hisImage: String){
        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        val intent = Intent(this,ChatActivity::class.java)
//        intent.putExtra(AppConstants.USER_NAME,item.userName)
        intent.putExtra(AppConstants.USER_ID,hisId)
        intent.putExtra(AppConstants.AVATAR,hisImage)
        intent.putExtra(AppConstants.USER_NAME,title)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val penDingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_layout)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources,R.color.colorblue100,null))
            .build()
        manager.notify(100,notification)
    }
}