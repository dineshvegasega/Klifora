package com.klifora.shop.ui.onBoarding.forgotPassword

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.ForgotPasswordBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.saveData
import com.klifora.shop.networking.password
import com.klifora.shop.networking.username
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ForgotPassword : Fragment() {
    private val viewModel: ForgotPasswordVM by viewModels()
    private var _binding: ForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForgotPasswordBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()!!.callBack(0)

        binding.apply {
            includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            btResetPassword.singleClick {
                Log.e("TAG", "count: $this")
                val adminJSON: JSONObject = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                if (editTextMobileNumber.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else if (editTextMobileNumber.text.toString().length != 10) {
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else if (editTextOtp.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterValidOTP))
                }  else {
                    viewModel.adminToken(adminJSON) {
                        val adminToken = this
                        saveData(ADMIN_TOKEN, adminToken)
                        Log.e("TAG", "ADMIN_TOKENAAAA: " + adminToken)
                        readData(ADMIN_TOKEN) {
                            val customerJSON: JSONObject = JSONObject().apply {
                                put("emailmobile", editTextMobileNumber.text.toString())
                            }
                            viewModel.websiteUrl(it.toString(), customerJSON) {
                                Log.e("TAG", "itAAA " + this)
//                                val website_id = JSONObject(this).getString("website_id")
                                saveData(WEBSITE_ID, this.website_id)
                                storeWebUrl = this.website_id
                                Log.e("TAG", "websiteUrlAAAA: " + storeWebUrl)

                                val sendOtpJSON: JSONObject = JSONObject().apply {
                                    put("mobilenumber", editTextMobileNumber.text.toString())
                                    put("otptype", "forgot")
                                    put("otpcode", editTextOtp.text.toString())
                                    put("oldmobile", "")
                                }
                                viewModel.verifyOTP(it.toString(), sendOtpJSON) {
                                    Log.e("TAG", "itAAA " + this)
//                                    val website_id = JSONObject(this).getString("website_id")
//                                    saveData(WEBSITE_ID, website_id)
//                                    storeWebUrl = website_id
//                                    Log.e("TAG", "websiteUrlAAAA: " + storeWebUrl)
                                    findNavController().navigate(
                                        R.id.action_forgotPassword_to_resetPassword,
                                        Bundle().apply {
                                            putString("mobileNumber", editTextMobileNumber.text.toString())
                                        })
                                }
                            }
                        }
                    }
                }
            }




            editTextMobileNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (editTextMobileNumber.text.toString().length == 10) {
                        textTitleRequestOTP.isEnabled = true
                        textTitleRequestOTP.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._E2B476
                            )
                        )
                    } else {
                        textTitleRequestOTP.isEnabled = false
                        textTitleRequestOTP.setTextColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._C08D54
                            )
                        )
                    }
                }
            })


            textTitleRequestOTP.singleClick {
                Log.e("TAG", "count: $this")
                val adminJSON: JSONObject = JSONObject().apply {
                    put("username", username)
                    put("password", password)
                }
                if (editTextMobileNumber.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else if (editTextMobileNumber.text.toString().length != 10) {
                    showSnackBar(getString(R.string.EnterValidMobileNumber))
                } else {
                    viewModel.adminToken(adminJSON) {
                        val adminToken = this
                        saveData(ADMIN_TOKEN, adminToken)
                        Log.e("TAG", "ADMIN_TOKENAAAA: " + adminToken)
                        readData(ADMIN_TOKEN) {
                            val customerJSON: JSONObject = JSONObject().apply {
                                put("emailmobile", editTextMobileNumber.text.toString())
                            }
                            viewModel.websiteUrl(it.toString(), customerJSON) {
                                Log.e("TAG", "itAAA " + this)
//                                val website_id = JSONObject(this).getString("website_id")
                                saveData(WEBSITE_ID, this.website_id)
                                storeWebUrl = this.website_id
                                Log.e("TAG", "websiteUrlAAAA: " + storeWebUrl)

                                val sendOtpJSON: JSONObject = JSONObject().apply {
                                    put("mobilenumber", editTextMobileNumber.text.toString())
                                    put("otptype", "forgot")
                                    put("resendotp", "")
                                    put("oldmobile", "")
                                }
                                viewModel.sendOTP(it.toString(), sendOtpJSON) {
                                    Log.e("TAG", "itAAA " + this)
//                                    val website_id = JSONObject(this).getString("website_id")
//                                    saveData(WEBSITE_ID, website_id)
//                                    storeWebUrl = website_id
//                                    Log.e("TAG", "websiteUrlAAAA: " + storeWebUrl)
                                }
                            }
                        }
                    }
                }
            }




            editTextOtp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (editTextOtp.text.toString().length == 6) {
                        btResetPassword.isEnabled = true
                        btResetPassword.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._E2B476
                                )
                            )
                    } else {
                        btResetPassword.isEnabled = false
                        btResetPassword.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._C08D54
                                )
                            )
                    }
                }
            })
        }

    }
}