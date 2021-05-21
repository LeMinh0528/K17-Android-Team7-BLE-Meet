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
import com.ceslab.team7_ble_meet.registerInformation.RegisterGenderActivity
import com.ceslab.team7_ble_meet.toast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpSignUpActivity()
        setupAction()

    }

    private fun setUpSignUpActivity(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this@SignUpActivity).get(SignUpViewModel::class.java)
        binding.signUpViewModel = viewModel
    }

    fun setupAction(){
        binding.apply {
            SignUpTvGoToSignIn.setOnClickListener {
                goToLogIn()
            }
            SignUp_btnSignUp.setOnClickListener{
                Log.d("SignUpActivity","sign up button")
                viewModel.register()
            }
        }

        viewModel.userResp.observe(this, Observer {response ->
            Log.d("SignUpActivity","signup observer")
            if (response != null) {
                if(response.type == "SUCCESS"){
                    toast(response.message)
                    goToRegisterGender()
                }else if(response.type == "FAILED"){
                    toast(response.message)
                }

            }
        })


    }

    private fun goToLogIn(){
        val intent = Intent(this, LogInActivity::class.java)
//        intent.putExtra("UsrNameFromSignUp2LogIn", viewModel.account.usrName)
        startActivity(intent)
    }

    private fun goToRegisterGender(){
        val intent = Intent(this, RegisterGenderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

    }

}