package com.ceslab.team7_ble_meet.dashboard

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.ceslab.team7_ble_meet.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class DashBoardActivity: AppCompatActivity() {
    lateinit var navigationView : ChipNavigationBar
    lateinit var viewPager: ViewPager
    lateinit private var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_dash_board)
        viewPager = findViewById(R.id.viewPager)
        navigationView = findViewById(R.id.navigationView)
        setUpViewPager()
        navigationView.setItemSelected(R.id.awesome,true)
        navigationView.setOnItemSelectedListener {
            Log.d("TAG","$it")
            when(it){
                R.id.awesome ->{
                    viewPager.setCurrentItem(0)
                }
                R.id.info ->{
                    viewPager.setCurrentItem(1)
                }
                R.id.chats ->{
                    viewPager.setCurrentItem(2)
                }
                R.id.bluetooth ->{
                    viewPager.setCurrentItem(3)
                }
            }
        }
    }
    fun setUpViewPager(){
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(SwipeFragment(),"Swipe")
        viewPagerAdapter.addFragment(InfomationFragment(),"Infomation")
        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(ConnectFragment(),"Bluetooth")
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
                    1 ->  navigationView.setItemSelected(R.id.info,true)
                    2 ->  navigationView.setItemSelected(R.id.chats,true)
                    3 ->  navigationView.setItemSelected(R.id.bluetooth,true)
                }
            }
        })
        viewPager.offscreenPageLimit = 3
    }
}