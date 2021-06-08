package com.ceslab.team7_ble_meet.repository

import android.content.Context
import android.content.SharedPreferences

object KeyValueDB {

    private const val CHAT_STATUS ="chat_status"
    private const val USER_ID = "user_id"
    private const val USER_AVATAR = "user_avatar"
    private const val USER_SHORT_ID = "user_short_id"
    private const val USER_REGISTERED = "user_registered"
    private const val FIRST_TIME_REGISTER = "first_time_register"
    private const val DAY_OF_BIRTH = "day_of_birth"
    private const val USER_GENDER = "user_gender"
    private const val USER_TAG = "user_tag"

    private var pref : SharedPreferences? = null

    fun createRef(context: Context){
        pref =context.applicationContext.getSharedPreferences("BLE_MEET",0)
    }

    fun setUserTag(tag: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_TAG,tag)
        editor?.apply()
    }

    fun setChatStatus(status: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(CHAT_STATUS,status)
        editor?.apply()
    }

    fun isChat():Boolean{
        return pref?.getBoolean(CHAT_STATUS,false)?:false
    }

    fun setUserAvatar(i: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_AVATAR,i)
        editor?.apply()
    }

    fun setUserId(id: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_ID,id)
        editor?.apply()
    }

    fun setUserShortId(id: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_SHORT_ID,id)
        editor?.apply()
    }


    fun isRegisterUserGender(): Boolean {
        return pref?.getBoolean(USER_GENDER,false)?: false
    }

    fun setDayOfBirth(i: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(DAY_OF_BIRTH, i)
        editor?.apply()
    }

    fun getUserId(): String {
        return pref?.getString(USER_ID,"")?: ""
    }

    fun getUserShortId(): String {
        return pref?.getString(USER_SHORT_ID,"")?: ""
    }

    fun setRegister(fistTime: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(FIRST_TIME_REGISTER,fistTime)
        editor?.apply()
    }

    fun isRegister(): Boolean{
        return pref?.getBoolean(FIRST_TIME_REGISTER,false)?: false
    }

    fun isRegistered(): Boolean{
        return pref?.getBoolean(USER_REGISTERED,false)?: false
    }

    fun clearData(){
        val editor : SharedPreferences.Editor? = pref?.edit()
        editor?.clear()
        editor?.apply()
    }
}