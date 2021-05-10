package com.ceslab.team7_ble_meet

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var connect: BleHandle
    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager :BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        connect = BleHandle()
        checkPermissions()
        binding.apply {
            btnStartAdver.setOnClickListener {
                if (bluetoothAdapter.isEnabled) {
                    Log.d(TAG, "BLE is enable")
                    val data = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
                    connect.advertise(data)
                } else {
                    Log.d(TAG, "BLE is no enable")
                    Toast.makeText(this@MainActivity, "Turn on Bluetooth!", Toast.LENGTH_SHORT).show()
                }
            }
            btnStartDiscover.setOnClickListener {
                if (bluetoothAdapter.isEnabled) {
                    Log.d(TAG, "BLE is enable")
                    connect.discover()
                } else {
                    Log.d(TAG, "BLE is no enable")
                    Toast.makeText(this@MainActivity, "Turn on Bluetooth!", Toast.LENGTH_SHORT).show()
                }
            }
            btnTurnOnBLE.setOnClickListener {
                bluetoothAdapter.enable()
                Log.d(TAG, "BLE is enable")
                Toast.makeText(this@MainActivity, "BLE turned on!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions() {
        val reqPermissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (reqPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, reqPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty())) {
                    grantResults.forEach { result ->
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            finish()
                            return
                        }
                    }
                }
            }
        }
    }
}