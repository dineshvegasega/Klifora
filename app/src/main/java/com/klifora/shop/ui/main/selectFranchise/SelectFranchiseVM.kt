package com.klifora.shop.ui.main.selectFranchise

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemSelectFranchiseBinding
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.ItemFranchise
import com.klifora.shop.models.ItemFranchiseArray
import com.klifora.shop.models.user.ItemUser
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SelectFranchiseVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var selectedPosition = -1
    val franchiseListAdapter = object : GenericAdapter<ItemSelectFranchiseBinding, ItemFranchise>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSelectFranchiseBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemSelectFranchiseBinding,
            dataClass: ItemFranchise,
            position: Int
        ) {
            binding.apply {
                root.singleClick {
                    selectedPosition = position
                    notifyDataSetChanged()
//                    dataClass.isSelected = !dataClass.isSelected

//                    mainThread {
//                        currentList.forEach {
//                            binding.layoutTop.setBackgroundResource(R.drawable.bg_all_round_franchise)
//                        }
//
//                        binding.layoutTop.backgroundTintList =
//                            ColorStateList.valueOf(
//                                ContextCompat.getColor(
//                                    binding.root.context,
//                                    R.color._B9F2FF
//                                )
//                            )
//                    }
                }

                if (selectedPosition == position) {
                    binding.layoutTop.backgroundTintList =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._B9F2FF
                            )
                        )
                } else {
//                    binding.layoutTop.setBackgroundResource(R.drawable.all_round)
                    binding.layoutTop.setBackgroundResource(R.drawable.bg_all_round_franchise)
                    binding.layoutTop.backgroundTintList = null
                }

                textTitle.text = dataClass.name
                textAddr.text = dataClass.register_address
                textPincode.text = "Pincode - "+dataClass.d_pincode
                textContact.text = "Contact - "+dataClass.mobile_number


//                textDesc.visibility =
//                    if (dataClass.isCollapse == true) View.VISIBLE else View.GONE
//                ivHideShow.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        root.context,
//                        if (dataClass.isCollapse == true) R.drawable.baseline_remove_24 else R.drawable.baseline_add_24
//                    )
//                )

//                textItem.singleClick {
//                    searchValue.value = dataClass.search_name
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





    fun franchiseList(callBack: ItemFranchiseArray.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemFranchiseArray>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        } else if (loginType == "guest") {
                        apiInterface.franchiseList()

                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemFranchiseArray>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                callBack(response.body()!!)
                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        showSnackBar(message.toString())
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }





    fun customerDetail(token: String, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemUser>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.userDetail(token)
                    override fun success(response: Response<ItemUser>) {
                        if (response.isSuccessful) {
                            try {
//                            val token = response.body().toString().substring(1, response.body().toString().length - 1)
                                if(response.body()?.size!! > 0){
                                    val item = Gson().toJson(response.body()?.get(0))
                                    Log.e("TAG", "customerDetailvvv: ${item}")
                                    callBack(item)
                                } else {
                                    callBack("false")
                                }
                            } catch (e: Exception) {
                            }

                        }
                    }

                    override fun error(message: String) {
                        super.error(message)
//                    showSnackBar(message)
                        callBack(message.toString())
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }








    fun placeOrderGuest(jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.placeOrderGuest(requestBody = jsonObject.getJsonRequestBody())
                    //                        } else if (loginType == "guest") {
//                        apiInterface.getQuoteId("Bearer " +adminToken, emptyMap)
                    //                        } else {
//                            apiInterface.products("Bearer " +adminToken, storeWebUrl, emptyMap)
//                        }
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
                        if(message.contains("fieldName")){
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




}