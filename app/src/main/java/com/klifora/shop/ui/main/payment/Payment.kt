package com.klifora.shop.ui.main.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.PaymentBinding
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.user.ItemUserItem
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class Payment : Fragment() {
    private val viewModel: PaymentVM by viewModels()

    private var _binding: PaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(0)
        //MainActivity.mainActivity.get()!!.callCartApi()
        binding.apply {
            topBarBack.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }

                ivCartLayout.visibility = View.GONE
            }



            layoutSort.singleClick {
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(
                            loginUser,
                            ItemUserItem::class.java
                        )

                        val billing_address = JSONObject().apply {
                            put("region", data.register_state)
                            put("region_id", data.register_resignid)
                            put("region_code", data.register_resigncode)
                            put("country_id", "IN")
                            put("street", JSONArray().put(data.register_address))
                            put("postcode", data.register_pincode)
                            put("city", data.register_city)
                            put("firstname", data.contact_person)
                            put("lastname", data.contact_person)
                            put("email", "")
                            put("telephone", data.mobile_number)
                        }

                        val addressInformation = JSONObject().apply {
                                put("billing_address", billing_address)
                                put("paymentMethod", JSONObject().apply {
                                    put("method", "checkmo")
                                })
                        }

                        Log.e("TAG", "jsonObjectMethod " + addressInformation)

                        readData(CUSTOMER_TOKEN) { token ->
                            viewModel.createOrder(token!!, addressInformation) {
                                Log.e("TAG", "createOrderonCallBack: ${this.toString()}")
                                findNavController().navigate(R.id.action_payment_to_thankyou)

//                                readData(QUOTE_ID) {quoteId->
//                                    val customerData = JSONObject().apply {
//                                        put("cartId", quoteId)
//                                        put("customFields", JSONObject().apply {
//                                            put("checkout_buyer_name", arguments?.getString("name"))
//                                            put("checkout_buyer_email", arguments?.getString("email"))
//                                            put("checkout_purchase_order_no", arguments?.getString("mobile"))
//                                            put("checkout_goods_mark", "")
//                                        })
//                                    }
//
//                                    viewModel.postCustomDetails(token!!, customerData) {
//                                        Log.e("TAG", "postCustomDetailsonCallBack: ${this.toString()}")
//                                        findNavController().navigate(R.id.action_payment_to_thankyou)
//                                    }
//                                }
                            }
                        }
                    }
                }

            }
        }
    }
}