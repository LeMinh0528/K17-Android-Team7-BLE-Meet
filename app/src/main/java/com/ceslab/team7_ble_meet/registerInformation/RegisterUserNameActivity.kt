package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterUserNameBinding
import com.ceslab.team7_ble_meet.dialog.ConfirmDialog
import com.ceslab.team7_ble_meet.dialog.ConfirmDialogListener
import com.ceslab.team7_ble_meet.signup.SignUpActivity
import com.ceslab.team7_ble_meet.toast
import kotlinx.android.synthetic.main.activity_register_user_name.*
import kotlinx.android.synthetic.main.dialog_confirm.*

class RegisterUserNameActivity : AppCompatActivity() {
    lateinit var viewModel: RegisterUserNameViewModel
    lateinit var binding: ActivityRegisterUserNameBinding
    private var confirmDialog: ConfirmDialog? = null
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
            btn_continue.setOnClickListener{
                viewmodel?.registerName()
            }
        }
        viewModel.userResp.observe(this, Observer { response ->
            if (response != null){
                if(response.type == "NONE" && response.status =="SUCCESS"){
                    gotoGender()
                }
                if(response.type == "NONE" && response.status =="FAILED"){
                    toast(response.message)
                }
                if(response.type == "DELETE" && response.status == "SUCCESS"){
                    goToSignUp()
                }
                if(response.type == "DELETE" && response.status == "FAILED"){
                    toast(response.message)
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

        //show dialog
        //if dialog show -> shut down dialog
        //else show dialog
        Log.d("TAG","onback press")
        confirmDialog = showConfirm(message = "Do you want to cancel process, all your data will be delete?",
            title = getString(R.string.confirmation),
            textYes = "Yes",
            textCancel = "Cancel",
            object: ConfirmDialogListener{
                override fun cancel() {
                    confirmDialog?.dismiss()
                }

                override fun confirm() {
                    clearData()
                    goToSignUp()
                }
            })
    }

    fun clearData(){
        viewModel.deleteUser()
    }

    fun goToSignUp(){
        val intent = Intent(this, SignUpActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    fun showConfirm(message: String,
                    title:String,
                    textYes: String,
                    textCancel: String,
                    listener: ConfirmDialogListener):
            ConfirmDialog{
        Log.d("TAG","onback press")
        val dialog = ConfirmDialog.Builder()
            .title(title)
            .info(message)
            .yesText(textYes)
            .cancelText(textCancel)
            .listener(listener)
            .build()
        dialog.show(supportFragmentManager,"CONFIRMATION")
        return dialog
    }
}