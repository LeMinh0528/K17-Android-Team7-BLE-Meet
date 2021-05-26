package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityRegisterPictureBinding
import kotlinx.android.synthetic.main.activity_register_picture.*
import me.shouheng.compress.Compress
import me.shouheng.compress.listener.CompressListener
import me.shouheng.compress.strategy.Strategies
import me.shouheng.compress.strategy.compress.Compressor
import java.io.File


class RegisterPictureActivity : AppCompatActivity() {
    val PICK_IMAGE = 1
    lateinit var viewModel: RegisterPictureViewModel
    lateinit var binding: ActivityRegisterPictureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_picture)
        binding()
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

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if press to choose image
        if (data != null) {
            Log.d("TAG","Pick image: ${data.data}")
        }
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Log.d("TAG","Pick image: ${data.data}")
            val uri: Uri? = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val bitmapDrawable  = BitmapDrawable(bitmap)
//            binding.icAvt.setBackgroundDrawable(bitmapDrawable)
            Glide.with(this)
                .load(uri)
                .into(binding.icAvt)
            var file = File(uri?.path)
            Compress.with(this,file)
                .setQuality(60)
                .setCompressListener(object: CompressListener{
                    override fun onSuccess(result: File?) {
                        Log.d("TAG","Pick image: $result")
                    }

                    override fun onError(throwable: Throwable?) {
                        Log.d("TAG","Error: $throwable")
                    }

                    override fun onStart() {
                        Log.d("TAG","Start compress")
                    }
                })
                .strategy(Strategies.compressor())
                .setMaxHeight(100f)
                .setMaxWidth(100f)
                .launch()
        }
    }

}