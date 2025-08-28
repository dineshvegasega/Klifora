package com.klifora.shop.ui.mainActivity

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.models.Items
import com.klifora.shop.models.cart.ItemCart
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(private val repository: Repository) : ViewModel() {
    companion object {
//        @JvmStatic
//        var quoteId : String = ""
//        var badgeCount = MutableLiveData<Boolean>(false)
        var cartItemLiveData = MutableLiveData<Boolean>(false)
        @JvmStatic
        var cartItemCount : Int = 0

        @JvmStatic
        var isApiCall : Boolean = false

        @JvmStatic
        var loginType = LoginType.CUSTOMER

        @JvmStatic
        var storeWebUrl: String = ""

        @JvmStatic
        var locale: Locale = Locale.getDefault()

//        var isHide: Boolean = false
//        var isHide = MutableLiveData<Boolean>()


//        val filters = hashMapOf<String, Any?>()

//        val arrayPrice : ArrayList<String> = ArrayList()
//        val arrayCategory : ArrayList<String> = ArrayList()
//        val arrayMaterial : ArrayList<String> = ArrayList()
//        val arrayShopFor : ArrayList<String> = ArrayList()




        var isFilterFrom: Boolean = false

        var mainPrice: ArrayList<Items> = ArrayList()
        val mainCategory : ArrayList<Items> = ArrayList()
        var mainMaterial: ArrayList<Items> = ArrayList()
        var mainShopFor: ArrayList<Items> = ArrayList()

        init {
            mainPrice.add(Items(name = "₹1000 - ₹10000"))
            mainPrice.add(Items(name = "₹10000 - ₹15000"))
            mainPrice.add(Items(name = "₹15000 - ₹20000"))
            mainPrice.add(Items(name = "₹20000 - ₹30000"))
            mainPrice.add(Items(name = "₹30000 - ₹50000"))
            mainPrice.add(Items(name = "₹50000 - ₹100000"))
            mainPrice.add(Items(name = "₹100000 - ₹1000000"))
            mainPrice.add(Items(name = "₹1000000 - Above"))


            mainCategory.add(Items(id = 19, parent_id = 18, name = "Rings", image = R.drawable.rings,
                subCategory = arrayListOf(
                    Items(id = -1, parent_id = 19, name = "All Rings", isAll = true),
                    Items(id = 13, parent_id = 19, name = "Solitaire Rings"),
                    Items(id = 14, parent_id = 19, name = "Everyday Wear"),
                    Items(id = 15, parent_id = 19, name = "Cocktail Rings"),
                    Items(id = 16, parent_id = 19, name = "Engagement Rings"),
                    Items(id = 17, parent_id = 19, name = "Bands"),
                    Items(id = 104, parent_id = 19, name = "Eternity Band"),
                    Items(id = 105, parent_id = 19, name = "Wedding Band"),
                    Items(id = 437, parent_id = 19, name = "Valentine Love Symbol"),
                    Items(id = 464, parent_id = 19, name = "Alphabets"),
                    )
            ))
            mainCategory.add(Items(id = 4, parent_id = 18, name = "Earrings", image = R.drawable.ear_ring,
                subCategory = arrayListOf(
                    Items(id = -1, parent_id = 4, name = "All Earrings", isAll = true),
                    Items(id = 434, parent_id = 4, name = "Hoop & Baali"),
                    Items(id = 435, parent_id = 4, name = "Drop"),
                    Items(id = 436, parent_id = 4, name = "Stud"))
            ))
            mainCategory.add(Items(id = 5, parent_id = 18, name = "Pendants", image = R.drawable.pendant,
                subCategory = arrayListOf(
                    Items(id = -1, parent_id = 5, name = "All Pendants", isAll = true),
                    Items(id = 439, parent_id = 5, name = "Everyday Wear"),
                    Items(id = 440, parent_id = 5, name = "Valentine Love Symbol"),
                    Items(id = 441, parent_id = 5, name = "Tanmaniya"))
            ))
            mainCategory.add(Items(id = 6, parent_id = 18, name = "Bracelets", image = R.drawable.bracelet,
//                subCategory = arrayListOf(
//                    Items(name = "All Bracelets", isAll = true),
//                    Items(name = "Casual"),
//                    Items(name = "Bangles"),
//                    Items(name = "Occasion"),
//                    Items(name = "Everyday"))
            ))
//            mainCategory.add(Items(id = 7, parent_id = 18, name = "Mangalsutras", image = R.drawable.mangalsutras,
////                subCategory = arrayListOf(
////                    Items(name = "All Mangalsutras", isAll = true),
////                    Items(name = "Casual"),
////                    Items(name = "Bangles"),
////                    Items(name = "Everyday"))
//            ))
            mainCategory.add(Items(id = 8, parent_id = 18, name = "Nosepins", image = R.drawable.nosepins,
//                subCategory = arrayListOf(
//                    Items(name = "All Nosepins", isAll = true),
//                    Items(name = "Balis"),
//                    Items(name = "Studs"),
//                    Items(name = "Drops"),
//                    Items(name = "Hoops"))
            ))
            mainCategory.add(Items(id = 9, parent_id = 18, name = "Solitaire", image = R.drawable.solitire,
//                subCategory = arrayListOf(
//                    Items(name = "All Solitaire", isAll = true),
//                    Items(name = "Balis"),
//                    Items(name = "Studs"),
//                    Items(name = "Drops"),
//                    Items(name = "Hoops"))
            ))
//            mainCategory.add(Items(id = 10, parent_id = 18, name = "Accessories", image = R.drawable.accessories,
////                subCategory = arrayListOf(
////                    Items(name = "All Accessories", isAll = true),
////                    Items(name = "Balis"),
////                    Items(name = "Studs"),
////                    Items(name = "Drops"),
////                    Items(name = "Hoops"))
//            ))
            mainCategory.add(Items(id = 11, parent_id = 18, name = "Bangles", image = R.drawable.bangles,
//                subCategory = arrayListOf(
//                    Items(name = "All Bangles", isAll = true),
//                    Items(name = "Balis"),
//                    Items(name = "Studs"),
//                    Items(name = "Drops"),
//                    Items(name = "Hoops"))
            ))

            mainCategory.add(Items(id = 465, parent_id = 18, name = "Necklace", image = R.drawable.mangalsutras,
//                subCategory = arrayListOf(
//                    Items(name = "All Bangles", isAll = true),
//                    Items(name = "Balis"),
//                    Items(name = "Studs"),
//                    Items(name = "Drops"),
//                    Items(name = "Hoops"))
            ))


            mainCategory.add(Items(id = 369, parent_id = 18, name = "Men", image = R.drawable.male_ring,
//                subCategory = arrayListOf(
//                    Items(name = "All Bangles", isAll = true),
//                    Items(name = "Balis"),
//                    Items(name = "Studs"),
//                    Items(name = "Drops"),
//                    Items(name = "Hoops"))
            ))


//            mainCategory.add(Items(id = 465, parent_id = 18, name = "All", image = R.drawable.mangalsutras,
////                subCategory = arrayListOf(
////                    Items(name = "All Bangles", isAll = true),
////                    Items(name = "Balis"),
////                    Items(name = "Studs"),
////                    Items(name = "Drops"),
////                    Items(name = "Hoops"))
//            ))



//            mainCategory.add(Items(id = 12, parent_id = 18, name = "Watches", image = R.drawable.watches,
////                subCategory = arrayListOf(
////                    Items(name = "All Watches", isAll = true),
////                    Items(name = "Balis"),
////                    Items(name = "Studs"),
////                    Items(name = "Drops"),
////                    Items(name = "Hoops"))
//            ))




            mainMaterial.add(Items(id = 12, name = "Gold"))
            mainMaterial.add(Items(id = 13,name = "Platinum"))



            mainShopFor.add(Items(id = 53, name = "Women"))
            mainShopFor.add(Items(id = 52, name = "Men"))
            mainShopFor.add(Items(id = 54, name = "Kids"))
        }
    }






    fun adminToken(jsonObject: JSONObject, callBack: String.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<JsonElement>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.adminToken(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<JsonElement>) {
                    if (response.isSuccessful){
//                        Log.e("TAG", "successAA: ${response.body().toString()}")
                        try {
                            val token = response.body().toString().substring(1, response.body().toString().length - 1)
                            callBack(token)
                        }catch (e : Exception){
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                    Log.e("TAG", "successBB: ${message.toString()}")
                    callBack(message.toString())
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
//                        if (loginType == "vendor") {
                        apiInterface.getQuoteId("Bearer " +adminToken, storeWebUrl, requestBody = jsonObject.getJsonRequestBody())
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







    fun getCart(customerToken: String, callBack: ItemCart.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
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
//                        if(message.contains("fieldName")){
//                            showSnackBar("Something went wrong!")
//                        } else {
//                            sessionExpired()
//                        }

                        if(message.contains("%fieldValue")){
//                            sessionExpired()
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
        }

}