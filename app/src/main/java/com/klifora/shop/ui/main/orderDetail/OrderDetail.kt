package com.klifora.shop.ui.main.orderDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.klifora.shop.R
import com.klifora.shop.databinding.OrderDetailBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.QUOTE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.models.guestOrderList.ItemGuestOrderListItem
import com.klifora.shop.networking.GST_PERCENT
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.utils.changeDateFormat
import com.klifora.shop.utils.getDate
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.parcelable
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import kotlin.collections.forEach


@AndroidEntryPoint
class OrderDetail : Fragment() {
    private var _binding: OrderDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderDetailVM by viewModels()

    companion object {
        var orderDetailLiveA = MutableLiveData<Boolean>(false)
        var orderDetailLiveB = MutableLiveData<Boolean>(false)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = OrderDetailBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callBack(2)
        MainActivity.mainActivity.get()!!.callCartApi()

        binding.apply {
//            rvListCategory1.setHasFixedSize(true)
//            viewModel.customerOrders.notifyDataSetChanged()
//            viewModel.customerOrders.submitList(viewModel.ordersTypesArray)
//            rvListCategory1.adapter = viewModel.customerOrders


            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }


            topBarBack.ivCart.singleClick {
                findNavController().navigate(R.id.action_orderDetail_to_cart)
            }

            topBarBack.ivCartLayout.visibility = View.VISIBLE

            timeline1.initLine(1)
            timeline2.initLine(0)
            timeline3.initLine(0)
            timeline4.initLine(0)
            timeline5.initLine(0)
            timeline6.initLine(0)
            timeline7.initLine(2)



            if (LoginType.CUSTOMER == loginType) {
                btComplainFeedback.visibility = View.GONE
            } else if (LoginType.VENDOR == loginType) {
                btComplainFeedback.visibility = View.VISIBLE
            }


            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility =
                    if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

            if (arguments?.getString("from") == "customerOrders") {
                val consentIntent = arguments?.parcelable<ItemGuestOrderListItem>("key")

                Log.e("TAG", "onViewCreatedAA: ${consentIntent.toString()}")

                textOrderNo.visibility = View.GONE
                btComplainFeedback.visibility = View.GONE

                textName.text = consentIntent?.CustomerName
                textMobile.text = consentIntent?.customerMobile
                textEmail.text = consentIntent?.customerEmail

                textDate.text = consentIntent?.updatedtime?.changeDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    "dd-MMM-yyyy"
                )
//                textTime.text =
//                    consentIntent?.updatedtime?.getDate()

                textTime.text =
                    consentIntent?.updatedtime?.changeDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                "hh:mm a"
                )

                Log.e("TAG", "ddddxx "+consentIntent?.updatedtime?.getDate())

                val typeToken = object : TypeToken<List<CartItem>>() {}.type
                val changeValue =
                    Gson().fromJson<List<CartItem>>(
                        Gson().fromJson(
                            consentIntent?.cartItem,
                            JsonElement::class.java
                        ), typeToken
                    )

                rvListCategory1.setHasFixedSize(true)
                viewModel.orderSKUCustomerOrders.notifyDataSetChanged()
                viewModel.orderSKUCustomerOrders.submitList(changeValue)
                rvListCategory1.adapter = viewModel.orderSKUCustomerOrders

                var price = 0.0
                changeValue.forEach {
                    price += it.price * it.qty
                }

                textSubTotalPrice.text = "₹ " + getPatternFormat("1", price)
//                textTotalAmountPrice.text = "₹ " + getPatternFormat("1", price)

                textGST.text = "GST(${GST_PERCENT}%)"
                var gstValue: Double = 0.0
                gstValue = (price * GST_PERCENT) / 100
                textGSTPrice.text = "₹ " + getPatternFormat("1", gstValue)
                var gstTotalPrice: Double = 0.0
                gstTotalPrice = price + gstValue
                textTotalAmountPrice.text = "₹ " + getPatternFormat("1", gstTotalPrice)

                if (consentIntent?.status == "pending") {
                    layoutSort.visibility = View.VISIBLE
                } else {
                    layoutSort.visibility = View.GONE
                }


                layoutSort.singleClick {
                    mainThread {
                        changeValue.forEach {cartItem ->
                            readData(QUOTE_ID) {
                                val json: JSONObject = JSONObject().apply {
                                    put("sku", cartItem.sku)
                                    put("qty", cartItem.qty)
                                    put("quote_id", it.toString())
                                }
                                val jsonCartItem: JSONObject = JSONObject().apply {
                                    put("cartItem", json)
                                }
                                readData(CUSTOMER_TOKEN) { token ->
                                    viewModel.addCart(token!!, jsonCartItem) {
                                        //cartMutableList.value = true
                                        Log.e("TAG", "onCallBack: ${this.toString()}")
                                        showSnackBar(getString(R.string.item_added_to_cart))
                                        MainActivity.mainActivity.get()!!.callCartApi()
                                    }
                                }
                            }
                        }



                        val jsonObjectStatus = JSONObject().apply {
                            put("id", consentIntent?.guestcustomeroder_id)
                            put("status", "complete")
                        }
                        viewModel.updateStatus(jsonObjectStatus) {
                            Log.e("TAG", "placeOrderGuest " + this)
                            if (this.toString().contains("updated")) {
                                layoutSort.visibility = View.GONE
                                MainActivity.mainActivity.get()!!.callCartApi()
                            } else {
                                showSnackBar("Something went wrong!")
                            }
                        }
                    }
                }
                textTitle0.visibility = View.GONE
                layoutStatus.visibility = View.GONE
            } else if (arguments?.getString("from") == "orderHistory") {
//                val consentIntent = arguments?.parcelable<Item>("key")
                val _id = arguments?.getString("_id")
//                Log.e("TAG", "onViewCreatedBB: ${_id.toString()}")

                btComplainFeedback.singleClick {
                    findNavController().navigate(
                        R.id.action_orderDetail_to_createNew,
                        Bundle().apply {
                            putString("order_id", _id)
                            putString("from", "order")
                        })
                }


                _id?.let {
                    readData(ADMIN_TOKEN) { token ->
                        viewModel.orderHistoryListDetail(token.toString(), _id) {
                            val itemOrderDetail = this
                            Log.e("TAG", "itemOrderDetailXX: ${itemOrderDetail.toString()}")
                            textOrderNo.text = "Order No. : " + itemOrderDetail?.increment_id
                            textOrderNo.visibility = View.VISIBLE

                            textDate.text = itemOrderDetail?.updated_at?.changeDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                "dd-MMM-yyyy"
                            )
//                            textTime.text =
//                                itemOrderDetail?.updated_at?.changeDateFormat(
//                                    "yyyy-MM-dd HH:mm:ss",
//                                    "HH:mm"
//                                )

                            textTime.text =
                                itemOrderDetail?.updated_at?.getDate()

                            rvListCategory1.setHasFixedSize(true)
                            viewModel.orderSKUOrderHistory.notifyDataSetChanged()
                            viewModel.orderSKUOrderHistory.submitList(itemOrderDetail?.items)
                            rvListCategory1.adapter = viewModel.orderSKUOrderHistory

                            layoutSort.visibility = View.GONE


                            Log.e("TAG", "itemOrderDetailAA " + itemOrderDetail?.base_subtotal)
                            textSubTotalPrice.text =
                                "₹ " + getPatternFormat("1", itemOrderDetail?.base_subtotal)

                            textGSTPrice.text =
                                "₹ " + getPatternFormat("1", itemOrderDetail?.tax_amount)

                            textShippingPrice!!.text =
                                "₹ " + getPatternFormat("1", itemOrderDetail?.shipping_amount)

                            textTotalAmountPrice.text =
                                "₹ " + getPatternFormat("1", itemOrderDetail?.base_grand_total)

//                            textGSTPrice.text = "₹" +itemOrderDetail?.base_shipping_incl_tax
//                            textTotalAmountPrice.text =
//                                "₹ " + getPatternFormat("1", itemOrderDetail?.base_subtotal)

//                            textGST.text = "GST(${GST_PERCENT}%)"
//                            var gstValue: Double = 0.0
//                            gstValue = (itemOrderDetail!!.base_subtotal * GST_PERCENT) / 100
//                            textGSTPrice.text = "₹ " + getPatternFormat("1", gstValue)
//                            var gstTotalPrice: Double = 0.0
//                            gstTotalPrice = itemOrderDetail!!.base_subtotal + gstValue
//                            textTotalAmountPrice.text = "₹ " + getPatternFormat("1", gstTotalPrice)


                            if(itemOrderDetail.items.size >= 1){
                                textGST.text = "GST(${itemOrderDetail.items[0].tax_percent}%)"
                            }


                            textTitle0.visibility = View.VISIBLE
                            layoutStatus.visibility = View.VISIBLE

                            when (itemOrderDetail.state) {
                                "new" -> {
                                    when (itemOrderDetail.status) {
                                        "pending" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                        }

                                        "Accepted" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }
                                    }
                                }

                                "processing" -> {
                                    when (itemOrderDetail.status) {
                                        "Accepted" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }

                                        "processing" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }

                                        "approved" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "Photography" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "Certification" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline4.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline4.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline4.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "Packaging" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline4.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline4.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline4.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline5.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline5.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline5.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "Dispatch" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline4.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline4.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline4.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline5.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline5.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline5.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline6.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline6.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline6.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "complete" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline4.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline4.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline4.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline5.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline5.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline5.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline6.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline6.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline6.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline7.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline7.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline7.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )
                                        }


                                        "Faild" -> {

                                        }

                                        "fraud" -> {

                                        }

                                        else -> {
                                        }
                                    }
                                }

                                "complete" -> {
                                    when (itemOrderDetail.status) {
                                        "complete" -> {
                                            timeline1.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline1.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )
                                            timeline1.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 1
                                            )

                                            timeline2.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline2.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline2.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline3.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline3.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline3.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline4.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline4.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline4.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline5.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline5.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline5.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline6.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline6.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline6.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 0
                                            )

                                            timeline7.marker = ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ellipse_black
                                            )
                                            timeline7.setStartLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                            timeline7.setEndLineColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                ), 2
                                            )
                                        }
                                    }
                                }



                                "canceled" -> {
                                    timeline8!!.marker = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ellipse_black
                                    )

                                    layoutCancel!!.visibility = View.VISIBLE

                                }
                            }


//                            when (itemOrderDetail.status) {
//                                "pending" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                }
//                                "Accepted" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "processing" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "photography" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline3.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline3.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline3.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "certification" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline3.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline3.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline3.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline4.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline4.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline4.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "packaging" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline3.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline3.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline3.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline4.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline4.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline4.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline5.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline5.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline5.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "dispatch" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline3.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline3.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline3.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline4.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline4.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline4.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline5.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline5.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline5.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline6.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline6.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline6.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//                                }
//                                "delivered" -> {
//                                    timeline1.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline1.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//                                    timeline1.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 1)
//
//                                    timeline2.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline2.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline2.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline3.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline3.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline3.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline4.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline4.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline4.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline5.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline5.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline5.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline6.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline6.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline6.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 0)
//
//                                    timeline7.marker = ContextCompat.getDrawable(requireContext(), R.drawable.ellipse_black)
//                                    timeline7.setStartLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                    timeline7.setEndLineColor(ContextCompat.getColor(requireContext(), R.color.app_color), 2)
//                                }
//                                else -> {
//
//                                }
//                            }

                        }
                    }





                    viewModel.orderHistoryDetail(_id!!) {
                        val checkout_buyer_name = JSONObject(this).getString("checkout_buyer_name")
                        val checkout_buyer_email =
                            JSONObject(this).getString("checkout_buyer_email")
                        val checkout_purchase_order_no =
                            JSONObject(this).getString("checkout_purchase_order_no")

                        textName.text = checkout_buyer_name
                        textMobile.text = checkout_purchase_order_no
                        textEmail.text = checkout_buyer_email

                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("TAG", "onDestroyView:")
//        if (arguments?.getString("from") == "customerOrders") {
        orderDetailLiveA.value = true
//        } else if (arguments?.getString("from") == "orderHistory") {
        orderDetailLiveB.value = true
//        }
    }


}


data class CartItem(
    val name: String,
    val price: Double,
    val sku: String,
    val qty: Int,
    var isSelected: Boolean = false,
)