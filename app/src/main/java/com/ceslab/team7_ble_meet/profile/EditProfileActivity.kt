package Activity

import android.app.ActionBar
import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.ceslab.team7_ble_meet.EditDialog.Edit_Dialog
import com.ceslab.team7_ble_meet.R
import kotlinx.android.synthetic.main.custom_spinner_gender.*
import kotlinx.android.synthetic.main.custom_spinner_gender.view.*


class EditProfileActivity : AppCompatActivity() {
    private lateinit var spinner_gender : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        spinner_gender = findViewById(R.id.spinner_genner)
        spinner_gender.setOnClickListener {
            //inflate the dialog with cutomview
//            val  Dialog_View = LayoutInflater.from(this).inflate(R.layout.custom_spinner_gender,null)
//            //Builder AlertDialog
//            val mBuilder = AlertDialog.Builder(this).setView(Dialog_View)
//            //show Dialog
//            val mAlertDialog = mBuilder.show()
//            val _men = Dialog_View.checkbox_Men.text.toString()
//            val _men1 = spinner_gender.text.toString()
//            if (_men == _men1)
//            {
//                if(checkbox_Men.isChecked){
//                    checkbox_Men.(true)
//                }
//            }

//            Dialog_View.checkbox_Men.setOnClickListener {
//                val Man = Dialog_View.checkbox_Men.text.toString()
//                spinner_gender.setText(Man)
//                mAlertDialog.dismiss()
//            }
//            Dialog_View.checkbox_Girl.setOnClickListener {
//                val Girl =Dialog_View.checkbox_Girl.text.toString()
//                spinner_gender.setText(Girl)
//                mAlertDialog.dismiss()
//            }
//            Dialog_View.checkbox_Gay.setOnClickListener {
//                val Gay =Dialog_View.checkbox_Gay.text.toString()
//                spinner_gender.setText(Gay)
//                mAlertDialog.dismiss()
//            }
            setupAlertDialog(object :Edit_Dialog.EditDialogCallback
            {
                override fun onConfirmClicked(data: String) {

                }
            })

        }



    }

    private fun setupAlertDialog(editDialogCallback: Edit_Dialog.EditDialogCallback) {
        val dialog = Edit_Dialog(this)
        dialog.setEditDialogCallback(editDialogCallback)
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        // lam mat vien vuong khi bo man hinh
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(800,LinearLayout.LayoutParams.WRAP_CONTENT)


    }
}