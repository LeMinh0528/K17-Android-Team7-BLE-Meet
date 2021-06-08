package com.ceslab.team7_ble_meet.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dialog.ConfirmDialog
import com.ceslab.team7_ble_meet.dialog.ReadDialog
import com.ceslab.team7_ble_meet.dialog.ReadDialogListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EditPassionsActivity : AppCompatActivity() {
    lateinit var chipGroup : ChipGroup
    lateinit var tv_countselect: TextView
    lateinit var btn_back : LinearLayout
    private var confirmDialog: ReadDialog? = null
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

        setContentView(R.layout.activity_edit_passions)
        bindView()
        setupChip()
        initData()
        setupChipListener()
        backintent()

    }

    private fun backintent() {
        btn_back.setOnClickListener {
            if (listChooser.size < 4)
            {
                Log.d("RegisterTagActivity",listChooser.size.toString())
                onBackPressed()
            }
        }
    }

    fun bindView(){
        chipGroup = findViewById(R.id.chipGroup1)
        tv_countselect = findViewById(R.id.tv_CountSelect)
        btn_back = findViewById(R.id.btn_back)
    }
    fun setupChip(){
        for(i in listChip){
            val chip = layoutInflater.inflate(R.layout.item_chip_filter,chipGroup,false) as Chip
            chip.text = i
            chip.isClickable = true
            chipGroup.addView(chip)
        }
    }
    fun initData(){
        if(listChooser.isNotEmpty()){
            for(i in listChooser){
                var pos = listChip.indexOf(i)
                val chip: Chip = chipGroup.getChildAt(pos) as Chip
                chip.isChecked = true
                chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorblue100))
            }
        }
        updateText()
    }
    fun setupChipListener(){
        for(index in 0 until chipGroup.childCount){
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            chip.setOnCheckedChangeListener {chipGroup,isChecked ->
                run {
                    Log.d("RegisterTagActivity","isChecked: $isChecked")
                    if (isChecked) {
                        if(listChooser.size > 9){
                            chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorgray400))
                            chip.isChecked = false
                        }else{
                            chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorblue100))
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

    override fun onBackPressed() {

        //show dialog
        //if dialog show -> shut down dialog
        //else show dialog
        Log.d("TAG","onback press")
        if (listChooser.size < 4) {
            confirmDialog =
                showConfirm(message = "To have passions display on your profile, you must select at least 3 passions",
                    title = getString(R.string.needpassions),
                    textYes = "Okay",
                    object : ReadDialogListener {
                        override fun ok() {
                            confirmDialog?.dismiss()
                        }
                    })
        }
    }
    private fun updateText(){
        tv_countselect.text = "(${listChooser.size}/10)"
    }
    fun showConfirm(message: String,
                    title:String,
                    textYes: String,
                    listener : ReadDialogListener):
            ReadDialog? {
        Log.d("TAG","onback press")
        val dialog = ReadDialog.Builder()
            .title(title)
            .info(message)
            .yesText(textYes)
            .listener(listener)
            .build()
        dialog.show(supportFragmentManager,"CONFIRMATION")
        return dialog
    }

}

