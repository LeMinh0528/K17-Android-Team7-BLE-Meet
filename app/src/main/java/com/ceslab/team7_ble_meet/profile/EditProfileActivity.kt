package Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ActivityEditprofileBinding
import com.ceslab.team7_ble_meet.dialog.*
import com.ceslab.team7_ble_meet.profile.EditPassionsActivity
import com.ceslab.team7_ble_meet.profile.EditProfileViewModel
import com.ceslab.team7_ble_meet.toast
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.ceslab.team7_ble_meet.utils.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.edit_bio_dialog.*
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditprofileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var bottomdialog : BottomSheetDialog? = null
    private var editBioDialog: EditBioDialog? = null
    private  var  REQUEST_CAMERA_AVATAR =2
    private val PICK_IMAGE_AVATAR = 1
    private val REQUEST_CAMERA_BACKGROUND = 3
    private val PICK_IMAGE_BACKGROUND = 4
    private val PERMISSION_REQUEST = 10
    private var setAvatar = false
    private var setBackground = false
    private var permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        bindView()

        if(!checkPermission(this, permission)){
            requestPermissions(permission, PERMISSION_REQUEST)
        }

        setAction()
    }
    private fun bindView(){
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_editprofile)
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun setAction(){
        binding.apply {
            tvEditavatar.setOnClickListener{
                setUpBottomSheet(REQUEST_CAMERA_AVATAR,PICK_IMAGE_AVATAR)
                setAvatar = true
                setBackground = false
                bottomdialog?.show()
            }
            tvEditbackground.setOnClickListener{
                setUpBottomSheet(REQUEST_CAMERA_BACKGROUND,PICK_IMAGE_BACKGROUND)
                setAvatar = false
                setBackground = true
                bottomdialog?.show()
            }
            addbio.setOnClickListener {
                editBioDialog = showConfirm(
                    title = "Edit Bio",
                    message = binding.bio.text.toString(),
                    textYes = "OK",
                    textCancel = "Cancel",
                    object: EditDialogListener {
                        override fun cancel() {
                            editBioDialog?.dismiss()
                        }

                        override fun confirm(result: String) {
                            Log.d("EditProfileActivity","result: $result")
                            if(!NetworkUtils.isNetworkAvailable(this@EditProfileActivity)){
                                toast("Error internet connection!")
                            }else{
                                viewModel.updateBio(result){ status, bio ->
                                    if(status == "SUCCESS"){
                                        binding.bio.text = bio
                                        toast("Update bio successful!")
                                    }else{
                                        toast("Error updating bio!")
                                    }

                                }
                            }

                        }
                    })
            }
            btnBackpress.setOnClickListener {
                finish()
            }
            tvtag.setOnClickListener{
                val intent = Intent(this@EditProfileActivity, EditPassionsActivity::class.java)
//                intent.putExtra("TAG",viewModel.tag)
                startActivity(intent)

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
            Log.d("EditProfileActivity","camera :$cameraCode")
            val takePictureintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureintent.resolveActivity(packageManager)!= null){
                startActivityForResult(takePictureintent, cameraCode)
            }
            bottomdialog!!.dismiss()
        }
        gallery.setOnClickListener {
            Log.d("EditProfileActivity","camera :$galaryCode")

            val gallery1 = Intent()
            gallery1.type = "image/*"
            gallery1.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallery1, "choose image"), galaryCode)
            bottomdialog!!.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if((requestCode == PICK_IMAGE_AVATAR || requestCode == PICK_IMAGE_BACKGROUND )&& data != null){
                Log.d("TAG", "Pick image: ${data.data}")
                val uri: Uri? = data.data
                if (uri != null) {
                    cropImage(uri)
                }

            }
            if((requestCode == REQUEST_CAMERA_AVATAR ||requestCode == REQUEST_CAMERA_BACKGROUND)&& data != null){
                val image = data.extras?.get("data") as Bitmap
                var uri = getImageUri(this,image)
                if(uri != null) {
                    cropImage(uri)
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == RESULT_OK){
                    val uri = result.uri
                    val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    if(!NetworkUtils.isNetworkAvailable(this)){
                        toast("Error internet connection!")
                    }else{
                        if(setAvatar){
                            viewModel.updateAvatar(outputStream.toByteArray()){ status, path ->
                                if(status == "SUCCESS"){
                                    GlideApp.with(this)
                                        .load(ImagesStorageUtils.pathToReference(path))
                                        .placeholder(R.drawable.ic_user)
                                        .into(binding.avatar)
                                    toast("Change image successful!")
                                }else{
                                    toast(path)
                                }
                            }
                        }
                        if(setBackground){
                            viewModel.updateBackground(outputStream.toByteArray()){status, path ->
                                if(status == "SUCCESS"){
                                    GlideApp.with(this)
                                        .load(ImagesStorageUtils.pathToReference(path))
                                        .placeholder(R.drawable.backgroud_default)
                                        .into(binding.background)
                                    toast("Change image successful!")
                                }else{
                                    toast(path)
                                }
                            }
                        }
                    }

//                    viewModel.selected = outputStream.toByteArray()
                }
            }
        }
    }

    private fun cropImage(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
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
            if(it.tag.isNotEmpty()){
                viewModel.tag = it.tag
            }
        }
    }

    private fun showFirstChip(listChip: MutableList<String>) {
        Log.d("InformationFragment", "tags chip: $listChip")
        binding.chipGroup.removeAllViews()
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
    private fun showConfirm(title:String,
                            message: String,
                            textYes: String,
                            textCancel: String,
                            listener: EditDialogListener):
            EditBioDialog{
        Log.d("TAG","onback press")
        val dialog = EditBioDialog.Builder()
            .title(title)
            .message(message)
            .yesText(textYes)
            .cancelText(textCancel)
            .listener(listener)
            .build()
        dialog.show(supportFragmentManager,"CONFIRMATION")
        return dialog
    }
}