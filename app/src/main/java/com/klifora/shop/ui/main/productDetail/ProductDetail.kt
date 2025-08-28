package com.klifora.shop.ui.main.productDetail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.klifora.shop.R
import com.klifora.shop.abc.ABC.DiamondItem
import com.klifora.shop.databinding.DialogFullImageBinding
import com.klifora.shop.databinding.DialogSizesBinding
import com.klifora.shop.databinding.ProductDetailBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.QUOTE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.CartModel
import com.klifora.shop.models.options.ItemOptionsItem
import com.klifora.shop.models.products.ItemProduct
import com.klifora.shop.models.products.MediaGalleryEntry
import com.klifora.shop.models.products.Value
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.interfaces.CallBackListener
import com.klifora.shop.ui.main.productZoom.ProductZoomPagerAdapter
import com.klifora.shop.ui.main.products.ProductsVM.Companion.isProductLoad
import com.klifora.shop.ui.main.search.Search.Companion.itemProductsAdd
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.isApiCall
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.utils.getPatternFormat
import com.klifora.shop.utils.getRecyclerView
import com.klifora.shop.utils.getSize
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.pdfviewer.util.hide
import com.klifora.shop.utils.round
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class ProductDetail : Fragment(), CallBackListener {
    private val viewModel: ProductDetailVM by viewModels()
    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!

//    viewModel.idsArray.joinToString(separator = ",") { it -> "${it}" })

    companion object {

        @JvmStatic
        lateinit var pagerAdapter: FragmentStateAdapter

        var callBackListener: CallBackListener? = null

        @JvmStatic
        lateinit var adapter2: RelatedProductAdapter

        var images: ArrayList<MediaGalleryEntry> = ArrayList()

//        var dialogBinding1: DialogFullImageBinding? = null

    }


    var isSizeCall1: Boolean = false

    var ktType = 0


    val arrayItemProduct = mutableListOf<ItemOptionsItem>()
//    var arrayAllProduct: ArrayList<ItemProduct> = ArrayList()

    lateinit var itemProduct: ItemProduct
    lateinit var itemProductThis: ItemProduct

    var productType = ""

    var currentSku: String = ""

    var isWebview = 0;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ProductDetailBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(35)
    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this
        isBackStack = true
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(0)
        MainActivity.mainActivity.get()!!.callCartApi()

        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }

                topBarBack.ivCart.singleClick {
                    findNavController().navigate(R.id.action_productDetail_to_cart)
                }
            }



            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility =
                    if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

//            product type is not correct

//            topBar.apply {
//                textViewTitle.visibility = View.VISIBLE
////                cardSearch.visibility = View.VISIBLE
//                ivSearch.visibility = View.GONE
//                ivCart.visibility = View.VISIBLE
//                textViewTitle.text = "Product Detail"
//
//                appicon.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        MainActivity.context.get()!!,
//                        R.drawable.baseline_west_24
//                    )
//                )
//
//                appicon.singleClick {
//                    findNavController().navigateUp()
//                }
//
//                ivCart.singleClick {
//                    findNavController().navigate(R.id.action_productDetail_to_cart)
//                }
//
//
//                badgeCount.observe(viewLifecycleOwner) {
//                    viewModel.getCartCount() {
//                        Log.e("TAG", "count: $this")
//                        menuBadge.text = "${this}"
//                        menuBadge.visibility = if (this != 0) View.VISIBLE else View.GONE
//                    }
//                }
//            }


//            val model = arguments?.parcelable<ItemProduct>("model")

            val baseSku = arguments?.getString("baseSku")
            val sku = arguments?.getString("sku")

            Log.e("TAG", "baseSkuAA " + baseSku)
            Log.e("TAG", "skuAA " + sku)

            callSKUDetails(baseSku)

//            if (isApiCall == false){
//                isApiCall = true

//            SAS0005
//            SRI0006
//            callApiDetails("SRI0006")
//            }

            Log.e("TAG", "isApiCall " + isApiCall)

            btUpdate.singleClick {
                Log.e("TAG", "itemProduct " + itemProduct.id)
                viewModel.updatePrice(""+itemProduct.id){
//                    val jsonObject = JSONObject(this.toString())
//                    Log.e("TAG", "jsonObjectQQ "+jsonObject.toString())
                    callApiDetailsConfigurable(
                        currentSku,
                        itemProduct
                    )
                }
            }

            btRingSize.singleClick {
                openDialogSize()
            }

            btGuide.singleClick {
                viewModel.openDialogPdf(1, "Ring-Size-Guide-Shimmer.pdf")
            }

            var isAvailable: Boolean = false

            viewModel.sizeMutableList.value = -1
            viewModel.sizeMutableList.observe(viewLifecycleOwner) {
                if (it != -1) {
                    mainThread {
                        //Log.e("TAG", "sizeMutableList " + viewModel.sizeMutableList.value)
                        arrayItemProduct.forEach { itemProductOptions ->
                            itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                if (itemCustomColorAttr.attribute_code == "metal_color") {
                                    itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                        if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                            if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.size == "" + viewModel.sizeMutableList.value) {
                                                Log.e(
                                                    "TAG",
                                                    "jsonObjectSizeAA: ${itemProductOptions.toString()}"
                                                )
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!isAvailable) {
                            showSnackBar(getString(R.string.product_not_available))
                        }
                        isAvailable = false
                    }
                }
            }

            var isAvailableSize = false
            linearRoseGold.singleClick { // 25
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "25") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "25") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            linearWhiteGold.singleClick { // 20
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "50") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "50") {

                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                                callApiDetailsConfigurable(
                                                                    currentSku,
                                                                    itemProduct
                                                                )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            linearYellowGold.singleClick { // 19
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "19") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomPurityAttr ->
                                    if (itemCustomPurityAttr.attribute_code == "metal_purity") {
                                        if (itemProductOptions.metal_purity == itemCustomPurityAttr.value && itemProductOptions.metal_color == "19") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }


            bt9.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "26") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "26") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }



                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            bt14.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "14") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "14") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }





                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            bt18.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "15") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "15") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            bt22.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "16") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "16") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            bt24.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }

                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "17") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "17") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }

            bt95.singleClick {
                isAvailableSize = false
                runBlocking {
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (itemCustomSizeAttr.attribute_code == "size") {
                                isAvailableSize = true
                                Log.e("TAG", "AAAAAAAAAA1111" + isAvailableSize)
                            }
                        }
                    }
                    arrayItemProduct.forEach { itemProductOptions ->
                        itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                            if (isAvailableSize == true) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.size == itemCustomSizeAttr.value && itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "18") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (isAvailableSize == false) {
                                itemProductThis.custom_attributes.forEach { itemCustomColorAttr ->
                                    if (itemCustomColorAttr.attribute_code == "metal_color") {
                                        if (itemProductOptions.metal_color == itemCustomColorAttr.value && itemProductOptions.metal_purity == "18") {
                                            if (isSizeCall1 == false) {
                                                isSizeCall1 = true
                                                currentSku = itemProductOptions.sku
                                                isAvailable = true
//                                                callApiDetailsConfigurable(
//                                                    itemProductOptions.sku,
//                                                    itemProduct
//                                                )
                                                arrayItemProduct.forEach {
                                                    if (currentSku == it.sku){
                                                        viewModel.updatePrice(it.entity_id){
                                                            callApiDetailsConfigurable(
                                                                currentSku,
                                                                itemProduct
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!isAvailable) {
                        showSnackBar(getString(R.string.product_not_available))
                    }
                    isAvailable = false
                    isSizeCall1 = false
                }
            }


            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility =
                    if (cartItemCount != 0) View.VISIBLE else View.GONE
            }

            layoutAddtoCart.singleClick {
                if (LoginType.CUSTOMER == loginType) {
                    mainThread {
                        if (productType == "simple" || productType == "virtual") {

                            val newUser = CartModel(
                                product_id = itemProduct.id,
                                name = itemProduct.name,
                                price = itemProduct.price,
                                quantity = 1,
                                sku = itemProduct.sku,
                                currentTime = System.currentTimeMillis()
                            )
                            itemProduct.custom_attributes.forEach {
                                if (it.attribute_code == "size") {
                                    newUser.apply {
                                        this.size = "" + it.value
                                    }
                                }


                                if (it.attribute_code == "metal_color") {
                                    newUser.apply {
                                        this.color = "" + it.value
                                    }
                                }

                                if (it.attribute_code == "metal_type") {
                                    Log.e("TAG", "metal_typeAAA " + it.value)
                                    if (it.value == "12") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProduct.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeBBB " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }

                                    if (it.value == "13") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProduct.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeCCC " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            db?.cartDao()?.insertAll(newUser)
                            showSnackBar(getString(R.string.item_added_to_cart))
                            MainActivity.mainActivity.get()!!.callCartApi()


                        } else {
                        val newUser = CartModel(
                            product_id = itemProductThis.id,
                            name = itemProductThis.name,
                            price = itemProductThis.price,
                            quantity = 1,
                            sku = itemProductThis.sku,
                            currentTime = System.currentTimeMillis()
                        )
                        itemProductThis.custom_attributes.forEach {
                            if (it.attribute_code == "size") {
                                newUser.apply {
                                    this.size = "" + it.value
                                }
                            }


                            if (it.attribute_code == "metal_color") {
                                newUser.apply {
                                    this.color = "" + it.value
                                }
                            }

                            if (it.attribute_code == "metal_type") {
                                Log.e("TAG", "metal_typeAAA " + it.value)
                                if (it.value == "12") {
                                    newUser.apply {
                                        this.material_type = "" + it.value
                                    }
                                    itemProductThis.custom_attributes.forEach { againAttributes ->
                                        if (againAttributes.attribute_code == "metal_purity") {
                                            Log.e("TAG", "metal_typeBBB " + againAttributes.value)
                                            newUser.apply {
                                                this.purity = "" + againAttributes.value
                                            }
                                        }
                                    }
                                }

                                if (it.value == "13") {
                                    newUser.apply {
                                        this.material_type = "" + it.value
                                    }
                                    itemProductThis.custom_attributes.forEach { againAttributes ->
                                        if (againAttributes.attribute_code == "metal_purity") {
                                            Log.e("TAG", "metal_typeCCC " + againAttributes.value)
                                            newUser.apply {
                                                this.purity = "" + againAttributes.value
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        db?.cartDao()?.insertAll(newUser)
                        showSnackBar(getString(R.string.item_added_to_cart))
                        MainActivity.mainActivity.get()!!.callCartApi()

                        }
                    }


                } else if (LoginType.VENDOR == loginType) {
                    readData(QUOTE_ID) {
                        val json: JSONObject = JSONObject().apply {
                            put("sku", currentSku)
                            put("qty", 1)
                            put("quote_id", it.toString())
                        }
                        val jsonCartItem: JSONObject = JSONObject().apply {
                            put("cartItem", json)
                        }
                        readData(CUSTOMER_TOKEN) { token ->
                            viewModel.addCart(token!!, jsonCartItem) {
                                //cartMutableList.value = true
                                Log.e("TAG", "onCallBack: ${this.toString()}")
                                showSnackBar(getString(R.string.item_added_to_cart))
                                MainActivity.mainActivity.get()!!.callCartApi()
                            }
                        }
                    }
                }
            }

            layoutBuyNow.singleClick {
                if (LoginType.CUSTOMER == loginType) {
                    mainThread {
                        if (productType == "simple" || productType == "virtual") {

                            val newUser = CartModel(
                                product_id = itemProduct.id,
                                name = itemProduct.name,
                                price = itemProduct.price,
                                quantity = 1,
                                sku = itemProduct.sku,
                                currentTime = System.currentTimeMillis()
                            )
                            itemProduct.custom_attributes.forEach {
                                if (it.attribute_code == "size") {
                                    newUser.apply {
                                        this.size = "" + it.value
                                    }
                                }


                                if (it.attribute_code == "metal_color") {
                                    newUser.apply {
                                        this.color = "" + it.value
                                    }
                                }

                                if (it.attribute_code == "metal_type") {
                                    Log.e("TAG", "metal_typeAAA " + it.value)
                                    if (it.value == "12") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProduct.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeBBB " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }

                                    if (it.value == "13") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProduct.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeCCC " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            db?.cartDao()?.insertAll(newUser)
                            showSnackBar(getString(R.string.item_added_to_cart))
                            MainActivity.mainActivity.get()!!.callCartApi()
                            findNavController().navigate(R.id.action_productDetail_to_cart)

                        } else {
                            val newUser = CartModel(
                                product_id = itemProductThis.id,
                                name = itemProductThis.name,
                                price = itemProductThis.price,
                                quantity = 1,
                                sku = itemProductThis.sku,
                                currentTime = System.currentTimeMillis()
                            )
                            itemProductThis.custom_attributes.forEach {
                                if (it.attribute_code == "size") {
                                    newUser.apply {
                                        this.size = "" + it.value
                                    }
                                }


                                if (it.attribute_code == "metal_color") {
                                    newUser.apply {
                                        this.color = "" + it.value
                                    }
                                }

                                if (it.attribute_code == "metal_type") {
                                    Log.e("TAG", "metal_typeAAA " + it.value)
                                    if (it.value == "12") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProductThis.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeBBB " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }

                                    if (it.value == "13") {
                                        newUser.apply {
                                            this.material_type = "" + it.value
                                        }
                                        itemProductThis.custom_attributes.forEach { againAttributes ->
                                            if (againAttributes.attribute_code == "metal_purity") {
                                                Log.e("TAG", "metal_typeCCC " + againAttributes.value)
                                                newUser.apply {
                                                    this.purity = "" + againAttributes.value
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            db?.cartDao()?.insertAll(newUser)
                            showSnackBar(getString(R.string.item_added_to_cart))
                            MainActivity.mainActivity.get()!!.callCartApi()
                            findNavController().navigate(R.id.action_productDetail_to_cart)
                        }
                    }
                } else if (LoginType.VENDOR == loginType) {
                    readData(QUOTE_ID) {
                        val json: JSONObject = JSONObject().apply {
                            put("sku", currentSku)
                            put("qty", 1)
                            put("quote_id", it.toString())
                        }
                        val jsonCartItem: JSONObject = JSONObject().apply {
                            put("cartItem", json)
                        }
                        readData(CUSTOMER_TOKEN) { token ->
                            viewModel.addCart(token!!, jsonCartItem) {
                                //cartMutableList.value = true
                                Log.e("TAG", "onCallBack: ${this.toString()}")
                                showSnackBar(getString(R.string.item_added_to_cart))
                                findNavController().navigate(R.id.action_productDetail_to_cart)
                            }
                        }
                    }
                }
            }




            layoutDescription.singleClick {
                if (layoutProductDetails.isVisible == true) {
                    layoutProductDetails.visibility = View.GONE
                    ivHideShow.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_right
                        )
                    )
                } else {
                    layoutProductDetails.visibility = View.VISIBLE
                    ivHideShow.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_down
                        )
                    )
                }
            }

            layoutDiamondAndGemstones.singleClick {
                if (layoutWD.isVisible == true) {
                    layoutWD.visibility = View.GONE
                    if (isWebview == 1){
                        webView.visibility = View.GONE
                    }
                    if (isWebview == 2){
                        recyclerView!!.visibility = View.GONE
                    }
                    ivHideShow2.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_right
                        )
                    )
                } else {
                    layoutWD.visibility = View.VISIBLE
//                    webView.visibility = View.VISIBLE
                    if (isWebview == 1){
                        webView.visibility = View.VISIBLE
                    }
                    if (isWebview == 2){
                        recyclerView!!.visibility = View.VISIBLE
                    }
                    ivHideShow2.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_down
                        )
                    )
                }
            }


            layoutPriceBreakup.singleClick {
                if (groupPriceBreakup.isVisible == true) {
                    groupPriceBreakup.visibility = View.GONE
                    ivHideShow3.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_right
                        )
                    )
                } else {
                    groupPriceBreakup.visibility = View.VISIBLE
                    ivHideShow3.setImageDrawable(
                        ContextCompat.getDrawable(
                            root.context,
                            R.drawable.arrow_down
                        )
                    )
                }
            }
        }
    }

    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBack: ${images.toString()}")
//        findNavController().navigate(R.id.action_productDetail_to_productZoom, Bundle().apply {
//            putParcelable("arrayList", ItemParcelable(images, binding.rvList1.currentItem))
//        })
        openDialogFullImage()

    }

    override fun onCallBackHideShow() {
    }


    @SuppressLint("NotifyDataSetChanged")
    fun openDialogSize() {
        val dialogBinding = DialogSizesBinding.inflate(
            MainActivity.activity.get()?.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
        )
        val dialog = BottomSheetDialog(MainActivity.context.get()!!)
        dialog.setContentView(dialogBinding.root)
        dialog.setOnShowListener { dia ->
            val bottomSheetDialog = dia as BottomSheetDialog
            val bottomSheetInternal: FrameLayout =
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
        }
        dialog.show()


        dialogBinding.rvListSize.setHasFixedSize(true)
        dialogBinding.rvListSize.adapter = viewModel.sizeAdapter
        viewModel.sizeAdapter.notifyDataSetChanged()
        viewModel.sizeAdapter.submitList(ArrayList(viewModel.arrayItemProductOptionsSize))



        dialogBinding.ivIconCross.singleClick {
            dialog.dismiss()
        }


        viewModel.sizeMutableListClose.value = false
        viewModel.sizeMutableListClose.observe(viewLifecycleOwner) {
            if (it) {
                mainThread {
                    dialog.dismiss()
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation", "UseRequireInsteadOfGet")
    fun callSKUDetails(skuId: String?) {
        arrayItemProduct.clear()

        binding.apply {
            mainLayout.visibility = View.GONE
            textCategories.visibility = View.GONE
            linearRoseGold.visibility = View.GONE
            linearWhiteGold.visibility = View.GONE
            linearYellowGold.visibility = View.GONE

            bt12.visibility = View.GONE
            bt13.visibility = View.GONE

            bt9.visibility = View.GONE
            bt14.visibility = View.GONE
            bt18.visibility = View.GONE
            bt95.visibility = View.GONE

            layoutRingSize.visibility = View.GONE
            layoutGuide.visibility = View.GONE

            mainThread {
                readData(ADMIN_TOKEN) { token ->
                    viewModel.show()
                    viewModel.getProductDetail(token.toString(), skuId!!) {
                        itemProduct = this
                        textTitle.text = itemProduct.name


                        if (itemProduct.type_id == "simple" || itemProduct.type_id == "virtual") {
                            mainLayout.visibility = View.VISIBLE
                            productType = ""+itemProduct.type_id

                            viewModel.hide()

                            textCustomizationDetails.visibility = View.GONE
                            layoutMetalType.visibility = View.GONE
                            layoutPuritySelect.visibility = View.GONE
                            layoutColor.visibility = View.GONE
                            view1213.visibility = View.GONE
                            layoutRingSize.visibility = View.GONE
                            layoutGuide.visibility = View.GONE
                            view1214.visibility = View.GONE

                            images = itemProduct.media_gallery_entries
                            textSKU.text = "SKU: " + itemProduct.sku
                            currentSku = ""+itemProduct.sku

                            if (itemProduct.extension_attributes.category_links != null) {
                                itemProduct.extension_attributes.category_links.forEach { itemProductAttr ->
                                    val filteredNot =
                                        mainCategory.filter { itemProductAttr.category_id == it.id.toString() }
                                    val idsArray : ArrayList<String> = ArrayList()
                                    filteredNot.forEach { filteredNotInner ->
                                        idsArray.add(""+filteredNotInner.name)
                                    }

                                    val text1 = "<font color=#383838>CATEGORIES: </font> <font color=#0099cc>"+idsArray.joinToString(separator = ",") { it -> "${it}" }+"</font>"
                                    textCategories.text = HtmlCompat.fromHtml(text1, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                    textCategories.visibility = View.VISIBLE
                                }
                            }






                            Log.e("TAG", "getProductDetailBBsize " + images.size)

                            setAllImages()


                            textPrice.text = "₹ " + getPatternFormat("1", itemProduct.price)
                            textTotalPrice.text = "₹ " + getPatternFormat("1", itemProduct.price)

                            var totalWe : Double = 0.0
                            var goldWeight : Double = 0.0
                            var diamondWeight : Double = 0.0
                            itemProduct.custom_attributes.forEach { itemProductAttr ->

                                if (itemProductAttr.attribute_code == "diamond_weight") {
                                    textWeightCt.text = "Total Weight " + itemProductAttr.value
                                    diamondWeight = itemProductAttr.value.toString().toDouble()
                                }
                                if (itemProductAttr.attribute_code != "diamond_weight") {
                                    itemProduct.custom_attributes.forEach { itemMainProductAttr ->
                                        if (itemMainProductAttr.attribute_code == "diamond_weight") {
                                            textWeightCt.text = "Total Weight " + itemMainProductAttr.value
                                            diamondWeight = itemMainProductAttr.value.toString().toDouble()
                                        }
                                    }
                                }


                                if (itemProductAttr.attribute_code == "diamond_number") {
                                    textDiamonds.text = "No of Diamonds " + itemProductAttr.value
                                }
                                if (itemProductAttr.attribute_code != "diamond_number") {
                                    itemProduct.custom_attributes.forEach { itemMainProductAttr ->
                                        if (itemMainProductAttr.attribute_code == "diamond_number") {
                                            textDiamonds.text = "No of Diamonds " + itemMainProductAttr.value
                                        }
                                    }
                                }


                                if (itemProductAttr.attribute_code == "totel_diamond_rate") {
                                    textDiamondPrice.text = "₹ " + getPatternFormat(
                                        "1",
                                        itemProductAttr.value.toString().toDouble()
                                    )
                                }

                                if (itemProductAttr.attribute_code == "totel_gold_rate") {
                                    textGoldPrice.text = "₹ " + getPatternFormat(
                                        "1",
                                        itemProductAttr.value.toString().toDouble()
                                    )

//                                    arrayItemProduct.forEach {
//                                        if (this.sku == currentSku){
//                                            textGoldPrice.text = "₹ " + getPatternFormat(
//                                                "1",
//                                                itemProductAttr.value.toString().toDouble() * it.metal_weight.toDouble()
//                                            )
//
                                        textWeight2.text = "" + itemProduct.weight.toDouble().round()+ " gram"
//
//                                        }
//                                    }

                                }


                                if (itemProductAttr.attribute_code == "metal_weight") {
                                    textWeight4.text = "" + itemProductAttr.value.toString()+ " gram"
                                }


                                if (itemProductAttr.attribute_code == "totel_making_charge") {
                                    textMakingChargesPrice.text = "₹ " + getPatternFormat(
                                        "1",
                                        itemProductAttr.value.toString().toDouble()
                                    )
                                }



                                if (itemProductAttr.attribute_code == "metal_purity") {
                                    if (itemProductAttr.value == "26") {
                                        itemProduct.custom_attributes.forEach { itemProductChildAttr ->
                                            if (itemProductChildAttr.attribute_code == "metal_color") {
                                                if (itemProductChildAttr.value == "19") {
                                                    textPurity1.text = "9 kt Yellow Gold"
                                                } else if (itemProductChildAttr.value == "50") {
                                                    textPurity1.text = "9 kt White Gold"
                                                } else if (itemProductChildAttr.value == "25") {
                                                    textPurity1.text = "9 kt Rose Gold"
                                                }

                                                itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                    if (itemProductAttr.attribute_code == "attr_9kt") {
                                                        textWeight4.text =
                                                            "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                        goldWeight = itemProductAttr.value.toString().toDouble()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (itemProductAttr.value == "14") {
                                        itemProduct.custom_attributes.forEach { itemProductChildAttr ->
                                            if (itemProductChildAttr.attribute_code == "metal_color") {
                                                if (itemProductChildAttr.value == "19") {
                                                    textPurity1.text = "14 kt Yellow Gold"
                                                } else if (itemProductChildAttr.value == "50") {
                                                    textPurity1.text = "14 kt White Gold"
                                                } else if (itemProductChildAttr.value == "25") {
                                                    textPurity1.text = "14 kt Rose Gold"
                                                }


                                                itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                    if (itemProductAttr.attribute_code == "attr_14kt") {
                                                        textWeight4.text =
                                                            "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                        goldWeight = itemProductAttr.value.toString().toDouble()
                                                    }

//                                                if (itemProductAttr.attribute_code == "diamond_weight") {
////                                                    textWeightCt.text = "Total Weight " + itemProductAttr.value
//                                                    diamondWeight = itemProductAttr.value.toString().toDouble()
//                                                }
                                                }
                                            }
                                        }
                                    }

                                    if (itemProductAttr.value == "15") {
                                        itemProduct.custom_attributes.forEach { itemProductChildAttr ->
                                            if (itemProductChildAttr.attribute_code == "metal_color") {
                                                if (itemProductChildAttr.value == "19") {
                                                    textPurity1.text = "18 kt Yellow Gold"
                                                } else if (itemProductChildAttr.value == "50") {
                                                    textPurity1.text = "18 kt White Gold"
                                                } else if (itemProductChildAttr.value == "25") {
                                                    textPurity1.text = "18 kt Rose Gold"
                                                }

                                                itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                    if (itemProductAttr.attribute_code == "attr_18kt") {
                                                        textWeight4.text =
                                                            "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                        goldWeight = itemProductAttr.value.toString().toDouble()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (itemProductAttr.value == "16") {
                                        itemProduct.custom_attributes.forEach { itemProductChildAttr ->
                                            if (itemProductChildAttr.attribute_code == "metal_color") {
                                                if (itemProductChildAttr.value == "19") {
                                                    textPurity1.text = "22 kt Yellow Gold"
                                                } else if (itemProductChildAttr.value == "50") {
                                                    textPurity1.text = "22 kt White Gold"
                                                } else if (itemProductChildAttr.value == "25") {
                                                    textPurity1.text = "22 kt Rose Gold"
                                                }

                                                itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                    if (itemProductAttr.attribute_code == "attr_22kt") {
                                                        textWeight4.text =
                                                            "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                        goldWeight = itemProductAttr.value.toString().toDouble()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (itemProductAttr.value == "17") {
                                        itemProduct.custom_attributes.forEach { itemProductChildAttr ->
                                            if (itemProductChildAttr.attribute_code == "metal_color") {
                                                if (itemProductChildAttr.value == "19") {
                                                    textPurity1.text = "24 kt Yellow Gold"
                                                } else if (itemProductChildAttr.value == "50") {
                                                    textPurity1.text = "24 kt White Gold"
                                                } else if (itemProductChildAttr.value == "25") {
                                                    textPurity1.text = "24 kt Rose Gold"
                                                }

                                                itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                    if (itemProductAttr.attribute_code == "attr_24kt") {
                                                        textWeight4.text =
                                                            "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                        goldWeight = itemProductAttr.value.toString().toDouble()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (itemProductAttr.value == "18") {
                                        textPurity1.text = "Platinum 95"
                                    }
                                }





                                if (itemProductAttr.attribute_code == "short_description") {
                                    isWebview = 1
//                                Log.e("TAG", "idsids " + "filteredNotBB")
                                    binding.webView.loadDataWithBaseURL(
                                        null,
                                        "" + itemProductAttr.value,
                                        "text/html",
                                        "utf-8",
                                        null
                                    )

                                    layoutDiamondAndGemstones.visibility = View.VISIBLE
                                }


                                if (itemProductAttr.attribute_code == "dynamic_row_attribute") {
                                    println("isWebview "+isWebview)
                                    isWebview = 2
                                    val diamondItemArray : ArrayList<DiamondItem> = ArrayList()

                                    val diamondItem = DiamondItem(
                                        "",
                                        "Clarity",
                                        "Weight (Carat)",
                                        "Shape",
                                        "Color",
                                        "Number (Pieces)"
                                    )
                                    diamondItemArray.add(diamondItem)

                                    try {
                                        val jsonArr = JSONArray(itemProductAttr.value)
                                        for (i in 0..<jsonArr.length()) {
                                            val jsonObj = jsonArr.getJSONObject(i)
                                            val diamondItem = DiamondItem(
                                                jsonObj.getString("record_id"),
                                                jsonObj.getString("diamond_clarity"),
                                                jsonObj.getString("diamond_weight"),
                                                jsonObj.getString("diamond_shape"),
                                                jsonObj.getString("diamond_color"),
                                                jsonObj.getString("diamond_number"),
                                            )
                                            diamondItemArray.add(diamondItem)
                                            println(jsonObj)
                                        }

                                        println("AAAAAAAAA "+diamondItemArray.size)
                                    }catch (e : Exception){
                                        println("BBBBBBBB "+diamondItemArray.size)
                                    }


                                    recyclerView!!.setHasFixedSize(true)
                                    binding.recyclerView!!.adapter = viewModel.diamondAdapter
                                    viewModel.diamondAdapter.notifyDataSetChanged()
                                    viewModel.diamondAdapter.submitList(diamondItemArray)


                                    layoutDiamondAndGemstones.visibility = View.VISIBLE
                                }

                            }

                        } else if(itemProduct.type_id == "configurable"){
                            Log.e("TAG", "getProductDetailAA22 ")
                            viewModel.getProductOptions("" + itemProduct.id) {
//                            val jsonObject = JSONObject(this.toString())
////                            Log.e("TAG", "arrayItemProduct.sizeB " + jsonObject)
////                            val ids =
////                                itemProduct.extension_attributes.configurable_product_links.toString()
////                                    .replace("[", "").replace("]", "").replace(" ", "")
////                                    .split(",")
////
////                            Log.e("TAG", "arrayItemProduct.sizeC " + ids)
////
////                            ids.forEach { idsInner ->
////                                val jsonObjectData = jsonObject.getJSONObject(idsInner)
////                                val jsonString = Gson().fromJson(
////                                    jsonObjectData.toString(),
////                                    ItemProductOptions::class.java
////                                )
////                                arrayItemProduct.add(jsonString)
////                            }

//                            viewModel.hide()

                                this.forEach {
                                    arrayItemProduct.add(it)
                                }

                                if (arrayItemProduct.size > 0) {
                                    if (currentSku == "") {
                                        currentSku = arrayItemProduct[0].sku
                                    }
                                    Log.e("TAG", "arrayItemProductcurrentSku " + currentSku)

                                    val sku = arguments?.getString("sku")

                                    if (sku == "") {
                                        Log.e("TAG", "skuXXX1 " + sku)
                                    } else {
                                        Log.e("TAG", "skuXXX2 " + sku)
                                        if (sku == null) {

                                        } else {
                                            currentSku = sku
                                        }

                                    }

//                                if (sku != null) {
//                                    currentSku = sku!!
//                                }

//                                if (sku != "") {
//                                    currentSku = sku!!
//                                }


                                    arrayItemProduct.forEach {
                                        if (currentSku == it.sku){
                                            viewModel.updatePrice(it.entity_id){
                                                Log.e("TAG", "updatedPrice "+this)
                                                val response = this.toString()
                                                Log.e("TAG", "responseDDD "+response)
                                                if(response.contains("No price update performed")){
                                                    viewModel.hide()
                                                    MaterialAlertDialogBuilder(
                                                        requireContext(),
                                                        R.style.LogoutDialogTheme
                                                    )
                                                        .setTitle(requireContext().getString(R.string.app_name))
                                                        .setMessage(requireContext().getString(R.string.product_type_is_not_correct))
                                                        .setPositiveButton(requireContext().getString(R.string.ok)) { dialog, _ ->
                                                            dialog.dismiss()
                                                            requireView().findNavController().navigateUp()
                                                        }
                                                        .setCancelable(false)
                                                        .show()
                                                } else {
                                                    callApiDetailsConfigurable(
                                                        currentSku,
                                                        itemProduct
                                                    )
                                                }
                                            }
                                        }
                                    }



//                                Log.e("TAG", "arrayItemProductcurrentSkuAfter " + currentSku)
//                                callApiDetailsConfigurable(
//                                    currentSku,
//                                    itemProduct
//                                )
                                } else {
                                    viewModel.hide()
                                }
                            }
                        }


                        else {
                            idDataNotFound!!.root.visibility = View.VISIBLE
                            idDataNotFound!!.textDesc.text = "Product type is not correct!"

                            nestedScrollView!!.visibility = View.GONE
                            filterLayout.visibility = View.GONE

                            viewModel.hide()
                        }


                    }
                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    fun callApiDetailsConfigurable(
        skuId: String?,
        itemProduct: ItemProduct,
    ) {
//        viewModel.show()
        binding.apply {
            mainThread {
                viewModel.show()
            }
            textCategories.visibility = View.GONE
            linearRoseGold.visibility = View.GONE
            linearWhiteGold.visibility = View.GONE
            linearYellowGold.visibility = View.GONE

            bt12.visibility = View.GONE
            bt13.visibility = View.GONE

            bt9.visibility = View.GONE
            bt14.visibility = View.GONE
            bt18.visibility = View.GONE
            bt95.visibility = View.GONE

            layoutRingSize.visibility = View.GONE
            layoutGuide.visibility = View.GONE
            mainThread {
                readData(ADMIN_TOKEN) { token ->
                    viewModel.getProductDetail(token.toString(), skuId!!) {
                        itemProductThis = this

//                        arrayItemProduct.forEach {
//                            if (it.sku == currentSku) {
//                                textWeight2.text = "" + it.weight.toDouble().round()+ " gram"
//                                textWeight4.text = "" + it.metal_weight.toDouble().round()+ " gram"
//                            }
//                        }

                        viewModel.arrayItemProductOptionsSize.clear()

//                        images = itemProduct.media_gallery_entries

                        if (itemProductThis.media_gallery_entries.size == 0) {
                            images = itemProduct.media_gallery_entries
                        } else {
                            images = itemProductThis.media_gallery_entries
                        }
                        Log.e("TAG", "getProductDetailBBsize " + images.size)

                        setAllImages()

                        textPrice.text = "₹ " + getPatternFormat("1", itemProductThis.price)

                        textTotalPrice.text = "₹ " + getPatternFormat("1", itemProductThis.price)

                        textSKU.text = "SKU: " + itemProductThis.sku

//                        textWeight2.text = "" + itemProductThis.weight + " gram"

//                        var idvalues = ""
//                        Log.e("TAG", "idsidsCCC " + itemProductThis.extension_attributes.category_links)
                        if (itemProductThis.extension_attributes.category_links != null) {
                            itemProductThis.extension_attributes.category_links.forEach { itemProductAttr ->
                                val filteredNot =
                                    mainCategory.filter { itemProductAttr.category_id == it.id.toString() }
                                var idsArray : ArrayList<String> = ArrayList()
                                filteredNot.forEach { filteredNotInner ->
//                                    idvalues += filteredNotInner.name + ", "
                                    idsArray.add(""+filteredNotInner.name)
                                }
//                                Log.e("TAG", "idsids " + filteredNot)
//                                textCategories.text = "CATEGORIES: " + idvalues
//                                textCategories.text = "CATEGORIES: " +idsArray.joinToString(separator = ",") { it -> "${it}" }//
//                                textCategories.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_open_hi, "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.re_open_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                                val text1 = "<font color=#383838>CATEGORIES: </font> <font color=#0099cc>"+idsArray.joinToString(separator = ",") { it -> "${it}" }+"</font>"
                                textCategories.text = HtmlCompat.fromHtml(text1, HtmlCompat.FROM_HTML_MODE_COMPACT)
                                textCategories.visibility = View.VISIBLE
                            }
                        }






//                        var metal_weight: String = "0.0 gram"

                        itemProduct.custom_attributes.forEach { itemProductAttr ->
//                            if (itemProductAttr.attribute_code == "metal_weight") {
//                                metal_weight = "" + itemProductAttr.value + " gram"
//                            }


//                            if (itemProductAttr.attribute_code == "totel_diamond_rate") {
//                                textDiamondPrice.text = "₹ " + getPatternFormat(
//                                    "1",
//                                    itemProductAttr.value.toString().toDouble()
//                                )
//                            }

//                            if (itemProductAttr.attribute_code == "totel_diamond_rate") {
//                                textDiamondPrice.text = "₹ " + getPatternFormat(
//                                    "1",
//                                    itemProductAttr.value.toString().toDouble()
//                                )
//                            }
                        }


                        var totalWe : Double = 0.0
                        var goldWeight : Double = 0.0
                        var diamondWeight : Double = 0.0
                        itemProductThis.custom_attributes.forEach { itemProductAttr ->

                            if (itemProductAttr.attribute_code == "size") {
                                btRingSize.text =
                                    "" + getSize(itemProductAttr.value.toString().toInt())
                                layoutRingSize.visibility = View.VISIBLE
                                layoutGuide.visibility = View.VISIBLE
                            }

//                            if (itemProductAttr.attribute_code == "metal_weight") {
//                                metal_weight = "" + itemProductAttr.value + " gram"
//                            }

//                            if (itemProductAttr.attribute_code == "totel_diamond_rate") {
//                                textDiamondPrice.text = "₹ " + itemProductAttr.value
//                            }

                            if (itemProductAttr.attribute_code == "metal_purity") {
                                if (itemProductAttr.value == "26") {
                                    itemProductThis.custom_attributes.forEach { itemProductChildAttr ->
                                        if (itemProductChildAttr.attribute_code == "metal_color") {
                                            if (itemProductChildAttr.value == "19") {
                                                textPurity1.text = "9 kt Yellow Gold"
                                            } else if (itemProductChildAttr.value == "50") {
                                                textPurity1.text = "9 kt White Gold"
                                            } else if (itemProductChildAttr.value == "25") {
                                                textPurity1.text = "9 kt Rose Gold"
                                            }

                                            itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                if (itemProductAttr.attribute_code == "attr_9kt") {
                                                    textWeight4.text =
                                                        "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                    goldWeight = itemProductAttr.value.toString().toDouble()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (itemProductAttr.value == "14") {
                                    itemProductThis.custom_attributes.forEach { itemProductChildAttr ->
                                        if (itemProductChildAttr.attribute_code == "metal_color") {
                                            if (itemProductChildAttr.value == "19") {
                                                textPurity1.text = "14 kt Yellow Gold"
                                            } else if (itemProductChildAttr.value == "50") {
                                                textPurity1.text = "14 kt White Gold"
                                            } else if (itemProductChildAttr.value == "25") {
                                                textPurity1.text = "14 kt Rose Gold"
                                            }


                                            itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                if (itemProductAttr.attribute_code == "attr_14kt") {
                                                    textWeight4.text =
                                                        "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                    goldWeight = itemProductAttr.value.toString().toDouble()
                                                }

//                                                if (itemProductAttr.attribute_code == "diamond_weight") {
////                                                    textWeightCt.text = "Total Weight " + itemProductAttr.value
//                                                    diamondWeight = itemProductAttr.value.toString().toDouble()
//                                                }
                                            }
                                        }
                                    }
                                }

                                if (itemProductAttr.value == "15") {
                                    itemProductThis.custom_attributes.forEach { itemProductChildAttr ->
                                        if (itemProductChildAttr.attribute_code == "metal_color") {
                                            if (itemProductChildAttr.value == "19") {
                                                textPurity1.text = "18 kt Yellow Gold"
                                            } else if (itemProductChildAttr.value == "50") {
                                                textPurity1.text = "18 kt White Gold"
                                            } else if (itemProductChildAttr.value == "25") {
                                                textPurity1.text = "18 kt Rose Gold"
                                            }

                                            itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                if (itemProductAttr.attribute_code == "attr_18kt") {
                                                    textWeight4.text =
                                                        "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                    goldWeight = itemProductAttr.value.toString().toDouble()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (itemProductAttr.value == "16") {
                                    itemProductThis.custom_attributes.forEach { itemProductChildAttr ->
                                        if (itemProductChildAttr.attribute_code == "metal_color") {
                                            if (itemProductChildAttr.value == "19") {
                                                textPurity1.text = "22 kt Yellow Gold"
                                            } else if (itemProductChildAttr.value == "50") {
                                                textPurity1.text = "22 kt White Gold"
                                            } else if (itemProductChildAttr.value == "25") {
                                                textPurity1.text = "22 kt Rose Gold"
                                            }

                                            itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                if (itemProductAttr.attribute_code == "attr_22kt") {
                                                    textWeight4.text =
                                                        "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                    goldWeight = itemProductAttr.value.toString().toDouble()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (itemProductAttr.value == "17") {
                                    itemProductThis.custom_attributes.forEach { itemProductChildAttr ->
                                        if (itemProductChildAttr.attribute_code == "metal_color") {
                                            if (itemProductChildAttr.value == "19") {
                                                textPurity1.text = "24 kt Yellow Gold"
                                            } else if (itemProductChildAttr.value == "50") {
                                                textPurity1.text = "24 kt White Gold"
                                            } else if (itemProductChildAttr.value == "25") {
                                                textPurity1.text = "24 kt Rose Gold"
                                            }

                                            itemProduct.custom_attributes.forEach { itemProductAttr ->
                                                if (itemProductAttr.attribute_code == "attr_24kt") {
                                                    textWeight4.text =
                                                        "" + itemProductAttr.value.toString().toDouble().round() + " gram"
                                                    goldWeight = itemProductAttr.value.toString().toDouble()
                                                }
                                            }
                                        }
                                    }
                                }

                                if (itemProductAttr.value == "18") {
                                    textPurity1.text = "Platinum 95"
                                }
                            }





                            if (itemProductAttr.attribute_code == "diamond_weight") {
                                textWeightCt.text = "Total Weight " + itemProductAttr.value
                                diamondWeight = itemProductAttr.value.toString().toDouble()
                            }
                            if (itemProductAttr.attribute_code != "diamond_weight") {
                                itemProduct.custom_attributes.forEach { itemMainProductAttr ->
                                    if (itemMainProductAttr.attribute_code == "diamond_weight") {
                                        textWeightCt.text = "Total Weight " + itemMainProductAttr.value
                                        diamondWeight = itemMainProductAttr.value.toString().toDouble()
                                    }
                                }
                            }


                            if (itemProductAttr.attribute_code == "diamond_number") {
                                textDiamonds.text = "No of Diamonds " + itemProductAttr.value
                            }
                            if (itemProductAttr.attribute_code != "diamond_number") {
                                itemProduct.custom_attributes.forEach { itemMainProductAttr ->
                                    if (itemMainProductAttr.attribute_code == "diamond_number") {
                                        textDiamonds.text = "No of Diamonds " + itemMainProductAttr.value
                                    }
                                }
                            }


                            if (itemProductAttr.attribute_code == "totel_diamond_rate") {
                                textDiamondPrice.text = "₹ " + getPatternFormat(
                                    "1",
                                    itemProductAttr.value.toString().toDouble()
                                )
                            }

                            if (itemProductAttr.attribute_code == "totel_gold_rate") {
                                textGoldPrice.text = "₹ " + getPatternFormat(
                                    "1",
                                    itemProductAttr.value.toString().toDouble()
                                )

//                                arrayItemProduct.forEach {
//                                    if (this.sku == currentSku){
//                                        textGoldPrice.text = "₹ " + getPatternFormat(
//                                            "1",
//                                            itemProductAttr.value.toString().toDouble() * it.metal_weight.toDouble()
//                                        )
//
////                                        textWeight2.text = "" + this.weight.toDouble().round()+ " gram"
////                                        textWeight4.text = "" + this.metal_weight.toDouble().round()+ " gram"
//                                    }
//                                }

                            }


//                            if (itemProductAttr.attribute_code == "totel_making_charge") {
//                                textMakingChargesPrice.text = "₹ " + getPatternFormat(
//                                    "1",
//                                    itemProductAttr.value.toString().toDouble()
//                                )
//                            }
                        }



                        totalWe = goldWeight + diamondWeight
                        Log.e("TAG", "SSSSDD"+totalWe)
                        textWeight2.text = "" + totalWe.round()+ " gram"
//                        textWeight2.text = "" + totalWe.round()+ " gram"

                        itemProduct.custom_attributes.forEach { itemProductAttr ->
//                            Log.e("TAG", "idsids " + "filteredNotAA")
                            if (itemProductAttr.attribute_code == "short_description") {
                                isWebview = 1
//                                Log.e("TAG", "idsids " + "filteredNotBB")
                                binding.webView.loadDataWithBaseURL(
                                    null,
                                    "" + itemProductAttr.value,
                                    "text/html",
                                    "utf-8",
                                    null
                                )

//                                binding.webView.loadDataWithBaseURL(
//                                    null,
//                                    "" + "<\\/style>\\r\\n<table dir=\\\"ltr\\\" border=\\\"1\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" data-sheets-root=\\\"1\\\" data-sheets-baot=\\\"1\\\"><colgroup><col width=\\\"100\\\"><col width=\\\"100\\\"><col width=\\\"100\\\"><col width=\\\"100\\\"><col width=\\\"100\\\"><\\/colgroup>\\r\\n<tbody>\\r\\n<tr>\\r\\n<td>Color<\\/td>\\r\\n<td>Clarity<\\/td>\\r\\n<td>Shaped<\\/td>\\r\\n<td>Pieces<\\/td>\\r\\n<td><font color =\"red\">  Weight</font><\\/td>\\r\\n<\\/tr>\\r\\n<tr>\\r\\n<td>EF<\\/td>\\r\\n<td>VVS\\/VS<\\/td>\\r\\n<td>Rounded<\\/td>\\r\\n<td>34<\\/td>\\r\\n<td>0.22<\\/td>\\r\\n<\\/tr>\\r\\n<\\/tbody>\\r\\n<\\/table>",
//                                    "text/html",
//                                    "utf-8",
//                                    null
//                                )
                                layoutDiamondAndGemstones.visibility = View.VISIBLE
                            }


                            if (itemProductAttr.attribute_code == "dynamic_row_attribute") {
                                println("isWebview "+isWebview)
                                isWebview = 2
                                val diamondItemArray : ArrayList<DiamondItem> = ArrayList()

                                val diamondItem = DiamondItem(
                                    "",
                                    "Clarity",
                                    "Weight (Carat)",
                                    "Shape",
                                    "Color",
                                    "Number (Pieces)"
                                )
                                diamondItemArray.add(diamondItem)

                                try {
                                    val jsonArr = JSONArray(itemProductAttr.value.toString())
                                    for (i in 0..<jsonArr.length()) {
                                        val jsonObj = jsonArr.getJSONObject(i)
                                        val diamondItem = DiamondItem(
                                            jsonObj.getString("record_id"),
                                            jsonObj.getString("diamond_clarity"),
                                            jsonObj.getString("diamond_weight"),
                                            jsonObj.getString("diamond_shape"),
                                            jsonObj.getString("diamond_color"),
                                            jsonObj.getString("diamond_number"),
                                        )
                                        diamondItemArray.add(diamondItem)
                                        println(jsonObj)
                                    }

                                    println("AAAAAAAAA "+diamondItemArray.size)
                                }catch (e : Exception){
                                    println("BBBBBBBB "+e.message)
                                }


                                recyclerView!!.setHasFixedSize(true)
                                binding.recyclerView!!.adapter = viewModel.diamondAdapter
                                viewModel.diamondAdapter.notifyDataSetChanged()
                                viewModel.diamondAdapter.submitList(diamondItemArray)


                                layoutDiamondAndGemstones.visibility = View.VISIBLE
                            }




                            if (itemProductAttr.attribute_code == "totel_making_charge") {
                                textMakingChargesPrice.text = "₹ " + getPatternFormat(
                                    "1",
                                    itemProductAttr.value.toString().toDouble() * goldWeight
                                )
                            }


                        }



                        itemProduct.extension_attributes.configurable_product_options.forEach { itemConfigurableProductAttr ->


                            if (itemConfigurableProductAttr.label == "Size") {
//                                Log.e(
//                                    "TAG",
//                                    "itemConfigurableProductSizeAttr " + itemConfigurableProductAttr.values
//                                )

                                itemConfigurableProductAttr.values.forEach { itemConfigurableProductSizeAttr ->
                                    itemProductThis.custom_attributes.forEach { itemCustomSizeAttr ->
                                        if (itemCustomSizeAttr.attribute_code == "size") {

                                            if (itemConfigurableProductSizeAttr.value_index == itemCustomSizeAttr.value.toString()
                                                    .toInt()
                                            ) {
                                                viewModel.arrayItemProductOptionsSize.add(
                                                    Value(
                                                        itemConfigurableProductSizeAttr.value_index,
                                                        true
                                                    )
                                                )
//                                                Log.e("TAG", "itemCustomSizeAttr.attribute_codeAA "+itemConfigurableProductSizeAttr.value_index + " :: "+ itemCustomSizeAttr.value)
                                            } else {
                                                viewModel.arrayItemProductOptionsSize.add(
                                                    Value(
                                                        itemConfigurableProductSizeAttr.value_index,
                                                        false
                                                    )
                                                )
//                                                Log.e("TAG", "itemCustomSizeAttr.attribute_codeBB "+itemConfigurableProductSizeAttr.value_index + " :: "+itemCustomSizeAttr.value)
                                            }
                                        }
                                    }
                                }
                            }

                            if (itemConfigurableProductAttr.label == "Metal Type") {
                                itemConfigurableProductAttr.values.forEach { itemConfigurableProductMetalTypeAttr ->
                                    if (itemConfigurableProductMetalTypeAttr.value_index == 12) {
                                        bt12.visibility = View.VISIBLE
                                        bt12.backgroundTintList =
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                )
                                            )
                                        bt12.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color._ffffff
                                            )
                                        )
                                        itemProduct.extension_attributes.configurable_product_options.forEach { itemConfigurableProductAttr ->
                                            if (itemConfigurableProductAttr.label == "Metal Purity") {
                                                itemConfigurableProductAttr.values.forEach { itemConfigurableProductMetalPurityAttr ->
                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 26) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "26") {
                                                                    bt9.visibility = View.GONE // btn9 VISIBlE
                                                                    bt9.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt9.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "26") {
                                                                    bt9.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt9.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                    bt9.visibility = View.GONE // btn9 VISIBlE
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 14) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "14") {
                                                                    bt14.visibility = View.VISIBLE
                                                                    bt14.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt14.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "14") {
                                                                    bt14.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt14.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                    bt14.visibility = View.VISIBLE
                                                                }
                                                            }
                                                        }

//                                                        itemProduct.custom_attributes.forEach { itemProductAttr ->
//                                                            if (itemProductAttr.attribute_code == "attr_14kt") {
//                                                                textWeight2.text =
//                                                                    "" + itemProductAttr.value + " gram"
//                                                            }
//                                                        }
                                                    }

                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 15) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "15") {
                                                                    bt18.visibility = View.VISIBLE
                                                                    bt18.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt18.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "15") {
                                                                    bt18.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt18.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                    bt18.visibility = View.VISIBLE
                                                                }
                                                            }
                                                        }

//                                                        itemProduct.custom_attributes.forEach { itemProductAttr ->
//                                                            if (itemProductAttr.attribute_code == "attr_18kt") {
//                                                                textWeight2.text =
//                                                                    "" + itemProductAttr.value + " gram"
//                                                            }
//                                                        }
                                                    }

                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 16) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "16") {
                                                                    bt22.visibility = View.VISIBLE
                                                                    bt22.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt22.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "16") {
                                                                    bt22.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt22.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                    bt22.visibility = View.VISIBLE
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 17) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "17") {
                                                                    bt24.visibility = View.VISIBLE
                                                                    bt24.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt24.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "17") {
                                                                    bt24.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt24.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                    bt24.visibility = View.VISIBLE
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (itemConfigurableProductMetalTypeAttr.value_index == 13) {
                                        bt13.visibility = View.VISIBLE
                                        bt13.backgroundTintList =
                                            ColorStateList.valueOf(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.app_color
                                                )
                                            )
                                        bt13.setTextColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color._ffffff
                                            )
                                        )
                                        itemProduct.extension_attributes.configurable_product_options.forEach { itemConfigurableProductAttr ->
                                            if (itemConfigurableProductAttr.label == "Metal Purity") {
                                                itemConfigurableProductAttr.values.forEach { itemConfigurableProductMetalPurityAttr ->
                                                    if (itemConfigurableProductMetalPurityAttr.value_index == 18) {
                                                        itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                                            if (itemProductAttr.attribute_code == "metal_purity") {
                                                                if (itemProductAttr.value == "18") {
                                                                    bt95.visibility = View.VISIBLE
                                                                    bt95.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color.app_color
                                                                            )
                                                                        )
                                                                    bt95.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color._ffffff
                                                                        )
                                                                    )
                                                                }
                                                                if (itemProductAttr.value != "18") {
                                                                    bt95.visibility = View.VISIBLE
                                                                    bt95.backgroundTintList =
                                                                        ColorStateList.valueOf(
                                                                            ContextCompat.getColor(
                                                                                requireContext(),
                                                                                R.color._ffffff
                                                                            )
                                                                        )
                                                                    bt95.setTextColor(
                                                                        ContextCompat.getColor(
                                                                            requireContext(),
                                                                            R.color.app_color
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                            if (itemConfigurableProductAttr.label == "Metal Color") {
//                                Log.e(
//                                    "TAG",
//                                    "itemConfigurableProductMetalColorAttr " + itemConfigurableProductAttr.values
//                                )
                                itemConfigurableProductAttr.values.forEach { itemConfigurableProductMetalColorAttr ->
                                    if (itemConfigurableProductMetalColorAttr.value_index == 19) {
                                        linearYellowGold.visibility = View.VISIBLE
                                    }
                                    if (itemConfigurableProductMetalColorAttr.value_index == 50) {
                                        linearWhiteGold.visibility = View.VISIBLE
                                    }
                                    if (itemConfigurableProductMetalColorAttr.value_index == 25) {
                                        linearRoseGold.visibility = View.VISIBLE
                                    }
                                }
                            }


                            itemProductThis.custom_attributes.forEach { itemProductAttr ->
                                if (itemProductAttr.attribute_code == "metal_color") {
                                    if ("" + itemProductAttr.value == "25") {
                                        linearRoseGold.visibility = View.VISIBLE
                                        viewModel.colors(binding, 1)
                                    }
                                    if ("" + itemProductAttr.value == "19") {
                                        linearYellowGold.visibility = View.VISIBLE
                                        viewModel.colors(binding, 3)
                                    }
                                    if ("" + itemProductAttr.value == "50") {
                                        linearWhiteGold.visibility = View.VISIBLE
                                        viewModel.colors(binding, 2)
                                    }
                                }
                            }

                        }



                        if (viewModel.arrayItemProductOptionsSize.size == 0) {
                            groupSize.visibility = View.GONE
                        } else if (viewModel.arrayItemProductOptionsSize.size == 1) {
                            viewModel.arrayItemProductOptionsSize.forEach {
                                if (it.value_index == 51) {
                                    groupSize.visibility = View.GONE
                                } else {
                                    groupSize.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            groupSize.visibility = View.VISIBLE
                        }


                        viewModel.relatedProducts(itemProduct.id) {
//                            Log.e("TAG", "relatedProductsAA "+this.size)
                            if (this.size == 0) {
                                layoutRelated.visibility = View.GONE
                            } else {
                                layoutRelated.visibility = View.VISIBLE
                            }

                            rvRelatedProducts.setHasFixedSize(true)
                            rvRelatedProducts.adapter = viewModel.productAdapter
                            viewModel.productAdapter.submitList(this)
                            viewModel.productAdapter.notifyDataSetChanged()
                            mainLayout.visibility = View.VISIBLE
//                        Log.e("TAG", "filteredNotfilteredNotA " )
                            viewModel.hide()
                        }
                        viewModel.hide()
                    }
                }


            }
        }
    }


    private fun setAllImages() {
        binding.apply {
            pagerAdapter = ProductDetailPagerAdapter(requireActivity(), images)
            rvList1.offscreenPageLimit = 1
            rvList1.overScrollMode = OVER_SCROLL_NEVER
            rvList1.adapter = pagerAdapter
            rvList1.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            (rvList1.getRecyclerView()
                .getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations =
                false

            viewModel.indicator(binding, images, 1)

            rvList1.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    super.onPageScrolled(
                        position,
                        positionOffset,
                        positionOffsetPixels
                    )
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.indicator(binding, images, position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    Log.e("TAG", "state" + state)
                }
            })
        }
    }



    fun openDialogFullImage() {
        val dialogBinding = DialogFullImageBinding.inflate(
            MainActivity.activity.get()?.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
        )
        val dialog = Dialog(MainActivity.context.get()!!, R.style.myFullscreenAlertDialogStyle)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        val window = dialog.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        window.setBackgroundDrawableResource(R.color._00000000)
//        val yes = mybuilder.findViewById<AppCompatImageView>(R.id.imageCross)
//        val title = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleMain)

        dialogBinding.ivIconCross.singleClick {
            dialog.dismiss()
        }

//        dialogBinding1 = dialogBinding
        val item1 : ArrayList<String> = ArrayList()
        images?.forEach {
            if (it.media_type.endsWith("image")){
                item1.add(it.file)
            }
        }

        Log.e("TAG", "arrayList: $item1")
//        Log.e("TAG", "position: ${images?.position}")

        pagerAdapter = ProductZoomPagerAdapter(requireActivity(), item1)
        dialogBinding.rvList1.offscreenPageLimit = 1
        dialogBinding.rvList1.overScrollMode = OVER_SCROLL_NEVER
        dialogBinding.rvList1.adapter = pagerAdapter
        dialogBinding.rvList1.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        dialogBinding.rvList1.setCurrentItem(binding.rvList1.currentItem, false)

        dialogBinding.rvList1.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                super.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.itemZoomMutable.value = position
                viewModel.productZoomAdapter.notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.e("TAG", "state" + state)
            }
        })


        viewModel.itemZoomMutable.value = binding.rvList1.currentItem
        viewModel.itemZoomMutable.observe(viewLifecycleOwner) {
            dialogBinding.rvList1.setCurrentItem(it, false)
        }

        Log.e("TAG", "videoList "+item1.size)
        (dialogBinding.rvList1.getRecyclerView()
            .getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations =
            false


        dialogBinding.rvListSlide.setHasFixedSize(true)
        dialogBinding.rvListSlide.adapter = viewModel.productZoomAdapter
        viewModel.productZoomAdapter.submitList(item1)
        viewModel.productZoomAdapter.notifyDataSetChanged()

//        viewModel.productZoomAdapter

    }



    override fun onDestroyView() {
        super.onDestroyView()
        isProductLoad = false
        itemProductsAdd = false
    }

}


