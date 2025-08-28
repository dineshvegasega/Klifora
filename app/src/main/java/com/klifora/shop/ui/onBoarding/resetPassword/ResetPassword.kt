package com.klifora.shop.ui.onBoarding.resetPassword

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.ResetPasswordBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_ID
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.saveData
import com.klifora.shop.networking.password
import com.klifora.shop.networking.username
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.storeWebUrl
import com.klifora.shop.utils.isValidPassword
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ResetPassword : Fragment() {
    private val viewModel: ResetPasswordVM by viewModels()
    private var _binding: ResetPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResetPasswordBinding.inflate(inflater)
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

            var counter = 0
            var start: Int
            var end: Int
            imgCreatePassword.singleClick {
                if(counter == 0){
                    counter = 1
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start=editTextNewPassword.getSelectionStart()
                    end=editTextNewPassword.getSelectionEnd()
                    editTextNewPassword.setTransformationMethod(null)
                    editTextNewPassword.setSelection(start,end)
                }else{
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start=editTextNewPassword.getSelectionStart()
                    end=editTextNewPassword.getSelectionEnd()
                    editTextNewPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextNewPassword.setSelection(start,end)
                }
            }



            var counter2 = 0
            var start2: Int
            var end2: Int
            imgConfirmCreatePassword.singleClick {
                if(counter2 == 0){
                    counter2 = 1
                    imgConfirmCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start2=editTextConfirmNewPassword.getSelectionStart()
                    end2=editTextConfirmNewPassword.getSelectionEnd()
                    editTextConfirmNewPassword.setTransformationMethod(null)
                    editTextConfirmNewPassword.setSelection(start2,end2)
                }else{
                    counter2 = 0
                    imgConfirmCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start2=editTextConfirmNewPassword.getSelectionStart()
                    end2=editTextConfirmNewPassword.getSelectionEnd()
                    editTextConfirmNewPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextConfirmNewPassword.setSelection(start2,end2)
                }
            }



            editTextConfirmNewPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!isValidPassword(editTextNewPassword.text.toString().trim())){
                        btLogin.isEnabled = false
                        btLogin.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._C08D54
                                )
                            )
                    } else if(!isValidPassword(editTextConfirmNewPassword.text.toString().trim())){
                        btLogin.isEnabled = false
                        btLogin.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._C08D54
                                )
                            )
                    } else if(editTextNewPassword.text.toString() == editTextConfirmNewPassword.text.toString()){
                        Log.e("TAG", "onTextChanged")
                        btLogin.isEnabled = true
                        btLogin.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._E2B476
                                )
                            )
                    } else {
                        btLogin.isEnabled = false
                        btLogin.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._C08D54
                                )
                            )
                    }
                }
            })


            btLogin.singleClick {
                if (binding.editTextNewPassword.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.EnterNewPassword))
                } else if(binding.editTextNewPassword.text.toString().length >= 0 && binding.editTextNewPassword.text.toString().length < 8){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if(!isValidPassword(editTextNewPassword.text.toString().trim())){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if (binding.editTextConfirmNewPassword.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.EnterConfirmNewPassword))
                } else if(binding.editTextConfirmNewPassword.text.toString().length >= 0 && binding.editTextConfirmNewPassword.text.toString().length < 8){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if(!isValidPassword(editTextConfirmNewPassword.text.toString().trim())){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if(editTextNewPassword.text.toString() != editTextConfirmNewPassword.text.toString()){
                    showSnackBar(getString(R.string.CreatePasswordReEnterPasswordisnotsame))
                } else {
                    Log.e("TAG", "count: $this")

//                    mobileNumber
                    val adminJSON: JSONObject = JSONObject().apply {
                        put("username", username)
                        put("password", password)
                    }
                    viewModel.show()
                    viewModel.adminToken(adminJSON) {
                        val adminToken = this
                        saveData(ADMIN_TOKEN, adminToken)
                        Log.e("TAG", "ADMIN_TOKENAAAA: " + adminToken)
                        readData(ADMIN_TOKEN) {
                            val customerJSON: JSONObject = JSONObject().apply {
                                put("mobilenumber", arguments?.getString("mobileNumber"))
                                put("password", editTextNewPassword.text.toString())
                            }
                            viewModel.websiteUrl(it.toString(), customerJSON) {
                                Log.e("TAG", "itAAA " + this)
//                                val website_id = JSONObject(this).getString("website_id")
                                saveData(WEBSITE_ID, this.website_id)
                                storeWebUrl = this.website_id
                                Log.e("TAG", "websiteUrlAAAA: " + storeWebUrl)

                                viewModel.resetPassword(it.toString(), customerJSON) {
                                    Log.e("TAG", "itAAAc " + this)
                                    viewModel.hide()
                                    val succeess = JSONObject(this).getString("succeess")
                                    if(succeess == "true"){
                                        showSnackBar(JSONObject(this).getString("successmsg"))
                                        findNavController().navigate(R.id.action_resetPassword_to_login)
                                    } else {
                                        showSnackBar(JSONObject(this).getString("successmsg"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}