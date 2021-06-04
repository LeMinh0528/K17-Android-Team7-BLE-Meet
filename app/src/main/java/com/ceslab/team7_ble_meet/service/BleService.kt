package com.ceslab.team7_ble_meet.service

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.model.BleDataScanned
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.or


class BleService: LifecycleService() {
    private val TAG = "Ble_Lifecycle"
    val CHANNEL_ID = "channel_ble"

    private lateinit var bleHandle: BleHandle
    private lateinit var target: List<Int>

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BleService on create")
        bleHandle = BleHandle()
        bleHandle.bleDataScanned.observe(this, {
            Log.d(TAG, "BleService ble data scanned")
            handleDataDiscovered(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "BleService on start command")
        if (intent != null) {
            intent.getByteArrayExtra("dataFromBleViewModel2BleService")?.let {
                bleHandle.advertise(
                    it
                )
                target = convertDataDiscovered(it)
                Log.d(TAG, it.toString())
                Log.d(TAG, "target: $target")
            }
        }
        bleHandle.scan()
        sendBleNotification()
        return START_STICKY
    }

    private fun sendBleNotification() {
        val bleNotification = NotificationCompat.Builder(this, "pushNotificationChannel")
            .setContentTitle("BLE MEET")
            .setContentText("Find friend is running")
            .setSmallIcon(R.drawable.ic_both)
            .build()

        startForeground(1, bleNotification)
    }

    private fun handleDataDiscovered(data: ByteArray) {
        Log.d(TAG, bytesToHex(data))
        val dataDiscovered = data.drop(4)
        val listOfCharacteristic = convertDataDiscovered(dataDiscovered.toByteArray())
        if (checkCharacteristic(listOfCharacteristic, target)) {
            addUser(listOfCharacteristic)
        }
    }

    private fun addUser(list: List<Int>) {
        BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().insert(
            BleDataScanned(list)
        )
        BleDataScannedDataBase.getDatabase(this).dataChanged()
    }

    private fun convertDataDiscovered(data: ByteArray): List<Int> {
        val rawData = ArrayList<Int>()
        Log.d(TAG, bytesToHex(data))
        val sizeEachCharacter = mutableListOf(24, 8, 1, 2, 8, 8, 8, 8, 8)
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

    private fun checkCharacteristic(input: List<Int>, filter: List<Int>): Boolean {
        var score = 0
        for (i in 4..8){
            for (j in 4..8){
                if(input[i] == filter[j]){
                    score ++
                }
            }
        }
        return score >= 3
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "stop ble service")
    }
}

//characteristicUser = listOf(1720148, 25, 1, 1, 165, 60, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17)
//target = listOf(18, 30, 1, 2, 3, 1, 2, 3, 160, 180, 50, 80, 1, 2, 3, 4, 5, 6, 7, 8, 9,10, 11, 12, 13, 14, 15, 16, 17, 18)
//val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)

//private fun setUpDataAdvertise(input: MutableList<Int>): ByteArray {
//    val output = ByteArray(24)
//    val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
//    var posBit = 0
//    var posByte = 0
//    for (i in 0 until input.size) {
//        var value = input[i]
//        while (sizeEachCharacter[i] > 0) {
//            val bitNeed = 8 - posBit
//            if (sizeEachCharacter[i] >= bitNeed) {
//                val bitShift = sizeEachCharacter[i] - bitNeed
//                output[posByte] = output[posByte] or (value ushr bitShift).toByte()
//                value = getLastBits(value, bitShift)
//                posBit += bitNeed
//                sizeEachCharacter[i] -= bitNeed
//            } else {
//                val bitShift = bitNeed - sizeEachCharacter[i]
//                output[posByte] = output[posByte] or (value shl bitShift).toByte()
//                posBit += sizeEachCharacter[i]
//                sizeEachCharacter[i] = 0
//            }
//            if (posBit >= 8) {
//                posBit -= 8
//                posByte++
//            }
//        }
//    }
//    return output
//}

//private fun checkCharacteristic(input: MutableList<Int>, filter: MutableList<Int>): Boolean {
//    var score = 0
//    if (filter[0] < input[0] && input[0] < filter[1]) score++
//    for (i in 2..4) {
//        if(input[1] == filter[i]) score++
//    }
//    for (i in 5..7) {
//        if(input[2] == filter[i]) score++
//    }
//    if (filter[8] < input[3] && input[3] < filter[9]) score++
//    if (filter[10] < input[4] && input[4] < filter[11]) score++
//    for (i in 12..13) {
//        if(input[5] == filter[i]) score++
//    }
//    for (i in 6..8) {
//        for (j in 14..16) {
//            if (input[i] == filter[j]) score++
//        }
//    }
//    for (i in 9..11) {
//        for (j in 17..19) {
//            if (input[i] == filter[j]) score++
//        }
//    }
//    for (i in 12..14) {
//        for (j in 20..22) {
//            if (input[i] == filter[j]) score++
//        }
//    }
//    for (i in 15..17) {
//        for (j in 23..25) {
//            if (input[i] == filter[j]) score++
//        }
//    }
//    for (i in 18..20) {
//        for (j in 26..28) {
//            if (input[i] == filter[j]) score++
//        }
//    }
//    return score >= 3
//}