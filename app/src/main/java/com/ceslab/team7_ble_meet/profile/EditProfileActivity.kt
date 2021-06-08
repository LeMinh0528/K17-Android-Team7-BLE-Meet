package Activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityEditprofileBinding
import com.ceslab.team7_ble_meet.dialog.EditGenderDialog
import com.ceslab.team7_ble_meet.profile.EditProfileViewModel
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_editprofile.*
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditprofileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var bottomdialog : BottomSheetDialog? = null
    private  var  REQUEST_CAMERA_AVATAR =2
    private val PICK_IMAGE_AVATAR = 1
    private val REQUEST_CAMERA_BACKGROUND = 3
    private val PICK_IMAGE_BACKGROUND = 4
    private val PERMISSION_REQUEST = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        bindView()
        getData()
    }
    private fun bindView(){
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_editprofile)
        binding.lifecycleOwner = this
        binding.apply {
            viewModel = viewModel
            tvEditavatar.setOnClickListener{
                setUpBottomSheet(REQUEST_CAMERA_AVATAR,PICK_IMAGE_AVATAR)
                bottomdialog?.show()
            }
            tvEditbackground.setOnClickListener{
                setUpBottomSheet(REQUEST_CAMERA_BACKGROUND,PICK_IMAGE_BACKGROUND)
                bottomdialog?.show()
            }
        }
    }

    private fun setUpBottomSheet(cameraCode: Int, galaryCode: Int){
        bottomdialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
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
                startActivityForResult(takePictureintent, cameraCode)
            }else{

            }
            bottomdialog!!.dismiss()
        }
        gallery.setOnClickListener {
            val gallery1 = Intent()
            gallery1.type = "image/*"
            gallery1.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallery1, "chon hinh anh"), galaryCode)
            bottomdialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_AVATAR && data != null){
                Log.d("TAG", "Pick image: ${data.data}")
                val uri: Uri? = data.data
                CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            }
            if(requestCode == REQUEST_CAMERA_AVATAR && data != null){
                val image = data.extras?.get("data") as Bitmap
                var uri = getImageUri(this,image)
                if(uri != null) {
                    CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .setMinCropWindowSize(500, 500)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                }
            }
            if(requestCode == REQUEST_CAMERA_BACKGROUND && data != null){

            }
            if(requestCode == PICK_IMAGE_BACKGROUND && data != null){

            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == RESULT_OK){
                    var uri = result.uri
//                    viewModel.selectedImage = uri
                    Glide.with(this)
                        .load(uri)
                        .into(binding.avatar)

                    val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

//                    viewModel.selected = outputStream.toByteArray()
                }
            }
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, inImage.toString(), null)
        return Uri.parse(path)
    }

    private fun getData(){
        viewModel.getUserInfor {
            if(it.avatar != ""){
                GlideApp.with(this)
                    .load(it.avatar?.let { it1 -> ImagesStorageUtils.pathToReference(it1)})
                    .placeholder(R.drawable.ic_user)
                    .into(binding.avatar)
            }
            if(it.background != ""){
                GlideApp.with(this)
                    .load(it.background?.let { it1 -> ImagesStorageUtils.pathToReference(it1)})
                    .placeholder(R.drawable.backgroud_default)
                    .into(binding.background)
            }
            if(it.bio != ""){
                binding.bio.text = it.bio
            }
            showFirstChip(it.tag)

        }
    }

    private fun showFirstChip(listChip: MutableList<String>) {
        Log.d("InformationFragment", "tags chip: $listChip")
        for (i in listChip) {
            val chip =
                layoutInflater.inflate(R.layout.item_chip_filter, binding.chipGroup, false) as Chip
            chip.text = i
            chip.isClickable = false
            chip.isChecked = true
            chip.setTextColor(ContextCompat.getColor(this, R.color.colorblue100))
            binding.chipGroup.addView(chip)
        }
    }
}