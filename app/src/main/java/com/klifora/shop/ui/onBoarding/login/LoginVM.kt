package com.klifora.shop.ui.onBoarding.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.databinding.LoaderBinding
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_ID
import com.klifora.shop.datastore.DataStoreUtil.saveData
import com.klifora.shop.datastore.DataStoreUtil.saveObject
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.models.user.ItemUser
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(private val repository: Repository) : ViewModel() {

    companion object {
        var storeToken = ""
    }



    var alertDialog: AlertDialog? = null
    init {
        val alert = AlertDialog.Builder(MainActivity.activity.get())
        val binding =
            LoaderBinding.inflate(LayoutInflater.from(MainActivity.activity.get()), null, false)
        alert.setView(binding.root.rootView)
        alert.setCancelable(false)
        alertDialog = alert.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        mainThread {
            if (alertDialog != null) {
                alertDialog?.dismiss()
                alertDialog?.show()
            }
        }
    }

    fun hide() {
        mainThread {
            if (alertDialog != null) {
                alertDialog?.dismiss()
            }
        }
    }


    fun websiteUrl(adminToken: String, jsonObject: JSONObject, callBack: ItemWebsite.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<ItemWebsite>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.websiteUrl(jsonObject.getString("emailmobile"))

                    override fun success(response: Response<ItemWebsite>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
//                                val json = JSONObject(response.body().toString())
//                                val status = json.getString("status")
                                if (response.body()!!.status == "true") {
                                    val website_id = response.body()!!.website_id
                                    saveData(WEBSITE_ID, website_id)
                                    storeWebUrl = website_id
                                    Log.e("TAG", "successAAXX11: ${response.body().toString()}")
                                    saveObject(
                                        WEBSITE_DATA,
                                        Gson().fromJson(Gson().toJson(response.body()),
                                            ItemWebsite::class.java
                                        )
                                    )
                                    callBack(response.body()!!)
                                } else {
                                    Log.e("TAG", "successAAXX22: ${response.body().toString()}")
                                    hide()
                                    showSnackBar("Invalid Credentials")
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        showSnackBar(message)
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun adminToken(jsonObject: JSONObject, callBack: String.() -> Unit) = viewModelScope.launch {
        repository.callApiWithoutLoader(
            callHandler = object : CallHandler<Response<JsonElement>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.adminToken(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<JsonElement>) {
                    if (response.isSuccessful) {
//                        Log.e("TAG", "successAA: ${response.body().toString()}")
                        try {
                            val token = response.body().toString()
                                .substring(1, response.body().toString().length - 1)
                            callBack(token)
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                    Log.e("TAG", "successBB: ${message.toString()}")
//                    callBack(message.toString())
                    hide()
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun customerLoginToken(
        adminToken: String,
        jsonObject: JSONObject,
        callBack: String.() -> Unit
    ) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.customerLoginToken(
                            "Bearer " + adminToken,
                            storeWebUrl,
                            requestBody = jsonObject.getJsonRequestBody()
                        )

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                val jsonObject = response.body().toString()
                                    .substring(1, response.body().toString().length - 1).toString()
                                    .replace("\\", "")
                                Log.e("TAG", "successAAB: ${jsonObject}")
                                if (JSONObject(jsonObject).getString("success") == "true"){
                                    storeToken = JSONObject(jsonObject).getString("token")
                                    callBack(storeToken)
                                } else {
                                    showSnackBar("Invalid Credentials")
                                    hide()
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
                        showSnackBar(message)
//                        callBack(message.toString())
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
                        apiInterface.userDetail(storeWebUrl)

                    override fun success(response: Response<ItemUser>) {
                        if (response.isSuccessful) {
                            try {
//                            val token = response.body().toString().substring(1, response.body().toString().length - 1)
                                if(response.body()?.size!! > 0){
                                    val item = Gson().toJson(response.body()?.get(0))
                                    Log.e("TAG", "customerDetailvvv: ${item}")
                                    callBack(item)
                                }
                            } catch (e: Exception) {
                            }
//                            hide()
                        }
                    }

                    override fun error(message: String) {
                        super.error(message)
//                    showSnackBar(message)
//                        callBack(message.toString())
                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun getQuoteId(adminToken: String, jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.getQuoteId(
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
                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


//
//
//    fun customerDetail(jsonObject: JSONObject) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<JsonElement>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.adminToken(requestBody = jsonObject.getJsonRequestBody())
//                override fun success(response: Response<JsonElement>) {
//                    if (response.isSuccessful){
//                        //itemLiveNoticeResultSecond.value =  response.body() as BaseResponseDC<Any>
//                    }
//
//                    Log.e("TAG", "success: ${response.body()}")
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
////                    showSnackBar(message)
//
//                    Log.e("TAG", "success: ${message}")
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }
}