package com.ceslab.team7_ble_meet.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.data.DataAccount
import com.ceslab.team7_ble_meet.Model.Account

class SignUpViewModel: ViewModel() {
    var account = Account()
    var rePassWord: String = ""

    var resultSignUp: MutableLiveData<String> = MutableLiveData()

    fun signUp(){
        val accountList = DataAccount.accountList

        accountList.signUpCallBack = object: DataAccount.SignUpCallback {
            override fun resultSignUp(message: String) {
                resultSignUp.value = message
            }
        }

        accountList.signUp(account, rePassWord)
    }
}