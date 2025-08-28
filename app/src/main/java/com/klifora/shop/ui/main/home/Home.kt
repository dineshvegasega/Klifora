package com.klifora.shop.ui.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.HomeBinding
import com.klifora.shop.ui.main.products.ProductsVM.Companion.isProductLoad
//import com.klifora.shop.ui.main.search.SearchVM.Companion.itemProductsAdd
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Home : Fragment() {
    private val viewModel: HomeVM by viewModels()
    private var _binding: HomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        @JvmStatic
        lateinit var adapter1: ListAdapter1

//        @JvmStatic
//        lateinit var adapter2: ListAdapter2

//        @JvmStatic
//        lateinit var adapter3: ListAdapter3
    }

//    var item1 : ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(1)
        MainActivity.mainActivity.get()!!.callCartApi()


//        item1.add("1")
//        item1.add("2")
//        item1.add("3")

        adapter1 = ListAdapter1()
//        adapter2 = ListAdapter2()
//        adapter3 = ListAdapter3()


        binding.apply {
//            button.setOnClickListener {
//                findNavController().navigate(R.id.action_home_to_products)
//            }

            layoutCustomDesign.layout11.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_customDesign)
            }

//            adapter1.submitData(viewModel.item1)
//            adapter1.notifyDataSetChanged()
//            binding.rvList1.offscreenPageLimit = 1
//            binding.rvList1.overScrollMode = OVER_SCROLL_NEVER
//            binding.rvList1.adapter = adapter1
//            binding.rvList1.setPageTransformer { page, position ->
//                binding.rvList1.updatePagerHeightForChild(page)
//            }
//            viewModel.indicator(binding, viewModel.item1, 1)
//
//            rvList1.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageScrolled(
//                    position: Int,
//                    positionOffset: Float,
//                    positionOffsetPixels: Int
//                ) {
//                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
////                    if (pageChangeValue != position) {
////                        Log.e("TAG", "positionA" + position)
////                    }
////                    pageChangeValue = position
//                }
//
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
////                    adapter1.updatePosition(position)
//                    viewModel.indicator(binding, viewModel.item1, position)
//                }
//
//                override fun onPageScrollStateChanged(state: Int) {
//                    super.onPageScrollStateChanged(state)
//                    Log.e("TAG", "state" + state)
////                    if (state == 0) {
////                    adapter1.notifyItemChanged(adapter1.counter)
////                        onClickItem(pageChangeValue)
////                    }
//                }
//            })

//            adapter2.submitData(mainCategory)
//            adapter2.notifyDataSetChanged()
//            binding.rvList2.adapter = adapter2


            rvList1.setHasFixedSize(true)
            rvList1.adapter = viewModel.categoryAdapter
            viewModel.categoryAdapter.notifyDataSetChanged()
            viewModel.categoryAdapter.submitList(mainCategory)



            viewModel.banner {
                Log.e("TAG", "bannerthis: $this")
                rvList3.setHasFixedSize(true)
                rvList3.adapter = viewModel.bannerAdapter
                viewModel.bannerAdapter.notifyDataSetChanged()
                viewModel.bannerAdapter.submitList(this)
            }

//            adapter3.submitData(viewModel.item3)
//            adapter3.notifyDataSetChanged()
//            binding.rvList3.adapter = adapter3



            topBar.apply {
                ivSearch.visibility = View.VISIBLE
                ivCart.visibility = View.VISIBLE

                ivSearch.singleClick {
                    findNavController().navigate(R.id.action_home_to_search)
                }

                ivCart.singleClick {
                    findNavController().navigate(R.id.action_home_to_cart)
                }

                cartItemLiveData.value = false
                cartItemLiveData.observe(viewLifecycleOwner) {
                    menuBadge.text = "$cartItemCount"
                    menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
//                    if (LoginType.CUSTOMER == loginType) {
//                        mainThread {
//                            val userList: List<CartModel>? = db?.cartDao()?.getAll()
//                            cartItemCount = 0
//                            userList?.forEach {
//                                cartItemCount += it.quantity
//                            }
//                            menuBadge.text = "${cartItemCount}"
//                            menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
//                        }
//
//                        menuBadge.text = "${cartItemCount}"
//                        menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
//                    }
//                    if (LoginType.VENDOR == loginType) {
//                        readData(CUSTOMER_TOKEN) { token ->
//                            viewModel.getCart(token!!) {
//                                val itemCart = this
//                                Log.e("TAG", "getCart " + this.toString())
//                                cartItemCount = 0
//                                itemCart.items.forEach {
//                                    cartItemCount += it.qty
//                                }
//
//                                menuBadge.text = "${cartItemCount}"
//                                menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
//                            }
//                        }
//
//                        menuBadge.text = "$cartItemCount"
//                        menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
//                    }
                }
            }


            textAboutUsSeemore?.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_aboutUs)
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