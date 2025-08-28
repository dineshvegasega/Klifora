package com.klifora.shop.ui.main.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.CheckoutBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreKeys.MOBILE_NUMBER
import com.klifora.shop.datastore.DataStoreKeys.QUOTE_ID
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.saveData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.models.cart.ItemCart
import com.klifora.shop.models.user.ItemUserItem
import com.klifora.shop.networking.GST_PERCENT
import com.klifora.shop.networking.RAZORPAY_KEY
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.navHostFragment
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.utils.Utility.Companion.isValidEmailId
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import com.razorpay.Checkout
import com.razorpay.PayloadHelper
import com.razorpay.PaymentData
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject


@AndroidEntryPoint
class Checkout : Fragment() {
    private val viewModel: CheckoutVM by viewModels()
    private var _binding: CheckoutBinding? = null
    private val binding get() = _binding!!

    var skuS = ""
    var name = ""
    var qunty = 0
    var totalPrice: Double = 0.0
    var gstTotalPrice: Double = 0.0

    var customerToken = ""
    var orderIDForSend = ""
    var orderID = ""
    var itemCartItem : ItemCart ?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CheckoutBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint(
        "NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n",
        "SuspiciousIndentation"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        MainActivity.mainActivity.get()!!.callBack(0)
        MainActivity.mainActivity.get()!!.callCartApi()


        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            topBarBack.ivCartLayout.visibility = View.GONE
            topBarBack.ivCart.singleClick {
                findNavController().navigate(R.id.action_checkout_to_cart)
            }

            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility =
                    if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(
                        loginUser,
                        ItemUserItem::class.java
                    )
//                    textFNTxt.text = "Name : " + data.contact_person
//                    textCompanyNameTxt.text = "Franchise Name : " + data.name
//                    textMobileTxt.text = "Mobile No : " + data.mobile_number
//                    textAdrressTxt.text = "Address : " + data.register_address
//                    textCityTxt.text = "City : " + data.register_city
//                    textStateTxt.text = "State : " + data.register_state
//                    textPinCodeTxt.text = "Pincode : " + data.register_pincode


                    textNameTxt.text = "Name : " + data.contact_person
                    textCompanyNameTxt.text = "Franchise Name : " + data.name
                    textMobileTxt.text = "Mobile No : " + data.mobile_number
                    textAdrressTxt.text =
                        "Address : " + data.register_address + ", " + data.register_city + ", " + data.register_state + ", " + data.register_pincode

                    textNameTxtS.text = "Name : " + data.contact_person
                    textCompanyNameTxtS.text = "Franchise Name : " + data.name
                    textMobileTxtS.text = "Mobile No : " + data.mobile_number
                    textAdrressTxtS.text =
                        "Address : " + data.d_address + ", " + data.d_city + ", " + data.d_state + ", " + data.d_pincode
                }
            }



            when (loginType) {
                LoginType.CUSTOMER -> {
                    groupVendor.visibility = View.GONE
                    groupGuest.visibility = View.VISIBLE
                    textPayment.text = resources.getString(R.string.select_franchise)
                }

                LoginType.VENDOR -> {
                    groupVendor.visibility = View.VISIBLE
                    groupGuest.visibility = View.VISIBLE
                    textPayment.text = resources.getString(R.string.proceed_to_payment)
                }
            }

            when (loginType) {
                LoginType.CUSTOMER -> {
                    mainThread {
                        val userList: List<CartModel>? = db?.cartDao()?.getAll()
                        binding.apply {
                            rvList.setHasFixedSize(true)
                            rvList.adapter = viewModel.ordersAdapter
                            viewModel.ordersAdapter.notifyDataSetChanged()
                            viewModel.ordersAdapter.submitList(userList)
                        }

                        var price: Double = 0.0
                        userList?.forEach {
                            price += (it.price!! * it.quantity)
                            Log.e(
                                "TAG",
                                "onViewCreated: " + it.name + " it.currentTime " + it.currentTime
                            )
                        }

//                        viewModel.subTotalPrice = price
////                textSubtotalPrice.text = "₹${getPatternFormat("1", price)}"
//
//                        val discountPriceAfter =
//                            (viewModel.subTotalPrice * viewModel.discountPrice) / 100
//                        textDiscountPrice.text = "₹${getPatternFormat("1", discountPriceAfter)}"
//
//                        val priceANDdiscountPrice = price + discountPriceAfter
//
//                        val cstPriceAfter = (priceANDdiscountPrice * viewModel.cgstPrice) / 100
////                textCGSTPrice.text = "₹${getPatternFormat("1", cstPriceAfter)}"
//
//                        val sgstPriceAfter = (priceANDdiscountPrice * viewModel.sgstPrice) / 100
////                textSGSTPrice.text = "₹${getPatternFormat("1", sgstPriceAfter)}"
//
//                        val priceANDGSTPrice =
//                            priceANDdiscountPrice + (cstPriceAfter + sgstPriceAfter) + viewModel.shippingPrice
                        textSubtotalPrice.text = "₹ ${getPatternFormat("1", price)}"
//                        textTotalPrice.text = "₹ ${getPatternFormat("1", price)}"

                        textGST.text = "GST(${GST_PERCENT}%)"
                        var gstValue: Double = 0.0
                        gstValue = (price * GST_PERCENT) / 100
                        textGSTPrice.text = "₹ " + getPatternFormat("1", gstValue)
                        var gstTotalPrice: Double = 0.0
                        gstTotalPrice = price + gstValue
                        textTotalPrice.text = "₹ " + getPatternFormat("1", gstTotalPrice)

//                textShippingPrice.text = "₹${getPatternFormat("1", viewModel.shippingPrice)}"


//                        textDiscount.text = "Discount (${viewModel.discountPrice}%)"
//                textCGST.text = "CGST (${viewModel.cgstPrice}%)"
//                textSGST.text = "SGST (${viewModel.sgstPrice}%)"
                    }
                }

                LoginType.VENDOR -> {


                    mainThread {
                        skuS = ""
                        name = ""
                        qunty = 0
                        totalPrice = 0.0
                        gstTotalPrice = 0.0

                        rvList.adapter = viewModel.cartAdapter
                        readData(CUSTOMER_TOKEN) { token ->
                            viewModel.getCart(token!!) {
                                val itemCart = this
                                itemCartItem = itemCart
                                Log.e("TAG", "getCart " + this.toString())
                                rvList.setHasFixedSize(true)
                                viewModel.cartAdapter.notifyDataSetChanged()
                                viewModel.cartAdapter.submitList(itemCart.items)


                                itemCart.items.forEach {
                                    totalPrice += (it.price * it.qty)
                                    qunty += it.qty
                                    name += it.name + ", "
                                    skuS += it.sku + ", "
                                }
//                                textSubtotalPrice.text = "₹ " + getPatternFormat("1", totalPrice)
//                                textTotalPrice.text = "₹ " + getPatternFormat("1", totalPrice)
//                                textItems.text = "${qunty} Item"

//                                textGST.text = "GST($GST_PERCENT%)"
//                                var gstValue: Double = 0.0
//                                gstValue = (totalPrice * GST_PERCENT) / 100
//                                textGSTPrice.text = "₹ " + getPatternFormat("1", gstValue)
//                                gstTotalPrice = totalPrice + gstValue
//                                textTotalPrice.text = "₹ " + getPatternFormat("1", gstTotalPrice)

                                if (!itemCart.items.isNullOrEmpty()) {
//                                    upperLayout.visibility = View.VISIBLE
                                    filterLayout.visibility = View.VISIBLE
                                } else {
//                                    upperLayout.visibility = View.GONE
                                    filterLayout.visibility = View.GONE
                                }


                                if (itemCart.items.size == 0) {
                                    binding.idDataNotFound.root.visibility = View.VISIBLE
                                } else {
                                    binding.idDataNotFound.root.visibility = View.GONE
                                }
                            }



                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(
                                        loginUser,
                                        ItemUserItem::class.java
                                    )

                                    val shipping_address = JSONObject().apply {
                                        put("region", data.d_state)
                                        put("region_id", data.d_resignid)
                                        put("region_code", data.d_resigncode)
                                        put("country_id", "IN")
                                        put("street", JSONArray().put(data.d_address))
                                        put("postcode", data.d_pincode)
                                        put("city", data.d_city)
                                        put("firstname", data.contact_person)
                                        put("lastname", data.contact_person)
                                        put("email", "")
                                        put("telephone", data.mobile_number)
                                    }

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
                                        put("addressInformation", JSONObject().apply {
                                            put("shipping_address", shipping_address)
                                            put("billing_address", billing_address)
                                            put("shipping_carrier_code", "flatrate")
                                            put("shipping_method_code", "flatrate")
                                        })
                                    }

                                    Log.e(
                                        "TAG",
                                        "jsonObjectaddressInformation " + addressInformation
                                    )

                                    readData(CUSTOMER_TOKEN) { token ->
                                        viewModel.updateShipping(token!!, addressInformation) {
                                            Log.e("TAG", "onCallBack22: ${this.toString()}")

                                            viewModel.getTotal(token!!) {
                                                val totals = this
                                                Log.e("TAG", "totals " + totals.toString())

                                                if (totals.items.size >= 1) {
                                                    textGST.text =
                                                        "GST(${totals.items[0].tax_percent}%)"
                                                }

                                                textSubtotalPrice.text =
                                                    "₹ " + getPatternFormat("1", totals.subtotal)
                                                textGSTPrice.text =
                                                    "₹ " + getPatternFormat("1", totals.tax_amount)
                                                textShippingPrice!!.text = "₹ " + getPatternFormat(
                                                    "1",
                                                    totals.shipping_amount
                                                )
                                                textTotalPrice.text =
                                                    "₹ " + getPatternFormat("1", totals.grand_total)

                                                gstTotalPrice = totals.grand_total
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }





            layoutSort.singleClick {
                when (loginType) {
                    LoginType.VENDOR -> {
                        if (editTextN.text.toString().isEmpty()) {
                            showSnackBar("Enter Full Name")
                        } else if (editEmail.text.toString().isEmpty()) {
                            showSnackBar("Enter Email")
                        } else if (!isValidEmailId(editEmail.text.toString().trim())) {
                            showSnackBar("Enter Valid Email")
                        } else if (editMobileNo.text.toString().isEmpty()) {
                            showSnackBar("Enter Mobile No")
                        } else {
                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(
                                        loginUser,
                                        ItemUserItem::class.java
                                    )

                                    readData(CUSTOMER_TOKEN) { tokenC ->
                                        customerToken = "" + tokenC


                                        val shipping_addressC = JSONObject().apply {
                                            put("region", data.d_state)
                                            put("region_id", data.d_resignid)
                                            put("region_code", data.d_resigncode)
                                            put("country_id", "IN")
                                            put("street", JSONArray().put(data.d_address))
                                            put("postcode", data.d_pincode)
                                            put("city", data.d_city)
                                            put("firstname", data.contact_person)
                                            put("lastname", data.contact_person)
                                            put("email", "")
                                            put("telephone", data.mobile_number)
                                        }

                                        val billing_addressC = JSONObject().apply {
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

                                        val addressInformationC = JSONObject().apply {
                                            put("addressInformation", JSONObject().apply {
                                                put("shipping_address", shipping_addressC)
                                                put("billing_address", billing_addressC)
                                                put("shipping_carrier_code", "flatrate")
                                                put("shipping_method_code", "flatrate")
                                            })
                                        }

                                        viewModel.updateShipping(tokenC!!, addressInformationC) {

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
                                                    put("method", "razorpay")
                                                })
                                            }

                                            Log.e("TAG", "jsonObjectMethod " + addressInformation)
                                            viewModel.createOrder(tokenC!!, addressInformation) {
                                                val orderIDE = this.toString()
                                                orderID = orderIDE

                                                if (orderIDE == "failed"){
                                                } else {
                                                    try {
                                                        cartItemCount = 0
                                                        cartItemLiveData.value = false

                                                        val customerData = JSONObject().apply {
                                                            put("cartId", "" + orderID)
                                                            put(
                                                                "checkout_buyer_name",
                                                                "" + binding.editTextN.text.toString()
                                                            )
                                                            put(
                                                                "checkout_buyer_email",
                                                                "" + binding.editEmail.text.toString()
                                                            )
                                                            put(
                                                                "checkout_purchase_order_no",
                                                                "" + binding.editMobileNo.text.toString()
                                                            )
                                                            put("checkout_goods_mark", "")
                                                        }

                                                        viewModel.postCustomDetails(tokenC, customerData) {
                                                            Log.e(
                                                                "TAG",
                                                                "postCustomDetailsonCallBack22: ${this.toString()}"
                                                            )
                                                            readData(ADMIN_TOKEN) { tokenAdmin ->
                                                                viewModel.orderHistoryListDetail(
                                                                    tokenAdmin!!,
                                                                    orderID
                                                                ) {
                                                                    val itemOrderDetail = this

                                                                    readData(WEBSITE_DATA) { webData ->
                                                                        if (webData != null) {
                                                                            val data = Gson().fromJson(
                                                                                webData,
                                                                                ItemWebsite::class.java
                                                                            )

//                                                                    val customerData = JSONObject().apply {
//                                                                        put("customerEmail", ""+data.email)
//                                                                    }
//
//
//                                                                    val invoiceData = JSONObject().apply {
//                                                                        put("capture", true)
//                                                                        put("notify", true)
//                                                                        put("appendComment", false)
//                                                                        put("comment", JSONObject().apply {
//                                                                            put("comment", "Invoice created for Razrpay transaction")
//                                                                        })
//                                                                        put("is_visible_on_front", 0)
//                                                                    }

                                                                            orderIDForSend =
                                                                                itemOrderDetail.increment_id

                                                                            readData(MOBILE_NUMBER) { number ->
                                                                                val co = Checkout()
                                                                                co.setKeyID(RAZORPAY_KEY)
                                                                                try {
                                                                                    Log.e(
                                                                                        "TAG",
                                                                                        "totalXXXCC: " + gstTotalPrice
                                                                                    )
                                                                                    val total: Double =
                                                                                        gstTotalPrice * 100
                                                                                    Log.e(
                                                                                        "TAG",
                                                                                        "totalXXXDD: " + total
                                                                                    )

//                                                            val sss = 130361.0 * 100
//                                                            Log.e("TAG", "totalXXXEE: "+sss)
                                                                                    val totalX =
                                                                                        total.toInt()

                                                                                    val options =
                                                                                        JSONObject()
                                                                                    options.put(
                                                                                        "name",
                                                                                        resources.getString(
                                                                                            R.string.app_name
                                                                                        )
                                                                                    )
//                                                            options.put("name","Razorpay Corp")
                                                                                    options.put(
                                                                                        "description",
                                                                                        "" + orderIDForSend
                                                                                    )
                                                                                    options.put(
                                                                                        "image",
                                                                                        R.drawable.klifora_logo1
                                                                                    )
                                                                                    options.put(
                                                                                        "currency",
                                                                                        "INR"
                                                                                    )
                                                                                    options.put(
                                                                                        "amount",
                                                                                        "" + totalX
                                                                                    )
                                                                                    options.put(
                                                                                        "send_sms_hash",
                                                                                        true
                                                                                    )
                                                                                    options.put(
                                                                                        "readOnlyName",
                                                                                        "com.klifora.shop"
                                                                                    )
                                                                                    options.put(
                                                                                        "orderId",
                                                                                        "" + skuS
                                                                                    )
                                                                                    options.put(
                                                                                        "theme.color",
                                                                                        "#9F0625"
                                                                                    )

                                                                                    val prefill =
                                                                                        JSONObject()
//                                                    prefill.put("email", "test@razorpay.com")
//                                                    prefill.put("contact", "9988397522")
                                                                                    prefill.put(
                                                                                        "name",
                                                                                        editTextN.text.toString()
                                                                                    )
                                                                                    prefill.put(
                                                                                        "email",
                                                                                        editEmail.text.toString()
                                                                                    )
                                                                                    prefill.put(
                                                                                        "contact",
                                                                                        editMobileNo.text.toString()
                                                                                    )
                                                                                    options.put(
                                                                                        "prefill",
                                                                                        prefill
                                                                                    )
                                                                                    co.open(
                                                                                        requireActivity(),
                                                                                        options
                                                                                    )
                                                                                } catch (e: Exception) {
//                                                        onPaymentError 0 ::: undefined ::: com.razorpay.PaymentData@dc1cb00
                                                                                    Toast.makeText(
                                                                                        requireContext(),
                                                                                        "Error in payment: " + e.message,
                                                                                        Toast.LENGTH_LONG
                                                                                    ).show()
                                                                                    e.printStackTrace()
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
//                                findNavController().navigate(R.id.action_checkout_to_thankyou)
                                                        }
//                                }
//                                .setCancelable(false)
//                                .show()
                                                    } catch (_: Exception) {

                                                    }
                                                }

                                            }
                                        }


                                    }
                                }
                            }
                        }
                    }

                    LoginType.CUSTOMER -> {
                        if (editTextN.text.toString().isEmpty()) {
                            showSnackBar("Enter Full Name")
                        } else if (editEmail.text.toString().isEmpty()) {
                            showSnackBar("Enter Email")
                        } else if (!isValidEmailId(editEmail.text.toString().trim())) {
                            showSnackBar("Enter Valid Email")
                        } else if (editMobileNo.text.toString().isEmpty()) {
                            showSnackBar("Enter Mobile No")
                        } else {

                            mainThread {
                                val userList: List<CartModel>? = db?.cartDao()?.getAll()

                                if (userList?.size!! > 0) {
                                    findNavController().navigate(
                                        R.id.action_checkout_to_selectFranchise,
                                        Bundle().apply {
                                            putString("name", editTextN.text.toString())
                                            putString("email", editEmail.text.toString())
                                            putString("mobile", editMobileNo.text.toString())
                                        })
                                }

//                                val jsonArrayCartItem = JSONArray()
//
//                                userList?.forEach {
//                                    jsonArrayCartItem.apply {
//                                        put(JSONObject().apply {
//                                            put("name", it.name)
//                                            put("price", it.price)
//                                            put("sku", it.sku)
//                                            put("qty", it.quantity)
//                                        })
//                                    }
//                                }
//
//                                val jsonObject = JSONObject().apply {
//                                    put("customerName", editTextN.text.toString())
//                                    put("customerEmail", editEmail.text.toString())
//                                    put("customerMobile", editMobileNo.text.toString())
//                                    put("franchiseCode", "")
//                                    put("status", "pending")
//                                    put("cartItem", jsonArrayCartItem)
//                                }
//
//
//                                val jsonObjectGuestcart = JSONObject().apply {
//                                    put("guestcart", jsonObject)
//                                }
//                                Log.e("TAG", "jsonObjectGuestcart " + jsonObjectGuestcart)
//
//                                val jsonObjectXX = Gson().fromJson(
//                                    jsonObjectGuestcart.toString(),
//                                    JsonElement::class.java
//                                )
//
//                                var dd = jsonObjectXX.getAsJsonObject().get("guestcart")
//
//                                var vv = dd.getAsJsonObject().get("customerMobile")
//                                vv.apply {
//                                    "asfsdfdf"
//                                }
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
//
//                            }
//                        }


                    }
                }
            }
        }
    }





    fun getCheckoutFragment(): com.klifora.shop.ui.main.checkout.Checkout? {
        // Get the NavHostFragment
//        val navHostFragment = getSupportFragmentManager()
//            .findFragmentById(R.id.navigation_bar)

        if (navHostFragment != null) {
            // Get the currently displayed fragment inside the NavHostFragment
            val currentFragment = navHostFragment!!.childFragmentManager.primaryNavigationFragment

            if (currentFragment is com.klifora.shop.ui.main.checkout.Checkout) {
                return currentFragment as com.klifora.shop.ui.main.checkout.Checkout
            }
        }
        return null
    }

    fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val data = Gson().fromJson(
                    loginUser,
                    ItemUserItem::class.java
                )


                val payJSON = JSONObject(p1?.data.toString())
//                Log.e("TAG", "payJSON " + payJSON)

                if (payJSON.has("razorpay_payment_id")) {
                    val payName = payJSON.getString("razorpay_payment_id")
                    Log.e("TAG", "payName " + payName)

                    readData(ADMIN_TOKEN) { tokenAdmin ->
                        readData(WEBSITE_DATA) { webData ->
                            if (webData != null) {
                                val data = Gson().fromJson(
                                    webData,
                                    ItemWebsite::class.java
                                )

                                val customerData = JSONObject().apply {
                                    put("customerEmail", "" + data.email)
                                }


                                val invoiceData = JSONObject().apply {
                                    put("capture", true)
                                    put("notify", true)
                                    put("appendComment", false)
                                    put("comment", JSONObject().apply {
                                        put("comment", "Invoice created for Razrpay transaction")
                                    })
                                    put("is_visible_on_front", 0)
                                }

                                viewModel.getInvoice(tokenAdmin!!, invoiceData, orderID) {

                                    val transactionsData = JSONObject().apply {
                                        put("data", JSONObject().apply {
                                            put("order_id", orderID)
                                            put("razorpay_order_id", "" + payName)
                                            put("razorpay_payment_id", "" + payName)
                                            put("razorpay_signature", "" + payName)
                                        })
                                    }


                                    viewModel.getTransactions(tokenAdmin, transactionsData) {
                                        viewModel.getQuoteId(customerToken, JSONObject()) {
                                            viewModel.resetToken(
                                                tokenAdmin,
                                                data.website_id,
                                                customerData
                                            ) {
                                                val token = this
                                                saveData(CUSTOMER_TOKEN, token)

                                                findNavController().navigate(
                                                    R.id.action_checkout_to_thankyou,
                                                    Bundle().apply {
                                                        putString("orderID", "" + orderIDForSend)
                                                    })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    showSnackBar("Error in payment")
                }
            }
        }
    }


    fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.e(
            "TAG",
            "onPaymentError " + p0.toString() + " ::: " + p1.toString() + " ::: " + p2?.data.toString()
        )
        if (!p2?.data.toString().equals("{}")) {
            try {

                val jsonNull = JSONObject(p1.toString())
                val errorJSON = jsonNull.getJSONObject("error")
                val description = errorJSON.getString("description")



                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(
                            loginUser,
                            ItemUserItem::class.java
                        )

                        readData(ADMIN_TOKEN) { tokenAdmin ->
                            readData(WEBSITE_DATA) { webData ->
                                if (webData != null) {
                                    val data = Gson().fromJson(
                                        webData,
                                        ItemWebsite::class.java
                                    )

                                    val customerData = JSONObject().apply {
                                        put("customerEmail", "" + data.email)
                                    }


                                    val invoiceData = JSONObject().apply {
                                        put("capture", "void")
                                        put("notify", true)
                                        put("appendComment", false)
                                        put("comment", JSONObject().apply {
                                            put(
                                                "comment",
                                                "Invoice created failed for Razorpay transaction"
                                            )
                                        })
                                        put("is_visible_on_front", 0)
                                    }

//                                    viewModel.getInvoice(tokenAdmin!!, invoiceData, orderID) {

                                        viewModel.getCancel(tokenAdmin!!, invoiceData, orderID) {
                                            viewModel.getQuoteId(customerToken, JSONObject()) {
                                                saveData(QUOTE_ID, this)
                                                viewModel.resetToken(
                                                    tokenAdmin,
                                                    data.website_id,
                                                    customerData
                                                ) {
                                                    val token = this
                                                    saveData(CUSTOMER_TOKEN, token)


                                                    mainThread {
                                                        itemCartItem?.let {
                                                            itemCartItem!!.items.forEach { cartItem ->
                                                                readData(QUOTE_ID) {
                                                                    val json: JSONObject =
                                                                        JSONObject().apply {
                                                                            put("sku", cartItem.sku)
                                                                            put("qty", cartItem.qty)
                                                                            put(
                                                                                "quote_id",
                                                                                it.toString()
                                                                            )
                                                                        }
                                                                    val jsonCartItem: JSONObject =
                                                                        JSONObject().apply {
                                                                            put("cartItem", json)
                                                                        }
                                                                    readData(CUSTOMER_TOKEN) { token ->
                                                                        viewModel.addCart(
                                                                            token!!,
                                                                            jsonCartItem
                                                                        ) {
                                                                            //cartMutableList.value = true
                                                                            Log.e(
                                                                                "TAG",
                                                                                "onCallBack: ${this.toString()}"
                                                                            )
//                                                                        showSnackBar(getString(R.string.item_added_to_cart))
                                                                            MainActivity.mainActivity.get()!!
                                                                                .callCartApi()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }



                                                    }



                                                        MaterialAlertDialogBuilder(
                                                        Companion.activity.get()!!,
                                                        R.style.LogoutDialogTheme
                                                    )
                                                        .setTitle(resources.getString(R.string.app_name))
                                                        .setMessage("Payment failed, please try again.")
                                                        .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                                                            dialog.dismiss()
//                        findNavController().navigate(R.id.action_checkout_to_cart2)
                                                        }
//                    .setNegativeButton(resources.getString(R.string.try_again)) { dialog, _ ->
//                        dialog.dismiss()
//                        readData(MOBILE_NUMBER) { number ->
//                            val co = Checkout()
//                            co.setKeyID(RAZORPAY_KEY)
//                            try {
//                                Log.e("TAG", "totalXXXCC: "+gstTotalPrice)
//                                val total : Double = gstTotalPrice * 100
//                                Log.e("TAG", "totalXXXDD: "+total)
//
//                                val totalX = total.toInt()
//
//                                val options = JSONObject()
//                                options.put("name",resources.getString(R.string.app_name))
////                                                            options.put("name","Razorpay Corp")
//                                options.put("description", name)
//                                options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
//                                options.put("currency","INR")
//                                options.put("amount", ""+totalX)
//                                options.put("send_sms_hash",true)
//                                options.put("readOnlyName","com.klifora.shop")
//                                options.put("orderId",""+skuS)
//
//                                val prefill = JSONObject()
////                                                    prefill.put("email", "test@razorpay.com")
////                                                    prefill.put("contact", "9988397522")
//                                prefill.put("name", binding.editTextN.text.toString())
//                                prefill.put("email", binding.editEmail.text.toString())
//                                prefill.put("contact", binding.editMobileNo.text.toString())
//                                options.put("prefill", prefill)
//                                co.open(requireActivity(), options)
//                            }catch (e: Exception){
////                                                        onPaymentError 0 ::: undefined ::: com.razorpay.PaymentData@dc1cb00
//                                Toast.makeText(requireContext(),"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
//                                e.printStackTrace()
//                            }
//                        }
//                    }
                                                        .setCancelable(false)
                                                        .show()

                                                }
                                            }
                                        }

//                                        val transactionsData = JSONObject().apply {
//                                            put("data", JSONObject().apply {
//                                                put("order_id", orderID)
//                                                put("razorpay_order_id", ""+payName)
//                                                put("razorpay_payment_id", ""+payName)
//                                                put("razorpay_signature", ""+payName)
//                                            })
//                                        }


//                                    }
                                }
                            }
                        }


                    }
                }


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        try {
            MaterialAlertDialogBuilder(Companion.activity.get()!!, R.style.LogoutDialogTheme)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage("External wallet was selected : Payment Data: ${p1?.data}")
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}