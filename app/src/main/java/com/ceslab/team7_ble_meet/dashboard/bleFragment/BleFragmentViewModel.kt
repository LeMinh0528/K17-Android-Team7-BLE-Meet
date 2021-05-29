package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.repository.BleDataScannedRepository

class BleFragmentViewModel(val bleDataRepo: BleDataScannedRepository) : ViewModel() {

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    init{
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun turnOnBle(){
        if(!bluetoothAdapter.isEnabled){
            bluetoothAdapter.enable()
        }
    }
}