package com.klifora.shop.ui.main.selectFranchise

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.SelectFranchiseBinding
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.models.user.ItemUserItem
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject


@AndroidEntryPoint
class SelectFranchise : Fragment() {
    private val viewModel: SelectFranchiseVM by viewModels()
    private var _binding: SelectFranchiseBinding? = null
    private val binding get() = _binding!!


    var franchiseCode = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectFranchiseBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callCartApi()

        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            topBarBack.ivCartLayout.visibility = View.GONE
            topBarBack.ivCart.singleClick {
                findNavController().navigate(R.id.action_selectFranchise_to_cart)
            }


            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

//            topBarSearch.apply {
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
////                viewModel.searchValue.observe(viewLifecycleOwner) {
////                    binding.topBarSearch.editTextSearch.setText(it)
////                }
////
////                viewModel.searchDelete.observe(viewLifecycleOwner) {
////                    if (it) {
////                        openSearchHistory()
////                    }
////                }
////                editTextSearch.singleClick {
////                    openSearchHistory()
////                }
//
////                editTextSearch.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
////                    if (hasFocus) {
////                        openSearchHistory()
////                    }
////                })
//            }

//            val iconTypeSearch = when (resources.getInteger(R.integer.layout_value)){
//                1 -> R.drawable.baseline_search_24
//                2 -> R.drawable.baseline_search_32
//                3 -> R.drawable.baseline_search_50
//                else -> R.drawable.baseline_search_24
//            }
//            Log.e("TAG", "onViewCreated: "+resources.getInteger(R.integer.layout_value))
//            topBarSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconTypeSearch, 0)







//            val jsonObject = Gson().fromJson(data, JSONObject::class.java)
//            Log.e("TAG", "jsonObject: "+jsonObject)
//
////            jsonObject.getJSONObject("guestcart").getString("franchiseCode").apply {
////                this.
////            }

            ivEditSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (ivEditSearch.text.toString().isEmpty()) {
                        showSnackBar("Enter Franchise Code")
                    } else {
                        viewModel.customerDetail(ivEditSearch.text.toString()) {
                            Log.e("TAG", "itCCC " + this)
                            if (this == "false") {
                                textBillingDetails.visibility = View.GONE
                                layoutTop.visibility = View.GONE
                                filterLayout.visibility = View.GONE
                                viewModel.selectedPosition = -1
                                franchiseCode = ""
                            } else {
                                val data = Gson().fromJson(
                                    this,
                                    ItemUserItem::class.java
                                )
//                                textTitleB.text = "Name : " + data.contact_person
                                textTitleB.text = "Name : " + data.name
                                textAddr.text = "Address : "+data.register_address +", "+ data.register_city+", "+ data.register_state
                                textPincode.text = "Pincode : " + data.register_pincode
                                textContact.text = "Contact : " + data.mobile_number

//                                textCityTxt.text = "City : " + data.register_city
//                                textStateTxt.text = "State : " + data.register_state
//                                textPinCodeTxt.text = "Pincode : " + data.register_pincode
                                textBillingDetails.visibility = View.VISIBLE
                                layoutTop.visibility = View.VISIBLE
                                filterLayout.visibility = View.VISIBLE
                                viewModel.selectedPosition = 1
                                franchiseCode = data.name

//                                rvList.setHasFixedSize(true)
//                                rvList.adapter = viewModel.franchiseListAdapter
//                                viewModel.franchiseListAdapter.notifyDataSetChanged()
//                                var itemFranchise = ItemFranchise(
//                                    contact_person = data.contact_person,
//                                    d_address =
//                                    d_data.name,
//                                    data.register_address,
//                                    data.d_pincode,
//                                    data.mobile_number
//                                )
//                                viewModel.franchiseListAdapter.submitList(arrayListOf(ItemFranchise(data.name, data.register_address, data.d_pincode, data.mobile_number)))
//                                rvList.visibility = View.VISIBLE

                            }
                        }
                    }
                }
                true
            }


//            viewModel.franchiseList(){
//                val fList = this
//                rvList.setHasFixedSize(true)
//                rvList.adapter = viewModel.franchiseListAdapter
//                viewModel.franchiseListAdapter.notifyDataSetChanged()
//                viewModel.franchiseListAdapter.submitList(fList)
//                rvList.visibility = View.VISIBLE
//
//                ivEditSearch.addTextChangedListener(object : TextWatcher {
//                    override fun afterTextChanged(s: Editable?) {
//                    }
//
//                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    }
//
//                    @SuppressLint("SuspiciousIndentation")
//                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                        val filteredNot = fList.filter { it.name.lowercase().contains(ivEditSearch.text.toString().lowercase()) }
//                        Log.e("TAG", "filteredNot "+filteredNot.toString())
//                        viewModel.franchiseListAdapter.submitList(filteredNot)
//                        viewModel.franchiseListAdapter.notifyDataSetChanged()
//                    }
//                })
//
//
//            }


            layoutSort.singleClick {
                when (loginType) {
                    LoginType.VENDOR -> {
//                        findNavController().navigate(R.id.action_orderSummary_to_home)
                    }

                    LoginType.CUSTOMER -> {
//                        if(viewModel.selectedPosition != -1){
//                        findNavController().navigate(R.id.action_selectFranchise_to_thankyou)
//                        }


                        if (franchiseCode == "") {
                            showSnackBar("Search Franchise Here")
                        } else {
                            mainThread {
                                val userList: List<CartModel>? = db?.cartDao()?.getAll()

                                val jsonArrayCartItem = JSONArray()

                                userList?.forEach {
                                    jsonArrayCartItem.apply {
                                        put(JSONObject().apply {
                                            put("name", it.name)
                                            put("price", it.price)
                                            put("sku", it.sku)
                                            put("qty", it.quantity)
                                        })
                                    }
                                }

                                val jsonObject = JSONObject().apply {
                                    put("customerName", arguments?.getString("name"))
                                    put("customerEmail", arguments?.getString("email"))
                                    put("customerMobile", arguments?.getString("mobile"))
                                    put("franchiseCode", franchiseCode)
                                    put("status", "pending")
                                    put("cartItem", jsonArrayCartItem)
                                }



                                val jsonObjectGuestcart = JSONObject().apply {
                                    put("guestcart", jsonObject)
                                }
                                Log.e("TAG", "jsonObjectGuestcart " + jsonObjectGuestcart)


                                viewModel.placeOrderGuest(jsonObjectGuestcart){
                                    Log.e("TAG", "placeOrderGuest " + this)
                                    if (this.toString().contains("success")){
                                        mainThread {
                                            userList?.forEach {
                                                db?.cartDao()?.delete(it)
                                            }
                                            findNavController().navigate(R.id.action_selectFranchise_to_thankyou)
                                        }
                                    } else {
                                        showSnackBar("Something went wrong!")
                                    }
                                }


//
//
//                                Log.e("TAG", "jsonObjectXX " + jsonObjectXX)
////                            findNavController().navigate(
////                                R.id.action_checkout_to_selectFranchise,
////                                Bundle().apply {
//////                                    putString("name", editTextN.text.toString())
//////                                    putString("email", editEmail.text.toString())
//////                                    putString("mobile", editMobileNo.text.toString())
////                                })
//                            }
                            }
                        }
                    }
                }
            }

        }


    }
}