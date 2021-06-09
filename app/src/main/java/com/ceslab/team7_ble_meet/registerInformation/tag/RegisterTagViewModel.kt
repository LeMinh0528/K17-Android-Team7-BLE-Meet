package com.ceslab.team7_ble_meet.registerInformation.tag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class RegisterTagViewModel: ViewModel() {
    private var instance = UsersFireStoreHandler()
//    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun register(tag:MutableList<String>,onComplete:(status: String) -> Unit){
        instance.updateTag(tag){
            onComplete(it)
        }
    }
}