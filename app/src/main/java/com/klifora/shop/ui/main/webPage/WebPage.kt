package com.klifora.shop.ui.main.webPage

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.klifora.shop.databinding.WebpageBinding
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.hideValueOff
import com.klifora.shop.ui.mainActivity.MainActivity.Companion.isBackStack
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebPage : Fragment() {
    private var _binding: WebpageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebpageBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isBackStack = false
        hideValueOff = 2
        MainActivity.mainActivity.get()!!.callBack(0)

        binding.apply {
            topBarBack.includeBackButton.apply {
                layoutBack.singleClick {
                    findNavController().navigateUp()
                }
                topBarBack.ivCartLayout.visibility = View.GONE
            }


            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    return super.shouldInterceptRequest(view, request)
                }
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                    //  view?.loadUrl(getString(R.string.url))
//                view?.loadUrl("javascript:docWrite('" + data + "')")
//                 view?.loadUrl("javascript:docWrite2('fsdfasfg')");
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    binding.progressBar.visibility = View.GONE
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.webView.loadUrl(arguments?.getString("url").toString())
            }, 200)

        }
    }

}