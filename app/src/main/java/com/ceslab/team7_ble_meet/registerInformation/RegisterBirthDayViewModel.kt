package com.ceslab.team7_ble_meet.registerInformation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import java.util.*

class RegisterBirthDayViewModel: ViewModel() {
    var day: String = if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10) "0${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}"
        else Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    var month: String = if(Calendar.getInstance().get(Calendar.MONTH) < 10) "0${Calendar.getInstance().get(Calendar.MONTH)}"
        else Calendar.getInstance().get(Calendar.MONTH).toString()
    var year: String = Calendar.getInstance().get(Calendar.YEAR).toString()

    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun updateBirthDay(){
        var dof = "$day/$month/$year"
        instance.updateBirthDay(dof)
    }

}