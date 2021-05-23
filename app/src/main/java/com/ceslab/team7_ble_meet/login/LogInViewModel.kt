package com.ceslab.team7_ble_meet.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.data.DataAccountHandler
import com.ceslab.team7_ble_meet.isValidEmail
import com.ceslab.team7_ble_meet.isValidPasswordFormat

class LogInViewModel(): ViewModel() {
    var email = ""
    var password = ""
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun logIn(){
        if(email.isEmpty() || password.isEmpty()) {
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED", "Empty field!"))
            return
        }else{
            if (!isValidEmail(email)) {
                userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED", "Wrong email format!"))
                return
            }
            if (!isValidPasswordFormat(password)) {
                userResp.postValue(
                    UsersFireStoreHandler.Resp(
                        "NONE",
                        "FAILED",
                        "Wrong password format, must contain /@$#._"
                    )
                )
                return
            }
        }
        instance.logInUser(email, password)
    }
}