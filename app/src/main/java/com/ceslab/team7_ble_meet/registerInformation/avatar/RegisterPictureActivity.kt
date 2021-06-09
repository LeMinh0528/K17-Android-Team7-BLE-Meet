package com.ceslab.team7_ble_meet.registerInformation.avatar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterPictureBinding
import com.ceslab.team7_ble_meet.registerInformation.dob.RegisterBirthdayActivity
import com.ceslab.team7_ble_meet.registerInformation.tag.RegisterTagActivity
import com.ceslab.team7_ble_meet.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register_birthday.*
import kotlinx.android.synthetic.main.activity_register_picture.*
import kotlinx.android.synthetic.main.activity_register_picture.btn_continue
import kotlinx.android.synthetic.main.activity_register_picture.tv_btn
import java.io.ByteArrayOutputStream


class RegisterPictureActivity : AppCompatActivity() {
    private val TAG = "RegisterPictureActivity"
    private  var  REQUEST_CAMERA =2
    val PICK_IMAGE = 1
    private val PERMISSION_REQUEST = 10
    lateinit var viewModel: RegisterPictureViewModel
    lateinit var binding: ActivityRegisterPictureBinding

    private var bottomdialog : BottomSheetDialog? = null
    private var permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding()

        if(!checkPermission(this, permission)){
            requestPermissions(permission, PERMISSION_REQUEST)
        }
        setUpBottomSheet()
        setAction()
    }

    fun binding(){
        viewModel = ViewModelProvider(this).get(RegisterPictureViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_picture)
        binding.viewmodel = viewModel
    }

    fun setAction(){
        binding.apply{
            btn_edit.setOnClickListener{
                bottomdialog?.show()
            }
            btn_continue.setOnClickListener{
                btn_continue.isEnabled = false
                progressbar.visibility = View.VISIBLE
                tv_btn.visibility = View.GONE
                viewmodel?.uploadImage()
            }
            btn_backpress_respicture.setOnClickListener {
                onBackPressed()
            }
        }
        viewModel.userResp.observe(this, Observer { response ->
            btn_continue.isEnabled = true
            binding.progressbar.visibility = View.GONE
            tv_btn.visibility = View.VISIBLE

            if (response != null) {
                if (response.type == "NONE" && response.status == "SUCCESS") {
                    gotoDashBoard()
                }
                if (response.type == "NONE" && response.status == "FAILED") {
                    toast(response.message)
                }
            }
        })
    }

    private fun setUpBottomSheet(){
        bottomdialog = BottomSheetDialog(this@RegisterPictureActivity, R.style.BottomSheetTheme)
        val sheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.custom_bottomdialog,
            findViewById(R.id.layoubottomsheetdialog)
        )
        bottomdialog!!.setContentView(sheetView)

        val camera = sheetView.findViewById(R.id.Opcamera) as LinearLayout
        val gallery = sheetView.findViewById(R.id.Opgallery) as LinearLayout
        camera.setOnClickListener {
            val takePictureintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureintent.resolveActivity(packageManager)!= null){
                startActivityForResult(takePictureintent, REQUEST_CAMERA)
            }
            bottomdialog!!.dismiss()
        }
        gallery.setOnClickListener {
            val gallery1 = Intent()
            gallery1.type = "image/*"
            gallery1.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallery1, "choose image"), PICK_IMAGE)
            bottomdialog!!.dismiss()
        }
    }

    private fun gotoDashBoard(){
        val intent = Intent(this, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if press to choose image
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE && data != null){
                Log.d("TAG", "Pick image: ${data.data}")
                val uri: Uri? = data.data
                if (uri != null) {
                    cropImage(uri)
                }
            }
            if(requestCode == REQUEST_CAMERA && data != null){
                val image = data.extras?.get("data") as Bitmap
                val uri = getImageUri(this,image)
                if(uri != null){
                    Log.d(TAG, "image: ${uri.path}")
                    cropImage(uri)
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == RESULT_OK){
                    val uri = result.uri
//                    viewModel.selectedImage = uri
                    Glide.with(this)
                        .load(uri)
                        .into(binding.icAvt)

                    val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

                    viewModel.selected = outputStream.toByteArray()
                    Log.d(TAG, "image array: ${viewModel.selected}")
                    Log.d(TAG, "image array: ${viewModel.selected}")
        }
            }
        }
    }

    private fun cropImage(uri: Uri){
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .setMinCropWindowSize(500, 500)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, inImage.toString(), null)
        return Uri.parse(path)
    }

    private fun checkPermission(context: Context, permissionArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionArray.indices){
            if(checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSuccess = false
            }
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var allSuccess = true
        if(requestCode == PERMISSION_REQUEST){
            for(i in permissions.indices){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        "show rationale"
                    )
                    if(requestAgain){
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Go to setting and enable", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        if(allSuccess){
            toast("Permission granted!")
        }
    }

}