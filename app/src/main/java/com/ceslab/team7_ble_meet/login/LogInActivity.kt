package com.ceslab.team7_ble_meet.login

import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityLogInBinding
import com.ceslab.team7_ble_meet.signup.SignUpActivity
import com.ceslab.team7_ble_meet.toast

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var viewModel: LogInViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLogIn()
        binding.apply {
            LogInTvGoToSignUp.setOnClickListener {
                goToSignUp()
            }
            LogInBtnLogIn.setOnClickListener{
                viewModel.logIn()
            }
        }

    }

    private fun setLogIn() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_log_in)
        viewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        binding.logInViewModel = viewModel

        viewModel.userResp.observe(this, Observer { response ->
            if(response != null){
                toast(response.message)
            }
        })
    }
    private fun goToSignUp(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}