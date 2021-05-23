package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.ble.ListDataDiscoveredAdapter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var listDataDiscovered: ArrayList<List<Int>> = ArrayList()
    private var listDataDisplay: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        listDataDisplay.add(setUpDataDisplay(listOf(1720145,23,0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19,20)))
//        listDataDisplay.add("phuc")
//        listDataDisplay.add("minh")
//        listDataDisplay.add("phat")

        listDataDiscovered.add(listOf(1720145,23,0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19,20))
        listDataDiscovered.add(listOf(1720146,23,0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19,20))
        listDataDiscovered.add(listOf(1720147,23,0,1,2,3,4,5,6,7,8,9,10,12,13,14,15,16,17,18,19,20))

        arrayAdapter = ListDataDiscoveredAdapter(this,listDataDiscovered)
        for(i in listDataDiscovered){
            Log.d(TAG, "data: ${i}")
        }
        binding.apply {
            listViewTest.adapter = arrayAdapter
        }
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
    private fun dosomething(): List<Int>{

        return listOf(1,2,3)
    }
}