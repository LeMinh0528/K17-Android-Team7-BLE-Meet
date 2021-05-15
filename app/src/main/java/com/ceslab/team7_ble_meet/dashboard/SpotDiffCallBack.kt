package com.ceslab.team7_ble_meet.dashboard

import androidx.recyclerview.widget.DiffUtil
import com.ceslab.team7_ble_meet.Model.User

class SpotDiffCallback(
    private val old: List<User>,
    private val new: List<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}