package com.ceslab.team7_ble_meet.registerInformation.avatar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.utils.NetworkUtils

class RegisterPictureViewModel: ViewModel() {
    var selected: ByteArray? = null
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    fun uploadImage() {
        Log.d("TAG", "image array: ${selected?.size}")
        if (selected == null || selected!!.isEmpty()) {
            userResp.postValue(
                UsersFireStoreHandler.Resp(
                    "NONE",
                    "FAILED",
                    "Not selected image!"
                )
            )
        } else if (!NetworkUtils.isNetworkAvailable(context)) {
            userResp.postValue(
                UsersFireStoreHandler.Resp(
                    "NONE",
                    "FAILED",
                    "Error internet connection!"
                )
            )
        } else {
            instance.updateAvatar(selected!!)
        }
    }
}