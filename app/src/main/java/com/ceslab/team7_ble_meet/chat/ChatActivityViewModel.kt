package com.ceslab.team7_ble_meet.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

class ChatActivityViewModel: ViewModel() {
    var userName: String? = ""
    private var instance = UsersFireStoreHandler()
    fun getChannel(otherUserId: String, onComplete:(channelId: String) -> Unit){
        instance.getOrCreateChatChannel(otherUserId) { channelId ->
            onComplete(channelId)
        }
    }

    fun setListener(channelId: String, context: Context, onComplete: (messages:List<Item>) -> Unit): ListenerRegistration{
        return instance.addChatListener(channelId,context){
            onComplete(it)
        }
    }

    fun getInit(userId:String,onComplete:(User) -> Unit){
        instance.getCurrentUser(userId){
            onComplete(it)
        }
    }

}