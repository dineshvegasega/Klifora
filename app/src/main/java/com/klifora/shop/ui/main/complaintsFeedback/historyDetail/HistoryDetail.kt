package com.klifora.shop.ui.main.complaintsFeedback.historyDetail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.klifora.shop.databinding.HistoryDetailBinding
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.utils.PaginationScrollListener
import com.klifora.shop.utils.callPermissionDialog
import com.klifora.shop.utils.getCameraPath
import com.klifora.shop.utils.getMediaFilePathFor
import com.klifora.shop.utils.loadImage
import com.klifora.shop.utils.showOptions
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import com.klifora.shop.R
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.ItemComplaintItem
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.utils.changeDateFormat
import com.klifora.shop.utils.encodeImage
import com.klifora.shop.utils.parcelable
import com.klifora.shop.utils.showSnackBar
import org.json.JSONObject

@AndroidEntryPoint
class HistoryDetail : Fragment() {
    private val viewModel: HistoryDetailVM by viewModels()
    private var _binding: HistoryDetailBinding? = null
    private val binding get() = _binding!!

//    val results : ArrayList<DataX> = ArrayList()

    var logoutAlert : AlertDialog?= null

    private var LOADER_TIME: Long = 500
    private var pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart

    var feedbackId : String = ""
    var status = ""

        @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryDetailBinding.inflate(inflater, container, false)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
//        } else {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        }
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callBack(0)

        val consentIntent = arguments?.parcelable<ItemComplaintItem>("key")
        feedbackId = ""+consentIntent?.ticket_id


//        var feedbackId = "84"
//        putString("ticketid", dataClass.ticket_id)
//        putString("customerid", dataClass.customer_id)

        binding.apply {
            topBarClose.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            textTrackingTxt.text = "Tracking Id: "+feedbackId
//            val title = when(consentIntent?.category_id) {
//                "1" -> {
//                    "Complaint"
//                }
//                "2" -> {
//                    "Feedback"
//                }
//                else -> {
//                    "Complaint"
//                }
//            }
            inclideHistoryType.textTypeValue.text = "Complaint"
            val date =
                consentIntent?.updated_at?.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy")
            inclideHistoryType.textRegistrationDateValue.text = date
            inclideHistoryType.textSubjectValue.text = consentIntent?.subject

            readData(WEBSITE_DATA) { webData ->
                if (webData != null) {
                    val data = Gson().fromJson(
                        webData,
                        ItemWebsite::class.java
                    )
                    viewModel.getComplaintDetail(feedbackId)
                    viewModel.messageHistory(feedbackId, data.entity_id)
                }
            }


            topBarClose.btClose.visibility = View.INVISIBLE
            viewModel.itemComplaintDetail?.observe(viewLifecycleOwner) {
                if (it.size > 0) {
                    if (it[0].status_id == "1") {
                        vBottom.visibility = View.VISIBLE
                        topBarClose.btClose.text = requireContext().getString(R.string.x_close)
                        topBarClose.btClose.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color._ED2525)
                    } else if (it[0].status_id == "0") {
                        vBottom.visibility = View.GONE
                        topBarClose.btClose.text = requireContext().getString(R.string.reopen)
                        topBarClose.btClose.backgroundTintList = ContextCompat.getColorStateList(binding.root.context, R.color._2eb82e)
                    }
                }
            }






//            inclideHeaderSearch.textHeaderTxt.text = HtmlCompat.fromHtml(getString(R.string.trackingId, "<b>"+feedbackId+"</b>"), HtmlCompat.FROM_HTML_MODE_LEGACY);
//         //   "Tracking Id: #12344682"
//            val typeface: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium)
//            inclideHeaderSearch.textHeaderTxt.typeface = typeface
//            inclideHeaderSearch.btClose.visibility = View.VISIBLE
//            inclideHeaderSearch.editTextSearch.visibility = View.GONE
//
//
//            inclideHeaderSearch.btClose.singleClick {
//                var msg = ""
//                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart(user_type, USER_TYPE)
//                if (status == "resolved"){
//                    requestBody.addFormDataPart("status", "re-open")
//                    msg = view.resources.getString(R.string.open_conversation)
//                } else if (status == "re-open"){
//                    requestBody.addFormDataPart(status, "resolved")
//                    msg = view.resources.getString(R.string.close_conversation)
//                } else if (status == "Pending" || status == "pending"){
//                    requestBody.addFormDataPart(status, "resolved")
//                    msg = view.resources.getString(R.string.close_conversation)
//                } else if (status == "in-progress"){
//                    requestBody.addFormDataPart(status, "resolved")
//                    msg = view.resources.getString(R.string.close_conversation)
//                } else if (status == "Closed"){
//                    requestBody.addFormDataPart(status, "re-open")
//                    msg = view.resources.getString(R.string.open_conversation)
//                }
//
//                if(logoutAlert?.isShowing == true) {
//                    return@singleClick
//                }
//                logoutAlert = MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
//                    .setTitle(resources.getString(R.string.app_name))
//                    .setMessage(msg)
//                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
//                        dialog.dismiss()
//                        readData(LOGIN_DATA) { loginUser ->
//                            if (loginUser != null) {
//                                val user = Gson().fromJson(loginUser, Login::class.java)
//                                requestBody.addFormDataPart(user_id, ""+user?.id)
//                                requestBody.addFormDataPart(feedback_id, ""+feedbackId)
//                                requestBody.addFormDataPart(media, "null")
//                                if(networkFailed) {
//                                    viewModel.addFeedbackConversationDetails(requestBody.build())
//                                } else {
//                                    requireContext().callNetworkDialog()
//                                }
//                            }
//                        }
//                    }
//                    .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .setCancelable(false)
//                    .show()
//            }




//            viewModel.adapter.submitList(results)
//            viewModel.adapter.notifyDataSetChanged()
            recyclerView.adapter=viewModel.adapter

            viewModel.messageHistory?.observe(viewLifecycleOwner) {
                viewModel.adapter.submitList(it)
                viewModel.adapter.notifyDataSetChanged()
                recyclerView.postDelayed({
                    (recyclerView.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset( (it.size
                        ?: 0) - 1, 0)
                    viewModel.adapter.notifyDataSetChanged()
                }, 50)
            }


            ivAttach.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }

            ivSend.singleClick {
//                if(editTextSelectYourChoice.text.toString().isEmpty()){
//                    if (viewModel.type == "1"){
//                        showSnackBar(getString(R.string.subject_of_complaint))
//                    } else if (viewModel.type == "2"){
//                        showSnackBar(getString(R.string.subject_of_feedback))
//                    }
//                } else if (editTextSubjectOfComplaint.text.toString().isEmpty()){
//                    showSnackBar(getString(R.string.enter_subject))
//                } else if (viewModel.priorityType.isEmpty()){
//                    showSnackBar(getString(R.string.select_priority_type))
//                } else if (editTextSelectOrder.text.toString().isEmpty()){
//                    showSnackBar(getString(R.string.select_order))
//                } else if (viewModel.idsArray.size == 0){
//                    showSnackBar(getString(R.string.select_products))
//                } else if (editTextYourName.text.toString().isEmpty()){
//                    showSnackBar(getString(R.string.your_full_name))
//                } else if (editTextYourMobileNumber.text.toString().isEmpty() || editTextYourMobileNumber.text.toString().length != 10){
//                    showSnackBar(getString(R.string.your_mobile_number))
//                } else
                if (etTypingMessage.text!!.isEmpty() && viewModel.uploadMediaImageBase64.isEmpty()){
                        showSnackBar(getString(R.string.enter_message))
                } else {
                    readData(WEBSITE_DATA) { webData ->
                        if (webData != null) {
                            val data = Gson().fromJson(
                                webData,
                                ItemWebsite::class.java
                            )

                            val jsonObjectStatus = JSONObject().apply {
                                put("file", viewModel.uploadMediaImageBase64)
                                put("customer_name", data?.name)
                                put("customer_email", data?.email)
                                put("customer_id",  data?.entity_id)
                                put("email", data?.email)
                                put("category_id", "1")
                                put("body", etTypingMessage.text.toString())
                                put("ticket_id",  feedbackId)
                                put("is_read",  "0")
                            }
                            viewModel.sendMessage(jsonObjectStatus){
                                viewModel.uploadMediaImage = null
                                viewModel.uploadMediaImageBase64 = ""
                                binding.relative1.visibility = View.GONE
                                binding.etTypingMessage.setText("")
                                viewModel.messageHistory(feedbackId, data.entity_id)
                            }
                        }
                    }
                }
            }


            btnImageShopImage.singleClick {
                viewModel.uploadMediaImage = null
                viewModel.uploadMediaImageBase64 = ""
                binding.relative1.visibility = View.GONE
            }
//
//            ivSend.singleClick {
//                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart(user_type, USER_TYPE)
//                if (!etTypingMessage.text!!.isEmpty() || viewModel.uploadMediaImage != null){
//                    readData(LOGIN_DATA) { loginUser ->
//                        if (loginUser != null) {
//                            val user = Gson().fromJson(loginUser, Login::class.java)
//                            requestBody.addFormDataPart(user_id, ""+user?.id)
//                            requestBody.addFormDataPart(feedback_id, ""+feedbackId)
//                            requestBody.addFormDataPart(reply, ""+etTypingMessage.text.toString())
//                            requestBody.addFormDataPart(status, "in-progress")
//                            if(viewModel.uploadMediaImage != null){
//                                requestBody.addFormDataPart(
//                                    media,
//                                    File(viewModel.uploadMediaImage!!).name,
//                                    File(viewModel.uploadMediaImage!!).asRequestBody("image/*".toMediaTypeOrNull())
//                                )
//                            }
//                            if(networkFailed) {
//                                viewModel.addFeedbackConversationDetails(requestBody.build())
//                                etTypingMessage.setText("")
//                                viewModel.uploadMediaImage = null
//                                binding.relative1.visibility = View.GONE
//                            } else {
//                                requireContext().callNetworkDialog()
//                            }
//
//                        }
//                    }
//                } else {
//                    showSnackBar(view.resources.getString(R.string.Pleaseentertextormedia))
//                }
//            }
        }

//        loadFirstPage()
//        observerDataRequest()
//        recyclerViewScroll()
    }



    private fun recyclerViewScroll() {
        binding.apply {
            recyclerView.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    isLoading = true
                    currentPage += 1
                    if(totalPages >= currentPage){
                        Handler(Looper.myLooper()!!).postDelayed({
//                                loadNextPage()
                        }, LOADER_TIME)
                    }
                }
                override fun getTotalPageCount(): Int {
                    return totalPages
                }
                override fun isLastPage(): Boolean {
                    return isLastPage
                }
                override fun isLoading(): Boolean {
                    return isLoading
                }
            })
        }
    }



//    private fun loadFirstPage() {
//        pageStart  = 1
//        isLoading = false
//        isLastPage = false
//        totalPages  = 1
//        currentPage  = pageStart
//        results.clear()
//        if(requireContext().isNetworkAvailable()) {
//            viewModel.feedbackConversationDetails(""+feedbackId , ""+currentPage)
//            binding.idNetworkNotFound.root.visibility = View.GONE
//        } else {
////            requireContext().callNetworkDialog()
//            binding.idNetworkNotFound.root.visibility = View.VISIBLE
//        }
//    }
//
//
//    fun loadNextPage() {
//            if(requireContext().isNetworkAvailable()) {
//                viewModel.feedbackConversationDetailsSecond(""+feedbackId , ""+currentPage)
//            } else {
//                requireContext().callNetworkDialog()
//            }
//    }


//    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
//    private fun observerDataRequest(){
//        viewModel.addFeedbackConversationLive.observe(requireActivity()) {
//            if(totalPages == 1){
//                    loadFirstPage()
//            } else {
//                    loadNextPage()
//            }
//        }
//
//
//
//        viewModel.feedbackConversationLive.observe(requireActivity()) {
//            binding.apply {
//                val complaintfeedback = if (it.type == "complaint") {
//                    requireContext().getString(R.string.complaint)
//                } else {
//                    requireContext().getString(R.string.feedback)
//                }
//                inclideHistoryType.textTypeValue.text = complaintfeedback
//                inclideHistoryType.textRegistrationDateValue.text =
//                    "${it.registration_date.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"
//                inclideHistoryType.textSubjectValue.text = it.subject
//                status = it.status
//
//                if (status == "resolved") {
//                    vBottom.visibility = View.GONE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.re_open)
//                    inclideHeaderSearch.btClose.icon =
//                        ContextCompat.getDrawable(root.context, R.drawable.check)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._138808)
//                } else if (status == "re-open") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else if (status == "Pending" || status == "pending") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else if (status == "in-progress") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else if (status == "Closed") {
//                    vBottom.visibility = View.GONE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.re_open)
//                    inclideHeaderSearch.btClose.icon =
//                        ContextCompat.getDrawable(root.context, R.drawable.check)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._138808)
//                } else {
//                    vBottom.visibility = View.GONE
//                }
//            }
//
//
//                var old = ""
//                it.data.data.map {
//                    val date = it.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")
//                    if (old != date){
//                        old = date!!
//                        it.dateShow = true
//                    }
//                }
//
//
//
//
//            results.add(DataX(it.media, it.message, it.registration_date , "in-progress" , it.user_id, USER_TYPE, false))
//
////            resultsFirst.clear()
////            resultsFirst.addAll(it.data.data)
//            results.addAll(it.data.data)
//
////            if(results.size == 0){
////                results.addAll(it.data.data)
////            }
//            viewModel.adapter.submitList(results)
//
//            totalPages = it.data?.total!! / it.data?.per_page!!
//            val reminder = it.data?.total!! % it.data?.per_page!!
//            if(reminder != 0){
//                totalPages += 1
//            }
//            if (currentPage == totalPages) {
//                viewModel.adapter.removeLoadingFooter()
//            } else if (currentPage <= totalPages) {
//                viewModel.adapter.addLoadingFooter()
//                isLastPage = false
//            } else {
//                isLastPage = true
//            }
//
////            if (viewModel.adapter.itemCount > 0) {
////                binding.idDataNotFound.root.visibility = View.GONE
////            } else {
////                binding.idDataNotFound.root.visibility = View.VISIBLE
////            }
//            binding.apply {
//                recyclerView.postDelayed({
//                    (recyclerView.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset( (results.size
//                        ?: 0) - 1, 0)
//                    viewModel.adapter.notifyDataSetChanged()
//                }, 50)
//            }
//        }
//
//
//        viewModel.feedbackConversationLiveSecond.observe(requireActivity()) {
//            binding.apply {
//                val complaintfeedback = if (it.type == "complaint") {
//                    requireContext().getString(R.string.complaint)
//                } else {
//                    requireContext().getString(R.string.feedback)
//                }
//                inclideHistoryType.textTypeValue.text = complaintfeedback
//                inclideHistoryType.textRegistrationDateValue.text =
//                    "${it.registration_date.changeDateFormat("yyyy-MM-dd", "dd MMM yyyy")}"
//                inclideHistoryType.textSubjectValue.text = it.subject
//                status = it.status
//
//                if (status == "resolved") {
//                    vBottom.visibility = View.GONE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.re_open)
//                    inclideHeaderSearch.btClose.icon =
//                        ContextCompat.getDrawable(root.context, R.drawable.check)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._138808)
//                } else if (status == "re-open") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else if (status == "Pending" || status == "pending") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else if (status == "in-progress") {
//                    vBottom.visibility = View.VISIBLE
//                    inclideHeaderSearch.btClose.text = requireContext().getString(R.string.x_close)
//                    inclideHeaderSearch.btClose.backgroundTintList =
//                        ContextCompat.getColorStateList(root.context, R.color._ED2525)
//                } else {
//                    vBottom.visibility = View.GONE
//                }
//            }
//
//            var old = ""
//            it.data.data.map {
//                val date = it.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")
//                if (old != date){
//                    old = date!!
//                    it.dateShow = true
//                }
//            }
//
//            it.data.data.map { _id ->
//                if (!Gson().toJson(results.toString()).contains(_id.reply_date.toString())){
//                    results.add(_id)
//                }
//            }
//
////            resultsTemp.clear()
//            //results.addAll(it.data.data)
////            Log.e("TAG","xxxx "+resultsFirst.size)
////            Log.e("TAG","yyyy "+resultsTemp.size)
//
////            results.addAll(resultsFirst)
////            results.addAll(resultsTemp)
//
////            Handler(Looper.myLooper()!!).postDelayed({
////                resultsTemp.clear()
////            }, 50)
////            Handler(Looper.myLooper()!!).postDelayed({
////                resultsTemp.addAll(it.data.data)
////            }, 50)
////            Handler(Looper.myLooper()!!).postDelayed({
////                results.addAll(resultsTemp)
////            }, 50)
//
//            viewModel.adapter.removeLoadingFooter()
//            isLoading = false
//            viewModel.adapter.submitList(results)
//
//
//            totalPages = it.data?.total!! / it.data?.per_page!!
//            val reminder = it.data?.total!! % it.data?.per_page!!
//            if(reminder != 0){
//                totalPages += 1
//            }
//            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
//            else isLastPage = true
//
//                binding.apply {
//                recyclerView.postDelayed({
//                    (recyclerView.getLayoutManager() as LinearLayoutManager).scrollToPositionWithOffset( (results.size
//                        ?: 0) - 1, 0)
//                    viewModel.adapter.notifyDataSetChanged()
//                }, 50)
//            }
//        }
//    }






    var imagePosition = 0
    private var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        lifecycleScope.launch {
            if (uri != null) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                        viewModel.uploadMediaImage = compressedImageFile.path
                        binding.ivImageImage.loadImage(type = 1, url = { viewModel.uploadMediaImage!! })
                        binding.relative1.visibility = View.VISIBLE
                        val encodedUrl = encodeImage(compressedImageFile.path)
                        Log.e("TAG", "encodedUrlB "+encodedUrl)
                        viewModel.uploadMediaImageBase64 = encodedUrl
                    }
                }
            }
        }
    }



    var uriReal : Uri?= null
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                        viewModel.uploadMediaImage = compressedImageFile.path
                        binding.ivImageImage.loadImage(type = 1, url = { viewModel.uploadMediaImage!! })
                        binding.relative1.visibility = View.VISIBLE
                        val encodedUrl = encodeImage(compressedImageFile.path)
                        Log.e("TAG", "encodedUrlB "+encodedUrl)
                        viewModel.uploadMediaImageBase64 = encodedUrl
                    }
                }
            }
        }
    }



    private fun callMediaPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            )
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else{
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }


    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                requireActivity().showOptions {
                    when(this){
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }





    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }



    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }




    override fun onDestroyView() {
//        _binding = null
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onDestroyView()
    }

}