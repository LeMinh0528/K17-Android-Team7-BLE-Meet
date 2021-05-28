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
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_information.*
import java.io.File
import java.util.*

class InformationFragment: Fragment() {
    lateinit var btnSetting : Button
    private var listChip : MutableList<String> = arrayListOf()
    lateinit var binding: FragmentInformationBinding
    private var handler = InformationHandler()
    private var instance = UsersFireStoreHandler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_information, container, false)
        binding.lifecycleOwner = this
//        btnSetting = view.findViewById(R.id.btn_setting)
//        btnSetting.setOnClickListener{
//            var intent = Intent(activity,SettingActivity::class.java)
//            startActivity(intent)
        binding.apply {
//            btn_setting.setOnClickListener{
//                gotoSetting()
//            }
            btnSetting.setOnClickListener{
                gotoSetting()
            }
            btnEdit.setOnClickListener {
                gotoEdit()
            }
        }
        return binding.root
    }

    private fun gotoSetting(){
        val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
    }

    private fun gotoEdit(){
        val intent = Intent(activity, EditProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFirstData()



    }

    private fun getFirstData(){
        instance.userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener { data ->
                if(data != null){
                    var list: List<String> = data["Tag"] as List<String>
                    listChip.addAll(data["Tag"] as List<String>)

                    //set name
                    binding.tvName.text = data["Name"].toString()
                    showFirstChip()

                    //set age
                    val year: Int = Calendar.getInstance().get(Calendar.YEAR);
                    var dob = data["DayOfBirth"].toString().split("/")
                    var current = dob[2].toInt()
                    binding.tvAge.text = (year - current).toString()

                    //set gender
                    if(data["Gender"].toString() == "Male"){
                        binding.icGender.setImageResource(R.drawable.ic_baseline_male)
                    }else{
                        binding.icGender.setImageResource(R.drawable.ic_baseline_female)
                    }
                }

            }
            .addOnFailureListener{

            }
        //set avatar
        val file = File.createTempFile("file","jpg")
        instance.imageRef.child(KeyValueDB.getUserShortId()).child("avatar")
            .getFile(file)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.icAvatar.setImageBitmap(bitmap)

            }
    }

    private fun showFirstChip(){
        Log.d("InformationFragment", "tags chip: $listChip")
        for(i in listChip){
            val chip = layoutInflater.inflate(R.layout.item_chip_filter, binding.chipGroup, false) as Chip
            chip.text = i
            chip.isClickable = false
            chip.isChecked = true
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorblue100))
            binding.chipGroup.addView(chip)
        }
    }
}