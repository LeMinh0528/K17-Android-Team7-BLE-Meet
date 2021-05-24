package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ceslab.team7_ble_meet.R
import kotlinx.android.synthetic.main.item_ble_data_discovered.view.*


class ListDataDiscoveredAdapter: RecyclerView.Adapter<ListDataDiscoveredAdapter.ViewHolder>(){
    interface ListDataDiscoveredAdapterListener{
        fun onClickListen(dataDiscovered: DataDiscoveredModel)
    }
    var listener : ListDataDiscoveredAdapterListener? = null
    var data: List<DataDiscoveredModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        listener?.let { holder.bind(item, it) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId = itemView.findViewById<TextView>(R.id.tvID)
        val tvCharacteristic = itemView.findViewById<TextView>(R.id.tvCharacteristic)
        var p = true
        companion object{
            fun from(parent: ViewGroup) : ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.item_ble_data_discovered, parent, false)
                return ViewHolder(view)
            }
        }

        fun bind(item : DataDiscoveredModel, listener: ListDataDiscoveredAdapterListener){
            tvId.text = item.ID.toString()
            tvCharacteristic.text = item.description
            itemView.setOnClickListener{
                Log.d("MainActivity","item clicked")
                itemView.tvCharacteristic.isSingleLine = p
                p = !p
            }
        }
    }
}