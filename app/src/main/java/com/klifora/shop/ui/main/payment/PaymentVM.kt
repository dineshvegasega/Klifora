package com.klifora.shop.ui.main.payment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.ApiTranslateInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.CallHandlerTranslate
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentVM @Inject constructor(private val repository: Repository) : ViewModel() {


    fun createOrder(adminToken: String, jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApiTranslate(
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


//    fun postCustomDetails(adminToken: String, jsonObject: JSONObject, callBack: JsonElement.() -> Unit) =
//        viewModelScope.launch {
//            repository.callApi(
//                callHandler = object : CallHandler<Response<JsonElement>> {
//                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        apiInterface.postCustomDetails("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
//                    @SuppressLint("SuspiciousIndentation")
//                    override fun success(response: Response<JsonElement>) {
//                        if (response.isSuccessful) {
//                            try {
//                                Log.e("TAG", "successAAXXZZ: ${response.body().toString()}")
//                                callBack(response.body()!!)
//                            } catch (_: Exception) {
//                            }
//                        }
//                    }
//
//                    override fun error(message: String) {
//                        showSnackBar(message)
////                        if(message.contains("fieldName")){
////                            showSnackBar("Something went wrong!")
////                        } else {
////                            sessionExpired()
////                        }
//                    }
//
//                    override fun loading() {
//                        super.loading()
//                    }
//                }
//            )
//        }

}