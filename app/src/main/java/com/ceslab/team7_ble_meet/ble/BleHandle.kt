package com.ceslab.team7_ble_meet.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.util.Log
import androidx.lifecycle.MutableLiveData


@Suppress("NAME_SHADOWING")
class BleHandle {


    private val TAG = "Ble_Lifecycle"
    private val manuId: Int = 0x6969

    private val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    private val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    var bleDataScanned: MutableLiveData<ByteArray> = MutableLiveData()

    fun advertise(data: ByteArray) {
        Log.d(TAG, "Advertise function called")
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setTimeout(0)
            .setConnectable(false)
            .build()
        val data: AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addManufacturerData(manuId, data)
            .build()

        val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
                Log.d(TAG, "BleHandler: Advertise Successfully")
                Log.d(TAG, "BLEHandler: $data")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e(TAG, "BleHandler: Advertise Failed $errorCode")
            }
        }
        Log.d(TAG, "BleHandler: advertise callback: $advertisingCallback")
        advertiser.startAdvertising(settings, data, advertisingCallback)
    }

    fun scan() {
        Log.d(TAG, "Scan function called")
        val scanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val adType = result.scanRecord?.bytes?.get(1)?.toInt()
                val manuIdUpper = result.scanRecord?.bytes?.get(2)?.toInt()
                val manuIdLower = result.scanRecord?.bytes?.get(3)?.toInt()
                if (adType == -1 && manuIdUpper == 105 && manuIdLower == 105) {
//                    Log.d(TAG, "Scan result:" + bytesToHex(result.scanRecord!!.bytes))
                    bleDataScanned.value = result.scanRecord!!.bytes
//                    Log.d(TAG, "Scan result DATA received:" + bleDataReceived.value)
                }
            }

            override fun onBatchScanResults(results: List<ScanResult?>?) {
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e(TAG, "Scan failed: $errorCode")
            }
        }

        val filter = ScanFilter.Builder().build()
        val filters = listOf(filter)
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build()
        scanner.startScan(filters, settings, scanCallback)
    }
}
