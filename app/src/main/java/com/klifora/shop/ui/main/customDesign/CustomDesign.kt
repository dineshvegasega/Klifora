package com.klifora.shop.ui.main.customDesign

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
import com.klifora.shop.databinding.CustomDesignBinding
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.utils.Utility.Companion.isValidEmailId
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class CustomDesign : Fragment() {
    private val viewModel: CustomDesignVM by viewModels()
    private var _binding: CustomDesignBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomDesignBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        MainActivity.mainActivity.get()!!.callBack(2)
        MainActivity.mainActivity.get()!!.callCartApi()

//        Log.e("TAG", "onViewCreated: ${isHide.value}")
        binding.apply {

            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }

                topBarBack.ivCart.singleClick {
                    findNavController().navigate(R.id.action_customDesign_to_cart)
                }
            }

            topBarBack.ivCartLayout.visibility = View.VISIBLE


            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

            when(loginType){
                LoginType.VENDOR ->  {
                    readData(WEBSITE_DATA) { webData ->
                        if (webData != null) {
                            val data = Gson().fromJson(
                                webData,
                                ItemWebsite::class.java
                            )
                            editTextN.setText(""+data.name)
                            editEmail.setText(""+data.email)
                            editMobileNo.setText(""+data.mobile_number)
                        }
                    }
                }
                LoginType.CUSTOMER ->  {

                }
            }



            layoutSort.singleClick {
                if (editTextN.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterFullName))
                } else if (editEmail.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterValidEmail))
                } else if (!isValidEmailId(editEmail.text.toString().trim())) {
                    showSnackBar(getString(R.string.EnterValidEmail))
                } else if (editMobileNo.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else if (editMobileNo.text.toString().length != 10){
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else if (editMobileNo.text.toString().startsWith("0")){
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else {
                    val customerJSON: JSONObject = JSONObject().apply {
                        put("sku", "")
                        put("name", editTextN.text.toString())
                        put("email", editEmail.text.toString())
                        put("mobile", editMobileNo.text.toString())
                    }
                    viewModel.customProduct(customerJSON) {
                        showSnackBar("Your query has been send successfully")
                        findNavController().navigate(
                            R.id.action_customDesign_to_thankyou,
                            Bundle().apply {
                                putString("from", "customDesign")
                            })
                    }

                }
            }
        }
    }

}