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
import com.google.firebase.firestore.ListenerRegistration


class BleFragmentViewModel() : ViewModel() {
    private val TAG = "Ble_Lifecycle"

    var context: Context? = null

    // Init Bluetooth adapter controlling state of bluetooth
    var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothOn: Boolean = false

    // Init state of service (running or not)
    var isRunning: MutableLiveData<Boolean> = MutableLiveData(false)

    var isBleDataScannedDisplay: MutableLiveData<Boolean> = MutableLiveData(false)

    //Instance to use Firebase
    private var instance = UsersFireStoreHandler()

    private var characteristicUser: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    lateinit var characteristicUser2ByteArray: ByteArray

    lateinit var listener: ListenerRegistration

    init {
        setUpData2Advertise()
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

    fun findFriend() {
        if (isRunning.value == true) {
            stopFindFriend()
        } else {
            startFindFriend()
        }
    }

    fun stopFindFriend() {
        val intent = Intent(context, BleService::class.java)
        context?.stopService(intent)
        isRunning.value = false
    }

    fun startFindFriend() {
        Log.d(TAG, "BleFragmentViewModel: start find friend: $characteristicUser")
        if (bluetoothAdapter.isEnabled) {
            val intent = Intent(context, BleService::class.java)
            val bundle = Bundle()
            bundle.putByteArray("dataFromBleViewModel2BleService", characteristicUser2ByteArray)
            intent.putExtras(bundle)
            context?.startService(intent)
            isRunning.value = true
        } else {
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
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

                    Log.d(TAG, "BleFragmentViewModel: set up data to advertise $characteristicUser")
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
                    characteristicUser2ByteArray = convertListCharacteristic2ByteArray(characteristicUser)
                    context?.let {
                        if (isRunning.value == true) {
                            startFindFriend()
                        }
                        deleteBleDataScanned(it)
                    }
                }
            }
            .addOnFailureListener {
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

    fun setUpListDataScanned(context: Context, lifecycleOwner: LifecycleOwner) {
        isBleDataScannedDisplay.value = BleDataScannedDataBase.getDatabase(context)
            .bleDataScannedDao()
            .getUserDiscover()
            .isNotEmpty()

        BleDataScannedDataBase.getDatabase(context).isDataChanged.observe(lifecycleOwner,
            {
                isBleDataScannedDisplay.value = BleDataScannedDataBase.getDatabase(context)
                    .bleDataScannedDao()
                    .getUserDiscover()
                    .isNotEmpty()
            })
    }

    fun deleteBleDataScanned(context: Context) {
        BleDataScannedDataBase.getDatabase(context).bleDataScannedDao().deleteAll()
        isBleDataScannedDisplay.value = false
    }
}
