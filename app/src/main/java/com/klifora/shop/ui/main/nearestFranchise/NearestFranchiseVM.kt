package com.klifora.shop.ui.main.nearestFranchise

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klifora.shop.models.ItemFranchiseArray
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NearestFranchiseVM @Inject constructor(private val repository: Repository) : ViewModel() {

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

}