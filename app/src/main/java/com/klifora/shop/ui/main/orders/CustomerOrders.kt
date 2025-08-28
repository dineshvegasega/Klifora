package com.klifora.shop.ui.main.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.klifora.shop.databinding.CustomerOrdersBinding
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.Items
import com.klifora.shop.models.user.ItemUserItem
import com.klifora.shop.utils.isLastItemDisplaying
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.internal.StringUtil.isNumeric


@AndroidEntryPoint
class CustomerOrders(
    private val activity: FragmentActivity,
    private val videoPath: Items,
    position: Int
) : Fragment() {
    private var _binding: CustomerOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrdersVM by viewModels()


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


//    var adapter2: CategoryChildTabAdapter ?= null

    var page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CustomerOrdersBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        adapter2 = CategoryChildTabAdapter()

        binding.apply {

                loadData("" , "")


            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val isNumeric = isNumeric(editTextSearch.text.toString())
                    viewModel.itemsCustomerOrders.clear()
                    page = 1
                    if(isNumeric == true){
                        loadData(""+editTextSearch.text.toString() , "")
                    } else {
                        loadData("" , ""+editTextSearch.text.toString())
                    }
                }
                true
            }



            editTextSearch.setOnTouchListener(View.OnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (editTextSearch.getRight() - editTextSearch.getCompoundDrawables()
                            .get(DRAWABLE_RIGHT).getBounds().width())
                    ) {
                        val isNumeric = isNumeric(editTextSearch.text.toString())
                        viewModel.itemsCustomerOrders.clear()
                        page = 1
                        if(isNumeric == true){
                            loadData(""+editTextSearch.text.toString() , "")
                        } else {
                            loadData("" , ""+editTextSearch.text.toString())
                        }
                        return@OnTouchListener true
                    }
                }
                false
            })



            rvListCategory1.setHasFixedSize(true)
            rvListCategory1.adapter = viewModel.customerOrders


            var isLoad = true
            viewModel.itemLiveCustomerOrders.observe(viewLifecycleOwner) {
                if (it.size != 0) {
                    viewModel.itemsCustomerOrders.addAll(it)
                    isLoad = true
                } else {
                    isLoad = false
                }

                idPBLoading.visibility = View.GONE

                Log.e("TAG", "onViewCreatedXXX: "+viewModel.itemsCustomerOrders.size)

                viewModel.customerOrders.submitList(viewModel.itemsCustomerOrders)
                viewModel.customerOrders.notifyDataSetChanged()

                if (viewModel.itemsCustomerOrders.size == 0) {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                } else {
                    binding.idDataNotFound.root.visibility = View.GONE
                }
            }


            rvListCategory1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (rvListCategory1.isLastItemDisplaying()) {
                        if (isLoad){
                            Log.e("TAG", "addOnScrollListener "+dx +"   "+dy)
                            page++
                            idPBLoading.visibility = View.VISIBLE
                            val isNumeric = isNumeric(editTextSearch.text.toString())
                            if(isNumeric == true){
                                loadData(""+editTextSearch.text.toString() , "")
                            } else {
                                loadData("" , ""+editTextSearch.text.toString())
                            }
                        }
                    }
                }
            })

        }
    }



    @SuppressLint("NotifyDataSetChanged")
    fun loadData(mobile : String, name : String) {
        binding.apply {
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(
                        loginUser,
                        ItemUserItem::class.java
                    )

                    viewModel.guestOrderList(data.name, mobile, name, page)
                }
            }
        }
    }



}