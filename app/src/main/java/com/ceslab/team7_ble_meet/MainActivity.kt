package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.or


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var listDataDiscovered: ArrayList<ArrayList<Int>> = ArrayList()
    private var listDataDisplay: ArrayList<String> = ArrayList()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        var data = listOf(1720145, 22, 1, 2, 150, 60, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        handleDataDiscovered(byteArrayOf(0x1F, 0x00, 0x069, 0x69).plus(setUpDataAdvertise(data as MutableList<Int>)))
        data = listOf(1720146, 21, 2, 3, 160, 65, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        handleDataDiscovered(byteArrayOf(0x1F, 0x00, 0x069, 0x69).plus(setUpDataAdvertise(data as MutableList<Int>)))
        data = listOf(1720147, 20, 3, 4, 170, 70, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        handleDataDiscovered(byteArrayOf(0x1F, 0x00, 0x069, 0x69).plus(setUpDataAdvertise(data as MutableList<Int>)))
        data = listOf(1720148, 19, 4, 5, 180, 75, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        handleDataDiscovered(byteArrayOf(0x1F, 0x00, 0x069, 0x69).plus(setUpDataAdvertise(data as MutableList<Int>)))
        Log.d(TAG, "list")
        for (i in listDataDiscovered) {
            Log.d(TAG, setUpDataDisplay(i))
        }
//        Log.d(TAG, convertByteArraytoString(setUpDataAdvertise(1720145)))
    }

    private fun handleDataDiscovered(data: ByteArray) {
        val dataDiscovered = data.drop(4)
        val listOfCharacteristic = convertDataDiscovered(dataDiscovered.toByteArray())
        var exist = false
        for (dataDiscovered in listDataDiscovered) {
            if (dataDiscovered[0] == listOfCharacteristic[0]) {
                Log.d(TAG, "exist")
                exist = true
                break
            }
        }
        if (!exist) {
            Log.d(TAG, "do not exist")
            val filter = listOf(18, 30, 1, 2, 3, 1, 2, 3, 160, 180, 50, 80, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
            if (checkCharacteristic(listOfCharacteristic.drop(1) as MutableList<Int>, filter as MutableList<Int>)) {
                Log.d(TAG, "matched")
                listDataDiscovered.add(listOfCharacteristic)
                listDataDisplay.add(setUpDataDisplay(listOfCharacteristic))
//                arrayAdapter.notifyDataSetChanged()
            }
            else{
                Log.d(TAG, "no matched")
            }
        }
    }

    private fun checkCharacteristic(input: MutableList<Int>, filter: MutableList<Int>): Boolean {
        Log.d(TAG, "start to check")
        var score = 0
        if (filter[0] < input[0] && input[0] < filter[1]) score++
        for (i in 2..4) {
            if(input[1] == filter[i]) score++
        }
        for (i in 5..7) {
            if(input[2] == filter[i]) score++
        }
        if (filter[8] < input[3] && input[3] < filter[9]) score++
        if (filter[10] < input[4] && input[4] < filter[11]) score++
        for (i in 12..13) {
            if(input[5] == filter[i]) score++
        }
        for (i in 6..8) {
            for (j in 14..16) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 9..11) {
            for (j in 17..19) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 12..14) {
            for (j in 20..22) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 15..17) {
            for (j in 23..25) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 18..20) {
            for (j in 26..28) {
                if (input[i] == filter[j]) score++
            }
        }
        return score >= 3
    }

    private fun convertDataDiscovered(data: ByteArray): ArrayList<Int> {
        val rawData = ArrayList<Int>()
        val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
        var posByte = 0
        var posBit = 7
        for (i in sizeEachCharacter) {
            var temp = 0
            var bitTaken = 0
            while (bitTaken < i) {
                val bitAvailable = if (i - bitTaken >= posBit + 1) (posBit + 1) else (i - bitTaken)
                temp = (temp shl bitAvailable) or getBitsFromPos(
                    data[posByte].toInt(),
                    posBit,
                    bitAvailable
                )
                posBit -= bitAvailable
                bitTaken += bitAvailable
                if (posBit < 0) {
                    posBit += 8
                    posByte++
                }
            }
            rawData.add(temp)
        }
        return rawData
    }

    private fun setUpDataDisplay(raw_data: ArrayList<Int>): String {
        return "ID: ${raw_data[0]}\n" +
                "Age: ${raw_data[1]}\n" +
                "Sex: ${Characteristic.sex[raw_data[2]]}\n" +
                "Sexuality Orientation: ${Characteristic.sexualOrientation[raw_data[3]]}\n" +
                "Tall: ${raw_data[4]}\n" +
                "Weight: ${raw_data[5]}\n" +
                "Zodiac: ${Characteristic.zodiac[raw_data[6]]}\n" +
                "Outlook: ${Characteristic.outlook[raw_data[7]]} - ${Characteristic.outlook[raw_data[8]]} - ${Characteristic.outlook[raw_data[9]]}\n" +
                "Job: ${Characteristic.job[raw_data[10]]} - ${Characteristic.job[raw_data[11]]} - ${Characteristic.job[raw_data[12]]}\n" +
                "Character: ${Characteristic.character[raw_data[13]]} - ${Characteristic.character[raw_data[14]]} - ${Characteristic.character[raw_data[15]]}\n" +
                "Favorite: ${Characteristic.hightlight[raw_data[16]]} - ${Characteristic.hightlight[raw_data[17]]} - ${Characteristic.hightlight[raw_data[18]]}\n" +
                "Hightlight: ${Characteristic.hightlight[raw_data[19]]} - ${Characteristic.hightlight[raw_data[20]]} - ${Characteristic.hightlight[raw_data[21]]}\n"
    }

    private fun setUpDataAdvertise(input: MutableList<Int>): ByteArray {
        val output = ByteArray(24)
        val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
        var posBit = 0
        var posByte = 0
        for (i in 0 until input.size) {
            while (sizeEachCharacter[i] > 0) {
                val bitNeed = 8 - posBit
                if (sizeEachCharacter[i] >= bitNeed) {
                    val bitShift = sizeEachCharacter[i] - bitNeed
                    output[posByte] = output[posByte] or (input[i] ushr bitShift).toByte()
                    input[i] = getLastBits(input[i], bitShift)
                    posBit += bitNeed
                    sizeEachCharacter[i] -= bitNeed
                } else {
                    val bitShift = bitNeed - sizeEachCharacter[i]
                    output[posByte] = output[posByte] or (input[i] shl bitShift).toByte()
                    posBit += sizeEachCharacter[i]
                    sizeEachCharacter[i] = 0
                }
                if (posBit >= 8) {
                    posBit -= 8
                    posByte++
                }
            }
        }
        return output
    }
}