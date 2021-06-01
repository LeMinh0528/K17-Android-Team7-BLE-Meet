package com.ceslab.team7_ble_meet.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.Model.UserAPI
import com.ceslab.team7_ble_meet.R

class CardStackAdapter(
    private var spots: List<UserAPI> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_spots, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        holder.name.text = "${spot.name}"
        holder.city.text = spot.id.toString()
        Glide.with(holder.image)
            .load(spot.getImagePath())
            .into(holder.image)
        holder.itemView.setOnClickListener { v ->
            Toast.makeText(v.context, spot.name, Toast.LENGTH_SHORT).show()
            Log.d("Adapter","image: ${spot.getImagePath()}")
        }
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<UserAPI>) {
        this.spots = spots
    }

    fun getSpots(): List<UserAPI> {
        return spots
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var city: TextView = view.findViewById(R.id.item_city)
        var image: ImageView = view.findViewById(R.id.item_image)
    }

}