package com.ceslab.team7_ble_meet.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.dialog.ConfirmDialog
import com.ceslab.team7_ble_meet.dialog.ConfirmDialogListener
import com.ceslab.team7_ble_meet.login.LogInActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.toast

class SettingActivity : AppCompatActivity() {
    lateinit var btnLogout: CardView
    lateinit var btnChangePassword : CardView
    private var instance = UsersFireStoreHandler()
    private var confirmDialog: ConfirmDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        bindView()
        bindAction()

    }

    fun bindView(){
        btnLogout = findViewById(R.id.btn_logout)
        btnChangePassword = findViewById(R.id.btn_changePassword)
    }

    fun bindAction(){
        btnLogout.setOnClickListener{
            confirmDialog = showConfirm(message = "Are you sure you want to log out?",
                title = getString(R.string.confirmation),
                textYes = "Yes",
                textCancel = "Cancel",
                object: ConfirmDialogListener{
                    override fun cancel() {
                        confirmDialog?.dismiss()
                    }
                    override fun confirm() {
                        logoutFireBase()

                    }
                })
        }
    }

    private fun logoutFireBase(){
        instance.mAuth.signOut()
        deleteToken()

    }

    private fun deleteToken(){
        Log.d("SettingActivity","status: ")
        instance.deleteToken(KeyValueDB.getUserToken()){ status ->
            Log.d("SettingActivity","status: $status")
            if(status == "SUCCESS"){
                BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().deleteAll()
                goToLogIn()
            }else{
                toast("Error: cannot logout")
            }

        }

    }

    private fun goToLogIn(){
        val intent = Intent(this, LogInActivity::class.java ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showConfirm(message: String,
                            title:String,
                            textYes: String,
                            textCancel: String,
                            listener: ConfirmDialogListener
    ):
            ConfirmDialog {
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