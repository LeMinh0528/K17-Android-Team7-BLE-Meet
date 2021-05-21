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

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var viewModel: LogInViewModel
    private lateinit var viewModelFactory: LogInViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLogIn()
        binding.apply {
            LogInTvGoToSignUp.setOnClickListener {
                goToSignUp()
            }
        }
        viewModel.resultLogIn.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setLogIn() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_log_in)
        val bundle = intent.extras
        val usrName = bundle?.getString("UsrNameFromSignUp2LogIn").toString()
        viewModelFactory = LogInViewModelFactory(usrName)
        viewModel = ViewModelProvider(this,viewModelFactory).get(LogInViewModel::class.java)
        binding.logInViewModel = viewModel
    }
    private fun goToSignUp(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

}