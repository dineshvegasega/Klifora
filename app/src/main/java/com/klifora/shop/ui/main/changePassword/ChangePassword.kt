package com.klifora.shop.ui.main.changePassword

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
import com.klifora.shop.databinding.ChangePasswordBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.CUSTOMER_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreKeys.MOBILE_NUMBER
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_ID
import com.klifora.shop.datastore.DataStoreUtil.clearDataStore
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.datastore.DataStoreUtil.removeKey
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.utils.isValidPassword
import com.klifora.shop.utils.mainThread
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.json.JSONObject

@AndroidEntryPoint
class ChangePassword : Fragment() {
    private val viewModel: ChangePasswordVM by viewModels()
    private var _binding: ChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChangePasswordBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(2)

        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
                topBarBack.ivCartLayout.visibility = View.GONE
                topBarBack.ivCart.singleClick {
                    findNavController().navigate(R.id.action_changePassword_to_cart)
                }
            }

//            topBar.apply {
//                textViewTitle.visibility = View.VISIBLE
//                ivSearch.visibility = View.VISIBLE
//                ivCart.visibility = View.VISIBLE
//                textViewTitle.text = "Change Password"
//
//                appicon.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        MainActivity.context.get()!!,
//                        R.drawable.baseline_west_24
//                    )
//                )
//
//                appicon.singleClick {
//                    findNavController().navigateUp()
//                }
//
//
//                ivSearch.singleClick {
//                    findNavController().navigate(R.id.action_changePassword_to_search)
//                }
//
//                ivCart.singleClick {
//                    findNavController().navigate(R.id.action_changePassword_to_cart)
//                }
//
//
//                badgeCount.observe(viewLifecycleOwner) {
//                    viewModel.getCartCount(){
//                        Log.e("TAG", "count: $this")
//                        menuBadge.text = "${this}"
//                        menuBadge.visibility = if (this != 0) View.VISIBLE else View.GONE
//                    }
////                    mainThread {
////                        val userList: List<CartModel> ?= db?.cartDao()?.getAll()
////                        var countBadge = 0
////                        userList?.forEach {
////                            countBadge += it.quantity
////                        }
////                        menuBadge.text = "${countBadge}"
////                        menuBadge.visibility = if (countBadge != 0) View.VISIBLE else View.GONE
////                    }
//                }
//            }


            var counter0 = 0
            var start0: Int
            var end0: Int
            imgOldPassword.singleClick {
                if(counter0 == 0){
                    counter0 = 1
                    imgOldPassword.setImageResource(R.drawable.ic_eye_open)
                    start0=editTextOldPassword.getSelectionStart()
                    end0=editTextOldPassword.getSelectionEnd()
                    editTextOldPassword.setTransformationMethod(null)
                    editTextOldPassword.setSelection(start0,end0)
                }else{
                    counter0 = 0
                    imgOldPassword.setImageResource(R.drawable.ic_eye_closed)
                    start0=editTextOldPassword.getSelectionStart()
                    end0=editTextOldPassword.getSelectionEnd()
                    editTextOldPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextOldPassword.setSelection(start0,end0)
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
//                    } else if(editTextNewPassword.text.toString() == editTextConfirmNewPassword.text.toString()){
//                        Log.e("TAG", "onTextChanged")
//                        btLogin.isEnabled = true
//                        btLogin.backgroundTintList =
//                            ColorStateList.valueOf(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color._07FFFC
//                                )
//                            )
                    } else {
                        btLogin.isEnabled = true
                        btLogin.backgroundTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color._E2B476
                                )
                            )
                    }
                }
            })


            btLogin.singleClick {
                if (binding.editTextOldPassword.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.EnterOldPassword))
                } else if(binding.editTextOldPassword.text.toString().length >= 0 && binding.editTextOldPassword.text.toString().length < 8){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if(!isValidPassword(editTextOldPassword.text.toString().trim())){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if (binding.editTextNewPassword.text.toString().isEmpty()){
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
//                    val obj: JSONObject = JSONObject().apply {
//                        put(mobile_number, binding.editTextMobileNumber.text.toString())
//                        put(password, binding.editTextPassword.text.toString())
//                    }
//                    if(networkFailed) {
//                        viewModel.login(view = requireView(), obj)
//                    } else {
//                        requireContext().callNetworkDialog()
//                    }


                    readData(MOBILE_NUMBER) { number ->
                        val customerJSON: JSONObject = JSONObject().apply {
                            put("emailmobile", number)
                            put("mobpassword", binding.editTextOldPassword.text.toString())
                        }
                        readData(ADMIN_TOKEN) {
                            viewModel.customerLoginToken(it.toString(), customerJSON){
                                val customerChangeJSON: JSONObject = JSONObject().apply {
                                    put("mobilenumber", number)
                                    put("password", binding.editTextConfirmNewPassword.text.toString())
                                }
                                viewModel.resetPassword(it.toString(), customerChangeJSON) {
                                    Log.e("TAG", "itAAAc " + this)
                                    val succeess = JSONObject(this).getString("succeess")
                                    if(succeess == "true"){
                                        showSnackBar(JSONObject(this).getString("successmsg"))
                                        removeKey(LOGIN_DATA) {}
                                        removeKey(CUSTOMER_TOKEN) {}
                                        removeKey(WEBSITE_ID) {}
                                        clearDataStore { }
                                        mainThread {
                                            delay(1000)
                                            findNavController().navigate(R.id.action_changePassword_to_login)
                                        }

                                    } else {
                                        showSnackBar(JSONObject(this).getString("successmsg"))
                                    }
                                }
                            }
                        }




//                        updateCart(token!!, jsonCartItem, dataClass.item_id) {
//                            notifyItemChanged(position)
//                            cartMutableList.value = true
//                        }
                    }
                }
            }

        }
    }
}