package com.klifora.shop.ui.main.complaintsFeedback.createNew


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.databinding.ItemTicketSkuBinding
import com.klifora.shop.databinding.LoaderBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.images.ItemImages
import com.klifora.shop.models.myOrdersDetail.ItemOrderDetail
import com.klifora.shop.models.myOrdersDetail.ItemX
import com.klifora.shop.models.myOrdersList.ItemOrders
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.IMAGE_URL
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.glideImage
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class CreateNewVM @Inject constructor(private val repository: Repository) : ViewModel() {

    companion object{
        var type: String = "1"
    }
    var typeSubject: String = ""
    var priorityType: String = "High"
//    var productsIds: String = ""
    var uploadMediaImage: String? = null
    var uploadMediaImageBase64: String = ""


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
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
                alertDialog?.show()
            }
        }
    }

    fun hide() {
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
            }
        }
    }


//    var itemComplaintType : ArrayList<ItemComplaintType> = ArrayList()
//    var complaintTypeId : Int = 0
//    fun complaintType(view: View) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemComplaintType>>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.complaintType()
//
//                override fun success(response: Response<BaseResponseDC<List<ItemComplaintType>>>) {
//                    if (response.isSuccessful){
//                        if (IS_LANGUAGE){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemComplaintType = response.body()?.data as ArrayList<ItemComplaintType>
//                            } else {
//                                val itemStateTemp = response.body()?.data as ArrayList<ItemComplaintType>
//                                show()
//                                mainThread {
//                                    itemStateTemp.forEach {
//                                        delay(50)
//                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
//                                        apply {
//                                            it.name = nameChanged
//                                        }
//                                    }
//                                    itemComplaintType = itemStateTemp
//                                    hide()
//                                }
//                            }
//                        } else {
//                            itemComplaintType = response.body()?.data as ArrayList<ItemComplaintType>
//                        }
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                }
//
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
//    fun newFeedback(
//        view: View,
//        hashMap: RequestBody
//    ) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.newFeedback( hashMap)
//
//                override fun success(response: Response<BaseResponseDC<Any>>) {
//                    if (response.isSuccessful){
//                        if (type == "complaint"){
//                            showSnackBar(view.resources.getString(R.string.complaint_added_successfully))
//                        } else if (type == "feedback"){
//                            showSnackBar(view.resources.getString(R.string.feedback_added_successfully))
//                        }
//                        view.findNavController().navigate(R.id.action_createNew_to_history)
//                    } else{
//                        showSnackBar(response.body()?.message.orEmpty())
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                    showSnackBar(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }


    fun orderHistoryList(
        franchiseId: String,
        mobile: String,
        name: String,
        callBack: ItemOrders.() -> Unit
    ) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemOrders>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.orderHistoryList(franchiseId, mobile, name, 1, 1000)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemOrders>) {
                        if (response.isSuccessful) {
//                            itemOrderHistoryResult.value = response.body()!!
//                            try {
//                                Log.e("TAG", "successAA: ${response.body().toString()}")
                            callBack(response.body()!!)
//                            } catch (e: Exception) {
//                            }
                        }
                    }

                    override fun error(message: String) {
//                        if (message.contains("authorized")) {
////                            sessionExpired()
//                        } else {
                        showSnackBar(message)
//                        }
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }





    var idsArray : ArrayList<String> = ArrayList()
    val orderSKUOrderHistory = object : GenericAdapter<ItemTicketSkuBinding, ItemX>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemTicketSkuBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemTicketSkuBinding,
            dataClass: ItemX,
            position: Int
        ) {
            binding.apply {

                textTitle.text = "SKU : ${dataClass.sku}"

                mainThread {
//                    readData(ADMIN_TOKEN) { token ->
//                        Log.e("TAG", "tokenOO: " + token)
//                        getProductDetail(token.toString(), dataClass.sku) {
//                            Log.e("TAG", "getProductDetailOO: " + this.name)
//                            if (this.media_gallery_entries.size > 0) {
//                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
//                                    binding.ivIcon.context,
//                                    binding.ivIcon
//                                )
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


                checkbox.visibility = View.VISIBLE
                radioButton.visibility = View.GONE
//                if (type == "2"){
//                    Log.e("TAG", "selectedPositionOrderHistory "+selectedPositionOrderHistory)
//                    checkbox.setChecked((if (selectedPositionOrderHistory == position) false else true))
//                }

                checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    Log.e("TAG", "typeAA "+type)

//                    if (type == "1"){
                        if (isChecked == true) {
                            idsArray.add(""+dataClass.product_id)
                        }
                        if (isChecked == false) {
                            idsArray.remove(""+dataClass.product_id)
                        }
//                    }

//                    if (type == "2"){
//                        selectedPositionOrderHistory = position
//                        notifyDataSetChanged()
//                    }
                }
            }
        }
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
//                                val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)

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


    var idsArrayFeedback : String = ""
    var selectedPositionOrderHistoryFeedback = -1
    val orderSKUOrderHistoryFeedback = object : GenericAdapter<ItemTicketSkuBinding, ItemX>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemTicketSkuBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemTicketSkuBinding,
            dataClass: ItemX,
            position: Int
        ) {
            binding.apply {

                textTitle.text = "SKU : ${dataClass.sku}"

                mainThread {
                    readData(ADMIN_TOKEN) { token ->
                        Log.e("TAG", "tokenOO: " + token)
                        getProductDetail(token.toString(), dataClass.sku) {
                            Log.e("TAG", "getProductDetailOO: " + this.name)
                            if (this.media_gallery_entries.size > 0) {
                                (IMAGE_URL + this.media_gallery_entries[0].file).glideImage(
                                    binding.ivIcon.context,
                                    binding.ivIcon
                                )
                            }
                        }
                    }
                }

                checkbox.visibility = View.GONE
                radioButton.visibility = View.VISIBLE

                radioButton.setChecked(if (position == selectedPositionOrderHistoryFeedback) true else false)


                radioButton.setOnClickListener {
                    selectedPositionOrderHistoryFeedback = position
                    idsArrayFeedback = ""+dataClass.product_id
                    notifyDataSetChanged()
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
                        apiInterface.productsDetailID("Bearer " + adminToken, skuId)

                    //                    } else {
//                        apiInterface.productsDetail("Bearer " +adminToken, storeWebUrl, skuId)
//                    }
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                val mMineUserEntity =
                                    Gson().fromJson(response.body(), ItemProduct::class.java)

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


    fun createTicket(jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.createTicket(
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



    fun createFeedback(jsonObject: JSONObject, callBack: String.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.createFeedback(
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


//    fun callApiTranslate(_lang: String, _words: String): String {
//        return repository.callApiTranslate(_lang, _words)
//    }
}