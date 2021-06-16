package com.ceslab.team7_ble_meet.registerInformation.gender

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.utils.NetworkUtils

class RegisterGenderViewModel: ViewModel() {
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null
    fun register(gender:String?,inter:String){
        if(!NetworkUtils.isNetworkAvailable(context)){
            userResp.postValue(UsersFireStoreHandler.Resp("DELETE","FAILED","Error wifi connection!"))
        }else{
            Log.d("RegisterGenderViewModel","gender: $gender")
            Log.d("RegisterGenderViewModel","inter: $inter")
            instance.updateGender(gender,inter)
        }
    }
    fun getGender(): String?{
        return instance.getGender()
    }
}