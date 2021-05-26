package com.ceslab.team7_ble_meet.registerInformation

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class RegisterPictureViewModel: ViewModel() {
    var selectedImage: Uri? = null

    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun uploadImage(){
        if(selectedImage == null){
            userResp.postValue(
                UsersFireStoreHandler.Resp(
                    "NONE",
                    "FAILED",
                    "Not selected image!"
                )
            )
        }else{
            instance.updateAvatar(selectedImage!!)
        }
    }
}