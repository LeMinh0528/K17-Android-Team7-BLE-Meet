package com.ceslab.team7_ble_meet

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test

    private fun getLastBits(value: Int, number: Int): Int{
        var temp = 0
        var number = number
        var value = value
        while(number > 0){
            temp = (temp shl 1) + 1
            number -= 1
        }
        value = value and temp
        return value
    }
}