package com.ceslab.team7_ble_meet.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.bytesToHex


@Suppress("NAME_SHADOWING")
class BleHandle {

    private val TAG = "Ble_Lifecycle"
    private val manuId: Int = 0x6969

    private val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    private var advertiseSettings: AdvertiseSettings
    private var advertiseCallback: AdvertiseCallback

    private var scanCallback: ScanCallback
    private val filters: List<ScanFilter>
    private val settings: ScanSettings


    lateinit var backupData: ByteArray

    private val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    var bleDataScanned: MutableLiveData<ByteArray> = MutableLiveData()

    init {
        advertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setTimeout(0)
            .setConnectable(false)
            .build()


        advertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e(TAG, "BleHandler: Advertise Failed $errorCode")
            }
        }

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val adType = result.scanRecord?.bytes?.get(1)?.toInt()
                val manuIdUpper = result.scanRecord?.bytes?.get(2)?.toInt()
                val manuIdLower = result.scanRecord?.bytes?.get(3)?.toInt()
                if (adType == -1 && manuIdUpper == 105 && manuIdLower == 105) {
                    bleDataScanned.value = result.scanRecord!!.bytes
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
        filters = listOf(filter)

        settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    }

    fun startAdvertise(input: ByteArray) {
        stopAdvertise()
        backupData = input
        val data: AdvertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addManufacturerData(manuId, input)
            .build()
        advertiser.startAdvertising(advertiseSettings, data, advertiseCallback)
    }

    fun stopAdvertise(){
        advertiser.stopAdvertising(advertiseCallback)
    }

    fun startScan() {
//        stopScan()
        scanner.startScan(filters, settings, scanCallback)
    }

    fun stopScan(){
        Log.d(TAG, "Stop scan function called")
        scanner.stopScan(scanCallback)
    }
}
