package com.klifora.shop.ui.main.search

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.DialogSearchHistoryBinding
import com.klifora.shop.databinding.SearchBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.db.SearchModel
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.db
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemCount
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
import com.klifora.shop.utils.ioThread
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.onRightDrawableClicked
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Search : Fragment() {
    private val viewModel: SearchVM by viewModels()
    private var _binding: SearchBinding? = null
    private val binding get() = _binding!!

    var page: Int = 1

    companion object{
        var itemProductsAdd : Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        MainActivity.mainActivity.get()!!.callBack(0)

        itemProductsAdd = false
        binding.apply {

            topBarSearch.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }

                ivCartLayout.singleClick {
                    findNavController().navigate(R.id.action_search_to_cart)
                }

                viewModel.searchValue.observe(viewLifecycleOwner) {
                    binding.topBarSearch.editTextSearch.setText(it)
                    binding.topBarSearch.editTextSearch.setSelection(binding.topBarSearch.editTextSearch.text!!.length)
                }

                viewModel.searchDelete.observe(viewLifecycleOwner) {
                    if (it) {
                        openSearchHistory()
                    }
                }
                editTextSearch.singleClick {
                    openSearchHistory()
                }



                editTextSearch.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        openSearchHistory()
                    }
                })


                cartItemLiveData.value = false
                cartItemLiveData.observe(viewLifecycleOwner) {
                    menuBadge.text = "$cartItemCount"
                    menuBadge.visibility = if (cartItemCount != 0) View.VISIBLE else View.GONE
                }
            }

//            val iconTypeSearch = when (resources.getInteger(R.integer.layout_value)){
//                1 -> R.drawable.baseline_search_24
//                2 -> R.drawable.baseline_search_32
//                3 -> R.drawable.baseline_search_50
//                else -> R.drawable.baseline_search_24
//            }
//            Log.e("TAG", "onViewCreated: "+resources.getInteger(R.integer.layout_value))
//            topBarSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconTypeSearch, 0)

            setSearchButtons()

            btProduct.singleClick {
                viewModel.searchType = 1
                setSearchButtons()
            }

            btSKU.singleClick {
                viewModel.searchType = 2
                setSearchButtons()
            }

            ivSearch.singleClick {
                if (topBarSearch.editTextSearch.text.toString().isNotEmpty()) {
                    viewModel.itemsSearch.clear()
                    page = 1
                    searchFilter()
                    val newUser = SearchModel(
                        search_name = topBarSearch.editTextSearch.text.toString(),
                        currentTime = System.currentTimeMillis()
                    )
                    ioThread {
                        db?.searchDao()?.insertAll(newUser)
                    }
                }
            }



            rvList2.setHasFixedSize(true)
            rvList2.adapter = viewModel.searchAdapter

           // viewModel.itemProductsAdd.observe(viewLifecycleOwner) {
//                if (it) {
                    viewModel.itemProducts?.observe(viewLifecycleOwner) {
//                        if (it.items.size != 0) {
////                    viewModel.itemsSearch.clear()
////                    idPBLoading.visibility = View.VISIBLE
                            Log.e("TAG", "itemProductsAdd: " + it.items.size)
////
//                        } else {
//
//                        }

                        mainThread {
                            if (itemProductsAdd){
                                viewModel.itemsSearch.addAll(it.items)
                            }
                            idPBLoading.visibility = View.GONE
                            viewModel.searchAdapter.submitList(viewModel.itemsSearch)
                            viewModel.searchAdapter.notifyDataSetChanged()

                            rvListSearchHistory.visibility = View.GONE
                            idNestedSV.visibility = View.VISIBLE

                            viewModel.isListVisible = true

                            if (viewModel.itemsSearch.size == 0) {
                                binding.idDataNotFound.root.visibility = View.VISIBLE
                            } else {
                                binding.idDataNotFound.root.visibility = View.GONE
                            }
                        }


                    }
//                }



//            }


            idNestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // on scroll change we are checking when users scroll as bottom.
                val aa = v.getChildAt(0).measuredHeight - v.measuredHeight

                Log.e("TAG", "scrollYAA $scrollY  ::  $aa")
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++
                    idPBLoading.visibility = View.VISIBLE
//
//                    getDataFromAPI(page)
                    searchFilter()
                }
            })

            if (viewModel.isListVisible) {
                mainThread {
                    idNestedSV.visibility = View.VISIBLE
                    rvListSearchHistory.visibility = View.GONE
                }
            }

        }


        searchHandler()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openSearchHistory() {
        mainThread {
            val userList: List<SearchModel>? = db?.searchDao()?.getAll()
            binding.apply {
                rvListSearchHistory.setHasFixedSize(true)
                rvListSearchHistory.adapter = viewModel.searchHistoryAdapter
                viewModel.searchHistoryAdapter.notifyDataSetChanged()
                viewModel.searchHistoryAdapter.submitList(userList)
                rvListSearchHistory.visibility = View.VISIBLE
//                rvList2.visibility = View.GONE
            }


//            userList?.forEach {
//                Log.e("TAG", "onViewCreated: "+it.search_name + " it.currentTime "+it.currentTime)
//            }
        }
    }

    private fun setSearchButtons() {
        if (viewModel.searchType == 1) {
            binding.topBarSearch.editTextSearch.hint =
                resources.getString(R.string.search_by_product)
            binding.btProduct.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btSKU.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btProduct.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btSKU.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btProduct.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btSKU.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
        } else {
            binding.topBarSearch.editTextSearch.hint = getString(R.string.search_by_sku)
            binding.btProduct.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btSKU.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btProduct.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btSKU.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btProduct.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
            binding.btSKU.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._9F0625))
        }

//        btProduct.setTextColor(ContextCompat.getColor(MainActivity.context.get()!!, R.color.white))
//        btProduct.setBackgroundResource(R.drawable.bg_all_round_black)
//        btSKU.setTextColor(ContextCompat.getColor(MainActivity.context.get()!!, R.color.black))
//        btSKU.setBackgroundResource(R.drawable.bg_top_round_corner)
    }


    private fun searchHandler() {
        binding.apply {
            topBarSearch.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (topBarSearch.editTextSearch.text.toString().isNotEmpty()) {
                        viewModel.itemsSearch.clear()
                        page = 1
                        searchFilter()
                        val newUser = SearchModel(
                            search_name = topBarSearch.editTextSearch.text.toString(),
                            currentTime = System.currentTimeMillis()
                        )
                        ioThread {
                            db?.searchDao()?.insertAll(newUser)
                        }
                    }
                }
                true
            }

            topBarSearch.editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val iconTypeSearch = when (resources.getInteger(R.integer.layout_value)) {
                        1 -> R.drawable.baseline_search_24
                        2 -> R.drawable.baseline_search_32
                        3 -> R.drawable.baseline_search_50
                        else -> R.drawable.baseline_search_24
                    }
                    topBarSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        if (count >= 1) R.drawable.ic_cross_white else iconTypeSearch,
                        0
                    );
                }
            })

            topBarSearch.editTextSearch.onRightDrawableClicked {
                it.text.clear()
//                loadFirstPage()
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun searchFilter() {
        binding.apply {
            itemProductsAdd = false
            Log.e("TAG", "searchFilter: " + itemProductsAdd)
//            if (viewModel.searchAdapter != null) {
//                if (page > viewModel.searchAdapter.getItemCount()) {
//                    idPBLoading.visibility = View.GONE
//                    return
//                }
//            }

            val emptyMap = mutableMapOf<String, String>()
            var count = 0

            var categoryIds: String = ""
            var countFrom1 = 0
            var countFrom2 = 0
            var mainCategoryBoolean = false


            if (viewModel.searchType == 1) {
                emptyMap["searchCriteria[filter_groups][0][filters][" + countFrom1 + "][field]"] =
                    "name"
                emptyMap["searchCriteria[filter_groups][0][filters][" + countFrom1 + "][value]"] =
                    "%" + topBarSearch.editTextSearch.text.toString() + "%"
                emptyMap["searchCriteria[filter_groups][0][filters][" + countFrom1 + "][condition_type]"] =
                    "like"

                countFrom1 = 1
                emptyMap["searchCriteria[filter_groups][" + countFrom1 + "][filters][" + 0 + "][field]"] =
                    "visibility"
                emptyMap["searchCriteria[filter_groups][" + countFrom1 + "][filters][" + 0 + "][value]"] =
                    "4"
                emptyMap["searchCriteria[filter_groups][" + countFrom1 + "][filters][" + 0 + "][condition_type]"] =
                    "eq"

            } else if (viewModel.searchType == 2) {
                emptyMap["searchCriteria[filter_groups][0][filters][" + countFrom1 + "][field]"] =
                    "sku"
                emptyMap["searchCriteria[filter_groups][0][filters][" + countFrom1 + "][value]"] =
                    "" + topBarSearch.editTextSearch.text.toString()
            }

            emptyMap["searchCriteria[currentPage]"] = "" + page
            emptyMap["searchCriteria[pageSize]"] = "10"

            countFrom1 += 1
            emptyMap["searchCriteria[filter_groups][" + countFrom1 + "][filters][" + 0 + "][field]"] =
                "status"
            emptyMap["searchCriteria[filter_groups][" + countFrom1 + "][filters][" + 0 + "][value]"] =
                "1"

            idPBLoading.visibility = View.VISIBLE
            readData(ADMIN_TOKEN) { token ->
                viewModel.getProducts(token.toString(), requireView(), emptyMap, page)
//                viewModel.getProducts(token.toString(), requireView(), emptyMap) {
//                    Log.e("TAG", "itAAA " + this)
//
//                    viewModel.searchAdapter.submitList(this.items)
//                    viewModel.searchAdapter.notifyDataSetChanged()
//
//                    rvListSearchHistory.visibility = View.GONE
//                    rvList2.visibility = View.VISIBLE
//
//                    viewModel.isListVisible = true
//
//                    if(this.items.size == 0){
//                        binding.idDataNotFound.root.visibility = View.VISIBLE
//                    }else{
//                        binding.idDataNotFound.root.visibility = View.GONE
//                    }
//                }
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun openDialog() {
        val dialogBinding = DialogSearchHistoryBinding.inflate(
            requireContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
        )
        val dialog = Dialog(requireContext())
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
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


//        dialogBinding.rvListSearchHistory.setHasFixedSize(true)
//        dialogBinding.rvListSearchHistory.adapter = viewModel.searchHistoryAdapter
//        viewModel.searchHistoryAdapter.notifyDataSetChanged()
//        viewModel.searchHistoryAdapter.submitList(viewModel.item1)

    }


//    override fun onResume() {
//        super.onResume()
//        viewModel.itemProductsAdd.value = false
//    }
//
    override fun onStart() {
        super.onStart()
        itemProductsAdd = false
    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        viewModel.itemProductsAdd.value = false
//    }

}