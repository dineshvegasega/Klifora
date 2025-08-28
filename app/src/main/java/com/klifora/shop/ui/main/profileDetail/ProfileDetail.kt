package com.klifora.shop.ui.main.profileDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.ProfileDetailBinding
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.user.ItemUserItem
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetail : Fragment() {
    private val viewModel: ProfileDetailVM by viewModels()
    private var _binding: ProfileDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileDetailBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(0)
        MainActivity.mainActivity.get()!!.callCartApi()

        binding.apply {

            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }

                topBarBack.ivCart.singleClick {
                    findNavController().navigate(R.id.action_profileDetail_to_cart)
                }
            }

            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
            }



            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null){
                   val data = Gson().fromJson(loginUser,
                        ItemUserItem::class.java
                    )
                    textNameTxt.text = "Name : "+data.contact_person
                    textCompanyNameTxt.text = "Franchise Name : "+data.name
                    textMobileTxt.text = "Mobile No : "+data.mobile_number
                    textAdrressTxt.text = "Address : "+data.register_address +", "+ data.register_city+", "+ data.register_state+", "+ data.register_pincode

                    textNameTxtS.text = "Name : "+data.contact_person
                    textCompanyNameTxtS.text = "Franchise Name : "+data.name
                    textMobileTxtS.text = "Mobile No : "+data.mobile_number
                    textAdrressTxtS.text = "Address : "+data.d_address +", "+ data.d_city+", "+ data.d_state+", "+ data.d_pincode

                    textAccountHolderName.text = "Account Holder : "+data.account_number
                    textAccountNumber.text = "Account Number : "+data.bank_account_number
                    textAccountIFSC.text = "IFSC Code : "+data.ifsc_number
                    textGST.text = "GST Number : "+data.gst

                }
            }

//            topBar.apply {
//                textViewTitle.visibility = View.VISIBLE
//                ivSearch.visibility = View.VISIBLE
//                ivCart.visibility = View.VISIBLE
//                textViewTitle.text = "Profile Detail"
//
//                appicon.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        MainActivity.context.get()!!,
//                        R.drawable.baseline_west_24
//                    )
//                )
//
//                appicon.singleClick {
//                    findNavController().navigateUp()
//                }
//
//
//                ivSearch.singleClick {
//                    findNavController().navigate(R.id.action_profileDetail_to_search)
//                }
//
//                ivCart.singleClick {
//                    findNavController().navigate(R.id.action_profileDetail_to_cart)
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
        }


    }
}