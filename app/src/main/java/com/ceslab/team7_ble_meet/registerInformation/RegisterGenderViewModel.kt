package com.ceslab.team7_ble_meet.registerInformation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class RegisterGenderViewModel: ViewModel() {
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun register(id:String,gender:String?,inter:String){
        Log.d("RegisterGenderViewModel","id: $id")
        Log.d("RegisterGenderViewModel","gender: $gender")
        Log.d("RegisterGenderViewModel","inter: $inter")
        instance.updateGender(id,gender,inter)

    }
    fun getGender(): String?{
        return instance.getGender()
    }
}