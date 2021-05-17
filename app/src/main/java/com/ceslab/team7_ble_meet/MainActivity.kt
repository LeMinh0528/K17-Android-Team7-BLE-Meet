package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.Model.BleCharacter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import java.util.*
import kotlin.experimental.or


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var listDataReceived: ArrayList<ByteArray> = ArrayList()
    private var listDataDisplay: ArrayList<String> = ArrayList()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        handleDataReceived(byteArrayOf(0x1F,0x00,0x069,0x69).plus(setUpDataAdvertise(1720145)))
//        handleDataReceived(byteArrayOf(0x1F,0x00,0x069,0x69).plus(setUpDataAdvertise(8521552)))
//        handleDataReceived(byteArrayOf(0x1F,0x00,0x069,0x69).plus(setUpDataAdvertise(1234567)))
//        handleDataReceived(byteArrayOf(0x1F,0x00,0x069,0x69).plus(setUpDataAdvertise(98745341)))
//        Log.d(TAG, "list")
//        for (i in listDataReceived) {
//            Log.d(TAG, bytesToHex(i))
//        }
//        Log.d(TAG,"${getBitsFromPos(26,7, 8)}")
        convertByteArraytoString(setUpDataAdvertise(1720145)).toString()
    }

    private fun handleDataReceived(value: ByteArray) {
        var exist = false
        for (i in listDataReceived) {
            if (i[0] == value[4] && i[1] == value[5] && i[2] == value[6]) {
                Log.d(TAG, "exist")
                exist = true
            }
        }
        if (!exist) {
            listDataReceived.add(value.drop(4).toByteArray())
        }
    }

    private fun convertByteArraytoString(value: ByteArray) {
//        var output: String = "ff"
        val list_size = listOf(24,7,3,3,8,8,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7)

//        val list_size = listOf(24,7,3,3,8,8,4)
        var pos_byte = 0
        var pos_bit = 7
        for (i in list_size) {
            var temp = 0
            var bit_taken = 0
            while (bit_taken < i) {
                var bit_available = if (i - bit_taken >= pos_bit + 1) (pos_bit + 1) else (i - bit_taken)
                Log.d(TAG, "value: ${value[pos_byte]}")
                Log.d(TAG, "pos bit before: $pos_bit")
                Log.d(TAG, "bit available: $bit_available")
                Log.d(TAG, "bit taken to value: ${getBitsFromPos(value[pos_byte].toInt(), pos_bit, bit_available)}")
                Log.d(TAG, "data before: $temp")
                Log.d(TAG, "data after shift left: ${temp shl bit_available}")
                temp = (temp shl bit_available) or getBitsFromPos(value[pos_byte].toInt(), pos_bit, bit_available)
                Log.d(TAG, "data result: $temp")
                pos_bit -= bit_available
                Log.d(TAG, "pos bit after: $pos_bit")
                bit_taken += bit_available
                Log.d(TAG, "bit taken: $bit_taken")
                if (pos_bit < 0) {
                    pos_bit += 8
                    pos_byte++
                }
                Log.d(TAG, "pos bit reduce: $pos_bit")
                Log.d(TAG, "pos byte: $pos_byte")
            }
            Log.d(TAG, "------------------------result: $temp")
        }
    }

    private fun setUpDataAdvertise(id: Int): ByteArray {
        val age = 21
        val sex = 1
        val sexuality_orientation = 2
        val height = 110
        val weight = 30
        val zodiac = 1
        val outlook = arrayOf(1, 2, 3)
        val professorial = arrayOf(1, 2, 3)
        val character = arrayOf(1, 2, 3)
        val favorite = arrayOf(1, 2, 3)
        val hightlight = arrayOf(1, 2, 3)
        val raw_data = ArrayList<BleCharacter>().also {
            it.add(BleCharacter(id, 24))
            it.add(BleCharacter(age, 7))
            it.add(BleCharacter(sex, 3))
            it.add(BleCharacter(sexuality_orientation, 3))
            it.add(BleCharacter(height, 8))
            it.add(BleCharacter(weight, 8))
            it.add(BleCharacter(zodiac, 4))
            it.add(BleCharacter(outlook[0], 7))
            it.add(BleCharacter(outlook[1], 7))
            it.add(BleCharacter(outlook[2], 7))
            it.add(BleCharacter(professorial[0], 7))
            it.add(BleCharacter(professorial[1], 7))
            it.add(BleCharacter(professorial[2], 7))
            it.add(BleCharacter(character[0], 7))
            it.add(BleCharacter(character[1], 7))
            it.add(BleCharacter(character[2], 7))
            it.add(BleCharacter(favorite[0], 7))
            it.add(BleCharacter(favorite[1], 7))
            it.add(BleCharacter(favorite[2], 7))
            it.add(BleCharacter(hightlight[0], 7))
            it.add(BleCharacter(hightlight[1], 7))
            it.add(BleCharacter(hightlight[2], 7))
        }
        val data = ByteArray(24)
        var pos_bit = 0
        var pos_byte = 0
        for (i in raw_data) {
            while (i.size > 0) {
                var bit_needed = 8 - pos_bit
                if (i.size >= bit_needed) {
                    var bit_shift = i.size - bit_needed
                    data[pos_byte] = data[pos_byte] or (i.data ushr bit_shift).toByte()
                    i.data = getLastBits(i.data, bit_shift)
                    pos_bit += bit_needed
                    i.size -= bit_needed
                } else {
                    var bit_shift = bit_needed - i.size
                    data[pos_byte] = data[pos_byte] or (i.data shl bit_shift).toByte()
                    pos_bit += i.size
                    i.size = 0
                }
                if (pos_bit >= 8) {
                    pos_bit -= 8
                    pos_byte++
                }
            }
        }
        return data
    }
}