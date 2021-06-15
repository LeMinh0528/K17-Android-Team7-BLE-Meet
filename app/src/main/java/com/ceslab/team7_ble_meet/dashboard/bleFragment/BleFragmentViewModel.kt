package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.app.ActivityManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.service.BleService
import com.google.firebase.firestore.ListenerRegistration


class BleFragmentViewModel() : ViewModel() {
    private val TAG = "Ble_Lifecycle"

    var context: Context? = null

    // Init Bluetooth adapter controlling state of bluetooth
    var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var isBluetoothOn: Boolean = false

    // Init state of service (running or not)
    var isRunning: MutableLiveData<Boolean> = MutableLiveData()

    var isBleDataScannedDisplay: MutableLiveData<Boolean> = MutableLiveData(false)

    lateinit var listener: ListenerRegistration

    fun isMyServiceRunning(){
        Log.d(TAG, "Context: $context")
        val manager = context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
        Log.d(TAG, "Manager: $manager")
        if (manager != null) {
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                Log.d(TAG,"Ble Fragment View Model: check service 2: $service")
                if ("com.ceslab.team7_ble_meet.service.BleService" == service.service.className) {
                    Log.d(TAG, "Ble Fragment View Model: service is running 2")
                    isRunning.value = true
                }
            }
        }
        Log.d(TAG, "service is no running")
        isRunning.value = false
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
        if (bluetoothAdapter.isEnabled) {
            val intent = Intent(context, BleService::class.java)
            context?.startService(intent)
            isRunning.value = true
        } else {
            Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
        }
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
