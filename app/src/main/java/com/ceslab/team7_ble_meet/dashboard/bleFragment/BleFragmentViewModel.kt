package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.service.BleService

class BleFragmentViewModel() : ViewModel() {

    val TAG: String = "Ble_ViewModel"
    var isBluetoothOn: Boolean = false
    var isRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    var isBleDataScannedDisplay: MutableLiveData<Boolean> = MutableLiveData(false)

    // Initializes Bluetooth adapter.
    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
        if (bluetoothAdapter.isEnabled) {
            val intent = Intent(context, BleService::class.java)
            context.startService(intent)
            isRunning.value = true
        } else {
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopFindFriend(context: Context) {
        if (bluetoothAdapter.isEnabled) {
            val intent = Intent(context, BleService::class.java)
            context.stopService(intent)
            isRunning.value = false
        } else {
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
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
