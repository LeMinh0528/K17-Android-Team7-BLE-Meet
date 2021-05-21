package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.toast
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RegisterTagActivity : AppCompatActivity() {
    lateinit var chipGroup : ChipGroup
    lateinit var btnContinue : LinearLayout
    lateinit var btnText: TextView
    lateinit var viewModel: RegisterTagViewModel
    private var userId =  KeyValueDB.getUserId()
    private var listChip = arrayListOf<String>("Intimate Chat","Karaoke","Walking","17DTV","Sushi","Trying New Things","Swimming",
    "Esports","Chatting When I'm Bored","Photography","Instagram","Street Food"," Dancing","Outdoors","Music","Sapiosexual",
    "Art","Korean Dramas","Working out","Environentalism","BTS","Pho","PotterHead","Horror Movies","Comedy","Athlete",
    "Shopping","Festivals","Geek","Golf","Board Games","Netflix","Hot Pot","Picnicking","Running","Go for a drive","Hip Hop",
    "LGBT+","Hiking","Disney","Vlogging","Vegan","Travel","Anime","Student","Cocking","Cat lover","Craft Beer","Foodie","Coffee",
    "Writer","Gamer","Wine","Bun cha","Dog lover","Gemini","Tea","Taurus","Aquarius","Plant-based","Nightlife","Motorcycles","Politic",
    "Soccer","Basketball","Fashion","Gardening","StreetFood")
    private var listChooser : MutableList<String> = arrayListOf("Intimate Chat")
//    private var listChooser : MutableList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_tag)
        bindView()
        setupChip()
        initData()
        setupChipListener()
    }

    fun bindView(){
        Log.d("RegisterTagActivity","list choose num: ${listChooser.size}")
        chipGroup = findViewById(R.id.chipGroup)
        btnContinue = findViewById(R.id.btn_continue)
        btnText = findViewById(R.id.tv_btn)
        viewModel = ViewModelProvider(this).get(RegisterTagViewModel::class.java)

        btnContinue.setOnClickListener {
            viewModel.register(userId,listChooser)
        }
        viewModel.userResp.observe(this, Observer { result ->
            Log.d("RegisterTagActivity","registertag observer")
            if(result != null){
                toast("result: ${result.message}")
                if(result.type == "SUCCESS"){
                    gotoDashBoard()
                }
            }
        })
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
        updateText()
    }

    fun setupChip(){
        for(i in listChip){
            val chip = layoutInflater.inflate(R.layout.item_chip_filter,chipGroup,false) as Chip
            chip.text = i
            chip.isClickable = true
            chipGroup.addView(chip)
        }
    }

    fun setupChipListener(){
        for(index in 0 until chipGroup.childCount){
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener {chipGroup,isChecked ->
                run {
                    Log.d("RegisterTagActivity","isChecked: $isChecked")
                    if (isChecked) {
                        if(listChooser.size > 4){
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
                updateText()
            }
        }
    }

    private fun updateText(){
        btnText.text = "Continue (${listChooser.size}/5)"
        btnContinue.isEnabled = listChooser.size > 4
    }

    private fun gotoDashBoard(){
        val intent = Intent(this, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

    }
}