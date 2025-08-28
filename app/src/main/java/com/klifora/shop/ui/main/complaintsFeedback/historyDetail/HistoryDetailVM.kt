package com.klifora.shop.ui.main.complaintsFeedback.historyDetail
import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.klifora.shop.models.ItemComplaint
import com.klifora.shop.models.ItemMessageHistory
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
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
class HistoryDetailVM @Inject constructor(private val repository: Repository): ViewModel() {
    val adapter by lazy { HistoryDetailAdapter() }

    var uploadMediaImage : String ?= null
    var uploadMediaImageBase64: String = ""

    private var messageHistoryLiveData = MutableLiveData<ItemMessageHistory>()
    val messageHistory : LiveData<ItemMessageHistory> get() = messageHistoryLiveData
    fun messageHistory(ticketId: String, _id: String) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemMessageHistory>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.messageHistory( storeWebUrl, ticketId, _id)
                override fun success(response: Response<ItemMessageHistory>) {
                    if (response.isSuccessful){
//                        if(response.body()!!.data != null){
                        messageHistoryLiveData.value = response.body()
//                        }
                    }
                }
                override fun error(message: String) {
                    super.error(message)
                }
                override fun loading() {
                    super.loading()
                }
            }
        )
    }









    fun sendMessage(jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.sendMessage(
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
                                showSnackBar(response.body().toString())
                            }
                        }
                    }

                    override fun error(message: String) {
//                        if(message.contains("fieldName")){
                        showSnackBar(message)
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
//
//
//
//
//
//    private var feedbackConversationLiveDataSecond = MutableLiveData<ItemChat>()
//    val feedbackConversationLiveSecond : LiveData<ItemChat> get() = feedbackConversationLiveDataSecond
//    fun feedbackConversationDetailsSecond( _id: String, page: String) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<ItemChat>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.feedbackConversationDetails(_id, page)
//                override fun success(response: Response<ItemChat>) {
//                    if (response.isSuccessful){
//                        if(response.body()!!.data != null){
//                            feedbackConversationLiveDataSecond.value = response.body()
//                        }
//                    }
//                }
//                override fun error(message: String) {
//                    super.error(message)
//                }
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }
//
//
//
//
//
//
//    private var addFeedbackConversationLiveData = MutableLiveData<BaseResponseDC<Any>>()
//    val addFeedbackConversationLive : LiveData<BaseResponseDC<Any>> get() = addFeedbackConversationLiveData
//    fun addFeedbackConversationDetails(hashMap: RequestBody) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.addFeedbackConversation(hashMap)
//                override fun success(response: Response<BaseResponseDC<Any>>) {
//                    if (response.isSuccessful){
//                        addFeedbackConversationLiveData.value = response.body()
//                    }
//                }
//                override fun error(message: String) {
//                    super.error(message)
//                }
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }



    private var itemComplaintDetailResult = MutableLiveData<ItemComplaint>()
    val itemComplaintDetail: LiveData<ItemComplaint> get() = itemComplaintDetailResult

    fun getComplaintDetail(_id: String) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemComplaint>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.complaintDetail(storeWebUrl, _id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemComplaint>) {
                        Log.e("TAG", "success222:")
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
//                                val mMineUserEntity =
//                                    Gson().fromJson(response.body(), ItemProductRoot::class.java)
                                itemComplaintDetailResult.value = response.body()
                            } catch (_: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
                        Log.e("TAG", "success333:")
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
                        showSnackBar(message)
//                        callBack(message.toString())

                        if (message.contains("fieldName")) {
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

}