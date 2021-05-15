package com.ceslab.team7_ble_meet.dashboard

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.Model.BleCharacter
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.bytesToHex
import com.ceslab.team7_ble_meet.databinding.FragmentConnectBinding
import com.ceslab.team7_ble_meet.getLastBits
import com.ceslab.team7_ble_meet.toast
import kotlin.experimental.or


class ConnectFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }
    private val TAG = "ConnectFragment"
    private lateinit var binding: FragmentConnectBinding

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var connect: BleHandle

    private var listDataReceived: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        checkPermissions()
        setUpBle()
        setUpUI(inflater, container)

        return binding.root
    }

    private fun setUpBle(){
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)

        bluetoothManager = activity?.let { getSystemService(it, BluetoothManager::class.java) }!!
        bluetoothAdapter = bluetoothManager.adapter
        connect = BleHandle()
    }

    private fun setUpUI(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, container, false)
        binding.apply {
            if(bluetoothAdapter.isEnabled){
                swTurnOnOffBLE.isChecked = true
            }
            swTurnOnOffBLE.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    bluetoothAdapter.enable()
                    Log.d(TAG, "BLE is enable")
                } else{
                    bluetoothAdapter.disable()
                    Log.d(TAG, "BLE is disable")
                }
            }
            btnFindFriend.setOnClickListener {
                if (bluetoothAdapter.isEnabled) {
                    connect.advertise(setUpDataAdvertise())
                    connect.discover()
                } else {
                    Toast.makeText(activity, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
                }
                if(vRipple.isRippleAnimationRunning) vRipple.stopRippleAnimation() else vRipple.startRippleAnimation()
            }

            arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listDataReceived
            )
            binding.listViewBleDataReceived.adapter = arrayAdapter
            connect.bleDataReceived.observe(viewLifecycleOwner, {
                requireContext().toast("Receive data")
                if (!binding.btnFindFriend.isGone) {
                    binding.btnFindFriend.isGone = true
                    binding.vRipple.isGone = true
                }
                bleDataReceivedHandler(it)
            })
        }
    }
    private fun setUpDataAdvertise(): ByteArray {
        val id = 1720145
        val age = 21
        val sex = 1
        val sexuality_orientation = 2
        val height = 110
        val weight = 30
        val zodiac = 1
        val outlook = arrayOf(1,2,3)
        val professorial = arrayOf(1,2,3)
        val character = arrayOf(1,2,3)
        val favorite = arrayOf(1,2,3)
        val hightlight = arrayOf(1,2,3)
        val raw_data = java.util.ArrayList<BleCharacter>().also {
            it.add(BleCharacter(id, 24))
            it.add(BleCharacter(age, 7))
            it.add(BleCharacter(sex, 3))
            it.add(BleCharacter(sexuality_orientation,3))
            it.add(BleCharacter(height,8))
            it.add(BleCharacter(weight, 8))
            it.add(BleCharacter(zodiac, 4))
            it.add(BleCharacter(outlook[0], 7))
            it.add(BleCharacter(outlook[1], 7))
            it.add(BleCharacter(outlook[2], 7))
            it.add(BleCharacter(professorial[0], 7))
            it.add(BleCharacter(professorial[1], 7))
            it.add(BleCharacter(professorial[2], 7))
            it.add(BleCharacter(character[0], 7))
            it.add(BleCharacter(character[1], 7))
            it.add(BleCharacter(character[2], 7))
            it.add(BleCharacter(favorite[0], 7))
            it.add(BleCharacter(favorite[1], 7))
            it.add(BleCharacter(favorite[2], 7))
            it.add(BleCharacter(hightlight[0], 7))
            it.add(BleCharacter(hightlight[1], 7))
            it.add(BleCharacter(hightlight[2], 7))
        }
        val data = ByteArray(24)
        var pos_bit = 0
        var pos_byte = 0
        var bit_needed = 0
        for (i in raw_data) {
            Log.d(TAG, "raw data : $i")
            while (i.size > 0) {
                Log.d(TAG,"------------------------------------------------------")
                Log.d(TAG, "pos_byte: $pos_byte")
                Log.d(TAG, "pos_bit after reduce: $pos_bit")
                bit_needed = 8 - pos_bit
                Log.d(TAG, "bit need: $bit_needed")
                Log.d(TAG, "size of raw data[i]: ${i.size}")
                if (i.size >= bit_needed) {
                    Log.d(TAG, "---------------------------bigger")
                    var bit_shift = i.size - bit_needed
                    Log.d(TAG, "bit shift: $bit_shift")
                    Log.d(TAG, "data raw after shift: ${i.data ushr bit_shift}")
                    Log.d(TAG, "data result $pos_byte before: ${data[pos_byte]}")
                    data[pos_byte] = data[pos_byte] or (i.data ushr bit_shift).toByte()
                    Log.d(TAG, "data result $pos_byte after: ${data[pos_byte]}")
                    i.data = getLastBits(i.data, bit_shift)
                    Log.d(TAG, "data raw after remove bit shift: ${i.data}")
                    pos_bit += bit_needed
                    Log.d(TAG, "pos_bit: $pos_bit")
                    i.size -= bit_needed
                    Log.d(TAG, "size of raw data[i]: ${i.size}")
                } else {
                    Log.d(TAG, "---------------------------smaller")
                    var bit_shift = bit_needed - i.size
                    Log.d(TAG, "bit shift to left: $bit_shift")
                    Log.d(TAG, "data raw after shift: ${i.data shl bit_shift}")
                    Log.d(TAG, "data result $pos_byte before: ${data[pos_byte]}")
                    data[pos_byte] = data[pos_byte] or (i.data shl bit_shift).toByte()
                    Log.d(TAG, "data result $pos_byte after: ${data[pos_byte]}")
//                    i.data = i.data ushr i.size
//                    Log.d(TAG, "data raw after shift right: ${i.data}")
                    pos_bit += i.size
                    Log.d(TAG, "pos bit: $pos_bit")
                    i.size = 0
                    Log.d(TAG, "size of raw data[i]: ${i.size}")
                }
                if(pos_bit >= 8){
                    pos_bit -= 8
                    pos_byte++
                }
            }
            Log.d(TAG, "data: ${bytesToHex(data)}")
        }
        return data
    }

    private fun bleDataReceivedHandler(value: ByteArray){
        var exist = false
        for(i in listDataReceived){
            if(i == bytesToHex(value)){
                Log.d(TAG, "exist")
                exist = true
            }
        }
        if(!exist){
            listDataReceived.add(bytesToHex(value))
            arrayAdapter.notifyDataSetChanged()
        }
    }

    private fun checkPermissions() {
        val reqPermissions = ArrayList<String>()
        if (activity?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
        } != PackageManager.PERMISSION_GRANTED) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= 23 && activity?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (reqPermissions.isNotEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, reqPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
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
//                            finish()
                            return
                        }
                    }
                }
            }
        }
    }
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        binding.swTurnOnOffBLE.isChecked = false
                        Toast.makeText(requireContext(), "Bluetooth off", Toast.LENGTH_LONG).show()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        binding.swTurnOnOffBLE.isChecked = true
                        Toast.makeText(requireContext(), "Bluetooth on", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
