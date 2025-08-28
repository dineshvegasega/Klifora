package com.klifora.shop.ui.main.category

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klifora.shop.models.Items

class CategoryPagerAdapter (
    private val activity: FragmentActivity,
    private val videoList: ArrayList<Items>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return videoList.size
    }

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment {
        if (position == 0){
            return CategoryChildTab(activity, videoList[position], position)
        } else if (position == 1){
            return CategoryChildTabMen(activity, videoList[position], position)
        } else {
            return CategoryChildTab(activity, videoList[position], position)
        }
//        return CategoryChildTab(activity, videoList[position], position)
    }
}