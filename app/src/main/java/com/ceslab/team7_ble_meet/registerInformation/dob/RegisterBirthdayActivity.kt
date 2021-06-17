package com.ceslab.team7_ble_meet.registerInformation.dob

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterBirthdayBinding
import com.ceslab.team7_ble_meet.registerInformation.tag.RegisterTagActivity
import com.ceslab.team7_ble_meet.toast
import kotlinx.android.synthetic.main.activity_register_birthday.*
import java.util.*


class RegisterBirthdayActivity : AppCompatActivity() {
    lateinit var dialogListener : DatePickerDialog.OnDateSetListener
    lateinit var viewModel: RegisterBirthDayViewModel
    lateinit var binding: ActivityRegisterBirthdayBinding
    private var datePickerDialog  : DatePickerDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onbinding()
        setupDialog()
        setAction()
    }

    fun gotoTag(){
        val intent = Intent(this, RegisterTagActivity::class.java).apply {
        }
        startActivity(intent)
    }

    fun setAction(){
        viewModel.userResp.observe(this,Observer{ response ->
            btn_continue.isEnabled = true
            binding.progressbar.visibility = View.GONE
            tv_btn.visibility = View.VISIBLE
            if(response != null){
                if(response.type == "NONE" && response.status == "SUCCESS"){
                    gotoTag()
                }
                if(response.type == "NONE" && response.status == "FAILED"){
                    toast(response.message)
                }
            }

        })

        binding.apply {
            btnDayPicker.setOnClickListener {
                datePickerDialog!!.show()
            }
            btnContinue.setOnClickListener{
                btn_continue.isEnabled = false
                progressbar.visibility = View.VISIBLE
                tv_btn.visibility = View.GONE
                viewmodel?.updateBirthDay()
            }
            btn_backpress.setOnClickListener {
                onBackPressed()
            }
        }


    }

    fun setupDialog(){
        val c= Calendar.getInstance()
        val year= c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        Log.d("RegisterBirthday","year: $year, month: $month, Day: $day")

        dialogListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            Log.d("RegisterBirthday","year: $year, month: $month, Day: $day")
            val m = month+1
            if(day< 10){
                binding.tvDay.text = "0$day"
            }else{
                binding.tvDay.text = day.toString()
            }
            if(month < 10){
                binding.tvMonth.text = "0$m"
            }else{
                binding.tvMonth.text = m.toString()
            }
            binding.tvYear.text = year.toString()
        }
        datePickerDialog = DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,dialogListener,year,month,day)
        datePickerDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    fun onbinding(){
        viewModel = ViewModelProvider(this).get(RegisterBirthDayViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register_birthday)
        binding.viewmodel = viewModel
        viewModel.context = this
    }
   
}