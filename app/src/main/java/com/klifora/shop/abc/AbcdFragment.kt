package com.klifora.shop.abc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.klifora.shop.databinding.AbcBinding
import com.klifora.shop.databinding.ThankyouBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.category.ChildrenData
import com.klifora.shop.models.category.ChildrenDataX
import com.klifora.shop.ui.main.thankYou.ThankYouVM
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.mainCategory
import com.klifora.shop.utils.mainThread
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AbcdFragment : Fragment() {
    private val viewModel: ABCVM by viewModels()
    private var _binding: AbcBinding? = null
    private val binding get() = _binding!!


    companion object {
        var monthListParent: ArrayList<ChildrenData> = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AbcBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            monthListParent.clear()
            readData(ADMIN_TOKEN) { tokenC ->
                viewModel.getCategories(tokenC!!){
                    Log.e("TAG", "getCategories "+it.toString())
                    rvList1.setHasFixedSize(true)
                    rvList1.adapter = viewModel.categoryAdapter
                    viewModel.categoryAdapter.notifyDataSetChanged()

                    val monthList: List<ChildrenData> = it.children_data.filter { s -> s.is_active == true }

//                monthListParent = monthList

                    viewModel.categoryAdapter.submitList(monthListParent)



                    rvList2.setHasFixedSize(true)


                    mainThread {
                        monthList.forEach {

                            val parent = it
//                    parent.children_data.clear()

//                    var child = it


                            val child = ChildrenData()
                            child.id = parent.id
                            child.is_active = parent.is_active
                            child.level = parent.level
                            child.name = parent.name
                            child.parent_id = parent.parent_id
                            child.position = parent.position
                            child.product_count = parent.product_count
                            child.isSelected = parent.isSelected
                            child.isCollapse = parent.isCollapse
                            child.isAll = parent.isAll




                            if (parent.children_data.size > 0){
                                val childArray: ArrayList<ChildrenDataX> = ArrayList()

                                val childAll = ChildrenDataX()
                                childAll.name = "All"
                                childAll.isAll = true
                                childAll.is_active = true
                                child.parent_id = monthList[0]?.parent_id ?: 0
                                childArray.add(childAll)

                                parent.children_data.forEach {
                                    if (it.is_active){
                                        childArray.add(it)
                                    }
                                }

                                child.children_data = childArray

                            }


                            monthListParent.add(child)

                        }

                        Log.e("TAG", "monthListParent "+monthListParent.toString())

                        rvList2.adapter = viewModel.categoryAdapter2
                        viewModel.categoryAdapter2.notifyDataSetChanged()
                        viewModel.categoryAdapter2.submitList(monthListParent)
                        viewModel.categoryAdapter2.notifyDataSetChanged()
                    }


//                viewModel.categoryAdapter2.submitList(monthList)


//                monthList.forEach {
//                    val child = ChildrenDataX()
//                       child.name = "All"
//                    it.children_data.add(ChildrenDataX())
//                }



//                val monthList: List<ChildrenData> = it.children_data.filter { s -> s.is_active == true }


//                val parentArray: ArrayList<ChildrenData> = ArrayList()
//                monthList.forEach {
//                    val childArray: ArrayList<ChildrenDataX> = ArrayList()
//
//
////                    it.children_data.forEach {
////                       val child = ChildrenDataX()
////                       child.name = "All"
//////                        childArray.add(it)
////                    }
//
//                    parentArray.add(it)
//                }

//                val monthList2: ArrayList<ChildrenDataX> = ArrayList()
//                    monthList2.forEach {
//                        monthList.add(it)
//                    }



//                it.children_data.forEach {
//
//                    it.children_data.
//
//
////                    it.children_data.forEach {
////                        it.apply {
////                            isSelected = false
////                            isCollapse = false
////                            isAll = false
////                            name = "All"
////                        }
////                    }
//                }

//                if(it.children_data.size  > 0 ){
//
//                }





                }
            }


        }


    }

}