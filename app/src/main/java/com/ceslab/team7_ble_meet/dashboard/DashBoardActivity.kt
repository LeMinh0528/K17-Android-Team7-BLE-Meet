package com.ceslab.team7_ble_meet.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.dashboard.bleFragment.BleFragment
import com.ceslab.team7_ble_meet.dashboard.discoverFragment.DiscoverFragment
import com.ceslab.team7_ble_meet.dashboard.inforFragment.InformationFragment
import com.ceslab.team7_ble_meet.dashboard.peoblesFragment.PeoplesFragment
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class DashBoardActivity: AppCompatActivity() {
    lateinit var navigationView : ChipNavigationBar
    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyValueDB.createRef(this)

        bindView()
        checkIntentChat()
        navigationView.setOnItemSelectedListener {
            Log.d("TAG","$it")
            when(it){
                R.id.awesome ->{
                    viewPager.currentItem = 0
                }
                R.id.bluetooth ->{
                    viewPager.currentItem = 1
                }
                R.id.chats ->{
                    viewPager.currentItem = 2
                }
                R.id.info ->{
                    viewPager.currentItem = 3
                }
            }
        }

    }

    private fun checkIntentChat(){
        if(!isTaskRoot){
            Log.d("DashBoard","isTaskRoot")
            finish()
        }
        if(intent != null){
            val isChat:Boolean = intent.getBooleanExtra("isOpenChat",false)
            if(isChat){
                val mIntent = Intent(this,ChatActivity::class.java).apply {
                    putExtra(AppConstants.USER_ID,intent.getStringExtra(AppConstants.USER_ID))
                    putExtra(AppConstants.AVATAR,intent.getStringExtra(AppConstants.AVATAR))
                    putExtra(AppConstants.USER_NAME,intent.getStringExtra(AppConstants.USER_NAME))
                }
                startActivity(mIntent)
            }
        }
    }

    private fun bindView(){
        setContentView(R.layout.activity_dash_board)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        viewPager = findViewById(R.id.viewPager)
        navigationView = findViewById(R.id.navigationView)
        setUpViewPager()
        navigationView.setItemSelected(R.id.awesome,true)
    }

    private fun setUpViewPager(){
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(DiscoverFragment(),"Swipe")
        viewPagerAdapter.addFragment(BleFragment(),"Bluetooth")
        viewPagerAdapter.addFragment(PeoplesFragment(),"Chats")
        viewPagerAdapter.addFragment(InformationFragment(),"Information")
        viewPager.adapter = viewPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("TAG","position: $position, ofset: $positionOffset, pixel: $positionOffsetPixels")

            }
            override fun onPageSelected(position: Int) {
                Log.d("TAG",position.toString())
                when(position){
                    0 -> navigationView.setItemSelected(R.id.awesome,true)
                    1 -> navigationView.setItemSelected(R.id.bluetooth,true)
                    2 -> navigationView.setItemSelected(R.id.chats,true)
                    3 -> navigationView.setItemSelected(R.id.info,true)
                }
            }
        })
        viewPager.offscreenPageLimit = 3
    }
}