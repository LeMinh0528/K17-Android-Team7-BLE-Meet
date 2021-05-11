package Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.ceslab.team7_ble_meet.R
import kotlinx.android.synthetic.main.custom_spinner_gender.*
import kotlinx.android.synthetic.main.custom_spinner_gender.view.*


class ProfileActivity : AppCompatActivity() {
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

        }


    }


}