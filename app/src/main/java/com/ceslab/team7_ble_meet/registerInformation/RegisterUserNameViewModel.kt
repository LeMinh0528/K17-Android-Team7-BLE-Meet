package com.ceslab.team7_ble_meet.registerInformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class RegisterUserNameViewModel : ViewModel() {
    var username: String = ""
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun registerName(){
        if(username.isEmpty()) {
            userResp.postValue(UsersFireStoreHandler.Resp("FAILED", "Empty field!"))
            return
        }
        instance.updateName(username)
    }

}