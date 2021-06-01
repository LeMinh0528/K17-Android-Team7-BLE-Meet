package com.ceslab.team7_ble_meet.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceslab.team7_ble_meet.AppConstants
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.dashboard.recyclerview.PersonItem
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*

class PeoplesFragment: Fragment() {
    private val TAG = "ChatsFragment"
    private lateinit var userListenerRegistration: ListenerRegistration

    private var shouldInitRecyclerView = true

    private lateinit var peopleSection: Section

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats,container,false)
        userListenerRegistration = UsersFireStoreHandler().addUserListener(requireContext(),this::updateRecyclerView)


        return view
    }

    private fun updateRecyclerView(items: List<PersonItem>){
        fun init(){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeoplesFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onClick)
                }
            }
            shouldInitRecyclerView = false
        }
        fun updateItems() = peopleSection.update(items)
        if(shouldInitRecyclerView){
            init()
        }else{
            updateItems()
        }

    }
    private val onClick = OnItemClickListener{item, view ->
        Log.d(TAG,"onClick item: ${item.id}")
        Log.d(TAG,"onClick item: ${item}")
       if(item is PersonItem){

           val intent = Intent(requireContext(),ChatActivity::class.java)
           intent.putExtra(AppConstants.USER_NAME,item.userName)
           intent.putExtra(AppConstants.USER_ID,item.userId)
           intent.putExtra(AppConstants.AVATAR,item.imagePath)


           startActivity(intent)
       }

    }

    override fun onPause() {
        Log.d(TAG,"onPause")
        UsersFireStoreHandler().removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
        super.onPause()

    }
    override fun onDestroy() {
        Log.d(TAG,"onDestroy")
        super.onDestroy()
        Log.d(TAG,"onDestroy")
        UsersFireStoreHandler().removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }
}