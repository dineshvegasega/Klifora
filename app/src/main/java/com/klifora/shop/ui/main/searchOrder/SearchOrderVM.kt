package com.klifora.shop.ui.main.searchOrder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemSearchOrderBinding
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.guestOrderList.ItemGuestOrderList
import com.klifora.shop.models.guestOrderList.ItemGuestOrderListItem
import com.klifora.shop.models.images.ItemImages
import com.klifora.shop.models.searchOrder.Item
import com.klifora.shop.models.searchOrder.ItemSearch
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.ui.main.orderDetail.CartItem
import com.klifora.shop.utils.changeDateFormat
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.glideImage
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchOrderVM @Inject constructor(private val repository: Repository) : ViewModel() {

    val searchOrderAdapter = object : GenericAdapter<ItemSearchOrderBinding, ItemSearch>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSearchOrderBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemSearchOrderBinding,
            dataClass: ItemSearch,
            position: Int
        ) {
            binding.apply {

                textTitle.text = "Order No: " + dataClass.increment_id
                val date =
                    dataClass.updated_at.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy")
                textDate.text = "Date: " + date


//                val typeToken = object : TypeToken<List<CartItem>>() {}.type
//                val changeValue =
//                    Gson().fromJson<List<Item>>(
//                        Gson().fromJson(
//                            dataClass?.cartItem,
//                            JsonElement::class.java
//                        ), typeToken
//                    )

                var qty = 0
                var price = 0.0
                dataClass.items.forEach {
                    price += (it.price.toDouble() * it.product_options.info_buyRequest.qty) + it.tax_amount.toDouble()
                    qty += it.product_options.info_buyRequest.qty
                }
                textNoOfOrders.text = "No Of Products: " + qty

                textAmount.text =
                    "Total Amount: ₹ " + getPatternFormat("1", price)


                if (dataClass.items.size > 0){
                    mainThread {
                        getImages(dataClass.items[0].sku) {
                            Log.e("TAG", "getProductDetailOO: "+this.toString())
                            val images = this
                            if (images.size > 0){
                                val images2 = images[0]
                                if (images2.size > 0){
                                    val images3 = images2[0]
                                    images3.glideImage(binding.ivIcon.context, binding.ivIcon)
                                }
                            }
                        }
                    }
                }


                root.singleClick {
                    root.findNavController().navigate(R.id.action_searchOrder_to_orderDetail, Bundle().apply {
                        putString("from" , "orderHistory")
                        putString("_id" , ""+dataClass.entity_id)
//                        putString("name" , ""+dataClass.checkout_buyer_name)
//                        putString("mobile" , ""+dataClass.checkout_purchase_order_no)
//                        putString("email" , ""+dataClass.checkout_buyer_email)
                    })
                }


//                root.singleClick {
//                    root.findNavController()
//                        .navigate(R.id.action_searchOrder_to_orderDetail, Bundle().apply {
//                            putString("from", "customerOrders")
//                            putParcelable("key" , dataClass)
//                        })
//                }


//
//                ivCross.singleClick {
//                    mainThread {
//                        db?.searchDao()?.delete(dataClass)
//                        searchDelete.value = true
//                    }
//                }
            }
        }
    }





//    fun orderHistoryListDetail(
//        adminToken: String,
//        id: String
//    ) =
//        viewModelScope.launch {
//            repository.callApi(
//                callHandler = object : CallHandler<Response<ItemOrderDetail>> {
//                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        apiInterface.orderHistoryListDetail("Bearer " + adminToken, id)
//
//                    @SuppressLint("SuspiciousIndentation")
//                    override fun success(response: Response<ItemOrderDetail>) {
//                        if (response.isSuccessful) {
////                            callBack(response.body()!!)
//                            orderHistoryListMutableLiveData.value = response.body()!!
////                            try {
////                                Log.e("TAG", "successAA: ${response.body().toString()}")
//////                                val jsonObject = response.body().toString().substring(1, response.body().toString().length - 1).toString().replace("\\", "")
//////                                Log.e("TAG", "successAAB: ${jsonObject}")
////                                callBack(response.body()!!)
////                            } catch (e: Exception) {
////                            }
//                        } else {
////                            callBack(response.body()!!)
//                            orderHistoryListMutableLiveData.value = response.body()!!
//
//                        }
//                    }
//
//                    override fun error(message: String) {
//                        Log.e("TAG", "successEE: ${message}")
////                        super.error(message)
////                        showSnackBar(message)
//                        orderHistoryListMutableLiveData.value = null
//
//                    }
//
//                    override fun loading() {
//                        super.loading()
//                    }
//                }
//            )
//        }



    fun getImages(skuId: String, callBack: ItemImages.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<ItemImages>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.getImages(skuId)
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemImages>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "getImages: ${response.body().toString()}")
                                // val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)

//                                viewModelScope.launch {
//                                    val userList: List<CartModel>? = db?.cartDao()?.getAll()
//                                    userList?.forEach { user ->
//                                        if (mMineUserEntity.id == user.product_id) {
//                                            mMineUserEntity.apply {
//                                                isSelected = true
//                                            }
//                                        } else {
//                                            mMineUserEntity.apply {
//                                                isSelected = false
//                                            }
//                                        }
//                                    }
//                                    callBack(mMineUserEntity)
//                                }
                                response.body()?.let { callBack(it) }

                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "successEE: ${message}")
//                        if(message.contains("customerId")){
//                            sessionExpired()
//                        } else {
//                            showSnackBar("Something went wrong!")
//                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



    var orderHistoryListMutableLiveData = MutableLiveData<ItemSearch?>()
    var orderHistoryBooleanMutableLiveData = MutableLiveData<Boolean>(false)

    fun guestOrderList(orderId: String) =
        viewModelScope.launch {

            repository.callApi(
                callHandler = object : CallHandler<Response<ItemSearch>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.searchOrderList("", "", "", orderId , 1)
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemSearch>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
//                                val mMineUserEntity = Gson().fromJson(response.body(), ItemProductRoot::class.java)
//                                callBack(response.body()!!.toString().toString().replace("\\", ""))
//                                callBack(response.body()!!)
                                orderHistoryListMutableLiveData.value = response.body()!!
                                orderHistoryBooleanMutableLiveData.value = true
                            } catch (e: Exception) {
                                Log.e("TAG", "successAAZZ: ${response.body().toString()}")
                                orderHistoryListMutableLiveData.value = null
                                orderHistoryBooleanMutableLiveData.value = false
                            }
                        } else {
                            Log.e("TAG", "successAADD: ${response.body().toString()}")
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "successAADDmessage: ${message.toString()}")
//                        val mMineUserEntity : ItemSearch ?= null
//                        orderHistoryListMutableLiveData.value = mMineUserEntity
                        orderHistoryBooleanMutableLiveData.value = false
                        if (message.contains("authorized")) {
                            sessionExpired()
                        } else {
//                            showSnackBar("Something went wrong!")
                        }


                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



//
//    fun orderHistoryListDetail(id: String, callBack: JsonElement.() -> Unit) =
//        viewModelScope.launch {
//            repository.callApi(
//                callHandler = object : CallHandler<Response<JsonElement>> {
//                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        apiInterface.orderHistoryDetail(id)
//                    @SuppressLint("SuspiciousIndentation")
//                    override fun success(response: Response<JsonElement>) {
//                        if (response.isSuccessful) {
//                            try {
//                                Log.e("TAG", "successAA: ${response.body().toString()}")
//                                val jsonObject = response.body().toString().substring(1, response.body().toString().length - 1).toString().replace("\\n", "").replace("\\", "").trim()
//                                Log.e("TAG", "successAAB: ${jsonObject}")
////                                val item = Gson().fromJson<>(response.body()?.get(0))
//
//                                var ddd = JSONObject(jsonObject).getString("checkout_buyer_email")
//                                Log.e("TAG", "jsonObjectAA: ${ddd}")
////                                val jsonObjectAA = Gson().fromJson(Gson().toJson(jsonObject), ItemOrderDetail::class.java)
////                                Log.e("TAG", "jsonObjectAA: ${jsonObjectAA}")
//                                callBack(response.body()!!)
//                            } catch (e: Exception) {
//                            }
//                        }
//                    }
//
//                    override fun error(message: String) {
//                        Log.e("TAG", "successEE: ${message}")
////                        super.error(message)
////                        showSnackBar(message)
//                    }
//
//                    override fun loading() {
//                        super.loading()
//                    }
//                }
//            )
//        }


}