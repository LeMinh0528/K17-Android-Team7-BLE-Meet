package com.ceslab.team7_ble_meet.registerInformation.name

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.utils.NetworkUtils

class RegisterUserNameViewModel : ViewModel() {
    var username: String = ""
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun registerName(){
        if(username.isEmpty()) {
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED", "Empty field!"))
        }else if (!NetworkUtils.isNetworkAvailable(context)){
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED","Error internet connection!"))
        }else{
            instance.updateName(username)
        }
    }

    fun deleteUser(){
        instance.deleteData()
    }

}