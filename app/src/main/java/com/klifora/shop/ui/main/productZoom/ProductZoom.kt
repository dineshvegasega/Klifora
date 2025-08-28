package com.klifora.shop.ui.main.productZoom

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.klifora.shop.databinding.ProductZoomBinding
import com.klifora.shop.models.ItemParcelable
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.utils.getRecyclerView
import com.klifora.shop.utils.parcelable
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductZoom  : Fragment() {
    private var _binding: ProductZoomBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        lateinit var pagerAdapter: FragmentStateAdapter
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductZoomBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(0)

        binding.apply {
            ivIconCross.singleClick {
                findNavController().navigateUp()
            }

            val itemParcelable = arguments?.parcelable<ItemParcelable>("arrayList")


            val item1 : ArrayList<String> = ArrayList()

            itemParcelable?.item?.forEach {
                if (it.media_type.endsWith("image")){
                    item1.add(it.file)
                }
            }

            Log.e("TAG", "arrayList: $item1")
            Log.e("TAG", "position: ${itemParcelable?.position}")

            pagerAdapter = ProductZoomPagerAdapter(requireActivity(), item1)
            rvList1.offscreenPageLimit = 1
            rvList1.overScrollMode = OVER_SCROLL_NEVER
            rvList1.adapter = pagerAdapter
            rvList1.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            rvList1.setCurrentItem(itemParcelable!!.position, false)

            Log.e("TAG", "videoList "+item1.size)
            (rvList1.getRecyclerView()
                .getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations =
                false


        }

    }
}