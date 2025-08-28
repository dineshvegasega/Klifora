package com.klifora.shop.ui.onBoarding.loginOptions

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.LoginOptionsBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.saveData
import com.klifora.shop.networking.password
import com.klifora.shop.networking.username
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class LoginOptions : Fragment() {
    private var _binding: LoginOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginOptionsVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginOptionsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callBack(0)
        binding.apply {

            layoutFranchise.singleClick {
                findNavController().navigate(R.id.action_loginOptions_to_login)
            }

            layoutUser.singleClick {
                val obj: JSONObject = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                viewModel.adminToken(obj) {
                    Log.e("TAG", "ADMIN_TOKENBBBB " + this)
                    saveData(ADMIN_TOKEN, this)
                    loginType = LoginType.CUSTOMER
                    readData(WEBSITE_ID) {
                        storeWebUrl = if (it != null) it.toString() else ""
                        findNavController().navigate(R.id.action_loginOptions_to_home)
                        MainActivity.mainActivity.get()!!.callMenus(2)
                    }
                }
            }
        }
    }
}