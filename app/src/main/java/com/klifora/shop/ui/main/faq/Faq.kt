package com.klifora.shop.ui.main.faq

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.FaqBinding
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Faq : Fragment() {
    private val viewModel: FaqVM by viewModels()
    private var _binding: FaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FaqBinding.inflate(inflater)
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
            topBarOthers.apply {
                ivSearch.singleClick {
                    findNavController().navigate(R.id.action_faq_to_search)
                }

                ivCart.singleClick {
                    findNavController().navigate(R.id.action_faq_to_cart)
                }
            }



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
//                    findNavController().navigate(R.id.action_faq_to_search)
//                }
//
//                ivCart.singleClick {
//                    findNavController().navigate(R.id.action_faq_to_cart)
//                }
//
//
//                badgeCount.observe(viewLifecycleOwner) {
//                    viewModel.getCartCount(){
//                        Log.e("TAG", "count: $this")
//                        menuBadge.text = "${this}"
//                        menuBadge.visibility = if (this != 0) View.VISIBLE else View.GONE
//                    }
////                    mainThread {
////                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
////                        var countBadge = 0
////                        userList?.forEach {
////                            countBadge += it.quantity
////                        }
////                        menuBadge.text = "${countBadge}"
////                        menuBadge.visibility = if (countBadge != 0) View.VISIBLE else View.GONE
////                    }
//                }
//            }


            rvList2.setHasFixedSize(true)
            rvList2.adapter = viewModel.faqAdapter
            viewModel.faqAdapter.notifyDataSetChanged()
            viewModel.faqAdapter.submitList(viewModel.item1)
        }
    }
}