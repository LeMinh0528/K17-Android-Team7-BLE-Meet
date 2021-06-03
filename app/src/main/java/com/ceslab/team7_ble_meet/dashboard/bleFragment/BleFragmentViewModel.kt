package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.getLastBits
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.BleService
import java.util.*
import kotlin.experimental.or


class BleFragmentViewModel() : ViewModel() {

    private val TAG = "Ble_Lifecycle"

    // Initializes Bluetooth adapter.
    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothOn: Boolean = false
    var isRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    var isBleDataScannedDisplay: MutableLiveData<Boolean> = MutableLiveData(false)

    private var instance = UsersFireStoreHandler()
    private var characteristicUser: List<Int> = listOf(0,0,0,0,0,0,0,0,0)
    private lateinit var characteristicUser2ByteArray: ByteArray
    var dataReady = false

    init{
        Log.d(TAG, "BleFragmentViewModel init")
        instance.userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener { data ->
                if(data != null){
                    //set age
                    val current: Int = Calendar.getInstance().get(Calendar.YEAR);
                    val yearOfBirth = data["DayOfBirth"].toString().split("/")[2].toInt()
                    val age = (current - yearOfBirth)

                    val sex = if(data["Gender"].toString() == "Male") 0 else 1

                    val genderOrientation = if(data["Interested"].toString() == "Male") 0 else {
                        if (data["Interested"].toString() != "Female") 1 else 2
                    }

                    val list: List<String> = data["Tag"] as List<String>
                    val a: Int = Characteristic.Tag.filterValues { it == list[0]}.keys.first()
                    val b: Int? = Characteristic.Tag.filterValues { it == list[1]}.keys.first()
                    val c: Int? = Characteristic.Tag.filterValues { it == list[2]}.keys.first()
                    val d: Int? = Characteristic.Tag.filterValues { it == list[3]}.keys.first()
                    val e: Int? = Characteristic.Tag.filterValues { it == list[4]}.keys.first()

                    if (a != null && b != null && c != null&& d != null&& e != null) {
                        characteristicUser = listOf(KeyValueDB.getUserShortId().toInt(), age, sex, genderOrientation, a.toInt(), b.toInt(), c.toInt(), d.toInt(), e.toInt())
                        characteristicUser2ByteArray = convertListCharacteristic2ByteArray(
                            characteristicUser as MutableList<Int>
                        )
                        dataReady = true
                    }
                    Log.d(TAG, characteristicUser.toString())
                }
            }
            .addOnFailureListener{
            }
    }

    fun changeBluetoothStatus(isChecked: Boolean) {
        if (isChecked) {
            bluetoothAdapter.enable()
            isBluetoothOn = true
            Log.d(TAG, "BLE is enable")
        } else {
            bluetoothAdapter.disable()
            isBluetoothOn = false
            Log.d(TAG, "BLE is disable")
        }
    }

    fun findFriend(context: Context) {
        if (isRunning.value == true) {
            stopFindFriend(context)
        } else {
            startFindFriend(context)
        }
    }

    fun startFindFriend(context: Context) {
        Log.d(TAG,characteristicUser.toString())
        if (bluetoothAdapter.isEnabled) {
            if(dataReady){
                val intent = Intent(context, BleService::class.java)
                val bundle = Bundle()
                bundle.putByteArray("dataFromBleViewModel2BleService", characteristicUser2ByteArray)
                intent.putExtras(bundle)
                context.startService(intent)
                isRunning.value = true
            } else {
                Toast.makeText(context, "Data is not ready, wait a moment", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
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

    fun stopFindFriend(context: Context) {
        val intent = Intent(context, BleService::class.java)
        context.stopService(intent)
        isRunning.value = false
    }

    fun setUpListDataScanned(context: Context, lifecycleOwner: LifecycleOwner) {
        isBleDataScannedDisplay.value =
            BleDataScannedDataBase.getDatabase(context).bleDataScannedDao().getUserDiscover()
                .isNotEmpty()

        BleDataScannedDataBase.getDatabase(context).isDataChanged.observe(lifecycleOwner,
            {
                isBleDataScannedDisplay.value = it
            })
    }

    fun deleteBleDataScanned(context: Context){
        BleDataScannedDataBase.getDatabase(context).bleDataScannedDao().deleteAll()
        isBleDataScannedDisplay.value = false
    }
}
