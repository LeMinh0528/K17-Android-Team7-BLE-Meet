package com.ceslab.team7_ble_meet.service

import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.ble.BleDataScanned
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.firestore.ListenerRegistration
import java.util.*
//import com.ceslab.team7_ble_meet.model.BleDataScanned
import kotlin.collections.ArrayList
import kotlin.experimental.or


class BleService: LifecycleService() {
    private val TAG = "Ble_Lifecycle"
    val CHANNEL_ID = "channel_ble"

    private lateinit var bleHandle: BleHandle

    //Instance to use Firebase
    private var instance = UsersFireStoreHandler()

    private var characteristicUser: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    lateinit var characteristicUser2ByteArray: ByteArray
    private var filter: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    lateinit var listener: ListenerRegistration

    override fun onCreate() {
        super.onCreate()
        bleHandle = BleHandle()
        bleHandle.bleDataScanned.observe(this, {
            Log.d(TAG, "Ble Service: ble data received")
            handleDataDiscovered(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        setUpData2Advertise()
        sendBleNotification()
        return START_STICKY
    }

    private fun setUpData2Advertise() {
        instance.userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener { data ->
                //Add id
                characteristicUser[0] = KeyValueDB.getUserShortId().toInt()

                if (data != null) {
                    //Add age
                    val current: Int = Calendar.getInstance().get(Calendar.YEAR);
                    val yearOfBirth = data["DayOfBirth"].toString().split("/")[2].toInt()
                    val age = (current - yearOfBirth)
                    characteristicUser[1] = age

                    //Add sex key
                    val sex = if (data["Gender"].toString() == "Male") 0 else 1
                    characteristicUser[2] = sex

                    //Add sex orientation key
                    val genderOrientation = if (data["Interested"].toString() == "Male") 0 else {
                        if (data["Interested"].toString() != "Female") 1 else 2
                    }
                    characteristicUser[3] = genderOrientation
                    characteristicUser2ByteArray = convertListCharacteristic2ByteArray(characteristicUser)
                }
                //Add TAG
                listener = UsersFireStoreHandler().setTagChangedListener() { listTag ->
                    for (i in 0 until listTag.size) {
                        val digit: Int = Characteristic.Tag.filterValues {
                            it == listTag[i]
                        }.keys.first()
                        characteristicUser[4 + i] = digit
                    }
                    filter = characteristicUser
//                    Log.d(TAG,"Ble Service: data from firebase: $characteristicUser")
//                    Log.d(TAG,"Ble Service: filter from firebase: $filter")
                    characteristicUser2ByteArray = convertListCharacteristic2ByteArray(characteristicUser)
                    bleHandle.startAdvertise(characteristicUser2ByteArray)
                }
            }
            .addOnFailureListener {
            }
        bleHandle.startScan()
    }

    private fun convertListCharacteristic2ByteArray(input: MutableList<Int>): ByteArray {
        val output = ByteArray(24)
        val sizeEachCharacter = mutableListOf(24, 8, 1, 2, 8, 8, 8, 8, 8)
        var posBit = 0
        var posByte = 0
        for (i in 0 until input.size) {
            var value = input[i]
            while (sizeEachCharacter[i] > 0) {
                val bitNeed = 8 - posBit
                if (sizeEachCharacter[i] >= bitNeed) {
                    val bitShift = sizeEachCharacter[i] - bitNeed
                    output[posByte] = output[posByte] or (value ushr bitShift).toByte()
                    value = getLastBits(value, bitShift)
                    posBit += bitNeed
                    sizeEachCharacter[i] -= bitNeed
                } else {
                    val bitShift = bitNeed - sizeEachCharacter[i]
                    output[posByte] = output[posByte] or (value shl bitShift).toByte()
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

    private fun sendBleNotification() {
        val bleNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BLE MEET")
            .setContentText("Find friend is running")
            .setSmallIcon(R.drawable.ic_layout)
            .build()

        startForeground(1, bleNotification)
    }

    private fun handleDataDiscovered(data: ByteArray) {
        Log.d(TAG,"handle data: ${bytesToHex(data)}")
        val dataDiscovered = data.drop(4)
        val listOfCharacteristic = convertByteArray2ListCharacteristic(dataDiscovered.toByteArray())
        if (checkCharacteristic(listOfCharacteristic, filter)) {
            addUser(listOfCharacteristic)
        }
    }

    private fun addUser(list: List<Int>) {
        BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().insert(
            BleDataScanned(list)
        )
        BleDataScannedDataBase.getDatabase(this).dataChanged()
    }

    private fun convertByteArray2ListCharacteristic(data: ByteArray): List<Int> {
        val rawData = ArrayList<Int>()
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
        if(input[2] == filter[3] || filter[3] == 2){
            for (i in 4..8){
                for (j in 4..8){
                    if(input[i] == filter[j]){
                        score ++
                    }
                }
            }
        }
        Log.d(TAG,"input: $input")
        Log.d(TAG,"filter: $filter")
        Log.d(TAG,"score $score")
        return score >= 3
    }

    override fun onDestroy() {
        super.onDestroy()
        bleHandle.stopAdvertise()
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
