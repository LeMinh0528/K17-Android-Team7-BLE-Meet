package com.ceslab.team7_ble_meet.registerInformation

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ceslab.team7_ble_meet.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RegisterTagActivity : AppCompatActivity() {
    lateinit var chipGroup : ChipGroup
    lateinit var btnContinue : LinearLayout
    private var listChip = arrayListOf<String>("Intimate Chat","Karaoke","Walking","17DTV","Sushi","Trying New Things","Swimming",
    "Esports","Chatting When I'm Bored","Photography","Instagram","Street Food"," Dancing","Outdoors","Music","Sapiosexual",
    "Art","Korean Dramas","Working out","Environentalism","BTS","Pho","PotterHead","Horror Movies","Comedy","Athlete",
    "Shopping","Festivals","Geek","Golf","Board Games","Netflix","Hot Pot","Picnicking","Running","Go for a drive","Hip Hop",
    "LGBT+","Hiking","Disney","Vlogging","Vegan","Travel","Anime","Student","Cocking","Cat lover","Craft Beer","Foodie","Coffee",
    "Writer","Gamer","Wine","Bun cha","Dog lover","Gemini","Tea","Taurus","Aquarius","Plant-based","Nightlife","Motorcycles","Politic",
    "Soccer","Basketball","Fashion","Gardening","StreetFood")
    private var listChooser : MutableList<String> = arrayListOf("Intimate Chat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tag)
        bindView()
        setupChip()
        initData()
    }

    fun bindView(){
        chipGroup = findViewById(R.id.chipGroup)
        btnContinue = findViewById(R.id.btn_continue)
    }

    fun initData(){
        if(listChooser.isNotEmpty()){
            for(i in listChooser){
                var pos = listChip.indexOf(i)
                val chip: Chip = chipGroup.getChildAt(pos) as Chip
                chip.isChecked = true
                chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colororange))
            }
        }
    }

    fun setupChip(){
        for(i in listChip){
            Log.d("TAG","i = $i")
            val chip = layoutInflater.inflate(R.layout.item_chip_filter,chipGroup,false) as Chip
            chip.text = i
            chip.isClickable = true
            chipGroup.addView(chip)
        }
        for(index in 0 until chipGroup.childCount){
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener {chipGroup,isChecked ->
                run {
                    Log.d("RegisterTagActivity","isChecked: $isChecked")
                    if (isChecked) {
                        if(listChooser.size > 5){
                            chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorgray400))
                            chip.isChecked = false
                        }else{
                            chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colororange))
                            listChooser.add(chipGroup.text.toString())
                            Log.d("RegisterTagActivity","listChooser: ${listChooser.size}")
                        }
                    }else{
                        chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorgray400))
                        listChooser.remove(chipGroup.text.toString())
                        Log.d("RegisterTagActivity","listChooser: ${listChooser.size}")
                    }
                }
            }
        }
    }
}