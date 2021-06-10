package com.ceslab.team7_ble_meet.registerInformation.avatar

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class RegisterPictureViewModel: ViewModel() {
    var selected: ByteArray? = null
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun uploadImage(){
        Log.d("TAG", "image array: ${selected?.size}")
        if(selected == null || selected!!.isEmpty()){
            userResp.postValue(
                UsersFireStoreHandler.Resp(
                    "NONE",
                    "FAILED",
                    "Not selected image!"
                )
            )
        }else{
            instance.updateAvatar(selected!!)
        }
    }
}