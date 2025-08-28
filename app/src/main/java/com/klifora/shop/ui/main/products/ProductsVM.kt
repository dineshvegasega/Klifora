package com.klifora.shop.ui.main.products

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.products.ItemProductRoot
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ProductsVM @Inject constructor(private val repository: Repository) : ViewModel() {

    companion object{
        var isProductLoad = false
        var isFilterLoad = false

    }

    var page: Int = 1
    val itemsProduct: ArrayList<ItemProduct> = ArrayList()
    var sortFilter: Int = 0


    fun getCartCount(callBack: Int.() -> Unit) {
        mainThread {
            val userList: List<CartModel>? = db?.cartDao()?.getAll()
            var countBadge = 0
            userList?.forEach {
                countBadge += it.quantity
                Log.e("TAG", "getCartCount: " + it.quantity)
            }
            callBack(countBadge)
        }
//        viewModelScope.launch {
//            val userList: List<CartModel> ?= db?.cartDao()?.getAll()
//            var countBadge = 0
//            userList?.forEach {
//                countBadge += it.quantity
//            }
//            callBack(countBadge)
//        }
    }


//    val productAdapter = object : GenericAdapter<ItemProductBinding, ItemProduct>() {
//        override fun onCreateView(
//            inflater: LayoutInflater,
//            parent: ViewGroup,
//            viewType: Int
//        ) = ItemProductBinding.inflate(inflater, parent, false)
//
//        override fun onBindHolder(
//            binding: ItemProductBinding,
//            dataClass: ItemProduct,
//            position: Int
//        ) {
//            binding.apply {
//
//                textTitle.text = dataClass.name
//                textPrice.text = "₹ " + getPatternFormat("1", dataClass.price)
//
//                (IMAGE_URL + if (dataClass.media_gallery_entries.size > 0) dataClass.media_gallery_entries[0].file else "").glideImage(
//                    ivIcon.context,
//                    ivIcon
//                )
//
//                ivIcon.setOnClickListener {
////                it.findNavController().navigate(R.id.action_products_to_productsDetail, Bundle().apply {
////                    putParcelable("model", model)
////                })
//                    it.findNavController()
//                        .navigate(R.id.action_products_to_productsDetail, Bundle().apply {
//                            putString("baseSku", dataClass.sku)
//                            putString("sku", "")
//                        })
//                }
//
//                ivAddCart.imageTintList =
//                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
//                        root.context,
//                        R.color.app_color
//                    ) else ContextCompat.getColorStateList(root.context, R.color._9A9A9A)
//
//
//                ivAddCart.setOnClickListener {
//                    dataClass.isSelected = !dataClass.isSelected
//                    mainThread {
//                        val newUser = CartModel(
//                            product_id = dataClass.id,
//                            name = dataClass.name,
//                            price = dataClass.price,
//                            quantity = 1,
//                            sku = dataClass.sku,
//                            currentTime = System.currentTimeMillis()
//                        )
//                        dataClass.custom_attributes.forEach {
//                            if (it.attribute_code == "size") {
//                                newUser.apply {
//                                    this.size = "" + it.value
//                                }
//                            }
//
//
//                            if (it.attribute_code == "metal_color") {
//                                newUser.apply {
//                                    this.color = "" + it.value
//                                }
//                            }
//
//                            if (it.attribute_code == "metal_type") {
//                                Log.e("TAG", "metal_typeAAA " + it.value)
//                                if (it.value == "12") {
//                                    newUser.apply {
//                                        this.material_type = "" + it.value
//                                    }
//                                    dataClass.custom_attributes.forEach { againAttributes ->
//                                        if (againAttributes.attribute_code == "metal_purity") {
//                                            Log.e("TAG", "metal_typeBBB " + againAttributes.value)
//                                            newUser.apply {
//                                                this.purity = "" + againAttributes.value
//                                            }
//                                        }
////                                    Log.e("TAG", "metal_typeBBB " + againAttributes.toString())
//                                    }
//                                }
//                            } else {
////                            Log.e("TAG", "metal_typeBBB " + it.value)
////                            model.custom_attributes.forEach { againAttributes ->
////                                if (againAttributes.attribute_code == "metal_purity"){
////                                    newUser.apply {
////                                        this.purity = ""+it.value
////                                    }
////                                }
////                            }
//                            }
//                        }
//                        if (dataClass.isSelected == true) {
//                            db?.cartDao()?.insertAll(newUser)
//                        } else {
//                            db?.cartDao()?.deleteById(newUser.product_id!!)
//                        }
//                        cartItemLiveData.value = true
//                        notifyItemChanged(position)
//                    }
//                }
//
//
//                btAddCart.setOnClickListener {
//                    mainThread {
//                        val newUser = CartModel(
//                            product_id = dataClass.id,
//                            name = dataClass.name,
//                            price = dataClass.price,
//                            quantity = 1,
//                            currentTime = System.currentTimeMillis()
//                        )
//                        db?.cartDao()?.insertAll(newUser)
//                        cartItemLiveData.value = true
//                        it.findNavController().navigate(R.id.action_products_to_cart)
//                    }
//                }
//            }
//        }
//    }



    private var itemProducstResult = MutableLiveData<ItemProductRoot>()
    val itemProducts: LiveData<ItemProductRoot> get() = itemProducstResult
    fun getProducts(adminToken: String, emptyMap: MutableMap<String, String>, pageNumber : Int) =
        viewModelScope.launch {
            if(pageNumber == 0 || pageNumber == 1){
                repository.callApi(
                    callHandler = object : CallHandler<Response<JsonElement>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.productsID("Bearer " + adminToken, emptyMap)
                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<JsonElement>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        val mMineUserEntity = Gson().fromJson(
                                            response.body(),
                                            ItemProductRoot::class.java
                                        )
//                                    mMineUserEntity.items.forEach { itemApi ->
//                                        val bool = getArrayValue(itemsProduct, itemApi)
//                                        Log.e("TAG", "boolAA: ${bool}")
//                                        if (!bool) {
//                                            itemsProduct.add(itemApi)
//                                        }
//                                    }

//                                    if (itemsProduct.size != 0) {
//                                        mMineUserEntity.items.forEach { itemApi ->
//                                            itemsProduct.forEach { array ->
//                                                Log.e(
//                                                    "TAG",
//                                                    "itemApiZZ " + itemApi.id + "   " + array.id
//                                                )
//                                                if (array.id != itemApi.id) {
//                                                    itemsProduct.add(itemApi)
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        itemsProduct.addAll(mMineUserEntity.items)
//                                    }



                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                            if (message.contains("authorized")) {
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
            } else {
                repository.callApiWithoutLoader(
                    callHandler = object : CallHandler<Response<JsonElement>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.productsID("Bearer " + adminToken, emptyMap)
                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<JsonElement>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        val mMineUserEntity = Gson().fromJson(
                                            response.body(),
                                            ItemProductRoot::class.java
                                        )
//                                    mMineUserEntity.items.forEach { itemApi ->
//                                        val bool = getArrayValue(itemsProduct, itemApi)
//                                        Log.e("TAG", "boolAA: ${bool}")
//                                        if (!bool) {
//                                            itemsProduct.add(itemApi)
//                                        }
//                                    }

//                                    if (itemsProduct.size != 0) {
//                                        mMineUserEntity.items.forEach { itemApi ->
//                                            itemsProduct.forEach { array ->
//                                                Log.e(
//                                                    "TAG",
//                                                    "itemApiZZ " + itemApi.id + "   " + array.id
//                                                )
//                                                if (array.id != itemApi.id) {
//                                                    itemsProduct.add(itemApi)
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        itemsProduct.addAll(mMineUserEntity.items)
//                                    }



                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                            if (message.contains("authorized")) {
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
        }


    private fun getArrayValue(
        itemProductArray: ArrayList<ItemProduct>,
        itemApi: ItemProduct
    ): Boolean {
        itemProductArray.forEach {
            Log.e("TAG", "getArrayValue: " + it.id + "   " + itemApi.id)
            if (it.id == itemApi.id) {
                return true
            } else {
                return false
            }
        }
        return false
    }
}