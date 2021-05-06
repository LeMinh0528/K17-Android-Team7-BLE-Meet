package Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import com.ceslab.team7_ble_meet.R

class Activity_Profile : AppCompatActivity() {
    lateinit var spi_genner : Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        spi_genner = findViewById(R.id.spi_gender) as Spinner
        val option_gender = resources.getStringArray(R.array.gender_spinner);
        spi_genner.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            option_gender
        )
        spi_genner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }
        }

    }
}