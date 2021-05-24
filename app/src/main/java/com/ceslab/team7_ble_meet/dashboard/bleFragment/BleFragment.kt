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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.Profile.ProfileActivity
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.databinding.FragmentBleBinding
import kotlin.experimental.or


@Suppress("NAME_SHADOWING")
class BleFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }
    private val TAG = "ConnectFragment"
    private lateinit var binding: FragmentBleBinding

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var connect: BleHandle

    private var listDataDiscovered: ArrayList<List<Int>> = ArrayList()
    private var listDataDisplay: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    val data = listOf(1720146,25,1,1,165,60,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17)
    private val filter = listOf(18,30,1,2,3,1,2,3,160,180,50,80,1,2,3,4,5,6,7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

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

        connect.bleDataReceived.observe(viewLifecycleOwner, {
            handleDataDiscovered(it)
        })
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
                    connect.advertise(setUpDataAdvertise(data as MutableList<Int>))
                    connect.discover()
                } else {
                    Toast.makeText(activity, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
                }
                if(vRipple.isRippleAnimationRunning) vRipple.stopRippleAnimation() else vRipple.startRippleAnimation()
            }

            arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                listDataDisplay
            )
            listViewBleDataDiscovered.adapter = arrayAdapter
            listViewBleDataDiscovered.setOnItemClickListener { parent, view, position, id ->
                requireContext().toast("click item")
                Log.d(TAG, "position: $position, data: ${listDataDiscovered[position][0]}")
                val intent = Intent(activity, ProfileActivity::class.java)
                intent.putExtra("idFromConnectFragmentToProfile", listDataDiscovered[position][0])
                startActivity(intent)
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
        var exist = false
        for (dataDiscovered in listDataDiscovered) {
            if (dataDiscovered[0] == listOfCharacteristic[0]) {
//                Log.d(TAG, "exist")
                exist = true
                break
            }
        }
        if (!exist) {
//            Log.d(TAG, "do not exist")
            if (checkCharacteristic(listOfCharacteristic.drop(1) as MutableList<Int>, filter as MutableList<Int>)) {
//                Log.d(TAG, "matched")
                listDataDiscovered.add(listOfCharacteristic)
                listDataDisplay.add(setUpDataDisplay(listOfCharacteristic))
                arrayAdapter.notifyDataSetChanged()
                if (!binding.btnFindFriend.isGone) {
                    binding.btnFindFriend.isGone = true
                    binding.vRipple.isGone = true
                }
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

    private fun setUpDataDisplay(raw_data: List<Int>): String {
        return "ID: ${raw_data[0]}\n" +
                "Age: ${raw_data[1]}\n" +
                "Sex: ${Characteristic.sex[raw_data[2]]}\n" +
                "Sexuality Orientation: ${Characteristic.sexualOrientation[raw_data[3]]}\n" +
                "Tall: ${raw_data[4]}\n" +
                "Weight: ${raw_data[5]}\n" +
                "Zodiac: ${Characteristic.zodiac[raw_data[6]]}\n" +
                "Outlook: ${Characteristic.outlook[raw_data[7]]} - ${Characteristic.outlook[raw_data[8]]} - ${Characteristic.outlook[raw_data[9]]}\n" +
                "Job: ${Characteristic.job[raw_data[10]]} - ${Characteristic.job[raw_data[11]]} - ${Characteristic.job[raw_data[12]]}\n" +
                "Character: ${Characteristic.character[raw_data[13]]} - ${Characteristic.character[raw_data[14]]} - ${Characteristic.character[raw_data[15]]}\n" +
                "Favorite: ${Characteristic.hightlight[raw_data[16]]} - ${Characteristic.hightlight[raw_data[17]]} - ${Characteristic.hightlight[raw_data[18]]}\n" +
                "Hightlight: ${Characteristic.hightlight[raw_data[19]]} - ${Characteristic.hightlight[raw_data[20]]} - ${Characteristic.hightlight[raw_data[21]]}\n"
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
