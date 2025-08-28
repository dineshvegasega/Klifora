package com.klifora.shop.ui.main.complaintsFeedback.history

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.HistoryBinding
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class History : Fragment() {
    private val viewModel: HistoryVM by viewModels()
    private var _binding: HistoryBinding? = null
    private val binding get() = _binding!!

    companion object{
        var isReadComplaintFeedback: Boolean? = false
    }


    private var LOADER_TIME: Long = 500
    private var pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callBack(0)
        MainActivity.mainActivity.get()!!.callCartApi()
        isReadComplaintFeedback = true

        binding.apply {
//            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.AllComplaintsFeedback)
//            idDataNotFound.textDesc.text = getString(R.string.currently_no_complaints)

//            loadFirstPage()

            topBarBack.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            topBarBack.ivCart.singleClick {
                findNavController().navigate(R.id.action_history_to_cart)
            }

            cartItemLiveData.value = false
            cartItemLiveData.observe(viewLifecycleOwner) {
                topBarBack.menuBadge.text = "$cartItemCount"
                topBarBack.menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
            }



            selectedButton()

            btComplaint.singleClick {
                readData(WEBSITE_DATA) { webData ->
                    if (webData != null) {
                        val data = Gson().fromJson(
                            webData,
                            ItemWebsite::class.java
                        )
                        Log.e("TAG", "success111:")
                        viewModel.getComplaint(data.entity_id)
                    }
                }
                viewModel.isSelectedButton = 1
                selectedButton()
            }


            btFeedback.singleClick {
                readData(WEBSITE_DATA) { webData ->
                    if (webData != null) {
                        val data = Gson().fromJson(
                            webData,
                            ItemWebsite::class.java
                        )
                        Log.e("TAG", "success111:")
                        viewModel.getFeedback(data.entity_id)
                    }
                }
                viewModel.isSelectedButton = 2
                selectedButton()
            }


            recyclerViewComplaint.setHasFixedSize(true)
            binding.recyclerViewComplaint.adapter = viewModel.complaintAdapter
            binding.recyclerViewComplaint.itemAnimator = DefaultItemAnimator()

            viewModel.itemComplaint?.observe(viewLifecycleOwner) {
                viewModel.complaintAdapter.submitList(it)
                viewModel.complaintAdapter.notifyDataSetChanged()
                if (it.size == 0) {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                    if (viewModel.isSelectedButton == 1){
                        recyclerViewComplaint.visibility = View.INVISIBLE
                        recyclerViewFeedback.visibility = View.INVISIBLE
                    }
                } else {
                    binding.idDataNotFound.root.visibility = View.GONE
                    if (viewModel.isSelectedButton == 1){
                        recyclerViewComplaint.visibility = View.VISIBLE
                        recyclerViewFeedback.visibility = View.INVISIBLE
                    }
                }
            }


            recyclerViewFeedback.setHasFixedSize(true)
            binding.recyclerViewFeedback.adapter = viewModel.feedbackAdapter
            binding.recyclerViewFeedback.itemAnimator = DefaultItemAnimator()

            viewModel.itemFeedback?.observe(viewLifecycleOwner) {
                viewModel.feedbackAdapter.submitList(it)
                viewModel.feedbackAdapter.notifyDataSetChanged()
                if (it.size == 0) {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                    if (viewModel.isSelectedButton == 2){
                        recyclerViewComplaint.visibility = View.INVISIBLE
                        recyclerViewFeedback.visibility = View.INVISIBLE
                    }
                } else {
                    binding.idDataNotFound.root.visibility = View.GONE
                    if (viewModel.isSelectedButton == 2){
                        recyclerViewComplaint.visibility = View.INVISIBLE
                        recyclerViewFeedback.visibility = View.VISIBLE
                    }
                }
            }


                readData(WEBSITE_DATA) { webData ->
                    if (webData != null) {
                        val data = Gson().fromJson(
                            webData,
                            ItemWebsite::class.java
                        )
                        Log.e("TAG", "success111:")
                        viewModel.getComplaint(data.entity_id)
                    }
                }

//            observerDataRequest()

//            recyclerViewScroll()
//
//            searchHandler()



            ivFabIcon.singleClick {
                findNavController().navigate(R.id.action_history_to_createNew,Bundle().apply {
                    putString("from", "history")
                })
            }
        }
    }

    private fun selectedButton() {
        if (viewModel.isSelectedButton == 1){
            binding.btComplaint.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btFeedback.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btComplaint.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btFeedback.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btComplaint.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btFeedback.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
        } else if (viewModel.isSelectedButton == 2){
            binding.btComplaint.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btFeedback.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btComplaint.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btFeedback.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btComplaint.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btFeedback.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
        }
    }


    private fun searchHandler() {
        binding.apply {
//            inclideHeaderSearch.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    loadFirstPage()
//                }
//                true
//            }
//
//            inclideHeaderSearch.editTextSearch.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                }
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                }
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    inclideHeaderSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, if(count >= 1) R.drawable.ic_cross_white else R.drawable.ic_search, 0);
//                }
//            })
//
//            inclideHeaderSearch.editTextSearch.onRightDrawableClicked {
//                it.text.clear()
//                loadFirstPage()
//            }
        }
    }


//    private fun recyclerViewScroll() {
//        binding.apply {
//            recyclerView.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
//                override fun loadMoreItems() {
//                    isLoading = true
//                    currentPage += 1
//                    if(totalPages >= currentPage){
//                        Handler(Looper.myLooper()!!).postDelayed({
////                            loadNextPage()
//                        }, LOADER_TIME)
//                    }
//                }
//                override fun getTotalPageCount(): Int {
//                    return totalPages
//                }
//                override fun isLastPage(): Boolean {
//                    return isLastPage
//                }
//                override fun isLoading(): Boolean {
//                    return isLoading
//                }
//            })
//        }
//    }


//    private fun loadFirstPage() {
//        pageStart  = 1
//        isLoading = false
//        isLastPage = false
//        totalPages  = 1
//        currentPage  = pageStart
//        results.clear()
//        readData(LOGIN_DATA) { loginUser ->
//            if (loginUser != null) {
//                val obj: JSONObject = JSONObject().apply {
//                    put(page, currentPage)
//                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
//                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
//                }
//                if(networkFailed) {
//                    viewModel.history(obj)
//                    binding.idNetworkNotFound.root.visibility = View.GONE
//                } else {
////                    requireContext().callNetworkDialog()
//                    binding.idNetworkNotFound.root.visibility = View.VISIBLE
//                }
//            }
//        }
//    }
//
//    fun loadNextPage() {
//        readData(LOGIN_DATA) { loginUser ->
//            if (loginUser != null) {
//                val obj: JSONObject = JSONObject().apply {
//                    put(page, currentPage)
//                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
//                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
//                }
//                if(networkFailed) {
//                    viewModel.historySecond(obj)
//                } else {
//                    requireContext().callNetworkDialog()
//                }
//            }
//        }
//    }
//
//
//    var results: MutableList<ItemHistory> = ArrayList()
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun observerDataRequest(){
//        viewModel.itemHistory.observe(requireActivity()) {
//            val typeToken = object : TypeToken<List<ItemHistory>>() {}.type
//            val changeValue = Gson().fromJson<List<ItemHistory>>(Gson().toJson(it.data), typeToken)
//            if(results.size == 0){
//                results.addAll(changeValue as MutableList<ItemHistory>)
//            }
//            viewModel.adapter.addAllSearch(results)
//            totalPages = it.meta?.total_pages!!
//            if (currentPage == totalPages) {
//                viewModel.adapter.removeLoadingFooter()
//            } else if (currentPage <= totalPages) {
//                viewModel.adapter.addLoadingFooter()
//                isLastPage = false
//            } else {
//                isLastPage = true
//            }
//
//            if (viewModel.adapter.itemCount > 0) {
//                binding.idDataNotFound.root.visibility = View.GONE
//            } else {
//                binding.idDataNotFound.root.visibility = View.VISIBLE
//            }
//        }
//
//        viewModel.itemHistorySecond.observe(requireActivity()) {
//            val typeToken = object : TypeToken<List<ItemHistory>>() {}.type
//            val changeValue = Gson().fromJson<List<ItemHistory>>(Gson().toJson(it.data), typeToken)
//            results.addAll(changeValue as MutableList<ItemHistory>)
//            viewModel.adapter.removeLoadingFooter()
//            isLoading = false
//            viewModel.adapter.addAllSearch(results)
//            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
//            else isLastPage = true
//        }
//
//
//
//    }



//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
}