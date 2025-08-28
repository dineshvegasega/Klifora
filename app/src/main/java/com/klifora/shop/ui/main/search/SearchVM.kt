package com.klifora.shop.ui.main.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemSearchBinding
import com.klifora.shop.databinding.ItemSearchHistoryBinding
import com.klifora.shop.datastore.db.SearchModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.products.ItemProductRoot
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.IMAGE_URL
import com.klifora.shop.networking.Repository
import com.klifora.shop.ui.main.search.Search.Companion.itemProductsAdd
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.getSize
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
class SearchVM @Inject constructor(private val repository: Repository) : ViewModel() {

   var isListVisible : Boolean = false

    var searchType = 1


    val itemsSearch: ArrayList<ItemProduct> = ArrayList()

    val searchAdapter = object : GenericAdapter<ItemSearchBinding, ItemProduct>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSearchBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemSearchBinding,
            dataClass: ItemProduct,
            position: Int
        ) {
            binding.apply {
                textTitle.text = dataClass.name
                textPrice.text = "Price: ₹ "+ getPatternFormat("1", dataClass.price!!)
                dataClass.custom_attributes.forEach {
                    if (it.attribute_code == "size"){
//                        textRingsize.text = "Ring size: "+it.value
                        textRingsize.text = "Size: "+ getSize(it.value.toString().toInt())
                        textRingsize.visibility=View.VISIBLE
                    }

                    if (it.attribute_code == "metal_color"){
                        Log.e("TAG", "ColorAAAA: " + it.value)
                        val colorW = when(it.value.toString()){
                            "19" -> {"Yellow Gold"}
                            "20" -> {"Gold White"}
                            "25" -> {"Rose Gold"}
                            else -> {"No color"}
                        }
                        textColor.text = "Color: "+colorW
//                        textColor.text = "Color: "+it.value
                        textColor.visibility=View.VISIBLE
                    }

                    if (it.attribute_code == "metal_purity"){
                        val purityW = when(it.value.toString()){
                            "26" -> {"9Kt"}
                            "14" -> {"14Kt"}
                            "15" -> {"18Kt"}
                            "16" -> {"22Kt"}
                            "17" -> {"24Kt"}
                            "18" -> {"95(Platinum)"}
                            else -> {"No purity"}
                        }
                        textPurity.text = "Purity: "+purityW
                        textPurity.visibility=View.VISIBLE
                    }


//                    if (it.attribute_code == "metal_type"){
//                        Log.e("TAG", "metal_typeAAA " + it.value)
//                        if (it.value == "12"){
//                            dataClass.custom_attributes.forEach { againAttributes ->
//                                if (againAttributes.attribute_code == "metal_purity"){
//                                    Log.e("TAG", "metal_typeBBB " + againAttributes.value)
//                                    textPurity.text = "Purity: "+againAttributes.value
//                                    textRingsize.visibility=View.VISIBLE
//                                }
//                            }
//                        }
//                    } else {
//                        dataClass.custom_attributes.forEach { againAttributes ->
//                            if (againAttributes.attribute_code == "metal_purity"){
//                                Log.e("TAG", "metal_typeBBB " + againAttributes.value)
//                                textPurity.text = "Purity: "+againAttributes.value
//                                textRingsize.visibility=View.VISIBLE
//                            }
//                        }
//                    }
                }


                if (dataClass.media_gallery_entries.size > 0){
                    (IMAGE_URL +dataClass.media_gallery_entries[0].file).glideImage(binding.ivIcon.context, binding.ivIcon)
                }

                root.singleClick {
                    root.findNavController()
                        .navigate(R.id.action_search_to_productDetail, Bundle().apply {
                            if (searchType == 1){
                                if (dataClass.sku.contains("-")){
                                    putString("baseSku", dataClass.sku.split("-")[0])
                                } else if (dataClass.sku.contains(" ")){
                                    putString("baseSku", dataClass.sku.split(" ")[0])
                                } else {
                                    putString("baseSku", dataClass.sku)
                                }
                            } else if (searchType == 2) {
                                if (dataClass.sku.contains("-")){
                                    putString("baseSku", dataClass.sku.split("-")[0])
                                    putString("sku", dataClass.sku)

//                                    Log.e("TAG", "baseSkuAA " + dataClass.sku.split("-")[0])
//                                    Log.e("TAG", "skuAA " + dataClass.sku)

                                } else if (dataClass.sku.contains(" ")) {
                                    putString("baseSku", dataClass.sku.split(" ")[0])
                                    putString("sku", dataClass.sku)
                                } else {
                                    putString("baseSku", dataClass.sku)
//                                    Log.e("TAG", "baseSkuBB " + dataClass.sku)
//                                    Log.e("TAG", "skuBB " + dataClass.sku)
                                }
                            }
                        })
                }
            }
        }
    }



    var searchDelete = MutableLiveData<Boolean>(false)
    var searchValue = MutableLiveData<String?>("")

    val searchHistoryAdapter = object : GenericAdapter<ItemSearchHistoryBinding, SearchModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSearchHistoryBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemSearchHistoryBinding,
            dataClass: SearchModel,
            position: Int
        ) {
            binding.apply {
                textItem.text = dataClass.search_name
//                textDesc.visibility =
//                    if (dataClass.isCollapse == true) View.VISIBLE else View.GONE
//                ivHideShow.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        root.context,
//                        if (dataClass.isCollapse == true) R.drawable.baseline_remove_24 else R.drawable.baseline_add_24
//                    )
//                )

                textItem.singleClick {
                    searchValue.value = dataClass.search_name
                }



                ivCross.singleClick {
                    mainThread {
                        db?.searchDao()?.delete(dataClass)
                        searchDelete.value = true
                    }
                }
            }
        }
    }




    private var itemProductsResult = MutableLiveData<ItemProductRoot>()
    val itemProducts : LiveData<ItemProductRoot> get() = itemProductsResult
    fun getProducts(adminToken: String, view: View, emptyMap: MutableMap<String, String>, pageNumber : Int) =
        viewModelScope.launch {
            Log.e("TAG", "getProductsZZ")
            if(pageNumber == 0 || pageNumber == 1){
                repository.callApi(
                    callHandler = object : CallHandler<Response<JsonElement>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.productsID("Bearer " +adminToken, emptyMap)
                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<JsonElement>) {
                            if (response.isSuccessful) {
                                try {
                                    Log.e("TAG", "successAA: ${response.body().toString()}")
                                    val mMineUserEntity = Gson().fromJson(response.body(), ItemProductRoot::class.java)
                                    itemProductsAdd = true
                                    itemProductsResult.value = mMineUserEntity
                                } catch (_: Exception) {
                                }
                            }
                        }

                        override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                            if(message.contains("fieldName")){
                                showSnackBar("Something went wrong!")
                            } else if(message.contains("customerId")){
                                sessionExpired()
                            } else if(message.contains("authorized")){
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
                            apiInterface.productsID("Bearer " +adminToken, emptyMap)
                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<JsonElement>) {
                            if (response.isSuccessful) {
                                try {
                                    Log.e("TAG", "successAA: ${response.body().toString()}")
                                    val mMineUserEntity = Gson().fromJson(response.body(), ItemProductRoot::class.java)
                                    itemProductsResult.value = mMineUserEntity
                                } catch (e: Exception) {
                                }
                            }
                        }

                        override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                            if(message.contains("fieldName")){
                                showSnackBar("Something went wrong!")
                            } else if(message.contains("customerId")){
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


    override fun onCleared() {
        super.onCleared()
//        itemProductsAdd.value = false
    }

}