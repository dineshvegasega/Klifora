package com.klifora.shop.ui.main.filters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemFilterBinding
import com.klifora.shop.databinding.ItemFilterCategoryBinding
import com.klifora.shop.databinding.ItemFilterChildBinding
import com.klifora.shop.genericAdapter.GenericAdapter
import com.klifora.shop.models.Items
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.typefacenunitosans_light
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.typefacenunitosans_semibold
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersVM @Inject constructor() : ViewModel() {

    var itemPriceCount = MutableLiveData<Int>(0)
    var itemCategoryCount = MutableLiveData<Int>(0)
    var itemMaterialCount = MutableLiveData<Int>(0)
    var itemShopForCount = MutableLiveData<Int>(0)

//    var itemPrice: ArrayList<Items> = ArrayList()
//    var itemCategory: ArrayList<Items> = ArrayList()
//    var itemMaterial: ArrayList<Items> = ArrayList()
//    var itemShopFor: ArrayList<Items> = ArrayList()

    init {
//        itemPrice.add(Items("₹1000 - ₹10000"))
//        itemPrice.add(Items("₹10000 - ₹15000"))
//        itemPrice.add(Items("₹15000 - ₹20000"))
//        itemPrice.add(Items("₹20000 - ₹30000"))
//        itemPrice.add(Items("₹30000 - ₹50000"))
//        itemPrice.add(Items("₹50000 - ₹100000"))
//        itemPrice.add(Items("₹100000 - ₹1000000"))
//        itemPrice.add(Items("₹1000000 - Above"))

//        itemCategory.add(Items("ring"))
//        itemCategory.add(Items("necklace"))
//        itemCategory.add(Items("earring"))
//        itemCategory.add(Items("bangle"))


//        mainCategory.add(Items("RINGS",
//            subCategory = arrayListOf(Items("All Rings"),
//                Items("Men's"),
//                Items("Band"),
//                Items("Casual"),
//                Items("Cocktail"),
//                Items("Engagement"),
//                Items("Solitaire"))
//        ))
//        mainCategory.add(Items("NECKLACE",
//            subCategory = arrayListOf(Items("All Necklace"),
//                Items("Men's"),
//                Items("Long Necklace"),
//                Items("Short Necklace"),
//                Items("Short Necklace"))
//        ))
//        mainCategory.add(Items("PENDANTS",
//            subCategory = arrayListOf(Items("All Pendants"),
//                Items("Alphabates"),
//                Items("Zodiac"),
//                Items("Casual"),
//                Items("Everyday"))
//        ))
//        mainCategory.add(Items("BRACELETS",
//            subCategory = arrayListOf(Items("All Bracelets"),
//                Items("Casual"),
//                Items("Bangles"),
//                Items("Occasion"),
//                Items("Everyday"))
//        ))
//        mainCategory.add(Items("MANGALSUTRA",
//            subCategory = arrayListOf(Items("All Mangalsutra"),
//                Items("Casual"),
//                Items("Bangles"),
//                Items("Everyday"))
//        ))
//        mainCategory.add(Items("EARRINGS",
//            subCategory = arrayListOf(Items("All Earrings"),
//                Items("Balis"),
//                Items("Studs"),
//                Items("Drops"),
//                Items("Hoops"))
//        ))

//        itemMaterial.add(Items("Gold 14 K"))
//        itemMaterial.add(Items("Gold 18 K"))
//        itemMaterial.add(Items("Platinum"))
//
//        itemShopFor.add(Items("Men"))
//        itemShopFor.add(Items("Women"))
//        itemShopFor.add(Items("Kids"))
    }


    var selectedPosition = -1

    val priceAdapter = object : GenericAdapter<ItemFilterBinding, Items>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemFilterBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemFilterBinding,
            dataClass: Items,
            position: Int
        ) {
            binding.apply {
                textItem.text = dataClass.name
                textItem.setTypeface(if (dataClass.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)
                ivIconCheck.imageTintList =
                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.app_color
                    )
                    else ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color._D9D9D9
                    )

                root.singleClick {
                    selectedPosition = position
                    dataClass.isSelected = !dataClass.isSelected

//                    if (dataClass.isSelected == true) {
//                        arrayPrice.add(dataClass.name)
//                    } else {
//                        arrayPrice.remove(dataClass.name)
//                    }
//                    arrayPrice.distinct()
                    notifyItemChanged(position)
                }

                val filteredNot = currentList.filter {
                    it.isSelected == true
                }
                itemPriceCount.value = filteredNot.size
            }
        }
    }



    val categoryAdapter = object : GenericAdapter<ItemFilterCategoryBinding, Items>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemFilterCategoryBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemFilterCategoryBinding,
            dataClass: Items,
            position: Int
        ) {
            binding.apply {

                textItem.text = dataClass.name

                if(dataClass.subCategory.isEmpty()){
                    layoutChild.visibility = View.GONE
                    ivHideShow.visibility = View.GONE
                } else {
                    layoutChild.visibility = View.VISIBLE
                    ivHideShow.visibility = View.VISIBLE
                }

                textItem.setTypeface(if (dataClass.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)
                ivIconCheck.imageTintList =
                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.app_color
                    )
                    else ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color._D9D9D9
                    )

                textItem.singleClick {
                    selectedPosition = position
                    dataClass.isSelected = !dataClass.isSelected

                    if (dataClass.isSelected == true) {
                        dataClass.subCategory.forEach {
                            it.isSelected = true
//                            it.isChildSelect = true
                        }
                    } else {
                        dataClass.subCategory.forEach {
                            it.isSelected = false
//                            it.isChildSelect = false
                        }
                    }

                    notifyItemChanged(position)
                }

                val filteredNot = currentList
//                itemCategoryCount.value = currentList.filter { it.isSelected == true }.size
////                itemCategoryCount.value = filteredNot.size

//                var cout = 0
//                filteredNot.forEach {
////                        it.subCategory.forEach { sub ->
////                            sub.isSelected = true
////                            sub.isChildSelect = true
////                        }
//                    cout = it.subCategory.filter { it.isSelected == true }.size
//                }
                itemCategoryCount.value = 0

                layoutChild.visibility =
                    if (dataClass.isCollapse == true) View.VISIBLE else View.GONE
                ivHideShow.setImageDrawable(
                    ContextCompat.getDrawable(
                        root.context,
                        if (dataClass.isCollapse == true) R.drawable.baseline_remove_24 else R.drawable.baseline_add_24
                    )
                )
                ivHideShow.singleClick {
                    dataClass.isCollapse = !dataClass.isCollapse
//                    if (dataClass.isSelected == true) {
//                        dataClass.subCategory.forEach {
//                            it.isSelected = true
//                            it.isChildSelect = true
//                        }
//                    } else {
//                        dataClass.subCategory.forEach {
//                            it.isSelected = false
//                            it.isChildSelect = false
//                        }
//                    }
                    notifyItemChanged(position)
                }

                val categoryChildAdapter =
                    object : GenericAdapter<ItemFilterChildBinding, Items>() {
                        override fun onCreateView(
                            inflater: LayoutInflater,
                            parent: ViewGroup,
                            viewType: Int
                        ) = ItemFilterChildBinding.inflate(inflater, parent, false)

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onBindHolder(
                            bindingChild: ItemFilterChildBinding,
                            dataClassChild: Items,
                            positionChild: Int
                        ) {
                            bindingChild.apply {
                                textItemChild.text = dataClassChild.name

                                root.singleClick {
//                                    isFilterFrom = true
                                    selectedPosition = positionChild
                                    dataClassChild.isSelected = !dataClassChild.isSelected
//                                    dataClassChild.isChildSelect = !dataClassChild.isChildSelect

                                    notifyItemChanged(positionChild)
                                }

//                                Log.e("TAG" , "filteredNotChild ${dataClassChild.isSelected}   currentList${dataClassChild.isChildSelect}")



                                val filteredNotChild = currentList.filter { it.isSelected == true }
//                                Log.e("TAG" , "filteredNotChild ${filteredNotChild.size}")

                                if (filteredNotChild.size == (currentList.size)) {
                                    dataClass.isSelected = true
                                } else {
                                    dataClass.isSelected = false
                                }

//                                itemCategoryCount.value =
//                                    filteredNot.filter { it.isSelected == true }.size
//                                var cout = 0
//                                currentList.forEach {
//                                    //cout += it.subCategory.filter { it.isSelected == true }.size
//                                    Log.e("TAG", " itemCategoryCount ${it.isSelected}")
//
//                                }
                                itemCategoryCount.value = 0



                                textItem.setTypeface(if (dataClass.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)
                                ivIconCheck.imageTintList =
                                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
                                        binding.root.context,
                                        R.color.app_color
                                    )
                                    else ContextCompat.getColorStateList(
                                        binding.root.context,
                                        R.color._D9D9D9
                                    )

//                                if (dataClassChild.isSelected == true) {
////                                if (dataClassChild.isChildSelect == true){
////                                    textItemChild.setTypeface(typefacenunitosans_light)
////                                } else {
//                                    textItemChild.setTypeface(typefacenunitosans_semibold)
////                                }
//                                } else {
//                                    if(parentPress == false){
//                                        if (dataClassChild.isChildSelect == true){
//                                            textItemChild.setTypeface(typefacenunitosans_semibold)
//                                        } else {
//                                            textItemChild.setTypeface(typefacenunitosans_light)
//                                        }
//                                    } else {
//                                        textItemChild.setTypeface(typefacenunitosans_light)
//                                    }
//                                }


                            textItemChild.setTypeface(if (dataClassChild.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)


                                ivIconCheckChild.imageTintList =
                                    if (dataClassChild.isSelected == true) ContextCompat.getColorStateList(
                                        bindingChild.root.context,
                                        R.color.app_color
                                    )
                                    else ContextCompat.getColorStateList(
                                        bindingChild.root.context,
                                        R.color._D9D9D9
                                    )
                            }
                        }
                    }

                rvListChild.setHasFixedSize(true)
                rvListChild.adapter = categoryChildAdapter
                categoryChildAdapter.notifyDataSetChanged()
                categoryChildAdapter.submitList(dataClass.subCategory.filter { !it.isAll})
            }
        }
    }


    val materialAdapter = object : GenericAdapter<ItemFilterBinding, Items>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemFilterBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemFilterBinding,
            dataClass: Items,
            position: Int
        ) {
            binding.apply {
                textItem.text = dataClass.name

                textItem.setTypeface(if (dataClass.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)
                ivIconCheck.imageTintList =
                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.app_color
                    )
                    else ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color._D9D9D9
                    )

                root.singleClick {
                    selectedPosition = position
                    dataClass.isSelected = !dataClass.isSelected
//                    if (dataClass.isSelected == true) {
//                        arrayMaterial.add(dataClass.name)
//                    } else {
//                        arrayMaterial.remove(dataClass.name)
//                    }
//                    arrayMaterial.distinct()
                    notifyItemChanged(position)
                }


                val filteredNot = currentList.filter { it.isSelected == true }
                itemMaterialCount.value = filteredNot.size

            }
        }
    }


    val shopForAdapter = object : GenericAdapter<ItemFilterBinding, Items>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemFilterBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(
            binding: ItemFilterBinding,
            dataClass: Items,
            position: Int
        ) {
            binding.apply {
                textItem.text = dataClass.name

                textItem.setTypeface(if (dataClass.isSelected == true) typefacenunitosans_semibold else typefacenunitosans_light)
                ivIconCheck.imageTintList =
                    if (dataClass.isSelected == true) ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color.app_color
                    )
                    else ContextCompat.getColorStateList(
                        binding.root.context,
                        R.color._D9D9D9
                    )

                root.singleClick {
                    selectedPosition = position
                    dataClass.isSelected = !dataClass.isSelected
//                    if (dataClass.isSelected == true) {
//                        arrayShopFor.add(dataClass.name)
//                    } else {
//                        arrayShopFor.remove(dataClass.name)
//                    }
//                    arrayShopFor.distinct()
                    notifyItemChanged(position)
                }


                val filteredNot = currentList.filter { it.isSelected == true }
                itemShopForCount.value = filteredNot.size

            }
        }
    }

}