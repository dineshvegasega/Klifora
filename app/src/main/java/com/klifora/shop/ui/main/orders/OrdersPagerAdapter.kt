package com.klifora.shop.ui.main.orders

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klifora.shop.models.Items

class OrdersPagerAdapter (
    private val activity: FragmentActivity,
    private val videoList: ArrayList<Items>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return videoList.size
    }

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return CustomerOrders(activity, videoList[position], position)
            1 -> return OrderHistory(activity, videoList[position], position)
        }
        return OrderHistory(activity, videoList[position], position)
    }
}