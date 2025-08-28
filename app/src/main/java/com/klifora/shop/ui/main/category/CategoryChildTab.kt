package com.klifora.shop.ui.main.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.CategoryChildTabBinding
import com.klifora.shop.models.Items
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainMaterial
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainPrice
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainShopFor
import com.klifora.shop.utils.endDrawable
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryChildTab(
    private val activity: FragmentActivity,
    private val videoPath: Items,
    position: Int,
) : Fragment() {
    private var _binding: CategoryChildTabBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryVM by viewModels()

    private var activityThis: FragmentActivity ?= null
    private var itemsThis: Items ?= null
    private var positionThis: Int ?= null

    init {
        this.activityThis = activity
        this.itemsThis = videoPath
        this.positionThis = position
    }


    companion object {
        @JvmStatic
        var mainSelect = 0
//        lateinit var adapter2: CategoryChildTabAdapter
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CategoryChildTabBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        adapter2 = CategoryChildTabAdapter()

        binding.apply {
//            adapter2.submitData(viewModel.item1)
//            adapter2.notifyDataSetChanged()
//            binding.rvList2.adapter = adapter2



            itemCategory1.textName.text = mainCategory[0].name
            itemCategory2.textName.text = mainCategory[1].name
            itemCategory3.textName.text = mainCategory[2].name
            itemCategory4.textName.text = mainCategory[3].name
            itemCategory5.textName.text = mainCategory[4].name
            itemCategory6.textName.text = mainCategory[5].name
            itemCategory7.textName.text = mainCategory[6].name
            itemCategory8.textName.text = mainCategory[7].name
//            itemCategory9.textName.text = mainCategory[8].name
//            itemCategory10.textName.text = mainCategory[9].name

            if (mainCategory[0].subCategory.isEmpty()){
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory1.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[1].subCategory.isEmpty()){
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory2.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[2].subCategory.isEmpty()){
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory3.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[3].subCategory.isEmpty()){
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory4.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[4].subCategory.isEmpty()){
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory5.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[5].subCategory.isEmpty()){
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory6.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[6].subCategory.isEmpty()){
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory7.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

            if (mainCategory[7].subCategory.isEmpty()){
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory8.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
            }

//            if (mainCategory[8].subCategory.isEmpty()){
//                itemCategory9.rvListCategory.visibility = View.GONE
//                itemCategory9.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
//            }
//
//            if (mainCategory[9].subCategory.isEmpty()){
//                itemCategory10.rvListCategory.visibility = View.GONE
//                itemCategory10.textName.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0)
//            }


            itemCategory1.rvListCategory.adapter = viewModel.subCategoryAdapter1

            itemCategory1.linearLayout.singleClick {
                itemCategory1.rvListCategory.adapter = viewModel.subCategoryAdapter1
                viewModel.subCategoryAdapter1.notifyDataSetChanged()
                viewModel.subCategoryAdapter1.submitList(mainCategory[0].subCategory)

                if (itemCategory1.rvListCategory.isVisible == true) {
                    itemCategory1.rvListCategory.visibility = View.GONE
                    itemCategory1.textName.endDrawable(R.drawable.arrow_right)
                } else {
                    itemCategory1.rvListCategory.visibility = View.VISIBLE
                    itemCategory1.textName.endDrawable(R.drawable.arrow_down)
                }
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
//                itemCategory1.textName.endDrawable(R.drawable.arrow_down)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 1
                Handler(Looper.myLooper()!!).postDelayed({
                    itemCategory1.rvListCategory.scrollTo(0, itemCategory1.rvListCategory.bottom)
                }, 100)
            }


            itemCategory2.linearLayout.singleClick {
                itemCategory2.rvListCategory.adapter = viewModel.subCategoryAdapter2
                viewModel.subCategoryAdapter2.notifyDataSetChanged()
                viewModel.subCategoryAdapter2.submitList(mainCategory[1].subCategory)
//                itemCategory1.rvListCategory.visibility = View.GONE
                if (itemCategory2.rvListCategory.isVisible == true) {
                    itemCategory2.rvListCategory.visibility = View.GONE
                    itemCategory2.textName.endDrawable(R.drawable.arrow_right)
                } else {
                    itemCategory2.rvListCategory.visibility = View.VISIBLE
                    itemCategory2.textName.endDrawable(R.drawable.arrow_down)
                }
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_down)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 2
                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[1].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
//                findNavController().navigate(R.id.action_category_to_products)
//                Handler(Looper.myLooper()!!).postDelayed({
//                    itemCategory2.rvListCategory.scrollTo(0, itemCategory2.rvListCategory.bottom)
//                }, 100)
            }


            itemCategory3.linearLayout.singleClick {
                itemCategory3.rvListCategory.adapter = viewModel.subCategoryAdapter3
                viewModel.subCategoryAdapter3.notifyDataSetChanged()
                viewModel.subCategoryAdapter3.submitList(mainCategory[2].subCategory)
//                itemCategory1.rvListCategory.visibility = View.GONE
//                itemCategory2.rvListCategory.visibility = View.GONE
                if (itemCategory3.rvListCategory.isVisible == true) {
                    itemCategory3.rvListCategory.visibility = View.GONE
                    itemCategory3.textName.endDrawable(R.drawable.arrow_right)
                } else {
                    itemCategory3.rvListCategory.visibility = View.VISIBLE
                    itemCategory3.textName.endDrawable(R.drawable.arrow_down)
                }
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_down)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 3
                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[2].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
//                findNavController().navigate(R.id.action_category_to_products)
//                Handler(Looper.myLooper()!!).postDelayed({
//                    itemCategory3.rvListCategory.scrollTo(0, itemCategory3.rvListCategory.bottom)
//                }, 100)
            }


            itemCategory4.linearLayout.singleClick {
                itemCategory4.rvListCategory.adapter = viewModel.subCategoryAdapter4
                viewModel.subCategoryAdapter4.notifyDataSetChanged()
                viewModel.subCategoryAdapter4.submitList(mainCategory[3].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
//                if (itemCategory4.rvListCategory.isVisible == true) {
                    itemCategory4.rvListCategory.visibility = View.GONE
//                    itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                } else {
//                    itemCategory4.rvListCategory.visibility = View.VISIBLE
//                    itemCategory4.textName.endDrawable(R.drawable.arrow_down)
//                }
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_down)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 4
                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[3].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
//                Handler(Looper.myLooper()!!).postDelayed({
//                    itemCategory4.rvListCategory.scrollTo(0, itemCategory4.rvListCategory.bottom)
//                }, 100)
            }


            itemCategory5.linearLayout.singleClick {
                itemCategory5.rvListCategory.adapter = viewModel.subCategoryAdapter5
                viewModel.subCategoryAdapter5.notifyDataSetChanged()
                viewModel.subCategoryAdapter5.submitList(mainCategory[4].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 5
                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[4].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


            itemCategory6.linearLayout.singleClick {
                itemCategory6.rvListCategory.adapter = viewModel.subCategoryAdapter6
                viewModel.subCategoryAdapter6.notifyDataSetChanged()
                viewModel.subCategoryAdapter6.submitList(mainCategory[5].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 6

                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[5].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


            itemCategory7.linearLayout.singleClick {
                itemCategory7.rvListCategory.adapter = viewModel.subCategoryAdapter7
                viewModel.subCategoryAdapter7.notifyDataSetChanged()
                viewModel.subCategoryAdapter7.submitList(mainCategory[6].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 7

                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[6].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


            itemCategory8.linearLayout.singleClick {
                itemCategory8.rvListCategory.adapter = viewModel.subCategoryAdapter8
                viewModel.subCategoryAdapter8.notifyDataSetChanged()
                viewModel.subCategoryAdapter8.submitList(mainCategory[7].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 8

                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[7].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


            itemCategory9.linearLayout.singleClick {
                itemCategory9.rvListCategory.adapter = viewModel.subCategoryAdapter9
                viewModel.subCategoryAdapter9.notifyDataSetChanged()
                viewModel.subCategoryAdapter9.submitList(mainCategory[8].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 9

                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[8].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


            itemCategory10.linearLayout.singleClick {
                itemCategory10.rvListCategory.adapter = viewModel.subCategoryAdapter10
                viewModel.subCategoryAdapter10.notifyDataSetChanged()
                viewModel.subCategoryAdapter10.submitList(mainCategory[9].subCategory)
                itemCategory1.rvListCategory.visibility = View.GONE
                itemCategory2.rvListCategory.visibility = View.GONE
                itemCategory3.rvListCategory.visibility = View.GONE
                itemCategory4.rvListCategory.visibility = View.GONE
                itemCategory5.rvListCategory.visibility = View.GONE
                itemCategory6.rvListCategory.visibility = View.GONE
                itemCategory7.rvListCategory.visibility = View.GONE
                itemCategory8.rvListCategory.visibility = View.GONE
                itemCategory9.rvListCategory.visibility = View.GONE
                itemCategory10.rvListCategory.visibility = View.GONE
                itemCategory1.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory2.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory3.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory4.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory5.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory6.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory7.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory8.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory9.textName.endDrawable(R.drawable.arrow_right)
//                itemCategory10.textName.endDrawable(R.drawable.arrow_right)
                mainSelect = 10

                mainCategory.forEach {
                    it.isSelected = false
                    it.subCategory.forEach {
                        it.isSelected = false
                    }
                }
                mainCategory.forEach {
                    if (it.isSelected) {
                        it.subCategory.forEach { sub ->
                            sub.isSelected = true
                        }
                    }
                }
                mainCategory[9].isSelected = true
                mainPrice.forEach {
                    it.isSelected = false
                }
                mainMaterial.forEach {
                    it.isSelected = false
                }
                findNavController().navigate(R.id.action_category_to_products)
            }


//
//            rvListCategory2.adapter = viewModel.subCategoryAdapter2
//            itemCategory3.linearLayout.singleClick {
//                rvListCategory1.adapter = viewModel.subCategoryAdapter1
//                viewModel.subCategoryAdapter2.notifyDataSetChanged()
//                viewModel.subCategoryAdapter2.submitList(mainCategory[2].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.VISIBLE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 3
//            }
//
//            itemCategory4.linearLayout.singleClick {
//                rvListCategory2.adapter = viewModel.subCategoryAdapter2
//                viewModel.subCategoryAdapter2.notifyDataSetChanged()
//                viewModel.subCategoryAdapter2.submitList(mainCategory[3].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.VISIBLE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 4
//            }
//
//
//
//            rvListCategory3.adapter = viewModel.subCategoryAdapter3
//            itemCategory5.linearLayout.singleClick {
//                rvListCategory3.adapter = viewModel.subCategoryAdapter3
//                viewModel.subCategoryAdapter3.notifyDataSetChanged()
//                viewModel.subCategoryAdapter3.submitList(mainCategory[4].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.VISIBLE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 5
//            }
//
//            itemCategory6.linearLayout.singleClick {
//                rvListCategory3.adapter = viewModel.subCategoryAdapter3
//                viewModel.subCategoryAdapter3.notifyDataSetChanged()
//                viewModel.subCategoryAdapter3.submitList(mainCategory[5].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.VISIBLE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 6
//            }
//
//
//
//            rvListCategory4.adapter = viewModel.subCategoryAdapter4
//            itemCategory7.linearLayout.singleClick {
//                rvListCategory4.adapter = viewModel.subCategoryAdapter4
//                viewModel.subCategoryAdapter4.notifyDataSetChanged()
//                viewModel.subCategoryAdapter4.submitList(mainCategory[6].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.VISIBLE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 7
//            }
//
//            itemCategory8.linearLayout.singleClick {
//                rvListCategory4.adapter = viewModel.subCategoryAdapter4
//                viewModel.subCategoryAdapter4.notifyDataSetChanged()
//                viewModel.subCategoryAdapter4.submitList(mainCategory[7].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.VISIBLE
//                rvListCategory5.visibility = View.GONE
//                mainSelect = 8
//            }
//
//
//
//
//            rvListCategory5.adapter = viewModel.subCategoryAdapter5
//            itemCategory9.linearLayout.singleClick {
//                rvListCategory5.adapter = viewModel.subCategoryAdapter5
//                viewModel.subCategoryAdapter5.notifyDataSetChanged()
//                viewModel.subCategoryAdapter5.submitList(mainCategory[8].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.VISIBLE
//                mainSelect = 9
//            }
//
//            itemCategory10.linearLayout.singleClick {
//                rvListCategory5.adapter = viewModel.subCategoryAdapter5
//                viewModel.subCategoryAdapter5.notifyDataSetChanged()
//                viewModel.subCategoryAdapter5.submitList(mainCategory[9].subCategory)
//                rvListCategory1.visibility = View.GONE
//                rvListCategory2.visibility = View.GONE
//                rvListCategory3.visibility = View.GONE
//                rvListCategory4.visibility = View.GONE
//                rvListCategory5.visibility = View.VISIBLE
//                mainSelect = 10
//            }


        }

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
//        Log.e("TAG", "onViewCreated: Fragment Position : ${videoPath.name}")
        mainShopFor.forEach {
            if (videoPath.name == it.name) {
                it.isSelected = true
                Log.e("TAG", "onViewCreated: Fragment PositionIF : ${it.name}")
                binding.apply {
                    if (it.name == "Men"){
                        Log.e("TAG", "onViewCreated: Fragment PositionIFA : ${it.name}")
//                        itemCategory1.root.visibility = View.GONE
//                        itemCategory2.root.visibility = View.GONE
//                        itemCategory3.root.visibility = View.GONE
//                        itemCategory4.root.visibility = View.GONE
//                        itemCategory5.root.visibility = View.GONE
//                        itemCategory6.root.visibility = View.GONE
//                        itemCategory7.root.visibility = View.GONE
//                        itemCategory8.root.visibility = View.GONE
                        itemCategory9.root.visibility = View.GONE
                        itemCategory10.root.visibility = View.GONE
                    }
                    if (it.name == "Women"){
                        Log.e("TAG", "onViewCreated: Fragment PositionIFB : ${it.name}")
//                        itemCategory8.root.visibility = View.GONE
                        itemCategory9.root.visibility = View.GONE
                        itemCategory10.root.visibility = View.GONE
//                        itemCategory5.linearLayout.visibility = View.VISIBLE
//                        itemCategory6.linearLayout.visibility = View.VISIBLE
//                        itemCategory9.linearLayout.visibility = View.VISIBLE
                    }
                    if (it.name == "Kids"){
                        Log.e("TAG", "onViewCreated: Fragment PositionIFC : ${it.name}")
//                        itemCategory8.root.visibility = View.GONE
                        itemCategory9.root.visibility = View.GONE
                        itemCategory10.root.visibility = View.GONE
//                        itemCategory5.rvListCategory.visibility = View.VISIBLE
//                        itemCategory6.rvListCategory.visibility = View.VISIBLE
//                        itemCategory9.rvListCategory.visibility = View.VISIBLE
                    }
                }
            } else {
                it.isSelected = false
                Log.e("TAG", "onViewCreated: Fragment PositionELSE : ${it.name}")
//                binding.apply {
//                    if (it.name == "Men"){
//                        Log.e("TAG", "onViewCreated: Fragment PositionELSEA : ${it.name}")
//                        itemCategory5.rvListCategory.visibility = View.GONE
//                        itemCategory6.rvListCategory.visibility = View.GONE
//                        itemCategory9.rvListCategory.visibility = View.GONE
//                    }
//                    if (it.name == "Women"){
//                        Log.e("TAG", "onViewCreated: Fragment PositionELSEB : ${it.name}")
//                        itemCategory5.rvListCategory.visibility = View.VISIBLE
//                        itemCategory6.rvListCategory.visibility = View.VISIBLE
//                        itemCategory9.rvListCategory.visibility = View.VISIBLE
//                    }
//                    if (it.name == "Kids"){
//                        Log.e("TAG", "onViewCreated: FragmentPositionELSEC : ${it.name}")
//                        itemCategory5.rvListCategory.visibility = View.VISIBLE
//                        itemCategory6.rvListCategory.visibility = View.VISIBLE
//                        itemCategory9.rvListCategory.visibility = View.VISIBLE
//                    }
//                }
            }
        }
//        adapter2 = CategoryChildTabAdapter()

        viewModel.subCategoryAdapter1.notifyDataSetChanged()
        viewModel.subCategoryAdapter2.notifyDataSetChanged()
        viewModel.subCategoryAdapter3.notifyDataSetChanged()
        viewModel.subCategoryAdapter4.notifyDataSetChanged()
        viewModel.subCategoryAdapter5.notifyDataSetChanged()
        viewModel.subCategoryAdapter6.notifyDataSetChanged()
        viewModel.subCategoryAdapter7.notifyDataSetChanged()
        viewModel.subCategoryAdapter8.notifyDataSetChanged()
        viewModel.subCategoryAdapter9.notifyDataSetChanged()
        viewModel.subCategoryAdapter10.notifyDataSetChanged()
    }



}