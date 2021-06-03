@file:Suppress("NAME_SHADOWING")

package com.ceslab.team7_ble_meet

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import java.util.regex.Pattern
import kotlin.random.Random

fun Context.toast(message: String) = Toast.makeText(this,message,Toast.LENGTH_LONG).show()
fun bytesToHex(value: ByteArray) : String {
    val HEX_CHARS: CharArray = "0123456789ABCDEF".toCharArray()
    if (value == null) {
        return ""
    }
    val hexChars = CharArray(value.size * 2)
    var v: Int
    for (j in value.indices) {
        v = value[j].toInt() and 0xFF
        hexChars[j * 2] = HEX_CHARS[v ushr 4]
        hexChars[j * 2 + 1] = HEX_CHARS[v and 0x0F]
    }
    return String(hexChars)
}
fun getLastBits(value: Int, number: Int): Int{
    var temp = 0
    var number = number
    val value = value
    while(number > 0){
        temp = (temp shl 1) + 1
        number -= 1
    }
    return value and temp
}
fun getBitsFromPos(value: Int, pos: Int, n: Int): Int{
    val bitShift = pos + 1 - n
    var value = value
    var n = n
    var temp = 0
    value = value ushr bitShift
    while(n > 0){
        temp = (temp shl 1) + 1
        n --
    }
    return value and temp
}
fun isValidPasswordFormat(password: String): Boolean {
    val passwordREGEX = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[!@#$%^&*()])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$"
    );
    return passwordREGEX.matcher(password).matches()
}
fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun addZeroNum(num: Int): String{
    if(num in 0..9){
        return "0000000$num"
    }else if(num in 10..99){
        return "000000$num"
    }else if(num in 100..999){
        return "00000$num"
    }else if(num in 1000..9999){
        return "0000$num"
    }else if(num in 10000..99999){
        return "000$num"
    }else if(num in 100000..999999){
        return "00$num"
    }else if(num in 1000000..9999999){
        return "0$num"
    }else{
        return "$num"
    }
}

fun generateUniqueID(): String{
    var random = (0..16777215).random()
    var result = ""
    if(random in 0..9){
        result = "0000000$random"
    }else if(random in 10..99){
        result = "000000$random"
    }else if(random in 100..999){
        result = "00000$random"
    }else if(random in 1000..9999){
        result = "0000$random"
    }else if(random in 10000..99999){
        result = "000$random"
    }else if(random in 100000..999999){
        result = "00$random"
    }else if(random in 1000000..9999999){
        result = "0$random"
    }else result = "$random"
    return result
}