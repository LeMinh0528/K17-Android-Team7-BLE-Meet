package com.ceslab.team7_ble_meet.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.bytesToHex
import java.util.*


@Suppress("NAME_SHADOWING")
class BleHandle {

    val TAG = "BLE_Handler"
    private val manu_id: Int = 0x6969

    var bleDataReceived: MutableLiveData<ByteArray> = MutableLiveData()

    fun advertise(data: ByteArray) {
        Log.d(TAG, "Advertise function called")
        val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setTimeout(0)
            .setConnectable(false)
            .build()
        val data: AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addManufacturerData(manu_id, data)
            .build()

        val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.d(TAG, "BleHandler: Advertise Successfully")
                Log.d(TAG, "BLEHandler: $data")
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "BleHandler: Advertise Failed $errorCode")
                super.onStartFailure(errorCode)
            }
        }
        Log.d(TAG, "BleHandler: advertise callback: $advertisingCallback")
        advertiser.startAdvertising(settings, data, advertisingCallback)
    }

    fun discover() {
        Log.d(TAG, "Discover function called")
        val mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
        val mScanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val adType = result.scanRecord?.bytes?.get(1)?.toInt()
                val manuID_upper = result.scanRecord?.bytes?.get(2)?.toInt()
                val manuID_lower = result.scanRecord?.bytes?.get(3)?.toInt()
                if (adType == -1 && manuID_upper == 105 && manuID_lower == 105) {
                    Log.d(TAG, "Scan result:" + bytesToHex(result.scanRecord!!.bytes))
                    bleDataReceived.value = result.scanRecord!!.bytes
                    Log.d(TAG, "Scan result DATA received:" + bleDataReceived.value)
                }
            }

            override fun onBatchScanResults(results: List<ScanResult?>?) {
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "Discovery onScanFailed: $errorCode")
                super.onScanFailed(errorCode)
            }
        }

        val filter = ScanFilter.Builder().build()
        val filters = listOf(filter)
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }
}
