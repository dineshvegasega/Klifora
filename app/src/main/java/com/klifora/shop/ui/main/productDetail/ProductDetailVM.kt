package com.klifora.shop.ui.main.productDetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.klifora.shop.R
import com.klifora.shop.abc.ABC.DiamondItem
import com.klifora.shop.databinding.DialogPdfBinding
import com.klifora.shop.databinding.ItemDiamondHistoryBinding
import com.klifora.shop.databinding.ItemProductBinding
import com.klifora.shop.databinding.ItemProductDiamondsBinding
import com.klifora.shop.databinding.ItemProductZoomBinding
import com.klifora.shop.databinding.ItemSizeBinding
import com.klifora.shop.databinding.LoaderBinding
import com.klifora.shop.databinding.ProductDetailBinding
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.ItemRelatedProducts
import com.klifora.shop.models.ItemRelatedProductsItem
import com.klifora.shop.models.cart.ItemCart
import com.klifora.shop.models.cart.ItemCartModel
import com.klifora.shop.models.options.ItemOptions
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.products.MediaGalleryEntry
import com.klifora.shop.models.products.Value
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.IMAGE_URL
import com.klifora.shop.networking.Repository
import com.klifora.shop.networking.getJsonRequestBody
//import com.klifora.shop.ui.main.productDetail.ProductDetail.Companion.dialogBinding1
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.getSize
import com.klifora.shop.utils.glideImage
import com.klifora.shop.utils.glideImageChache
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.pdfviewer.PdfRendererView
import com.klifora.shop.utils.pdfviewer.util.FileUtils.fileFromAsset
import com.klifora.shop.utils.pdfviewer.util.FileUtils.uriToFile
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProductDetailVM @Inject constructor(private val repository: Repository) : ViewModel() {

//    var isApiCall : Boolean = false

    var arrayItemProductOptionsColor: MutableList<Value> = ArrayList()
    var arrayItemProductOptionsPurity: MutableList<Value> = ArrayList()
    var arrayItemProductOptionsSize: MutableList<Value> = ArrayList()


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

    var itemZoomMutable = MutableLiveData<Int>(0)
    val productZoomAdapter = object : GenericAdapter<ItemProductZoomBinding, String>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemProductZoomBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemProductZoomBinding,
            dataClass: String,
            position: Int
        ) {
            binding.apply {
                Log.e("TAG", "IMAGE_URL+dataClass"+IMAGE_URL + dataClass)
                (IMAGE_URL + dataClass).glideImageChache(binding.ivIcon.context, binding.ivIcon)

                currentList.forEach {
                    if (itemZoomMutable.value == position) {
                        constant1111.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.ivIcon.context, R.color.app_color))
                    } else {
                        constant1111.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.ivIcon.context, R.color.white))
                    }
                }

                ivIcon.setOnClickListener {
                    itemZoomMutable.value = position
                    notifyDataSetChanged()
//                    if (itemZoomMutable.value == position){
//                        constant1111.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.ivIcon.context, R.color.app_color))
//                    } else {
//                        constant1111.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(binding.ivIcon.context, R.color._a5a8ab))
//                    }
                }
            }
        }
    }


    fun getCartCount(callBack: Int.() -> Unit) {
        viewModelScope.launch {
            val userList: List<CartModel>? = db?.cartDao()?.getAll()
            var countBadge = 0
            userList?.forEach {
                countBadge += it.quantity
            }
            callBack(countBadge)
        }
    }


    fun addCart(adminToken: String, jsonObject: JSONObject, callBack: ItemCartModel.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemCartModel>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
//                        if (loginType == "vendor") {
                        apiInterface.addCart(
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
                    override fun success(response: Response<ItemCartModel>) {
                        if (response.isSuccessful) {
                            try {
//                                Log.e("TAG", "successAAXX: ${response.body().toString()}")
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
                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun updatePrice(_id: String, callBack: JsonElement.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.updatedPrice(_id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        Log.e("TAG", "successAACCC: ${response.body()}")
                        callBack(response.body()!!)
//                        if (response.isSuccessful) {
//                        try {
////                                Log.e("TAG", "successAACCC: ${response.body().toString()}")
////                                //     val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)
////                                Log.e("TAG", "getProductDetailAAXXXX ")
//                            callBack(response.body()!!)
//                        } catch (e: Exception) {
//                            showSnackBar("Product not available!")
//                            hide()
//                        }
//                        }
                    }

                    override fun error(message: String) {
                        if (message.contains("fieldName")) {
                            showSnackBar("Something went wrong!")
                        } else if (message.contains("The product that was requested doesn't exist")) {
                            showSnackBar(message)
                        } else if (message.contains("error")) {
                            showSnackBar(message)
                        } else if(message.contains("customerId")){
                            sessionExpired()
                        } else {
                            showSnackBar("Something went wrong!")
                        }
                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }




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
//                            showSnackBar("Something went wrong!")
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
                        apiInterface.productsDetailID("Bearer " + adminToken, skuId)
                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
//                                Log.e("TAG", "successAA: ${response.body().toString()}")
                                val mMineUserEntity =
                                    Gson().fromJson(response.body(), ItemProduct::class.java)

//                                viewModelScope.launch {
////                                    mMineUserEntity.forEach {items ->
//                                        val userList: List<CartModel>? = db?.cartDao()?.getAll()
//                                        userList?.forEach { user ->
//                                            if (mMineUserEntity.id == user.product_id) {
//                                                mMineUserEntity.apply {
//                                                    isSelected = true
//                                                }
////                                                Log.e( "TAG", "YYYYYYYYY: " )
//                                            } else {
//                                                mMineUserEntity.apply {
//                                                    isSelected = false
//                                                }
////                                                Log.e( "TAG", "NNNNNNNNNN: " )
//                                            }
//                                        }
////                                    }
//                                }
                                callBack(mMineUserEntity)

                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                        if (message.contains("fieldName")) {
                            showSnackBar("Something went wrong!")
                        } else if (message.contains("The product that was requested doesn't exist")) {
                            showSnackBar(message)
                        } else if(message.contains("customerId")){
                            sessionExpired()
                        } else {
                            showSnackBar("Something went wrong!")
                        }

                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    fun getProductOptions(_id: String, callBack: ItemOptions.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<ItemOptions>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.productsOptions(_id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemOptions>) {
                        Log.e("TAG", "successAACCC: ${response.body()}")
//                        if (response.isSuccessful) {
                            try {
//                                Log.e("TAG", "successAACCC: ${response.body().toString()}")
//                                //     val mMineUserEntity = Gson().fromJson(response.body(), ItemProduct::class.java)
//                                Log.e("TAG", "getProductDetailAAXXXX ")
                                callBack(response.body()!!)
                            } catch (e: Exception) {
                                showSnackBar("Product not available!")
                                hide()
                            }
//                        }
                    }

                    override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                        if (message.contains("fieldName")) {
                            showSnackBar("Something went wrong!")
                        } else if (message.contains("The product that was requested doesn't exist")) {
                            showSnackBar(message)
                        } else if (message.contains("error")) {
                            showSnackBar(message)
                        } else if(message.contains("customerId")){
                            sessionExpired()
                        } else {
                            showSnackBar("Something went wrong!")
                        }
                        hide()
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }





    fun allProducts(
        adminToken: String,
        view: View,
        skuId: String,
        callBack: ArrayList<ItemProduct>.() -> Unit
    ) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<JsonElement>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.allProducts("Bearer " + adminToken, skuId)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<JsonElement>) {
                        if (response.isSuccessful) {
                            try {
//                                Log.e("TAG", "successAABBCC: ${response.body().toString()}")
                                val typeToken = object : TypeToken<ArrayList<ItemProduct>>() {}.type
                                val changeValue = Gson().fromJson<ArrayList<ItemProduct>>(
                                    response.body(),
                                    typeToken
                                )
//                                changeValue.forEach {
//                                    Log.e("TAG", "allProductsAA: "+it.sku)
//                                }
                                if (changeValue.isNotEmpty()) {
                                    callBack(changeValue)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }

                    override fun error(message: String) {
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                        if (message.contains("fieldName")) {
                            showSnackBar("Something went wrong!")
                        } else if (message.contains("The product that was requested doesn't exist")) {
                            showSnackBar(message)
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


    val recentAdapter = object : GenericAdapter<ItemProductDiamondsBinding, ItemProduct>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemProductDiamondsBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemProductDiamondsBinding,
            dataClass: ItemProduct,
            position: Int
        ) {
            binding.apply {
                dataClass.custom_attributes.forEach { itemProductAttr ->

//                    if (itemProductAttr.attribute_code == "metal_color") {
//                        textDiamondColorText.text = "" + itemProductAttr.value
//                    }
                    if (itemProductAttr.attribute_code == "size") {
                        textDiamondSizeText.text = "" + itemProductAttr.value
                    }

                    if (itemProductAttr.attribute_code == "metal_purity") {
                        if (itemProductAttr.value == "14") {
                            dataClass.custom_attributes.forEach { itemProductChildAttr ->
                                if (itemProductChildAttr.attribute_code == "metal_color") {
                                    if (itemProductChildAttr.value == "19") {
                                        textDiamondColorText.text = "14 kt Yellow Gold"
                                    } else if (itemProductChildAttr.value == "20") {
                                        textDiamondColorText.text = "14 kt White Gold"
                                    } else if (itemProductChildAttr.value == "18") {
                                        textDiamondColorText.text = "14 kt Rose Gold"
                                    }
                                }
                            }
                        }

                        if (itemProductAttr.value == "15") {
                            dataClass.custom_attributes.forEach { itemProductChildAttr ->
                                if (itemProductChildAttr.attribute_code == "metal_color") {
                                    if (itemProductChildAttr.value == "19") {
                                        textDiamondColorText.text = "18 kt Yellow Gold"
                                    } else if (itemProductChildAttr.value == "20") {
                                        textDiamondColorText.text = "18 kt White Gold"
                                    } else if (itemProductChildAttr.value == "18") {
                                        textDiamondColorText.text = "18 kt Rose Gold"
                                    }
                                }
                            }
                        }

                        if (itemProductAttr.value == "16") {
                            textDiamondColorText.text = "Platinum 95"
                        }
                    }

                }

                textDiamondClarityText.text = ""
                textDiamondShapeText.text = ""
                textDiamondNoText.text = ""
                textDiamondTotalWeightText.text = "" + dataClass.weight

            }
        }
    }


    var sizeMutableListClose = MutableLiveData<Boolean>(false)
    var sizeMutableList = MutableLiveData<Int>(0)

    //    var sizeValue : Int = 0
    var selectedPosition = -1
    val sizeAdapter = object : GenericAdapter<ItemSizeBinding, Value>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemSizeBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemSizeBinding,
            dataClass: Value,
            position: Int
        ) {
            binding.apply {
                textSize.text = "" + getSize(dataClass.value_index)
//                textMM.text = dataClass.mm.toString() +" mm"

                ivIcon.singleClick {
                    selectedPosition = position
                    val list = currentList
                    list.apply {
                        forEach {
                            it.isSelected = false
                        }
                        this[position].isSelected = true
                    }
////                    dataClass.isSelected = !dataClass.isSelected
//                    notifyItemChanged(position)


                    sizeMutableList.value = dataClass.value_index.toInt()
                    sizeMutableListClose.value = true
                    notifyDataSetChanged()
                }

//                if (selectedPosition == position) {
//                    ivIcon.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.app_color))
//                } else {
//                    ivIcon.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color._E1E2DB))
//                }

                textSize.setTextColor(
                    if (dataClass.isSelected) ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                    else ContextCompat.getColor(binding.root.context, R.color._000000)
                )

                ivIcon.setBackgroundColor(
                    if (dataClass.isSelected) ContextCompat.getColor(
                        binding.root.context,
                        R.color.app_color
                    )
                    else ContextCompat.getColor(binding.root.context, R.color._E1E2DB)
                )
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public fun indicator(
        binding: ProductDetailBinding,
        arrayList: ArrayList<MediaGalleryEntry>,
        current: Int
    ) {
        val views: ArrayList<View> = ArrayList()
        views.add(binding.indicatorLayout.view1)
        views.add(binding.indicatorLayout.view2)
        views.add(binding.indicatorLayout.view3)
        views.add(binding.indicatorLayout.view4)
        views.add(binding.indicatorLayout.view5)
        views.add(binding.indicatorLayout.view6)
        views.add(binding.indicatorLayout.view7)
        views.add(binding.indicatorLayout.view8)
        views.add(binding.indicatorLayout.view9)
        views.add(binding.indicatorLayout.view10)
        views.add(binding.indicatorLayout.view11)
        views.add(binding.indicatorLayout.view12)
        views.add(binding.indicatorLayout.view13)
        views.add(binding.indicatorLayout.view14)
        views.add(binding.indicatorLayout.view15)
        views.add(binding.indicatorLayout.view16)
        views.add(binding.indicatorLayout.view17)
        views.add(binding.indicatorLayout.view18)
        views.add(binding.indicatorLayout.view19)
        views.add(binding.indicatorLayout.view20)

        var index = 0
        views.forEach {
            Log.e("TAG", " index " + index + " size " + arrayList.size + " current " + current)
            if (index <= (arrayList.size - 1)) {
                it.visibility = VISIBLE
                if (arrayList[index].file.endsWith(".mp4")) {
                    it.setBackgroundResource(R.drawable.ic_triangle_right)
                } else {
                    it.setBackgroundResource(R.drawable.bg_all_round_black)
                }
                if (index == current) {
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            MainActivity.context.get()!!, R.color.app_color
                        )
                    )
                } else {
                    it.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            MainActivity.context.get()!!, R.color._9A9A9A
                        )
                    )
                }
            } else {
                it.visibility = GONE
            }
            index++
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public fun colors(binding: ProductDetailBinding, type: Int) {
        when (type) {
            1 -> {
                binding.linearRoseGold.setBackgroundResource(R.drawable.rounds_black_5)
                binding.linearWhiteGold.setBackgroundColor(Color.WHITE)
                binding.linearYellowGold.setBackgroundColor(Color.WHITE)
            }

            2 -> {
                binding.linearRoseGold.setBackgroundColor(Color.WHITE)
                binding.linearWhiteGold.setBackgroundResource(R.drawable.rounds_black_5)
                binding.linearYellowGold.setBackgroundColor(Color.WHITE)
            }

            3 -> {
                binding.linearRoseGold.setBackgroundColor(Color.WHITE)
                binding.linearWhiteGold.setBackgroundColor(Color.WHITE)
                binding.linearYellowGold.setBackgroundResource(R.drawable.rounds_black_5)
            }
        }
    }


    public fun openDialogPdf(type: Int, pdf: String) {
        val dialogBinding = DialogPdfBinding.inflate(
            MainActivity.activity.get()?.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
        )
        val dialog = Dialog(MainActivity.context.get()!!)
        dialog.setContentView(dialogBinding.root)
//        dialog.setOnShowListener { dia ->
//            val bottomSheetDialog = dia as BottomSheetDialog
//            val bottomSheetInternal: FrameLayout =
//                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//            bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
//        }
        dialog.show()
        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        window.setBackgroundDrawableResource(R.color._00000000)
//        val yes = mybuilder.findViewById<AppCompatImageView>(R.id.imageCross)
//        val title = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleMain)

        dialogBinding.imageCross.singleClick {
            dialog.dismiss()
        }

        initPdfViewerWithPath(dialogBinding, pdf)
    }


    var isFromAssets = true
    private fun initPdfViewerWithPath(bindingPdf: DialogPdfBinding, filePath: String?) {
        if (TextUtils.isEmpty(filePath)) {
            onPdfError(bindingPdf, "")
            return
        }
        try {
            val file = if (filePath!!.startsWith("content://")) {
                uriToFile(MainActivity.activity.get()!!, Uri.parse(filePath))
            } else if (isFromAssets) {
                fileFromAsset(MainActivity.context.get()!!, filePath)
            } else {
                File(filePath)
            }
            bindingPdf.pdfView.initWithFile(file)
        } catch (e: Exception) {
            onPdfError(bindingPdf, e.toString())
        }


        bindingPdf.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPdfLoadStart() {
                MainActivity.activity.get()?.runOnUiThread {
                    true.showProgressBar(bindingPdf)
                }
            }

            override fun onPdfLoadProgress(
                progress: Int,
                downloadedBytes: Long,
                totalBytes: Long?
            ) {
                //Download is in progress
            }

            override fun onPdfLoadSuccess(absolutePath: String) {
                MainActivity.activity.get()?.runOnUiThread {
                    false.showProgressBar(bindingPdf)
                    //downloadedFilePath = absolutePath
                }
            }

            override fun onError(error: Throwable) {
                MainActivity.activity.get()?.runOnUiThread {
                    false.showProgressBar(bindingPdf)
                    onPdfError(bindingPdf, error.toString())
                }
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                //Page change. Not require
            }
        }
    }


    private fun onPdfError(bindingPdf: DialogPdfBinding, e: String) {
        Log.e("Pdf render error", e)
        AlertDialog.Builder(MainActivity.context.get())
            .setTitle("pdf_viewer_error")
            .setMessage("error_pdf_corrupted")
            .setPositiveButton("pdf_viewer_retry") { dialog, which ->
                MainActivity.activity.get()?.runOnUiThread {
                    initPdfViewerWithPath(bindingPdf, "quote.pdf")
                }
            }
            .setNegativeButton("pdf_viewer_cancel", null)
            .show()
    }

    private fun Boolean.showProgressBar(bindingPdf: DialogPdfBinding) {
        bindingPdf.progressBar.visibility = if (this) VISIBLE else GONE
    }


    fun relatedProducts(_id: Int, callBack: ItemRelatedProducts.() -> Unit) =
        viewModelScope.launch {
            repository.callApiWithoutLoader(
                callHandler = object : CallHandler<Response<ItemRelatedProducts>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.relatedProducts(_id)

                    @SuppressLint("SuspiciousIndentation")
                    override fun success(response: Response<ItemRelatedProducts>) {
                        if (response.isSuccessful) {
                            callBack(response.body()!!)
//                            Log.e("TAG", "successAA: ${response.body().toString()}")
                        } else {
                            hide()
                            callBack(ItemRelatedProducts())
                        }
                    }

                    override fun error(message: String) {
                        showSnackBar(message.toString())
                        hide()
                        callBack(ItemRelatedProducts())
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    val productAdapter = object : GenericAdapter<ItemProductBinding, ItemRelatedProductsItem>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemProductBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemProductBinding,
            dataClass: ItemRelatedProductsItem,
            position: Int
        ) {
            binding.apply {

                textTitle.text = dataClass.name
                textPrice.text = "₹ " + getPatternFormat("1", dataClass.price.toDouble())

                val image = IMAGE_URL + dataClass.image
                image.glideImage(
                    this.root.context,
                    ivIcon
                )

                ivIcon.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_productDetail_to_productDetail, Bundle().apply {
                            putString("baseSku", dataClass.sku)
                            putString("sku", "")
                        })
                }
            }
        }
    }







    val diamondAdapter = object : GenericAdapter<ItemDiamondHistoryBinding, DiamondItem>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemDiamondHistoryBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemDiamondHistoryBinding,
            dataClass: DiamondItem,
            position: Int
        ) {
            binding.apply {
//                textClarity.text = dataClass.record_id

                if (position == 0){
                    layoutMain.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color._c9cdd0))
                } else {
                    layoutMain.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
                    textClarity.text = dataClass.diamond_clarity
                    textWeight.text = dataClass.diamond_weight
                    textShape.text = dataClass.diamond_shape.capitalize()
                    textColor.text = dataClass.diamond_color.capitalize()
                    textNumbers.text = dataClass.diamond_number


                }

            }
        }
    }

}