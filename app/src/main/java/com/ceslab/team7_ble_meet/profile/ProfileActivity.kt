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
            val  Dialog_View = LayoutInflater.from(this).inflate(R.layout.custom_spinner_gender,null)
            //Builder AlertDialog
            val mBuilder = AlertDialog.Builder(this).setView(Dialog_View)
            //show Dialog
            val mAlertDialog = mBuilder.show()
            Dialog_View.text_Men.setOnClickListener {
                    val men = Dialog_View.text_Men.text.toString()
                spinner_gender.setText(men)
                mAlertDialog.dismiss()
            }
            Dialog_View.text_Girl.setOnClickListener {
                val girl = Dialog_View.text_Girl.text.toString()
                spinner_gender.setText(girl)
                mAlertDialog.dismiss()
            }

        }


    }
    private fun setDialog(){
            android.R.layout.simple_list_item_multiple_choice
    }
}