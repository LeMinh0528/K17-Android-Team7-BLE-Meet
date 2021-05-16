package com.ceslab.team7_ble_meet.repository

import android.content.Context
import android.content.SharedPreferences

object KeyValueDB {

    private const val USER_REGISTERED = "user_registered"
    private const val FIRST_TIME_REGISTER = "first_time_register"
    private const val USER_NAME = "user_name"
    private const val EMAIL = "email"
    private const val PASSWORD = "password"

    private var pref : SharedPreferences? = null

    fun createRef(context: Context){
        pref =context.applicationContext.getSharedPreferences("BLE_MEET",0)
    }

    fun saveUserStatus(isRegistered: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_REGISTERED,isRegistered)
    }
    fun setFirstTimeRegister(fistTime: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(FIRST_TIME_REGISTER,fistTime)
    }
    fun setUserName(user: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_NAME,user)
    }
    fun setEmail(email: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(EMAIL,email)
    }
    fun setPassWord(pass: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(PASSWORD,pass)
    }
    fun isFirstTimeRegister(): Boolean{
        return pref?.getBoolean(FIRST_TIME_REGISTER,false)?: false
    }

    fun isRegistered(): Boolean{
        return pref?.getBoolean(USER_REGISTERED,false)?: false
    }
}