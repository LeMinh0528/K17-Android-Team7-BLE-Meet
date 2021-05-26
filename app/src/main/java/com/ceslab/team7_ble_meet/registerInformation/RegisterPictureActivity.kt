package com.ceslab.team7_ble_meet.registerInformation

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterPictureBinding
import com.ceslab.team7_ble_meet.toast
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register_picture.*
import java.io.File


class RegisterPictureActivity : AppCompatActivity() {
    val PICK_IMAGE = 1
    private val PERMISSION_REQUEST = 10
    lateinit var viewModel: RegisterPictureViewModel
    lateinit var binding: ActivityRegisterPictureBinding
    private var permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )
    private var imageStorage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding()

        if(!checkPermission(this, permission)){
            requestPermissions(permission, PERMISSION_REQUEST)
        }
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
                val gallery = Intent()
                gallery.type = "image/*"
                gallery.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(gallery, "chon hinh anh"), PICK_IMAGE)
            }
            btn_continue.setOnClickListener{
                viewmodel?.uploadImage()
            }
        }
        viewModel.userResp.observe(this, Observer { response ->
            if(response != null){
                if(response.type == "NONE" && response.status == "SUCCESS"){
                    gotoDashBoard()
                }
                if(response.type == "NONE" && response.status == "FAILED"){

                }
            }
        })
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
        if (data != null) {
            Log.d("TAG", "Pick image: ${data.data}")
        }
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Log.d("TAG", "Pick image: ${data.data}")
            val uri: Uri? = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val bitmapDrawable  = BitmapDrawable(bitmap)
//            binding.icAvt.setBackgroundDrawable(bitmapDrawable)

            var file = File(uri?.path)
            CropImage.activity(uri)
                .setAspectRatio(1, 1)
                .setMinCropWindowSize(500, 500)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if(resultCode == RESULT_OK){
                var uri = result.uri
                viewModel.selectedImage = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.icAvt)

//                upload to firestore
//                var random = "10837606"
//                var ref = imageStorage.child("images/$random/avatar")
//
//                ref.putFile(uri)
//                    .addOnSuccessListener {
//                        it.metadata?.let { it1 -> Log.d("RegisterPictureActivity", it1.path) }
//                        ref.downloadUrl.addOnSuccessListener {responseUrl ->
//
//                        }
//                    }
//                    .addOnFailureListener{
//                        Log.d("RegisterPictureActivity","${it.message}")
//                    }

            }
        }
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean{
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