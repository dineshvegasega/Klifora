package com.klifora.shop.ui.main.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.klifora.shop.R
import com.klifora.shop.databinding.CategoryBinding
import com.klifora.shop.ui.main.products.ProductsVM.Companion.isProductLoad
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainShopFor
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.singleClick
import com.klifora.shop.utils.updatePagerHeightForChild
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class Category : Fragment() {
    private val viewModel: CategoryVM by viewModels()
    private var _binding: CategoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CategoryBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(1)
        MainActivity.mainActivity.get()!!.callCartApi()
        binding.apply {
//            button.setOnClickListener {
//                findNavController().navigate(R.id.action_category_to_products)
//            }


            topBarOthers.apply {
                ivSearch.singleClick {
                    findNavController().navigate(R.id.action_category_to_search)
                }

                ivCart.singleClick {
                    findNavController().navigate(R.id.action_category_to_cart)
                }
            }

            layoutCustomDesign.layout11.setOnClickListener {
                findNavController().navigate(R.id.action_category_to_customDesign)
            }
//            layoutCustomDesign.ivCustomDesign.setOnClickListener {
//
//            }




            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarOthers.menuBadge.text = "$cartItemCount"
                topBarOthers.menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
            }


//            topBar.apply {
//                textViewTitle.visibility = View.VISIBLE
//                ivSearch.visibility = View.VISIBLE
//                ivCart.visibility = View.VISIBLE
//
//                ivSearch.singleClick {
//                    findNavController().navigate(R.id.action_category_to_search)
//                }
//
//                ivCart.singleClick {
//                    findNavController().navigate(R.id.action_category_to_cart)
//                }
//
//
//                badgeCount.observe(viewLifecycleOwner) {
//                    viewModel.getCartCount() {
//                        Log.e("TAG", "count: $this")
//                        menuBadge.text = "${this}"
//                        menuBadge.visibility = if (this != 0) View.VISIBLE else View.GONE
//                    }
//                }
//
//
//
//
////                    rvList1.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
////                        override fun onPageScrolled(
////                            position: Int,
////                            positionOffset: Float,
////                            positionOffsetPixels: Int
////                        ) {
////                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//////                    if (pageChangeValue != position) {
//////                        Log.e("TAG", "positionA" + position)
//////                    }
//////                    pageChangeValue = position
////                        }
////
////                        override fun onPageSelected(position: Int) {
////                            super.onPageSelected(position)
//////                    adapter1.updatePosition(position)
//////                            viewModel.indicator(binding, viewModel.item1, position)
//////                            pagerAdapter.notifyDataSetChanged()
////                        }
////
////                        override fun onPageScrollStateChanged(state: Int) {
////                            super.onPageScrollStateChanged(state)
////                            Log.e("TAG", "state" + state)
//////                    if (state == 0) {
//////                    adapter1.notifyItemChanged(adapter1.counter)
//////                        onClickItem(pageChangeValue)
//////                    }
////                        }
////                    })
//            }


            viewModel.show()
            mainThread {
                try{
                    val pagerAdapter = CategoryPagerAdapter(requireActivity(), mainShopFor)
//                    pagerAdapter.notifyDataSetChanged()
                    rvList1.offscreenPageLimit = 3
                    rvList1.overScrollMode = OVER_SCROLL_NEVER

                    rvList1.adapter = pagerAdapter
                    delay(200)

                    rvList1.setPageTransformer { page, position ->
                        rvList1.updatePagerHeightForChild(page)
                    }
//                TabLayoutMediator(tabLayout, rvList1) { tab, position ->
//                    tab.text = mainShopFor[position].name
//                }.attach()
                    viewModel.hide()



                    positionSelectFun()


                    linearWomen.singleClick {
                        rvList1.currentItem = 0
                    }

                    linearMen.singleClick {
                        rvList1.currentItem = 1
                    }

                    linearKids.singleClick {
                        rvList1.currentItem = 2
                    }

                    rvList1.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        }


                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            Log.e("TAG", "onPageSelectedAA: $position")
                            viewModel.positionSelect = position
                            positionSelectFun()
                        }


                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)
                            Log.e("TAG", "onPageScrollStateChangedAA: $state")
                        }
                    })
                }catch (e: Exception){

                }
            }
        }
    }

    private fun positionSelectFun() {
        binding.apply {
            if (viewModel.positionSelect == 0) {
                linear1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)
                linear2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)
                linear3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)

                view1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)
                view2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
                view3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
            } else if (viewModel.positionSelect == 1) {
                linear1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)
                linear2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)
                linear3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)

                view1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
                view2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)
                view3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
            } else if (viewModel.positionSelect == 2) {
                linear1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)
                linear2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._D6DBDF)
                linear3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)

                view1.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
                view2.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.white)
                view3.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color._74071E)
            }
        }
    }



    override fun onStop() {
        super.onStop()
        isProductLoad = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isProductLoad = false
    }
}