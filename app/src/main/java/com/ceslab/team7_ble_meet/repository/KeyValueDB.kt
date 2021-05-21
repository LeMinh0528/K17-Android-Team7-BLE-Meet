package com.ceslab.team7_ble_meet.repository

import android.content.Context
import android.content.SharedPreferences

object KeyValueDB {

    private const val USER_ID = "user_id"
    private const val USER_REGISTERED = "user_registered"
    private const val FIRST_TIME_REGISTER = "first_time_register"
    private const val USER_NAME = "user_name"
    private const val EMAIL = "email"
    private const val PASSWORD = "password"
    private const val USER_GENDER = "user_gender"
    private const val USER_TAG = "user_tag"
    private const val USER_INTERESTED = "user_interested"

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


    fun getUserGender(): String {
        return pref?.getString(USER_GENDER,"")?: ""
    }

    fun setUserGender(gender: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_GENDER,gender)
        editor?.apply()
    }

    fun getUserInterested(): String {
        return pref?.getString(USER_GENDER,"")?: ""
    }

    fun setUserInterested(gender: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_GENDER,gender)
        editor?.apply()
    }

    fun getUserId(): String {
        return pref?.getString(USER_ID,"")?: ""
    }

    fun saveUserStatus(isRegistered: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(USER_REGISTERED,isRegistered)
    }
    fun setFirstTimeRegister(fistTime: Boolean){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putBoolean(FIRST_TIME_REGISTER,fistTime)
        editor?.apply()
    }
    fun setUserName(user: String){
        val editor: SharedPreferences.Editor? = pref?.edit()
        editor?.putString(USER_NAME,user)
        editor?.apply()
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
    fun isFirstTimeRegister(): Boolean{
        return pref?.getBoolean(FIRST_TIME_REGISTER,false)?: false
    }

    fun isRegistered(): Boolean{
        return pref?.getBoolean(USER_REGISTERED,false)?: false
    }
}