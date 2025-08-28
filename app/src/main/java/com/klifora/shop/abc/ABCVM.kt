package com.klifora.shop.abc

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.klifora.shop.R
import com.klifora.shop.abc.ABC.DiamondItem
import com.klifora.shop.abc.AbcdFragment.Companion.monthListParent
import com.klifora.shop.databinding.ItemCategoryBinding
import com.klifora.shop.databinding.ItemChildBinding
import com.klifora.shop.databinding.ItemDiamondHistoryBinding
import com.klifora.shop.databinding.ItemHomeCategoryBinding
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.Items
import com.klifora.shop.models.category.ChildrenData
import com.klifora.shop.models.category.ChildrenDataX
import com.klifora.shop.models.category.ItemCategory
import com.klifora.shop.models.products.ItemProductRoot
import com.klifora.shop.networking.ApiInterface
import com.klifora.shop.networking.CallHandler
import com.klifora.shop.networking.Repository
import com.klifora.shop.ui.main.category.CategoryChildTab.Companion.mainSelect
import com.klifora.shop.ui.main.products.ProductsVM.Companion.isProductLoad
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainMaterial
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainPrice
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainShopFor
import com.klifora.shop.utils.endDrawable
import com.klifora.shop.utils.glideImageChache
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.sessionExpired
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ABCVM @Inject constructor(private val repository: Repository) : ViewModel() {

    fun getCategories(token: String, callBack: (ItemCategory) -> Unit) =
        viewModelScope.launch {
            Log.e("TAG", "aaaaaaaaaaaaa")
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemCategory>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getCategories("Bearer " + token)
                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemCategory>) {
                            Log.e("TAG", "cccccccccccc")
                            if (response.isSuccessful) {
                                callBack(response.body()!!)
                            }
                        }

                        override fun error(message: String) {
                            Log.e("TAG", "bbbbbbbbbbbb")
//                        Log.e("TAG", "successAA: ${message}")
//                        super.error(message)
//                        showSnackBar(message)
//                        callBack(message.toString())

                            if (message.contains("authorized")) {
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




    val categoryAdapter = object : GenericAdapter<ItemHomeCategoryBinding, ChildrenData>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemHomeCategoryBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemHomeCategoryBinding,
            dataClass: ChildrenData,
            position: Int
        ) {
            binding.apply {
//                dataClass.image.glideImageChache(binding.ivIcon.context, binding.ivIcon)

                textTitle.text = dataClass.name
                ivIcon.setOnClickListener {
                    currentList.forEach {
                        it.isSelected = false
                        it.isCollapse = false
                        it.children_data.forEach {
                            it.isSelected = false
//                            it.isChildSelect = false
                        }
                    }
                    dataClass.apply {
                        isSelected = true
                        children_data.forEach {
                            it.isSelected = true
//                            it.isChildSelect = true
                        }
                    }
                    mainPrice.forEach {
                        it.isSelected = false
//                        it.isChildSelect = false
                    }
                    mainMaterial.forEach {
                        it.isSelected = false
//                        it.isChildSelect = false
                    }
                    mainShopFor.forEach {
                        it.isSelected = false
//                        it.isChildSelect = false
                    }


                    it.findNavController().navigate(R.id.action_home_to_products)
                }
            }
        }
    }



    var selectedPosition = -1
    val categoryAdapter2 = object : GenericAdapter<ItemCategoryBinding, ChildrenData>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemCategoryBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemCategoryBinding,
            dataClassParent: ChildrenData,
            position: Int
        ) {
            binding.apply {
//                dataClass.image.glideImageChache(binding.ivIcon.context, binding.ivIcon)

                textName.text = dataClassParent.name

                if (dataClassParent.children_data.size > 0){
                    this.rvListCategory.visibility =
                        if (selectedPosition == position) View.VISIBLE else View.GONE

                    textName.endDrawable(if (selectedPosition == position) R.drawable.arrow_down else R.drawable.arrow_right)

                } else {
                    this.rvListCategory.visibility = View.GONE
                    this.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
                }



                this.linearLayout.setOnClickListener {
                    if (dataClassParent.children_data.size > 0){
                        if (selectedPosition == position) {
                            if (this.rvListCategory.isVisible == true) {
                                selectedPosition = -1
                            }
                            if (this.rvListCategory.isVisible == false) {
                                selectedPosition = position
                            }
                        } else {
                            selectedPosition = position
                        }


                        currentList.forEach {
                            it.isSelected = false
                        }
//                        Log.e("TAG", "AAAAAAAAAAA "+dataClassParent.id + "  :::  "+dataClass.id)

//                        dataClassParent.isSelected = true

//                        currentList.forEach {
//                            Log.e("TAG", "AAAAAAAAAAA "+it.toString() + "  :::  ")
//                        }


                        notifyDataSetChanged()
                    } else {
                        this.rvListCategory.visibility = View.GONE
                        this.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
                        mainThread {
                            currentList.forEach {
                                it.isSelected = false
                            }
                            dataClassParent.isSelected = true
                            monthListParent.forEach {
                                Log.e("TAG", "BBBBBBBBBB "+it.toString())
                            }
                            isProductLoad = true
                            root.findNavController().navigate(R.id.action_abc_to_productList)
                        }
                    }
                }



                val subCategoryAdapter1 = object : GenericAdapter<ItemChildBinding, ChildrenDataX>() {
                    override fun onCreateView(
                        inflater: LayoutInflater,
                        parent: ViewGroup,
                        viewType: Int
                    ) = ItemChildBinding.inflate(inflater, parent, false)

                    override fun onBindHolder(
                        binding: ItemChildBinding,
                        dataClass: ChildrenDataX,
                        position: Int
                    ) {
                        binding.apply {
                            if (dataClass.name.contains(" ")){
                                val temp = dataClass.name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                                var test2 = ""
                                for (i in temp.indices) {
                                    if (i == 0){
                                        test2 += temp[i]+"\n"
                                        print(temp[i])
                                    } else {
                                        test2 += temp[i]+" "
                                    }
                                }
                                textName.text = test2
                            } else {
                                textName.text = dataClass.name
                            }

                            layoutChild.singleClick {
//                                Log.e("TAG", "CCCCCCCCCCC "+dataClassParent.id + "  :::  "+dataClass.id)
                                mainThread {
                                    if(dataClass.isAll == true){
                                        currentList.forEach {
                                            it.isSelected = true
                                        }
                                        monthListParent.forEach {
                                            Log.e("TAG", "DDDDDDDDDDDD "+it.toString())
                                        }
                                        isProductLoad = true
                                        root.findNavController().navigate(R.id.action_abc_to_productList)
                                    } else {
                                        currentList.forEach {
                                            it.isSelected = false
                                        }
                                        dataClass.isSelected = true
                                        monthListParent.forEach {
                                            Log.e("TAG", "CCCCCCCCCCC "+it.toString())
                                        }
                                        isProductLoad = true
                                        root.findNavController().navigate(R.id.action_abc_to_productList)
                                    }
                                }
                            }
                        }
                    }
                }
                rvListCategory.setHasFixedSize(true)
                rvListCategory.adapter = subCategoryAdapter1
                subCategoryAdapter1.notifyDataSetChanged()

                val monthList: List<ChildrenDataX> = dataClassParent.children_data.filter { s -> s.is_active == true }

//
//                val childArray: ArrayList<ChildrenDataX> = ArrayList()
//                val child = ChildrenDataX()
//                child.name = "All"
//                child.isAll = true
//                child.parent_id = monthList[0]?.parent_id ?: 0
//                childArray.add(child)
//
//                monthList.forEach {
//                    childArray.add(it)
//                }


                subCategoryAdapter1.submitList(monthList)

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
                    textShape.text = dataClass.diamond_shape
                    textColor.text = dataClass.diamond_color
                    textNumbers.text = dataClass.diamond_number


                }

            }
        }
    }




}