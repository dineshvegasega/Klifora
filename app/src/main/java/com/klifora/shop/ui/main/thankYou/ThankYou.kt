package com.klifora.shop.ui.main.thankYou

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.klifora.shop.R
import com.klifora.shop.databinding.ThankyouBinding
import com.klifora.shop.ui.enums.LoginType
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.ui.mainActivity.MainActivityVM.Companion.loginType
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThankYou : Fragment() {
    private val viewModel: ThankYouVM by viewModels()
    private var _binding: ThankyouBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ThankyouBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = true
        MainActivity.mainActivity.get()!!.callBack(2)


        binding.apply {
            if(arguments?.getString("from") == "customDesign"){
                textTitle2.text = "Your request has been submitted regarding Custom Design. Someone from our team will get in touch with you within 24 hours"
            } else {
                when(loginType){
                    LoginType.VENDOR ->  {
                        var orderID = "" + arguments?.getString("orderID")
                        val text1 = "<font color=#000000>Your order has been successfully placed.<br>Your Order No is - </font> <font color=#003E4D>"+orderID+"</font>"
                        textTitle2.text = HtmlCompat.fromHtml(text1, HtmlCompat.FROM_HTML_MODE_COMPACT)
                    }
                    LoginType.CUSTOMER ->  {
                        textTitle2.text = "Your order has been successfully placed.\n" +
                                "Franchise will contact you within 24hrs"
                    }
                }
            }
            btHome.singleClick {
                findNavController().navigate(R.id.action_thankyou_to_home)
            }
        }
    }
}