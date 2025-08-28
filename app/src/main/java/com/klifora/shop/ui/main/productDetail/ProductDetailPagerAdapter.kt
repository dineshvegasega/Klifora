package com.klifora.shop.ui.main.productDetail

import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.klifora.shop.models.products.MediaGalleryEntry

class ProductDetailPagerAdapter(
    private val activity: FragmentActivity,
    private val videoList: ArrayList<MediaGalleryEntry>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return videoList.size
    }

    @OptIn(UnstableApi::class)
    override fun createFragment(position: Int): Fragment {
        return ProductDetailPageFragment(activity, videoList[position], position)
    }
}