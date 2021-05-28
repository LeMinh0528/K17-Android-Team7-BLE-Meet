package com.ceslab.team7_ble_meet.dashboard.bleFragment

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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.Model.BleDataScanned
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.databinding.FragmentBleBinding
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import kotlin.experimental.or

class BleFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }
    private val TAG = "BleFragment"
    private lateinit var binding: FragmentBleBinding

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bleHandle: BleHandle

    private var listDataDiscoveredAdapter = ListBleDataScanedAdapter()

    private val characteristicUser = listOf(1720148, 25, 1, 1, 165, 60, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17)
    private val target = listOf(18, 30, 1, 2, 3, 1, 2, 3, 160, 180, 50, 80, 1, 2, 3, 4, 5, 6, 7, 8, 9,10, 11, 12, 13, 14, 15, 16, 17, 18)

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

    private fun setUpBle(){
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)

        bluetoothManager = activity?.let { getSystemService(it, BluetoothManager::class.java) }!!
        bluetoothAdapter = bluetoothManager.adapter
        bleHandle = BleHandle()

        bleHandle.bleDataScanned.observe(viewLifecycleOwner, {
            handleDataDiscovered(it)
        })
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
                        if(binding.vRipple.isRippleAnimationRunning) binding.vRipple.stopRippleAnimation()
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

    private fun setUpUI(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble, container, false)
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
                    if(!vRipple.isRippleAnimationRunning){
                        bleHandle.advertise(setUpDataAdvertise(characteristicUser as MutableList<Int>))
                        bleHandle.scan()
                        vRipple.startRippleAnimation()
                    }else{
                        bleHandle.stopAdvertise()
                        bleHandle.stopScan()
                        vRipple.stopRippleAnimation()
                    }
                } else {
                    Toast.makeText(activity, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }
            btnStopFindFriend.setOnClickListener {
                vRipple.stopRippleAnimation()
                if (bluetoothAdapter.isEnabled) {
                    bleHandle.stopAdvertise()
                    bleHandle.stopScan()
                }
            }
            btnDelete.setOnClickListener{
                deleteAllUser()
                btnFindFriend.isGone = false
                vRipple.isGone = false
                rcListDataDiscovered.isGone = true
            }


            listDataDiscoveredAdapter.data = BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
            rcListDataDiscovered.adapter = listDataDiscoveredAdapter
            if(BleDataScannedDataBase.getDatabase(requireContext())
                    .bleDataScannedDao()
                    .getUserDiscover()
                    .isNotEmpty()
            ){
                btnFindFriend.isGone = true
                vRipple.isGone = true
                rcListDataDiscovered.visibility = View.VISIBLE
            }

            listDataDiscoveredAdapter.listener = object : ListBleDataScanedAdapter.IdClickedListener{
                override fun onClickListen(id: String) {
                    Log.d(TAG,id)
//                    val intent = Intent(activity, ProfileActivity::class.java)
//                    intent.putExtra("idFromConnectFragmentToProfile", id)
//                    startActivity(intent)
                }
            }
        }
    }

    private fun setUpDataAdvertise(input: MutableList<Int>): ByteArray {
        val output = ByteArray(24)
        val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
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

    private fun handleDataDiscovered(data: ByteArray) {
        val dataDiscovered = data.drop(4)
        val listOfCharacteristic = convertDataDiscovered(dataDiscovered.toByteArray())
        if (checkCharacteristic(listOfCharacteristic.drop(1) as MutableList<Int>, target as MutableList<Int>)) {
            addUser(listOfCharacteristic)
            if (!binding.btnFindFriend.isGone) {
                binding.btnFindFriend.isGone = true
                binding.vRipple.isGone = true
                binding.rcListDataDiscovered.visibility = View.VISIBLE
            }
        }
    }

    private fun convertDataDiscovered(data: ByteArray): List<Int> {
        val rawData = ArrayList<Int>()
        Log.d(TAG, bytesToHex(data))
        val sizeEachCharacter = mutableListOf(24, 7, 3, 3, 8, 8, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7)
        var posByte = 0
        var posBit = 7
        for (i in sizeEachCharacter) {
            var temp = 0
            var bitTaken = 0
            while (bitTaken < i) {
                val bitAvailable = if (i - bitTaken >= posBit + 1) (posBit + 1) else (i - bitTaken)
                temp = (temp shl bitAvailable) or getBitsFromPos(
                    data[posByte].toInt(),
                    posBit,
                    bitAvailable
                )
                posBit -= bitAvailable
                bitTaken += bitAvailable
                if (posBit < 0) {
                    posBit += 8
                    posByte++
                }
            }
            rawData.add(temp)
        }
        return rawData
    }

    private fun checkCharacteristic(input: MutableList<Int>, filter: MutableList<Int>): Boolean {
        Log.d(TAG, "start to check")
        var score = 0
        if (filter[0] < input[0] && input[0] < filter[1]) score++
        for (i in 2..4) {
            if(input[1] == filter[i]) score++
        }
        for (i in 5..7) {
            if(input[2] == filter[i]) score++
        }
        if (filter[8] < input[3] && input[3] < filter[9]) score++
        if (filter[10] < input[4] && input[4] < filter[11]) score++
        for (i in 12..13) {
            if(input[5] == filter[i]) score++
        }
        for (i in 6..8) {
            for (j in 14..16) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 9..11) {
            for (j in 17..19) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 12..14) {
            for (j in 20..22) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 15..17) {
            for (j in 23..25) {
                if (input[i] == filter[j]) score++
            }
        }
        for (i in 18..20) {
            for (j in 26..28) {
                if (input[i] == filter[j]) score++
            }
        }
        Log.d(TAG, "result: $score")
        return score >= 3
    }

    private fun addUser(list: List<Int>) {
        BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().insert(BleDataScanned(list))
        listDataDiscoveredAdapter.data = BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
    }

    private fun deleteAllUser(){
        BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().deleteAll()
        listDataDiscoveredAdapter.data = BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
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

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
    }
}