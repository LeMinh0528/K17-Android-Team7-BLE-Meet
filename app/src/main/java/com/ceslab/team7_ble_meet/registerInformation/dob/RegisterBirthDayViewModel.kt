package com.ceslab.team7_ble_meet.registerInformation.dob

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.utils.NetworkUtils
import java.util.*

class RegisterBirthDayViewModel: ViewModel() {
    var day: String = if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10) "0${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)}"
        else Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    var month: String = if(Calendar.getInstance().get(Calendar.MONTH) < 10) "0${Calendar.getInstance().get(Calendar.MONTH)}"
        else Calendar.getInstance().get(Calendar.MONTH).toString()
    var year: String = Calendar.getInstance().get(Calendar.YEAR).toString()
    @SuppressLint("StaticFieldLeak")
    var context : Context? = null

    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun updateBirthDay(){
        val c= Calendar.getInstance()
        val curentYear= c.get(Calendar.YEAR)
        if (!NetworkUtils.isNetworkAvailable(context)){
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED","Error internet connection!"))
        }else if (curentYear-year.toInt() <0){
            userResp.postValue(UsersFireStoreHandler.Resp("NONE","FAILED","Please set right age!"))
        } else{
            val dof = "$day/$month/$year"
            instance.updateBirthDay(dof)
        }

    }

}