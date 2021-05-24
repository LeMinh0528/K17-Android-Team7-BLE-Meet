package com.ceslab.team7_ble_meet.repository

import android.content.Context
import android.content.SharedPreferences

object KeyValueDB {

    private const val USER_ID = "user_id"
    private const val USER_SHORT_ID = "user_short_id"
    private const val USER_REGISTERED = "user_registered"
    private const val FIRST_TIME_REGISTER = "first_time_register"
    private const val DAY_OF_BIRTH = "day_of_birth"
    private const val USER_NAME = "user_name"
    private const val EMAIL = "email"
    private const val PASSWORD = "password"
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

    fun getUserTag(): Boolean{
        return pref?.getBoolean(USER_TAG,false)?: false
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

    fun setRegisterUserGender(gender: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_GENDER,gender)
        editor?.apply()
    }

    fun setDayOfBirth(i: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(DAY_OF_BIRTH, i)
        editor?.apply()
    }

    fun isRegisterDOB(): Boolean{
        return pref?.getBoolean(DAY_OF_BIRTH,false)?: false
    }

    fun getUserId(): String {
        return pref?.getString(USER_ID,"")?: ""
    }

    fun getUserShortId(): String {
        return pref?.getString(USER_SHORT_ID,"")?: ""
    }

    fun saveUserStatus(isRegistered: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_REGISTERED,isRegistered)
    }
    fun setRegister(fistTime: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(FIRST_TIME_REGISTER,fistTime)
        editor?.apply()
    }
    fun setUserName(user: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_NAME,user)
        editor?.apply()
    }
    fun isRegisterUserName(): Boolean {
        return pref?.getBoolean(USER_NAME,false)?: false
    }
    fun setEmail(email: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(EMAIL,email)
        editor?.apply()
    }
    fun setPassWord(pass: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(PASSWORD,pass)
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