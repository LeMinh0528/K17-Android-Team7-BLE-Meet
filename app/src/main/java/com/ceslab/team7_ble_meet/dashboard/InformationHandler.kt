package com.ceslab.team7_ble_meet.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class InformationHandler{
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun getInit(onComplete:(User) -> Unit){
        instance.getCurrentUser(KeyValueDB.getUserShortId()){
            onComplete(it)
        }
    }
}