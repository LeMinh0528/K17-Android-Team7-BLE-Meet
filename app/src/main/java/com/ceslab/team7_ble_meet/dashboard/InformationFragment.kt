package com.ceslab.team7_ble_meet.dashboard

import Activity.EditProfileActivity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.databinding.FragmentInformationBinding
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.setting.SettingActivity
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.android.material.chip.Chip
import java.io.File
import java.util.*

class InformationFragment : Fragment() {
    lateinit var binding: FragmentInformationBinding
    private var handler = InformationHandler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_information, container, false)
        binding.lifecycleOwner = this
        binding.apply {
            btnSetting.setOnClickListener {
                gotoSetting()
            }
            btnEdit.setOnClickListener {
                gotoEdit()
            }
        }
        return binding.root
    }

    private fun gotoSetting() {
        val intent = Intent(activity, SettingActivity::class.java)
        startActivity(intent)
    }

    private fun gotoEdit() {
        val intent = Intent(activity, EditProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFirstData()

    }

    private fun getFirstData() {
        handler.getInit { user ->
            binding.tvName.text = user.Name
            if (user.bio != "") {
                binding.tvBio.text = user.bio
            }
            if (user.gender == "Male") {
                binding.icGender.setImageResource(R.drawable.ic_baseline_male)
            } else {
                binding.icGender.setImageResource(R.drawable.ic_baseline_female)
            }
            val year: Int = Calendar.getInstance().get(Calendar.YEAR);
            var dob = user.dob.split("/")
            var current = dob[2].toInt()
            binding.tvAge.text = (year - current).toString()
            binding.tvBio.text = user.bio

            GlideApp.with(this)
                .load(user.avatar?.let { ImagesStorageUtils.pathToReference(it) })
                .placeholder(R.drawable.avatar_default)
                .into(binding.icAvatar)

            showFirstChip(user.tag)
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
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorblue100))
            binding.chipGroup.addView(chip)
        }
    }
}