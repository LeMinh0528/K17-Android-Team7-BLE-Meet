package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterUserNameBinding
import kotlinx.android.synthetic.main.activity_register_user_name.*

class RegisterUserNameActivity : AppCompatActivity() {
    lateinit var viewModel: RegisterUserNameViewModel
    lateinit var binding: ActivityRegisterUserNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()
        initAction()
    }
    private fun bindView(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register_user_name)
        viewModel = ViewModelProvider(this).get(RegisterUserNameViewModel::class.java)
        binding.viewmodel = viewModel
    }
    private fun initAction(){
        binding.apply {
            btn_setusername.setOnClickListener{
                viewmodel?.registerName()
            }
        }
        viewModel.userResp.observe(this, Observer { response ->
            if (response != null){
                if(response.type == "SUCCESS"){
                    gotoGender()
                }
            }
        })

    }
    private fun gotoGender(){
        val intent = Intent(this, RegisterGenderActivity::class.java).apply {
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //show dialog
        //if dialog show -> shut down dialog
        //else show dialog
    }
}