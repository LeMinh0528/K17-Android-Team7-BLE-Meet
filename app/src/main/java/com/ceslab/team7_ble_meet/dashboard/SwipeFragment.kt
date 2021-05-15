package com.ceslab.team7_ble_meet.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ceslab.team7_ble_meet.Model.User
import com.ceslab.team7_ble_meet.Model.UserResp
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.rest.RestClient
import com.yuyakaido.android.cardstackview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class SwipeFragment: Fragment(),CardStackListener {
    lateinit private var cardStack: CardStackView
//    private var userList: MutableList<User> = arrayListOf()
    var handler = GetDataRespHandler()
    lateinit var manager: CardStackLayoutManager
//    private val manager by lazy { CardStackLayoutManager(requireContext(), this) }
    lateinit private var adapter : CardStackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_swipe,container,false)
        setupButton(view)
        cardStack = view.findViewById(R.id.cardStack)
        setupAdapter()
        setupCardStack()
        getFirstData()
        cardStack.isNestedScrollingEnabled = false
        cardStack.requestDisallowInterceptTouchEvent(true)

        //diable parent scrollview
        cardStack.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //parent can't affect to cardStack swipe event
                        cardStack.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        })
        return view
    }

    private fun setupButton(view:View) {
        val skip = view.findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStack.swipe()
        }

        val rewind = view.findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStack.rewind()
        }

        val like = view.findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStack.swipe()
        }
    }

    fun setupAdapter(){
        adapter = CardStackAdapter(arrayListOf())
    }

    fun getFirstData(){
        Log.d("CardStackView","Get first data")
        requestAPI()
    }

    fun setupCardStack(){
        manager = CardStackLayoutManager(requireContext(),this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStack.layoutManager = manager
        cardStack.adapter = adapter
        cardStack.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        cardStack.dispatchNestedScroll(0,0,0,0,null)
        if (manager.topPosition == adapter.itemCount - 10) {
            paginate()
        }
    }

    private fun paginate() {
        requestAPI()
    }

    fun requestAPI(){
        handler.setOnResponseListener(object: GetDataRespHandler.OnResponseListener{
            override fun onResponse(message: String, resp: List<User>) {
                Log.d("CardStackView","paginate ")
                val old = adapter.getSpots()
                Log.d("CardStackView","paginate: old: $old ")
                val new = old.plus(resp)
                Log.d("CardStackView","paginate: new: $new ")
                val callback = SpotDiffCallback(old, new)
                val result = DiffUtil.calculateDiff(callback)
                adapter.setSpots(new)
                result.dispatchUpdatesTo(adapter)
            }
        })
        handler.getData()
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    class GetDataRespHandler(){
        private var listener: OnResponseListener? = null
        interface OnResponseListener{
            fun onResponse(message: String, userList: List<User>)
        }
        fun setOnResponseListener(ob: OnResponseListener){
            listener = ob
        }
        fun getData(){
            var page = (1..500).random()
            Log.d("CardStackView" , "page: $page")
            try{
                var call = RestClient.getInstance().API.listUser("en-US",page)
                call.enqueue(object: Callback<UserResp> {
                    override fun onFailure(call: Call<UserResp>, t: Throwable) {
                        Log.d("CardStackView" , "error")
                        listener?.onResponse("FAILED", arrayListOf<User>())
                    }

                    override fun onResponse(call: Call<UserResp>, response: Response<UserResp>) {
                        Log.d("CardStackView" , "success")
                        Log.d("CardStackView" , "response: ${response.body()}")
                        response.body()?.results?.let { listener?.onResponse("SUCCESS", it) }
                    }
                })

            }catch (ex: Exception){
                Log.d("CardStackView" , "$ex")
                listener?.onResponse("FALIED", arrayListOf())
            }
        }
    }
}