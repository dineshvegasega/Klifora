package com.klifora.shop.ui.main.searchOrder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.SearchOrderBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.searchOrder.Item
import com.klifora.shop.models.searchOrder.ItemSearch
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchOrder : Fragment() {
    private val viewModel: SearchOrderVM by viewModels()
    private var _binding: SearchOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchOrderBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callBack(0)
//        loginType = LoginType.CUSTOMER
        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            topBarBack.ivCartLayout.visibility = View.GONE
            ivEditSearch.setHint(resources.getString(R.string.enterOrderNumber))

//            val userList = listOf(
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//                SearchModel(_id = 1, search_name = "Search1", currentTime = System.currentTimeMillis()),
//            )



//            rvList.setHasFixedSize(true)
//            rvList.adapter = viewModel.searchOrderAdapter
//            viewModel.searchOrderAdapter.notifyDataSetChanged()
//            viewModel.searchOrderAdapter.submitList(userList)
//            rvList.visibility = View.VISIBLE


            viewModel.orderHistoryBooleanMutableLiveData.observe(viewLifecycleOwner) { check ->
                Log.e("TAG", "statusErrorAA: " + check)

                val items: ArrayList<ItemSearch> = ArrayList()

//                if (viewModel.orderHistoryBooleanMutableLiveData){
                if (viewModel.orderHistoryBooleanMutableLiveData.value == true && viewModel.orderHistoryListMutableLiveData.value != null){
                    items.add(viewModel.orderHistoryListMutableLiveData.value!!)
                    idDataNotFound!!.root.visibility = View.GONE
                } else {
                    idDataNotFound!!.root.visibility = View.VISIBLE
                }

//                    viewModel.searchOrderAdapter.submitList(items)
//                } else {
//                    items.clear()
//                    viewModel.searchOrderAdapter.submitList(items)
//                }

                viewModel.searchOrderAdapter.submitList(items)
                viewModel.searchOrderAdapter.notifyDataSetChanged()

                rvList.setHasFixedSize(true)
                rvList.adapter = viewModel.searchOrderAdapter





//                if(orderHistoryMutable?.size!! > 0){
//                    viewModel.searchOrderAdapter.notifyDataSetChanged()
//                        viewModel.searchOrderAdapter.submitList(orderHistoryMutable)
//                        rvList.setHasFixedSize(true)
//                        rvList.adapter = viewModel.searchOrderAdapter
//                } else {
//                    showSnackBar("No result")
//                    viewModel.searchOrderAdapter.notifyDataSetChanged()
//                    viewModel.searchOrderAdapter.submitList(orderHistoryMutable)
//                    rvList.setHasFixedSize(true)
//                    rvList.adapter = viewModel.searchOrderAdapter
//                }


//                val userList = mutableListOf<ItemGuestOrderList>()
//                if (orderHistoryMutable != null){
//                    userList.add(SearchOrderModel(
//                        orderNo = ""+orderHistoryMutable.entity_id,
//                        noofItems = orderHistoryMutable.items.size,
//                        dateTime = orderHistoryMutable.created_at,
//                        base_subtotal = orderHistoryMutable.base_subtotal))
//                        viewModel.searchOrderAdapter.notifyDataSetChanged()
//                        viewModel.searchOrderAdapter.submitList(userList)
//                        rvList.setHasFixedSize(true)
//                        rvList.adapter = viewModel.searchOrderAdapter
//                } else{
//                    userList.clear()
//                    viewModel.searchOrderAdapter.notifyDataSetChanged()
//                    viewModel.searchOrderAdapter.submitList(userList)
//                    rvList.setHasFixedSize(true)
//                    rvList.adapter = viewModel.searchOrderAdapter
//                    showSnackBar("No result")
//                }
            }

                ivEditSearch.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        readData(ADMIN_TOKEN) { token ->
//                            viewModel.orderHistoryListDetail(token.toString(), ivEditSearch.text.toString())

                            viewModel.guestOrderList(ivEditSearch.text.toString())

//                            viewModel.orderHistoryListDetail(token.toString(), ivEditSearch.text.toString()) {
//                                val userList = mutableListOf<SearchOrderModel>()
////                                if (orderHistoryMutable == true){
//                                    val itemOrderDetail = this
//                                    Log.e("TAG", "itemOrderDetail "+itemOrderDetail.created_at)
//                                    userList.add(SearchOrderModel(
//                                        orderNo = ""+itemOrderDetail.entity_id,
//                                        noofItems = itemOrderDetail.items.size,
//                                        dateTime = itemOrderDetail.created_at,
//                                        base_subtotal = itemOrderDetail.base_subtotal,
//                                    ))
//                                    viewModel.searchOrderAdapter.notifyDataSetChanged()
//                                    viewModel.searchOrderAdapter.submitList(userList)
//                                    rvList.setHasFixedSize(true)
//                                    rvList.adapter = viewModel.searchOrderAdapter
////                                } else {
////                                    userList.clear()
////                                    viewModel.searchOrderAdapter.notifyDataSetChanged()
////                                    viewModel.searchOrderAdapter.submitList(userList)
////                                    rvList.setHasFixedSize(true)
////                                    rvList.adapter = viewModel.searchOrderAdapter
////                                }
//
//                            }
                        }
                    }
                    true
//                }
            }
        }

    }
}




data class SearchOrderModel (
    val orderNo: String = "",
    val noofItems: Int = 0,
    val dateTime: String = "",
    val base_subtotal: Double = 0.0,

)