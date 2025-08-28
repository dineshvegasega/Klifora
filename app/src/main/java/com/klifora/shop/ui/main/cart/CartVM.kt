package com.klifora.shop.ui.main.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemCartBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.QUOTE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.cart.ItemCart
import com.klifora.shop.models.cart.ItemCartModel
import com.klifora.shop.models.images.ItemImages
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.total.ItemTotal
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.getPatternFormat
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
class CartVM @Inject constructor(private val repository: Repository) : ViewModel() {


//    var refreshPrice = MutableLiveData<Boolean>(false)
    var subTotalPrice : Double = 0.0
    var discountPrice : Double = 10.0
    var shippingPrice : Double = 100.0
    var cgstPrice : Double = 9.0
    var sgstPrice : Double = 9.0
    var totalPrice : Double = 0.0

    var cartMutableList = MutableLiveData<Boolean>(false)
//    val cartAdapter = object : GenericAdapter<ItemCartBinding, CartModel>() {
//        override fun onCreateView(
//            inflater: LayoutInflater,
//            parent: ViewGroup,
//            viewType: Int
//        ) = ItemCartBinding.inflate(inflater, parent, false)
//
//        @SuppressLint("NotifyDataSetChanged")
//        override fun onBindHolder(
//            binding: ItemCartBinding,
//            dataClass: CartModel,
//            position: Int
//        ) {
//            binding.apply {
//
//                ivIcon.singleClick {
//                    binding.root.findNavController().navigate(R.id.action_cart_to_productDetail, Bundle().apply {
//                        putString("sku", dataClass.sku)
//                    })
//                }
//
//                textTitle.text = dataClass.name
//                textDesc.text =  "SKU: "+dataClass.sku
//
//                ivCount.text = dataClass.quantity.toString()
//                textPrice.text = "Price: ₹"+getPatternFormat("1", dataClass.price!!)
//
//                ivMinus.singleClick {
//                    if (dataClass.quantity > 1) {
//                        dataClass.quantity--
//                        mainThread {
//                            db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.quantity)
//                            val userList: List<CartModel> ?= db?.cartDao()?.getAll()
//                            userList?.forEach {
//                                Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
//                            }
//                        }
//                        notifyItemChanged(position)
//                    }
//                    cartMutableList.value = true
//                }
//
//                ivPlus.singleClick {
//                    dataClass.quantity++
//                    mainThread {
//                        db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.quantity)
//                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
//                        userList?.forEach {
//                            Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
//                        }
//                    }
//                    notifyItemChanged(position)
//                    cartMutableList.value = true
//                }
//
//                btDelete.singleClick {
//                    mainThread {
//                        db?.cartDao()?.deleteById(dataClass.product_id!!)
////                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
////                        userList?.forEach {
////                            Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
////                        }
//                    }
////                    Log.e("TAG", "onViewCreated: "+position)
////
////                    item1.removeAt(position)
////                    notifyDataSetChanged()
//
//                    cartMutableList.value = true
//
//                }
//            }
//        }
//    }


    val cartAdapter = object : GenericAdapter<ItemCartBinding, ItemCartModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemCartBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemCartBinding,
            dataClass: ItemCartModel,
            position: Int
        ) {
            binding.apply {
                ivIcon.singleClick {
                    binding.root.findNavController().navigate(R.id.action_cart_to_productDetail, Bundle().apply {
                        putString("baseSku", dataClass.sku.split("-")[0])
                        putString("sku", dataClass.sku)
                    })
                }

                textTitle.text = dataClass.name
                textSKU.text =  "SKU: "+dataClass.sku

                ivCount.text = dataClass.qty.toString()
                textPrice.text = "Price: ₹ "+getPatternFormat("1", dataClass.price!!)
                textTotalPrice?.text = "Total Price: ₹ "+ getPatternFormat("1",dataClass.price * dataClass.qty)


                mainThread {
//                    readData(ADMIN_TOKEN) { token ->
//                        Log.e("TAG", "tokenOO: "+token)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: "+this.name)
//                            if (this.media_gallery_entries.size > 0){
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
//                            }
//                        }
//                    }


                    getImages(dataClass.sku) {
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

                ivMinus.singleClick {
                    if (dataClass.qty > 1) {
                        dataClass.qty--
                        mainThread {
                            readData(QUOTE_ID) {
                                val json: JSONObject = JSONObject().apply {
                                    put("sku", dataClass.sku)
                                    put("qty", dataClass.qty)
                                    put("quote_id", it.toString())
                                }
                                val jsonCartItem: JSONObject = JSONObject().apply {
                                    put("cartItem", json)
                                }
                                readData(CUSTOMER_TOKEN) { token ->
                                    updateCart(token!!, jsonCartItem, dataClass.item_id) {
                                        notifyItemChanged(position)
                                        cartMutableList.value = true
                                    }
                                }
                            }
                        }
//                        mainThread {
//                            db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.quantity)
//                            val userList: List<CartModel> ?= db?.cartDao()?.getAll()
//                            userList?.forEach {
//                                Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
//                            }
//                        }
//                        notifyItemChanged(position)
                    }
//                    cartMutableList.value = true
                }

                ivPlus.singleClick {
                    dataClass.qty++
                    mainThread {
                        readData(QUOTE_ID) {
                            val json: JSONObject = JSONObject().apply {
                                put("sku", dataClass.sku)
                                put("qty", dataClass.qty)
                                put("quote_id", it.toString())
                            }
                            val jsonCartItem: JSONObject = JSONObject().apply {
                                put("cartItem", json)
                            }
                            readData(CUSTOMER_TOKEN) { token ->
                                updateCart(token!!, jsonCartItem, dataClass.item_id) {
                                    notifyItemChanged(position)
                                    cartMutableList.value = true
                                }
                            }
                        }


//                        db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.qty)
//                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
//                        userList?.forEach {
//                            Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
//                        }
                    }

                }

                btDelete.singleClick {
                    mainThread {
//                        val json: JSONObject = JSONObject().apply {
//                            put("sku", dataClass.sku)
//                            put("qty", 1)
//                            put("quote_id", quoteId)
//                        }
//                        val jsonCartItem: JSONObject = JSONObject().apply {
//                            put("cartItem", json)
//                        }


                        MaterialAlertDialogBuilder(root.context, R.style.LogoutDialogTheme)
                            .setTitle(root.context.getString(R.string.app_name))
                            .setMessage(root.context.getString(R.string.are_your_sure_want_to_delete))
                            .setPositiveButton(root.context.getString(R.string.yes)) { dialog, _ ->
                                dialog.dismiss()
                                readData(CUSTOMER_TOKEN) { token ->
                                    deleteCart(token!!, dataClass.item_id) {
                                        notifyItemChanged(position)
                                        cartMutableList.value = true
                                    }
                                }                            }
                            .setNegativeButton(root.context.getString(R.string.cancel)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            }
        }
    }


    val cartAdapterCustomer = object : GenericAdapter<ItemCartBinding, CartModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemCartBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemCartBinding,
            dataClass: CartModel,
            position: Int
        ) {
            binding.apply {
                ivIcon.singleClick {
                    binding.root.findNavController().navigate(R.id.action_cart_to_productDetail, Bundle().apply {
                        putString("baseSku", dataClass.sku.split("-")[0])
                        putString("sku", dataClass.sku)
                    })
                }

                textTitle.text = dataClass.name
                textSKU.text =  "SKU: "+dataClass.sku

                ivCount.text = dataClass.quantity.toString()
                textPrice.text = "Price: ₹ "+getPatternFormat("1", dataClass.price!!)
                textTotalPrice?.text = "Total Price: ₹ "+ getPatternFormat("1",dataClass.price * dataClass.quantity)


                mainThread {
                    readData(ADMIN_TOKEN) { token ->
                        Log.e("TAG", "tokenOO: "+token)
                        Log.e("TAG", "dataClass.sku: "+dataClass.sku)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: "+this.name)
//                            if (this.media_gallery_entries.size > 0){
//                                (IMAGE_URL +this.media_gallery_entries[0].file).glideImageChache(binding.ivIcon.context, binding.ivIcon)
////                                Glide.with(binding.ivIcon.context)
////                                    .load(IMAGE_URL +this.media_gallery_entries[0].file)
////                                    .apply(myOptionsGlide)
////                                    .into(binding.ivIcon)
//
//                            }
//                        }

                        getImages(dataClass.sku) {
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

                ivMinus.singleClick {
                    if (dataClass.quantity > 1) {
                        dataClass.quantity--
                        mainThread {
                            db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.quantity)
                            val userList: List<CartModel> ?= db?.cartDao()?.getAll()
                            userList?.forEach {
                                Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
                            }
                            notifyItemChanged(position)
                            cartMutableList.value = true
                        }
                    }
                }

                ivPlus.singleClick {
                    dataClass.quantity++
                    mainThread {
                        db?.cartDao()?.updateById(dataClass.product_id!!, dataClass.quantity)
                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
                        userList?.forEach {
                            Log.e("TAG", "onViewCreated: "+it.name + " it.currentTime "+it.quantity)
                        }
                        notifyItemChanged(position)
                        cartMutableList.value = true
                    }
                }

                btDelete.singleClick {
                    MaterialAlertDialogBuilder(root.context, R.style.LogoutDialogTheme)
                        .setTitle(root.context.getString(R.string.app_name))
                        .setMessage(root.context.getString(R.string.are_your_sure_want_to_delete))
                        .setPositiveButton(root.context.getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                            mainThread {
                                db?.cartDao()?.deleteById(dataClass.product_id!!)
                                notifyItemChanged(position)
                                cartMutableList.value = true
                            }                           }
                        .setNegativeButton(root.context.getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }
    }


//    fun getQuoteId(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
//        viewModelScope.launch {
//            repository.callApi(
//                callHandler = object : CallHandler<Response<JsonElement>> {
//                    override suspend fun sendRequest(apiInterface: ApiInterface) =
////                        if (loginType == "vendor") {
//                            apiInterface.getQuoteId("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
////                        } else if (loginType == "guest") {
////                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
//                    //                        } else {
////                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
////                        }
//                    @SuppressLint("SuspiciousIndentation")
//                    override fun success(response: Response<JsonElement>) {
//                        if (response.isSuccessful) {
//                            try {
//                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
//                                callBack(response.body().toString())
//                            } catch (_: Exception) {
//                            }
//                        }
//                    }
//
//                    override fun error(message: String) {
//                        if(message.contains("customerId")){
//                            sessionExpired()
//                        } else {
//                            showSnackBar("Something went wrong!")
//                        }
//                    }
//
//                    override fun loading() {
//                        super.loading()
//                    }
//                }
//            )
//        }


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



    fun addCart(adminToken: String, jsonObject: JSONObject, callBack: ItemCartModel.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemCartModel>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.addCart("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemCartModel>) {
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



    fun updateCart(adminToken: String, jsonObject: JSONObject, itemId: Int, callBack: ItemCartModel.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemCartModel>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.updateCart("Bearer " +adminToken, storeWebUrl, itemId, requestBody = jsonObject.getJsonRequestBody())
                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemCartModel>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        if(message.contains("customerId")){
                            sessionExpired()
                        } else {
                            showSnackBar("Something went wrong!")
                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }



    fun deleteCart(adminToken: String, itemId: Int, callBack: Boolean.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<Boolean>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.deleteCart("Bearer " +adminToken, storeWebUrl, itemId)
                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        if(message.contains("Cart doesn't contain")){
                            showSnackBar(message)
                        } else {
                            if(message.contains("customerId")){
                                sessionExpired()
                            } else {
                                showSnackBar("Something went wrong!")
                            }
//                            if(message.contains("fieldName")){
//                                showSnackBar("Something went wrong!")
//                            } else {
//                                sessionExpired()
//                            }
                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
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
                        if(message.contains("customerId")){
                            sessionExpired()
                        } else {
                            showSnackBar("Something went wrong!")
                        }
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


}