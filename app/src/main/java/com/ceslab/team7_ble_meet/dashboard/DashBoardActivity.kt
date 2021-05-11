package com.ceslab.team7_ble_meet.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.ceslab.team7_ble_meet.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class DashBoardActivity: AppCompatActivity() {
    lateinit var navigationView : ChipNavigationBar
    lateinit var viewPager: ViewPager
    lateinit private var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        viewPager = findViewById(R.id.viewPager)
        navigationView = findViewById(R.id.navigationView)
        setUpViewPager()
//        navigationView.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.chats -> {
//                    viewPager.setCurrentItem(0)
//                    Log.d("TAG","chats")
//                }
//                R.id.bluetooth -> {
//                    viewPager.setCurrentItem(1)
//                    Log.d("TAG","bluetooth")
//                }
//                R.id.info -> {
//                    viewPager.setCurrentItem(2)
//                    Log.d("TAG","info")
//                }
//            }
//            true
//        }
        navigationView.setItemSelected(R.id.info,true)
        navigationView.setOnItemSelectedListener {
            Log.d("TAG","$it")
            when(it){
                R.id.info ->{
                    viewPager.setCurrentItem(0)
                }
                R.id.chats ->{
                    viewPager.setCurrentItem(1)
                }
                R.id.bluetooth ->{
                    viewPager.setCurrentItem(2)
                }
            }
        }
    }
    fun setUpViewPager(){
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
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

            }
            override fun onPageSelected(position: Int) {
                Log.d("TAG",position.toString())
                when(position){
                    0 ->  navigationView.setItemSelected(R.id.info,true)
                    1 ->  navigationView.setItemSelected(R.id.chats,true)
                    2 ->  navigationView.setItemSelected(R.id.bluetooth,true)
                }
            }
        })
    }
}