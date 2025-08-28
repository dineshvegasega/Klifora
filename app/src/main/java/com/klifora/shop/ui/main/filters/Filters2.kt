package com.klifora.shop.ui.main.filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.klifora.shop.R
import com.klifora.shop.abc.AbcdFragment.Companion.monthListParent
import com.klifora.shop.databinding.FiltersBinding
import com.klifora.shop.datastore.DataStoreKeys.FILTER_CATEGORY
import com.klifora.shop.datastore.DataStoreKeys.FILTER_GENDER
import com.klifora.shop.datastore.DataStoreKeys.FILTER_MATERIAL
import com.klifora.shop.datastore.DataStoreKeys.FILTER_PRICE
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.saveObject
import com.klifora.shop.models.Items
import com.klifora.shop.models.category.ChildrenData
import com.klifora.shop.ui.main.products.ProductsVM.Companion.isFilterLoad
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.typefacenunitosans_light
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.typefacenunitosans_semibold
//import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainMaterial
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainPrice
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainShopFor
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class Filters2 : Fragment() {
    private val viewModel: Filters2VM by viewModels()
    private var _binding: FiltersBinding? = null
    private val binding get() = _binding!!


    companion object {
//        var headerAdapter : GenericAdapter<ItemFilterCategoryBinding, Items>? = null
    }

    var defaultRun = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FiltersBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        hideValueOff = 3
        MainActivity.mainActivity.get()!!.callBack(0)


        binding.apply {

            saveObject(FILTER_PRICE, mainPrice)
            saveObject(FILTER_CATEGORY, monthListParent)
            saveObject(FILTER_MATERIAL, mainMaterial)
            saveObject(FILTER_GENDER, mainShopFor)

            ivIconCross.singleClick {
                readData(FILTER_PRICE) { filter ->
                    val typeToken = object : TypeToken<ArrayList<Items>>() {}.type
                    val changeValue = Gson().fromJson<ArrayList<Items>>(filter, typeToken)
                    mainPrice.clear()
                    mainPrice.addAll(changeValue)
                }

                readData(FILTER_CATEGORY) { filter ->
                    val typeToken = object : TypeToken<ArrayList<ChildrenData>>() {}.type
                    val changeValue = Gson().fromJson<ArrayList<ChildrenData>>(filter, typeToken)
                    monthListParent.clear()
                    monthListParent.addAll(changeValue)
                }

                readData(FILTER_MATERIAL) { filter ->
                    val typeToken = object : TypeToken<ArrayList<Items>>() {}.type
                    val changeValue = Gson().fromJson<ArrayList<Items>>(filter, typeToken)
                    mainMaterial.clear()
                    mainMaterial.addAll(changeValue)
                }

                readData(FILTER_GENDER) { filter ->
                    val typeToken = object : TypeToken<ArrayList<Items>>() {}.type
                    val changeValue = Gson().fromJson<ArrayList<Items>>(filter, typeToken)
                    mainShopFor.clear()
                    mainShopFor.addAll(changeValue)
                }

                isFilterLoad = false
                findNavController().navigateUp()
            }


            layoutClear.singleClick {
                mainPrice.forEach {
                    it.isSelected = false
                }

                monthListParent.forEach {
                    it.isSelected = false
                    if (it.isSelected == false) {
                        it.children_data.forEach { sub ->
                            sub.isSelected = false
                        }
                    }
                }

                mainMaterial.forEach {
                    it.isSelected = false
                }

                mainShopFor.forEach {
                    it.isSelected = false
                }

                viewModel.itemPriceCount.value = 0
                viewModel.itemCategoryCount.value = 0
                viewModel.itemMaterialCount.value = 0
                viewModel.itemShopForCount.value = 0

                when (defaultRun) {
                    1 -> {
                        rvList2.adapter = viewModel.priceAdapter
                        viewModel.priceAdapter.notifyDataSetChanged()
                        viewModel.priceAdapter.submitList(mainPrice)
                    }

                    2 -> {
                        rvList2.adapter = viewModel.categoryAdapter
                        viewModel.categoryAdapter.notifyDataSetChanged()
                        viewModel.categoryAdapter.submitList(monthListParent!!)
                    }

                    3 -> {
                        rvList2.adapter = viewModel.materialAdapter
                        viewModel.materialAdapter.notifyDataSetChanged()
                        viewModel.materialAdapter.submitList(mainMaterial)
                    }

                    4 -> {
                        rvList2.adapter = viewModel.shopForAdapter
                        viewModel.shopForAdapter.notifyDataSetChanged()
                        viewModel.shopForAdapter.submitList(mainShopFor)
                    }
                }
            }

            layoutApply.singleClick {
                isFilterLoad = true
                findNavController().navigate(R.id.action_filters_to__productList)
            }


            val filteredPrice = mainPrice.filter { it.isSelected == true }
            viewModel.itemPriceCount.value = filteredPrice.size

            rvList2.setHasFixedSize(true)
            rvList2.adapter = viewModel.priceAdapter
            viewModel.priceAdapter.notifyDataSetChanged()
            viewModel.priceAdapter.submitList(mainPrice)


            val filteredMaterial = mainMaterial.filter { it.isSelected == true }
            viewModel.itemMaterialCount.value = filteredMaterial.size


            val filteredShopFor = mainShopFor.filter { it.isSelected == true }
            viewModel.itemShopForCount.value = filteredShopFor.size



            viewModel.itemPriceCount.observe(viewLifecycleOwner) {
                textPrice.text = if (it == 0) "Price" else "Price ($it)"
            }

            viewModel.itemCategoryCount.observe(viewLifecycleOwner) {
                var count = 0
                monthListParent.forEach {
                    it.children_data.forEach { sub ->
                        if (sub.isSelected && sub.isAll == false) {
                            Log.e("TAG", "it.isSelected ${sub.isSelected}")
                            count += 1
                        }
                    }
                }


                Log.e("TAG", "count ${count}")
                textCategories.text = if (count == 0) "Categories" else "Categories ($count)"
            }

            viewModel.itemMaterialCount.observe(viewLifecycleOwner) {
                textMaterial.text = if (it == 0) "Material" else "Material ($it)"
            }

            viewModel.itemShopForCount.observe(viewLifecycleOwner) {
                textShopFor.text = if (it == 0) "Shop For" else "Shop For ($it)"
            }

            textPrice.singleClick {
                defaultRun = 1
                textPrice.setTypeface(typefacenunitosans_semibold)
                textCategories.setTypeface(typefacenunitosans_light)
                textMaterial.setTypeface(typefacenunitosans_light)
                textShopFor.setTypeface(typefacenunitosans_light)

                Handler(Looper.getMainLooper()).postDelayed({
                    rvList2.setHasFixedSize(true)
                    rvList2.adapter = viewModel.priceAdapter
                    viewModel.priceAdapter.notifyDataSetChanged()
                    viewModel.priceAdapter.submitList(mainPrice)
                }, 100)
            }

            textCategories.singleClick {
//                isFilterFromFrom = false
                defaultRun = 2
                textPrice.setTypeface(typefacenunitosans_light)
                textCategories.setTypeface(typefacenunitosans_semibold)
                textMaterial.setTypeface(typefacenunitosans_light)
                textShopFor.setTypeface(typefacenunitosans_light)

                Handler(Looper.getMainLooper()).postDelayed({

                    rvList2.setHasFixedSize(true)
                    rvList2.adapter = viewModel.categoryAdapter
                    viewModel.categoryAdapter.notifyDataSetChanged()
                    viewModel.categoryAdapter.submitList(monthListParent)
                }, 100)
            }

            textMaterial.singleClick {
                defaultRun = 3
                textPrice.setTypeface(typefacenunitosans_light)
                textCategories.setTypeface(typefacenunitosans_light)
                textMaterial.setTypeface(typefacenunitosans_semibold)
                textShopFor.setTypeface(typefacenunitosans_light)

                Handler(Looper.getMainLooper()).postDelayed({
//                    arrayMaterial.clear()
//                    arrayMaterial.apply {
//                        add("gold")
//                        add("silver")
//                    }

//                    viewModel.itemMaterial.map { mapMaterial ->
//                        if (mapMaterialMatch(mapMaterial.name) == true){
//                            mapMaterial.isSelected = true
//                        } else {
//                            mapMaterial.isSelected = false
//                        }
//                    }

                    rvList2.setHasFixedSize(true)
                    rvList2.adapter = viewModel.materialAdapter
                    viewModel.materialAdapter.notifyDataSetChanged()
                    viewModel.materialAdapter.submitList(mainMaterial)
                }, 100)
            }

            textShopFor.singleClick {
                defaultRun = 3
                textPrice.setTypeface(typefacenunitosans_light)
                textCategories.setTypeface(typefacenunitosans_light)
                textMaterial.setTypeface(typefacenunitosans_light)
                textShopFor.setTypeface(typefacenunitosans_semibold)

                Handler(Looper.getMainLooper()).postDelayed({
//                    arrayMaterial.clear()
//                    arrayMaterial.apply {
//                        add("gold")
//                        add("silver")
//                    }

//                    viewModel.itemMaterial.map { mapShopFor ->
//                        if (mapShopForMatch(mapShopFor.name) == true){
//                            mapShopFor.isSelected = true
//                        } else {
//                            mapShopFor.isSelected = false
//                        }
//                    }

                    rvList2.setHasFixedSize(true)
                    rvList2.adapter = viewModel.shopForAdapter
                    viewModel.shopForAdapter.notifyDataSetChanged()
                    viewModel.shopForAdapter.submitList(mainShopFor)
                }, 100)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        itemsProduct.clear()
//        page = 1
//        isProductLoad = true
    }
}