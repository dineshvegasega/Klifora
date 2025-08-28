package com.klifora.shop.ui.main.productZoom

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProductZoomPagerAdapter(
    private val activity: FragmentActivity,
    private val videoList: ArrayList<String>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return videoList.size
    }

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment {
        return ProductZoomPageFragment(activity, videoList[position], position)
    }
}