package com.ceslab.team7_ble_meet.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.os.ParcelUuid
import android.util.Log
import java.nio.charset.Charset
import java.util.*


@Suppress("NAME_SHADOWING")
class BleHandle {

    private val HEX_CHARS: CharArray = "0123456789ABCDEF".toCharArray()
    val TAG = "BLE_Handler"
    private val manu_id: Int = 0x6969

    fun init() {

    }

    fun advertise(data: ByteArray) {
        Log.d(TAG, "Advertise function called")
        val advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
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
        settings?.let {
            Log.d(TAG, "BleHandler: setting is not null")
        }
        Log.d(TAG, "BleHandler: data: $data")
        Log.d(TAG, "BleHandler: advertise callback: $advertisingCallback")
        advertiser.startAdvertising(settings, data, advertisingCallback)
    }

    fun discover() {
        Log.d(TAG, "Discover function called")
        val mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner;
        val mScanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val adType = result.scanRecord?.bytes?.get(1)?.toInt()
                val manuID_upper = result.scanRecord?.bytes?.get(2)?.toInt()
                val manuID_lower = result.scanRecord?.bytes?.get(3)?.toInt()
                if (adType == -1 && manuID_upper == 105 && manuID_lower == 105) {
                    Log.d(TAG, "Scan result:" + bytesToHexWhitespaceDelimited(result.scanRecord?.bytes))
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

//        val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString("00001234-0000-1000-8000-00805F9B34FB"))).build()
        val filter = ScanFilter.Builder().build()
        val filters = listOf(filter)
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }

    // Gets value in hexadecimal system
    fun bytesToHexWhitespaceDelimited(value: ByteArray?): String {
        if (value == null) {
            return ""
        }
        val hexChars = CharArray(value.size * 2)
        var v: Int
        for (j in value.indices) {
            v = value[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_CHARS[v ushr 4]
            hexChars[j * 2 + 1] = HEX_CHARS[v and 0x0F]
        }
        return String(hexChars)
    }
}
