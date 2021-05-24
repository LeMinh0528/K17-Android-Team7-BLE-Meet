package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterPictureBinding
import kotlinx.android.synthetic.main.activity_register_picture.*


class RegisterPictureActivity : AppCompatActivity() {
    val PICK_IMAGE = 1
    lateinit var viewModel: RegisterPictureViewModel
    lateinit var binding: ActivityRegisterPictureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_picture)
        binding()
        setAction()
    }

    fun binding(){
        viewModel = ViewModelProvider(this).get(RegisterPictureViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_picture)
        binding.viewmodel = viewModel
    }

    fun setAction(){
        binding.apply{
            btn_edit.setOnClickListener{
                val gallery = Intent()
                gallery.type = "image/*"
                gallery.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(gallery, "chon hinh anh"), PICK_IMAGE)
            }
            btn_continue.setOnClickListener{

            }
        }
    }

}