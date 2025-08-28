package com.klifora.shop.ui.main.checkout

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemCheckoutBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.cart.ItemCart
import com.klifora.shop.models.cart.ItemCartModel
import com.klifora.shop.models.images.ItemImages
import com.klifora.shop.models.myOrdersDetail.ItemOrderDetail
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.total.ItemTotal
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.ApiTranslateInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.CallHandlerTranslate
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.getSize
import com.klifora.shop.utils.glideImage
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CheckoutVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var subTotalPrice : Double = 0.0
    var discountPrice : Double = 10.0
    var shippingPrice : Double = 100.0
    var cgstPrice : Double = 0.0
    var sgstPrice : Double = 0.0
    var totalPrice : Double = 0.0

    fun getCartCount(callBack: Int.() -> Unit){
        viewModelScope.launch {
            val userList: List<CartModel> ?= db?.cartDao()?.getAll()
            var countBadge = 0
            userList?.forEach {
                countBadge += it.quantity
            }
            callBack(countBadge)
        }
    }


    val ordersAdapter = object : GenericAdapter<ItemCheckoutBinding, CartModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemCheckoutBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemCheckoutBinding,
            dataClass: CartModel,
            position: Int
        ) {
            binding.apply {
                textTitle.text = dataClass.name
//                textColor.text = "Color: "+dataClass.color
//                if (dataClass.material_type == "12"){
//                    textPurity.text = "Purity: "+dataClass.purity
//                } else if (dataClass.material_type == ""){
//                    textPurity.text = "Purity: "+dataClass.purity
//                }
//                textRingSize.text = "Ring size: "+dataClass.size
                textQty.text = "Quantity: "+dataClass.quantity
                textPrice.text = "Price: ₹ "+getPatternFormat("1", dataClass.price!!)
                textTotalPrice?.text = "Total Price: ₹ "+ getPatternFormat("1",dataClass.price * dataClass.quantity)

                mainThread {
//                    readData(ADMIN_TOKEN) { token ->
//                        Log.e("TAG", "tokenOO: "+token)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: "+this.name)
//                            if (this.media_gallery_entries.size > 0){
//                                (IMAGE_URL +this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                            }
//                        }
//                    }

                    getImages(dataClass.sku) {
                        Log.e("TAG", "getProductDetailOO: "+this.toString())
//                        if (this.media_gallery_entries.size > 0){
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                        }
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

                val colorW = when(dataClass.color){
                    "19" -> {"Yellow Gold"}
                    "50" -> {"Gold White"}
                    "25" -> {"Rose Gold"}
                    else -> {""}
                }
                textColor.text = "Color: "+colorW
                Log.e("TAG", "onBindHolderColor: "+dataClass.color)

                val purityW = when(dataClass.purity){
                     "26" -> {"9Kt"}
                     "14" -> {"14Kt"}
                     "15" -> {"18Kt"}
                     "16" -> {"22Kt"}
                     "17" -> {"24Kt"}
                     "18" -> {"95(Platinum)"}
                    else -> {""}
                }
                textPurity.text = "Purity: "+purityW

                Log.e("TAG", "onBindHolderSize: "+dataClass.size)

                if (dataClass.size.isEmpty()){
                    textRingSize.visibility = ViewGroup.GONE
                } else {
                    textRingSize.visibility = ViewGroup.VISIBLE
                }

                textRingSize.text = "Size: "+ getSize(dataClass.size.toInt())

                ivIcon.singleClick {
                    binding.root.findNavController().navigate(R.id.action_checkout_to_productDetail, Bundle().apply {
                        putString("baseSku", dataClass.sku.split("-")[0])
                        putString("sku", dataClass.sku)
                    })
                }

            }
        }
    }


    val cartAdapter = object : GenericAdapter<ItemCheckoutBinding, ItemCartModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemCheckoutBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemCheckoutBinding,
            dataClass: ItemCartModel,
            position: Int
        ) {
            binding.apply {
                ivIcon.singleClick {
                    binding.root.findNavController().navigate(R.id.action_checkout_to_productDetail, Bundle().apply {
                        putString("baseSku", dataClass.sku.split("-")[0])
                        putString("sku", dataClass.sku)
                    })
                }

                textTitle.text = dataClass.name

                textQty.text = "Quantity: "+dataClass.qty
                textPrice.text = "Price: ₹ "+getPatternFormat("1", dataClass.price!!)
//                textPrice.text = "Price: ₹"+getPatternFormat("1", dataClass.price!!) + " x "+dataClass.qty + " = ₹"+getPatternFormat("1", (dataClass.price?.times(dataClass.qty.toDouble())))
                textTotalPrice?.text = "Total Price: ₹ "+ getPatternFormat("1",dataClass.price * dataClass.qty)

                mainThread {
                    getImages(dataClass.sku) {
                        Log.e("TAG", "getProductDetailOO: "+this.toString())
//                        if (this.media_gallery_entries.size > 0){
//                            (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                        }
                        val images = this
                        if (images.size > 0){
                            val images2 = images[0]
                            if (images2.size > 0){
                                val images3 = images2[0]
                                images3.glideImage(binding.ivIcon.context, binding.ivIcon)
                            }
                        }
                    }

                    readData(ADMIN_TOKEN) { token ->
                        Log.e("TAG", "tokenOO: "+token)

                        getProductDetail(token.toString(), dataClass.sku) {
                            Log.e("TAG", "getProductDetailOO: "+this.name)
//                            if (this.media_gallery_entries.size > 0){
//                                (IMAGE_URL +this.media_gallery_entries[0].file).glideImageChache(binding.ivIcon.context, binding.ivIcon)
//                            }


                            this.custom_attributes.forEach { itemProductAttr ->
                                if (itemProductAttr.attribute_code == "metal_color") {
                                    if (itemProductAttr.value == "19") {
                                        textColor.text = "Color: Yellow Gold"
                                    } else if (itemProductAttr.value == "50") {
                                        textColor.text = "Color: White Gold"
                                    } else if (itemProductAttr.value == "25") {
                                        textColor.text = "Color: Rose Gold"
                                    }
                                }


                                if (itemProductAttr.attribute_code == "metal_purity") {
                                    if (itemProductAttr.value == "26") {
                                        textPurity.text = "Purity: 9 kt"
                                    } else if (itemProductAttr.value == "14") {
                                        textPurity.text = "Purity: 14 kt"
                                    } else if (itemProductAttr.value == "15") {
                                        textPurity.text = "Purity: 18 kt"
                                    } else if (itemProductAttr.value == "16") {
                                        textPurity.text = "Purity: 22 kt"
                                    } else if (itemProductAttr.value == "17") {
                                        textPurity.text = "Purity: 24 kt"
                                    } else if (itemProductAttr.value == "18") {
                                        textPurity.text = "Purity: Platinum 95"
                                    }
                                }

                                if (itemProductAttr.attribute_code == "size") {
                                    textRingSize.text = "Size: "+ getSize(itemProductAttr.value.toString().toInt())
                                    textRingSize.visibility = ViewGroup.VISIBLE
                                }

                                if (itemProductAttr.attribute_code != "size") {
                                    textRingSize.visibility = ViewGroup.GONE
                                }
                            }
                        }
                    }
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
                        apiInterface.productsDetailID("Bearer " +adminToken, skuId)
                    //                    } else {
//                        apiInterface.productsDetail("Bearer " +adminToken, storeWebUrl, skuId)
//                    }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)

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
                                //val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)

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
                                response.body()?.let { callBack(it) }
//                                }


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



    fun getCart(customerToken: String, callBack: ItemCart.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemCart>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.getCart("Bearer " +customerToken, storeWebUrl)
                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemCart>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        if(message.contains("fieldName")){
                            showSnackBar("Something went wrong!")
                        } else {
//                            sessionExpired()
                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



    fun getTotal(customerToken: String, callBack: ItemTotal.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemTotal>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.getTotals("Bearer " +customerToken, storeWebUrl)
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemTotal>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "customerIdXX "+message)
                        if(message.contains("%fieldValue")){
                            sessionExpired()
                        } else {
                            // showSnackBar("Something went wrong!")
                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



    fun updateShipping(adminToken: String, jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.updateShipping("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
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
                        showSnackBar(message)
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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


    fun addCart(adminToken: String, jsonObject: JSONObject, callBack: ItemCartModel.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
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
//                        showSnackBar(message)
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




    fun postCustomDetails(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<String>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        apiInterface.postCustomDetails( requestBody = jsonObject.getJsonRequestBody())
                        apiInterface.postCustomDetails(jsonObject.getString("cartId"), jsonObject.getString("checkout_buyer_name"), jsonObject.getString("checkout_buyer_email"), jsonObject.getString("checkout_purchase_order_no"))
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<String>) {
                        Log.e("TAG", "successAAXXZZXX: ${response.toString()}")
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXXZZ: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                                showSnackBar("Something went wrong!")
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


    fun getQuoteId(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.getQuoteId(
                            "Bearer " + adminToken,
                            storeWebUrl,
                            requestBody = jsonObject.getJsonRequestBody()
                        )
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body().toString())
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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



    fun getInvoice(adminToken: String, jsonObject: JSONObject, orderId: String, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApiTranslate (
                callHandler = object : CallHandlerTranslate<Response<JsonElement>> {
                    override suspend fun sendRequestTranslate(apiInterface: ApiTranslateInterface) =
                        apiInterface.invoice(
                            "Bearer " + adminToken,
                            orderId,
                            requestBody = jsonObject.getJsonRequestBody()
                        )
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body().toString())
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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



    fun getTransactions(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.transactions(
                            "Bearer " + adminToken,
                            storeWebUrl,
                            requestBody = jsonObject.getJsonRequestBody()
                        )
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body().toString())
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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




    fun getCancel(adminToken: String, jsonObject: JSONObject, orderId: String, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApiTranslate (
                callHandler = object : CallHandlerTranslate<Response<JsonElement>> {
                    override suspend fun sendRequestTranslate(apiInterface: ApiTranslateInterface) =
                        apiInterface.cancel(
                            "Bearer " + adminToken,
                            orderId,
                            requestBody = jsonObject.getJsonRequestBody()
                        )
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body().toString())
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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




    fun resetToken(
        adminToken: String,
        id: String,
        jsonObject: JSONObject,
        callBack: String.() -> Unit
    ) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.resetToken("Bearer " + adminToken, id, jsonObject.getJsonRequestBody())

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA11: ${response.body().toString()}")
                                val jsonObject = response.body().toString().substring(1, response.body().toString().length - 1).toString().replace("\\", "")
//                                Log.e("TAG", "successAAB: ${jsonObject}")
                                callBack(jsonObject)
                            } catch (e: Exception) {
                                Log.e("TAG", "successFF: ${e.message}")
                                callBack(e.message.toString())
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




    fun createOrder(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApiTranslate (
                callHandler = object : CallHandlerTranslate<Response<JsonElement>> {
                    override suspend fun sendRequestTranslate(apiInterface: ApiTranslateInterface) =
                        apiInterface.createOrder("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                val orderID = response.body()!!.toString().replace("\"", "")
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(orderID)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        var response: Response<JsonElement>
//                        showSnackBar(message)
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
//                        } else {
//                            sessionExpired()
//                        }
//                        val tempPhone = JsonNull.getAsString()

                        callBack("failed")
//                        createOrder2nd(adminToken, jsonObject, callBack)

                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



    fun createOrder2nd(adminToken: String, jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApiTranslate (
                callHandler = object : CallHandlerTranslate<Response<JsonElement>> {
                    override suspend fun sendRequestTranslate(apiInterface: ApiTranslateInterface) =
                        apiInterface.createOrder("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
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
                        showSnackBar(message)
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
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

}