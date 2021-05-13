package com.ceslab.team7_ble_meet

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String) = Toast.makeText(this,message,Toast.LENGTH_LONG).show()
fun bytesToHex(value: ByteArray?) : String {
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
