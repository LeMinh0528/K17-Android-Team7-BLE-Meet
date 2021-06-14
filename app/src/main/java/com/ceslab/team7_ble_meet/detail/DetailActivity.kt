package com.ceslab.team7_ble_meet.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.inforFragment.InformationHandler
import com.ceslab.team7_ble_meet.databinding.ActivityDetailBinding
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.android.material.chip.Chip
import java.util.*

class DetailActivity:AppCompatActivity() {
    lateinit var binding  : ActivityDetailBinding
    private var handler = DetailHandler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail)
        binding.btnBackpress.setOnClickListener {
            finish()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        getFirstData()

    }

    @SuppressLint("SetTextI18n")
    private fun getFirstData() {
        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        if (otherUserId != null) {
            handler.getInit(otherUserId) { user ->
                binding.tvName.text = user.Name
                if (user.bio != "") {
                    binding.tvBio.text = user.bio
                }
                if (user.gender == "Male") {
                    binding.icGender.setImageResource(R.drawable.ic_baseline_male)
                } else {
                    binding.icGender.setImageResource(R.drawable.ic_baseline_female)
                }
                val year: Int = Calendar.getInstance().get(Calendar.YEAR)
                val dob = user.dob.split("/")
                val current = dob[2].toInt()
                binding.tvAge.text = (year - current).toString()
                binding.tvBio.text = user.bio
                binding.tvId.text = "ID: $otherUserId"
                GlideApp.with(this)
                    .load(user.avatar?.let { ImagesStorageUtils.pathToReference(it) })
                    .placeholder(R.drawable.ic_user)
                    .into(binding.icAvatar)
                if(user.background != ""){
                    GlideApp.with(this)
                        .load(user.background?.let{ ImagesStorageUtils.pathToReference(it)})
                        .placeholder(R.drawable.backgroud_default)
                        .into(binding.ln)
                }
                showFirstChip(user.tag)
            }
        }
    }

    private fun showFirstChip(listChip: MutableList<String>) {
        binding.chipGroup.removeAllViews()
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