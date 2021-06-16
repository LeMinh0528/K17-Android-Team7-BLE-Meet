package com.ceslab.team7_ble_meet.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dialog.ReadDialog
import com.ceslab.team7_ble_meet.dialog.ReadDialogListener
import com.ceslab.team7_ble_meet.toast
import com.ceslab.team7_ble_meet.utils.NetworkUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class EditPassionsActivity : AppCompatActivity() {
    lateinit var chipGroup : ChipGroup
    lateinit var tv_countselect: TextView
    lateinit var btnBack : LinearLayout
    private var confirmDialog: ReadDialog? = null
    lateinit var viewmodel : EditPassionViewModel
    private var listChip = arrayListOf<String>("Intimate Chat","Karaoke","Walking","17DTV","Sushi","Trying New Things","Swimming",
        "Esports","Chatting When I'm Bored","Photography","Instagram","Street Food","Dancing","Outdoors","Music","Sapiosexual",
        "Art","Korean Dramas","Working out","Environentalism","BTS","Pho","PotterHead","Horror Movies","Comedy","Athlete",
        "Shopping","Festivals","Geek","Golf","Board Games","Netflix","Hot Pot","Picnicking","Running","Go for a drive","Hip Hop",
        "LGBT+","Hiking","Disney","Vlogging","Vegan","Travel","Anime","Student","Cocking","Cat lover","Craft Beer","Foodie","Coffee",
        "Writer","Gamer","Wine","Bun cha","Dog lover","Gemini","Tea","Taurus","Aquarius","Plant-based","Nightlife","Motorcycles","Politic",
        "Soccer","Basketball","Fashion","Gardening","StreetFood")
    private var listChooser : MutableList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_passions)
        bindView()
        setupChip()
        initData()
        backIntent()
    }



    fun bindView(){
        viewmodel = ViewModelProvider(this).get(EditPassionViewModel::class.java)
        chipGroup = findViewById(R.id.chipGroup1)
        tv_countselect = findViewById(R.id.tv_CountSelect)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun backIntent() {
        btnBack.setOnClickListener {
            Log.d("EditPassionActivity","set onclick btn back")
            if (listChooser.size < 5) {
                confirmDialog =
                    showConfirm(message = "To have passions display on your profile, you must select at least 5 passions",
                        title = getString(R.string.needpassions),
                        textYes = "Okay",
                        object : ReadDialogListener {
                            override fun ok() {
                                confirmDialog?.dismiss()
                            }
                        })
            }else{
                viewmodel.updateTag(listChooser){
                    if(it == "SUCCESS"){
                        toast("Update tag successful!")
                    }else{
                        toast("Error update tag")
                    }
                    super.onBackPressed()
                }

            }
        }
    }

    private fun setupChip(){
        for(i in listChip){
            val chip = layoutInflater.inflate(R.layout.item_chip_filter,chipGroup,false) as Chip
            chip.text = i
            chip.isClickable = true
            chipGroup.addView(chip)
        }
    }
    private fun initData(){
        viewmodel.getTags {
            if(it.isNotEmpty()){
                for(i in it){
                    val pos = listChip.indexOf(i)
                    val chip: Chip = chipGroup.getChildAt(pos) as Chip
                    chip.isChecked = true
                    chip.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorblue100))
                    listChooser.add(i)
                }
            }
            setupChipListener()
            updateText()
        }
    }

    private fun setupChipListener(){
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
        Log.d("EditPassionActivity","on back press")
        if (listChooser.size < 5) {
            confirmDialog =
                showConfirm(message = "To have passions display on your profile, you must select at least 5 passions",
                    title = getString(R.string.needpassions),
                    textYes = "Okay",
                    object : ReadDialogListener {
                        override fun ok() {
                            confirmDialog?.dismiss()
                        }
                    })
        }else{
            if(!NetworkUtils.isNetworkAvailable(this)){
                toast("Error wifi connection!")
                super.onBackPressed()
            }else{
                viewmodel.updateTag(listChooser){
                    if(it == "SUCCESS"){
                        toast("Update tag successful!")
                    }else{
                        toast("Error update tag")
                    }
                    super.onBackPressed()
                }
            }


        }
    }

    private fun checkCount(){

    }

    private fun updateText(){
        Log.d("EditPassionsActivity","listchoosesize: ${listChooser.size}")
        tv_countselect.text = "(${listChooser.size}/5)"
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

