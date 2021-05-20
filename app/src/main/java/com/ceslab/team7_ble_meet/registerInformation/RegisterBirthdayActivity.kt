package com.ceslab.team7_ble_meet.registerInformation

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ceslab.team7_ble_meet.R
import java.util.*


class RegisterBirthdayActivity : AppCompatActivity() {
    lateinit var mdataPickerDialog : DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_birthday)
//        initdatapicker()
        val bt_pickerdate = findViewById(R.id.btn_setbirthday)  as LinearLayout
        val vv = findViewById(R.id.tv_birthday) as TextView
        // set ondate o ngoai moi ra popup khong no ra cai tam lich
        val mdataPickerDialog = DatePickerDialog.OnDateSetListener { view, mYear,mMonth , mDay ->
            val mmMonth = mMonth+1
            if (mMonth<10)
            {
                val stringmonth = "0$mMonth"
                val date = "$mDay $stringmonth $mYear"
                vv.setText(date)
            }
            else
            {
                val date = "$mDay $mMonth $mYear"
                vv.setText(date)
            }

        }

         bt_pickerdate.setOnClickListener {
             val c= Calendar.getInstance()
             val year= c.get(Calendar.YEAR)
             val month = c.get(Calendar.MONTH)
             val day = c.get(Calendar.DAY_OF_MONTH)
             var dpd = DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,mdataPickerDialog,year,month,day)
             dpd.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             dpd.show()
        }





    }

//    private fun initdatapicker() {
//        val dateSetListener =
//            OnDateSetListener { datePicker, year, month, day ->
//                var month = month
//                month = month + 1
//
//            }
//        val cal = Calendar.getInstance()
//        val year = cal[Calendar.YEAR]
//        val month = cal[Calendar.MONTH]
//        val day = cal[Calendar.DAY_OF_MONTH]
//
//        val style: Int = AlertDialog.THEME_HOLO_LIGHT
//
//        val datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
//
//    }



   
}