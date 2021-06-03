package com.ceslab.team7_ble_meet.chat

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.Model.ImageMessage
import com.ceslab.team7_ble_meet.Model.Message
import com.ceslab.team7_ble_meet.Model.MessageType
import com.ceslab.team7_ble_meet.Model.TextMessage
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.databinding.ActivityChatBinding
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.MyApplication.Companion.context
import com.ceslab.team7_ble_meet.toast
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.firebase.firestore.ListenerRegistration
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_person.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var TAG = "ChatActivity"
    lateinit var  viewmodel : ChatActivityViewModel
    lateinit var binding : ActivityChatBinding
    private  var otherUserId: String? = ""
    private lateinit var messageListenerRegistration : ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messageSection : Section
    private val PICK_IMAGE = 1
    private var currentChannelId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"name: ${intent.getStringExtra(AppConstants.USER_NAME)}")
        viewmodel = ViewModelProvider(this).get(ChatActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        binding.lifecycleOwner = this
        binding.viewModel = viewmodel
        viewmodel.userName = intent.getStringExtra(AppConstants.USER_NAME)
        Log.d(TAG,"name: ${viewmodel.userName}")

        GlideApp.with(this)
            .load(intent.getStringExtra(AppConstants.AVATAR)?.let {
                ImagesStorageUtils.pathToReference(
                    it
                )
            })
            .placeholder(R.drawable.ic_user)
            .into(binding.avatar)
        //set channel
        otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        if(otherUserId != null){
            UsersFireStoreHandler().getOrCreateChatChannel(otherUserId){ channelId ->
                currentChannelId = channelId
                messageListenerRegistration = UsersFireStoreHandler().addChatListener(channelId,this,this::updateRecyclerView)
                binding.btnSend.setOnClickListener{
                    binding.tvText.clearFocus()
                    val messagetoSend = TextMessage(binding.tvText.text.toString(),Calendar.getInstance().time,
                        KeyValueDB.getUserShortId(),MessageType.TEXT)
                    binding.tvText.setText("")
                    UsersFireStoreHandler().sendMessage(messagetoSend, otherUserId!!,channelId)
                    sendPushnotificationToToken(messagetoSend, otherUserId!!)
                    //update lasttext to endgagedChatChannel
                    UsersFireStoreHandler().userRef.document()
                }
                binding.btnImage.setOnClickListener{
                    val gallery1 = Intent()
                    gallery1.type = "image/*"
                    gallery1.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(gallery1, "chon hinh anh"), PICK_IMAGE)
                }
            }
        }


        binding.apply {
            btnBack.setOnClickListener{
                finish()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //if press to choose image
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE && data != null){
                Log.d("TAG", "Pick image: ${data.data}")
                val uri: Uri? = data.data
                CropImage.activity(uri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            }
//            if(requestCode == REQUEST_CAMERA && data != null){
//                val image = data.extras?.get("data") as Bitmap
//                var uri = getImageUri(this,image)
//                if(uri != null){
//                    Log.d(TAG, "image: ${uri.path}")
//                    CropImage.activity(uri)
//                        .setAspectRatio(1, 1)
//                        .setMinCropWindowSize(500, 500)
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(this);
//                }else{
//                    Log.d(TAG, "image null: ")
//                }
//                Log.d(TAG, "image: $image")
//            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == RESULT_OK){
                    var uri = result.uri


                    val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val outputStream = ByteArrayOutputStream()
                    selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

                    ImagesStorageUtils.uploadMessagePhoto(outputStream.toByteArray()) { path ->
                        Log.d(TAG,"upload image: $path")
                        val imageMessage = ImageMessage(
                            path,
                            Calendar.getInstance().time,
                            KeyValueDB.getUserShortId(),
                            MessageType.IMAGE
                        )
                        otherUserId?.let {
                            UsersFireStoreHandler().sendMessage(imageMessage,
                                it,currentChannelId)
                            sendPushnotificationToToken(imageMessage, it)
                        }

                    }

                }
            }
        }
    }

    private fun updateRecyclerView(messages:List<Item>){
        toast("onMessageChanged")
        fun init(){
            binding.recyclerview.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messageSection = Section(messages)
                    this.add(messageSection)
                }
            }
        }
        fun update() = messageSection.update(messages)

        if(shouldInitRecyclerView){
            init()
            shouldInitRecyclerView = false
        }else update()
        binding.recyclerview.adapter?.itemCount?.minus(1)?.let {
            binding.recyclerview.scrollToPosition(
                it
            )
        }
    }

    override fun onDestroy() {
        UsersFireStoreHandler().removeListener(messageListenerRegistration)
        shouldInitRecyclerView = true
        super.onDestroy()
    }

    private fun sendPushnotificationToToken(message: Message, otherUserId: String){
        UsersFireStoreHandler().getCurrentUser(otherUserId){ user ->
            val tokens = user.token
            UsersFireStoreHandler().getCurrentUser(KeyValueDB.getUserShortId()){ currentUser ->
                tokens.forEach {
                    val token = it

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", message.senderId)
                    data.put("hisImage", currentUser.avatar)
                    data.put("title", currentUser.Name)

                    if(message.type == MessageType.IMAGE){
                        data.put("message", currentUser.Name+" send an image. ")
                    }else{
                        data.put("message", message)
                    }
                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                }
            }

        }
    }

    private fun sendNotification(to: JSONObject) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)

    }


}