package com.ceslab.team7_ble_meet.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivitySignUpBinding
import com.ceslab.team7_ble_meet.login.LogInActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSignUpActivity()
        binding.apply {
            SignUpTvGoToSignIn.setOnClickListener {
                goToLogIn()
            }
        }
        viewModel.resultSignUp.observe(this, Observer {
            Toast.makeText(this@SignUpActivity,it,Toast.LENGTH_SHORT).show()
            if(it == "Sign Up Successfully"){
                goToLogIn()
            }
        })
    }
    private fun setUpSignUpActivity(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this@SignUpActivity).get(SignUpViewModel::class.java)
        binding.signUpViewModel = viewModel
    }
    private fun goToLogIn(){
        val intent = Intent(this, LogInActivity::class.java)
        intent.putExtra("UsrNameFromSignUp2LogIn", viewModel.account.usrName)
        startActivity(intent)
    }
}