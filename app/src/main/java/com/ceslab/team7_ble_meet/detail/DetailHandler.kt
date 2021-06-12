package com.ceslab.team7_ble_meet.detail

import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class DetailHandler {
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun getInit(userId:String,onComplete:(User) -> Unit){
        instance.getCurrentUser(userId){
            onComplete(it)
        }
    }
}