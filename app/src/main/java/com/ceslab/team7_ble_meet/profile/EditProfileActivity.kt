package Activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dialog.EditGenderDialog


class EditProfileActivity : AppCompatActivity() {
    private lateinit var spinner_gender : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)


    }

    private fun setupAlertDialog_corner(editDialogCallback: EditGenderDialog.EditDialogCallback) {
        val dialog = EditGenderDialog(this)
        dialog.setEditDialogCallback(editDialogCallback)
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        // lam mat vien vuong khi bo man hinh
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(800,LinearLayout.LayoutParams.WRAP_CONTENT)


    }
    private fun setupAlertDialog(editDialogCallback: EditGenderDialog.EditDialogCallback) {
        val dialog = EditGenderDialog(this)
        dialog.setEditDialogCallback(editDialogCallback)
        dialog.show()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)




    }
}