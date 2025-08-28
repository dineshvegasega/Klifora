package com.klifora.shop.ui.main.orderDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemSkusBinding
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.cart.ItemCartModel
import com.klifora.shop.models.images.ItemImages
import com.klifora.shop.models.myOrdersDetail.ItemOrderDetail
import com.klifora.shop.models.myOrdersDetail.ItemX
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.glideImage
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OrderDetailVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var selectedPositionCustomerOrders = -1
    val orderSKUCustomerOrders = object : GenericAdapter<ItemSkusBinding, CartItem>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSkusBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemSkusBinding,
            dataClass: CartItem,
            position: Int
        ) {
            binding.apply {

                textTitle.text = "SKU : ${dataClass.sku}"

                mainThread {

                    getImages(dataClass.sku) {
                        Log.e("TAG", "getProductDetailOO: "+this.toString())
//                        if (this.media_gallery_entries.size > 0){
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIconChild.context, binding.ivIconChild)
//                        }

                        val images = this
                        if (images.size > 0){
                            val images2 = images[0]
                            if (images2.size > 0){
                                val images3 = images2[0]
                                images3.glideImage(binding.ivIcon.context, binding.ivIcon)
                                images3.glideImage(binding.ivIconChild.context, binding.ivIconChild)
                            }
                        }
                    }


//                    readData(ADMIN_TOKEN) { token ->
//                        Log.e("TAG", "tokenOO: " + token)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: " + this.name)
//                            if (this.media_gallery_entries.size > 0) {
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
//                                    binding.ivIcon.context,
//                                    binding.ivIcon
//                                )
////                                Glide.with(binding.ivIcon.context)
////                                    .load(IMAGE_URL +this.media_gallery_entries[0].file)
////                                    .apply(myOptionsGlide)
////                                    .into(binding.ivIcon)
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
//                                    binding.ivIcon.context,
//                                    binding.ivIconChild
//                                )
//                            }
//                        }
//                    }
                }


                skuChild.text = "${dataClass.sku}"
                textQtyChild.text = "${dataClass.qty}"
                textPriceChild.text = "₹ " + getPatternFormat("1", dataClass.price)
                textTotalPriceChild.text =
                    "₹ " + getPatternFormat("1", dataClass.price * dataClass.qty)

                layoutChild.visibility =
                    if (selectedPositionCustomerOrders == position) View.VISIBLE else View.GONE
                imageCross.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if (selectedPositionCustomerOrders == position) R.drawable.arrow_down else R.drawable.arrow_right
                    )
                )

                imageCross.singleClick {
                    if (selectedPositionCustomerOrders == position) {
                        if (layoutChild.isVisible == true) {
                            selectedPositionCustomerOrders = -1
                        }
                        if (layoutChild.isVisible == false) {
                            selectedPositionCustomerOrders = position
                        }
                    } else {
                        selectedPositionCustomerOrders = position
                    }
                    notifyDataSetChanged()
                }


                ivIcon.singleClick {
                    root.findNavController()
                        .navigate(R.id.action_orderDetail_to_productDetail, Bundle().apply {
                            if (dataClass.sku.contains("-")) {
                                putString("baseSku", dataClass.sku.split("-")[0])
                                putString("sku", dataClass.sku)
                            } else if (dataClass.sku.contains(" ")) {
                                putString("baseSku", dataClass.sku.split(" ")[0])
                                putString("sku", dataClass.sku)
                            } else {
                                putString("baseSku", dataClass.sku)
                            }
                        })
                }
            }
        }
    }


    var selectedPositionOrderHistory = -1
    val orderSKUOrderHistory = object : GenericAdapter<ItemSkusBinding, ItemX>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSkusBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemSkusBinding,
            dataClass: ItemX,
            position: Int
        ) {
            binding.apply {

                textTitle.text = "SKU : ${dataClass.sku}"

                mainThread {
                    getImages(dataClass.sku) {
                        Log.e("TAG", "getProductDetailOO: "+this.toString())
//                        if (this.media_gallery_entries.size > 0){
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIconChild.context, binding.ivIconChild)
//                        }

                        val images = this
                        if (images.size > 0){
                            val images2 = images[0]
                            if (images2.size > 0){
                                val images3 = images2[0]
                                images3.glideImage(binding.ivIcon.context, binding.ivIcon)
                                images3.glideImage(binding.ivIconChild.context, binding.ivIconChild)
                            }
                        }
                    }

//                    readData(ADMIN_TOKEN) { token ->
//                        Log.e("TAG", "tokenOO: " + token)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: " + this.name)
//                            if (this.media_gallery_entries.size > 0) {
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
//                                    binding.ivIcon.context,
//                                    binding.ivIcon
//                                )
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
//                                    binding.ivIcon.context,
//                                    binding.ivIconChild
//                                )
//                            }
//                        }
//                    }
                }

                skuChild.text = "${dataClass.sku}"
                textQtyChild.text = "${dataClass.qty_ordered}"
                textPriceChild.text = "₹ " + getPatternFormat("1", dataClass.price.toDouble())
                textTotalPriceChild.text =
                    "₹ " + getPatternFormat("1", dataClass.price.toDouble() * dataClass.qty_ordered)

                layoutChild.visibility =
                    if (selectedPositionOrderHistory == position) View.VISIBLE else View.GONE
                imageCross.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if (selectedPositionOrderHistory == position) R.drawable.arrow_down else R.drawable.arrow_right
                    )
                )

                imageCross.singleClick {
                    if (selectedPositionOrderHistory == position) {
                        if (layoutChild.isVisible == true) {
                            selectedPositionOrderHistory = -1
                        }
                        if (layoutChild.isVisible == false) {
                            selectedPositionOrderHistory = position
                        }
                    } else {
                        selectedPositionOrderHistory = position
                    }
                    notifyDataSetChanged()
                }


                ivIcon.singleClick {
                    root.findNavController()
                        .navigate(R.id.action_orderDetail_to_productDetail, Bundle().apply {
                            if (dataClass.sku.contains("-")) {
                                putString("baseSku", dataClass.sku.split("-")[0])
                                putString("sku", dataClass.sku)
                            } else if (dataClass.sku.contains(" ")) {
                                putString("baseSku", dataClass.sku.split(" ")[0])
                                putString("sku", dataClass.sku)
                            } else {
                                putString("baseSku", dataClass.sku)
                            }
                        })
                }
            }
        }
    }


    fun getProductDetail(adminToken: String, skuId: String, callBack: ItemProduct.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    if (loginType == "vendor") {
//                        apiInterface.productsDetail("Bearer " +adminToken, storeWebUrl, skuId)
//                    } else if (loginType == "guest") {
                        apiInterface.productsDetailID("Bearer " + adminToken, skuId)

                    //                    } else {
//                        apiInterface.productsDetail("Bearer " +adminToken, storeWebUrl, skuId)
//                    }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                val mMineUserEntity =
                                    Gson().fromJson(response.body(), ItemProduct::class.java)
                                viewModelScope.launch {
//                                    mMineUserEntity.forEach {items ->
                                    val userList: List<CartModel>? = db?.cartDao()?.getAll()
                                    userList?.forEach { user ->
                                        if (mMineUserEntity.id == user.product_id) {
                                            mMineUserEntity.apply {
                                                isSelected = true
                                            }
//                                                Log.e( "TAG", "YYYYYYYYY: " )
                                        } else {
                                            mMineUserEntity.apply {
                                                isSelected = false
                                            }
//                                                Log.e( "TAG", "NNNNNNNNNN: " )
                                        }
                                    }
//                                    }


                                    callBack(mMineUserEntity)
                                }


                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "successEE: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
//                        } else if(message.contains("The product that was requested doesn't exist")){
//                            showSnackBar(message)
//                        } else {
//                            sessionExpired()
//                        }

                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


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
//                                val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)

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



    fun updateStatus(jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.updateStatus(requestBody = jsonObject.getJsonRequestBody())

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        if (message.contains("fieldName")) {
                            showSnackBar("Something went wrong!")
                        } else {
                            //sessionExpired()
                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun orderHistoryListDetail(
        adminToken: String,
        id: String,
        callBack: ItemOrderDetail.() -> Unit
    ) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemOrderDetail>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.orderHistoryListDetail("Bearer " + adminToken, id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemOrderDetail>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA11: ${response.body().toString()}")
//                                val jsonObject = response.body().toString().substring(1, response.body().toString().length - 1).toString().replace("\\", "")
//                                Log.e("TAG", "successAAB: ${jsonObject}")
                                callBack(response.body()!!)
                            } catch (e: Exception) {
                                Log.e("TAG", "successFF: ${e.message}")
                                callBack(response.body()!!)
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "successEE: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun orderHistoryDetail(id: String, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.orderHistoryDetail(id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                val jsonObject = response.body().toString()
                                    .substring(1, response.body().toString().length - 1).toString()
                                    .replace("\\n", "").replace("\\", "").trim()
                                Log.e("TAG", "successAAB: ${jsonObject}")
//                                val item = Gson().fromJson<>(response.body()?.get(0))

//                                var ddd = JSONObject(jsonObject).getString("checkout_buyer_email")
//                                Log.e("TAG", "jsonObjectAA: ${ddd}")
//                                val jsonObjectAA = Gson().fromJson(Gson().toJson(jsonObject), ItemOrderDetail::class.java)
//                                Log.e("TAG", "jsonObjectAA: ${jsonObjectAA}")
                                callBack(jsonObject)
                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "successEE: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }





    fun addCart(adminToken: String, jsonObject: JSONObject, callBack: ItemCartModel.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemCartModel>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.addCart(
                            "Bearer " + adminToken,
                            storeWebUrl,
                            requestBody = jsonObject.getJsonRequestBody()
                        )

                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemCartModel>) {
                        if (response.isSuccessful) {
                            try {
//                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        showSnackBar(message)
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
//                        } else {
//                            sessionExpired()
//                        }
//                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }
}