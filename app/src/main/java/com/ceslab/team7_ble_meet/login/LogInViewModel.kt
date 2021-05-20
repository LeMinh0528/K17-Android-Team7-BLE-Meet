package com.ceslab.team7_ble_meet.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.data.DataAccountHandler

class LogInViewModel(var usrname: String = ""): ViewModel() {
    var usrName: String = ""
    var password: String = ""
    var resultLogIn: MutableLiveData<String> = MutableLiveData()
    init {
        usrName = usrname
        password = ""
    }
    fun logIn() {
//        val listAccount = DataAccountHandler.accountList
//        //set up login callback
//        listAccount.logInCallBack = object : DataAccountHandler.LogInCallback{
//            override fun resultLogIn(message: String) {
//                resultLogIn.value = message
//            }
//        }

//        listAccount.logIn(usrName, password)
    }
}