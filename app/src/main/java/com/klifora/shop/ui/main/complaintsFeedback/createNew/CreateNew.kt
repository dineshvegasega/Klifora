package com.klifora.shop.ui.main.complaintsFeedback.createNew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.klifora.shop.R
import com.klifora.shop.databinding.CreateNewBinding
import com.klifora.shop.datastore.DataStoreKeys.ADMIN_TOKEN
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.models.myOrdersList.ItemOrdersItem
import com.klifora.shop.ui.main.complaintsFeedback.createNew.CreateNewVM.Companion.type
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.utils.callPermissionDialog
import com.klifora.shop.utils.encodeImage
import com.klifora.shop.utils.getCameraPath
import com.klifora.shop.utils.getMediaFilePathFor
import com.klifora.shop.utils.showDropDownDialog
import com.klifora.shop.utils.showOptions
import com.klifora.shop.utils.showSnackBar
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class CreateNew : Fragment() {
    private val viewModel: CreateNewVM by viewModels()
    private var _binding: CreateNewBinding? = null
    private val binding get() = _binding!!
    var itemOrders: ArrayList<ItemOrdersItem> = ArrayList()

    //    var itemOrders: ItemOrders = ItemOrders()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateNewBinding.inflate(inflater)
        return binding.root
    }
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callBack(0)
        binding.apply {
//            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.complaintsSlashfeedback)
//            inclideHeaderSearch.editTextSearch.visibility = View.GONE

//            btCancel.singleClick {
//               findNavController().navigateUp()
//            }

            topBarClose.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            topBarClose.btClose.visibility = View.INVISIBLE

            readData(WEBSITE_DATA) { webData ->
                if (webData != null) {
                    val data = Gson().fromJson(
                        webData,
                        ItemWebsite::class.java
                    )

                    editTextYourName.setText("" + data.name)
                    editTextYourMobileNumber.setText("" + data.mobile_number)


                    viewModel.orderHistoryList(data.website_id, "", "") {
                        itemOrders.clear()
//                        Log.e("TAG", "success111:"+this.toString())
                        val order_id = arguments?.getString("order_id")
                        if (order_id != null) {
                            for (value in this) {
                                if (order_id == "" + value.entity_id) {
                                    Log.e("TAG", "34" + "thisAA " + value.entity_id)
                                    itemOrders.add(value)
                                }
                            }
                        } else {
                            itemOrders = this
                        }
                    }
                }
            }

            editTextSelectYourChoice.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    editTextSelectYourChoice.setText(name)
                    when (position) {
                        0 -> {
                            type = "1"
                            textSubjectOfComplaintTxt.text =
                                getString(R.string.subject_of_complaint)
                            groupComplaint.visibility = View.VISIBLE
                            groupFeedback.visibility = View.GONE
                        }

                        1 -> {
                            type = "2"
                            textSubjectOfComplaintTxt.text = getString(R.string.subject_of_feedback)
                            groupComplaint.visibility = View.GONE
                            groupFeedback.visibility = View.VISIBLE
                        }
                    }
                }
            }

            editTextSelectSubject.singleClick {
                requireActivity().showDropDownDialog(type = 4) {
                    editTextSelectSubject.setText(name)
                    when (position) {
                        0 -> {
                            viewModel.typeSubject = name
                        }

                        1 -> {
                            viewModel.typeSubject = name
                        }
                    }
                }
            }


            editTextSelectPriorityType.singleClick {
                requireActivity().showDropDownDialog(type = 2) {
                    editTextSelectPriorityType.setText(name)
                    when (position) {
                        0 -> {
                            viewModel.priorityType = "1"
                        }

                        1 -> {
                            viewModel.priorityType = "2"
                        }

                        2 -> {
                            viewModel.priorityType = "3"
                        }
                    }
                }
            }


            editTextSelectOrder.singleClick {
                if (itemOrders.size > 0) {
                    var index = 0
                    val list = arrayOfNulls<String>(itemOrders.size)
                    for (value in itemOrders) {
                        list[index] = value.increment_id
                        index++
                    }

                    requireActivity().showDropDownDialog(type = 3, list) {
                        editTextSelectOrder.setText(name)
                        readData(ADMIN_TOKEN) { token ->
                            viewModel.orderHistoryListDetail(
                                token.toString(),
                                itemOrders[position].entity_id
                            ) {
                                val itemOrderDetail = this
                                itemOrderDetail.apply {
                                    if (type == "1") {
                                        rvListCategory1.setHasFixedSize(true)
                                        viewModel.orderSKUOrderHistory.notifyDataSetChanged()
                                        viewModel.orderSKUOrderHistory.submitList(itemOrderDetail?.items)
                                        rvListCategory1.adapter = viewModel.orderSKUOrderHistory
                                        textOrdersTxt.visibility = View.VISIBLE
                                        rvListCategory1.visibility = View.VISIBLE
                                    } else if (type == "2") {
                                        rvListCategory1.setHasFixedSize(true)
                                        viewModel.orderSKUOrderHistoryFeedback.notifyDataSetChanged()
                                        viewModel.orderSKUOrderHistoryFeedback.submitList(
                                            itemOrderDetail?.items
                                        )
                                        rvListCategory1.adapter =
                                            viewModel.orderSKUOrderHistoryFeedback
                                        textOrdersTxt.visibility = View.VISIBLE
                                        rvListCategory1.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                } else {
                    showSnackBar(getString(R.string.not_complaint_type))
                }
            }

            galleryLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                        val imageUri: Uri? = result.data!!.data
                        Toast.makeText(requireContext(), "Image selected successfully! "+imageUri, Toast.LENGTH_SHORT).show()
                    }
                }


            btUploadMedia.singleClick {
//                imagePosition = 1
                callMediaPermissionsWithLocation()
//                openMediaPicker()
            }



            layoutSort.singleClick {
                if (type == "1") {
                    if (editTextSelectYourChoice.text.toString().isEmpty()) {
                        if (type == "1") {
                            showSnackBar(getString(R.string.subject_of_complaint))
                        } else if (type == "2") {
                            showSnackBar(getString(R.string.subject_of_feedback))
                        }
                    } else if (viewModel.typeSubject.isEmpty()) {
                        showSnackBar(getString(R.string.select_subject))
                    } else if (editTextSelectOrder.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.select_order))
                    } else if (viewModel.idsArray.size == 0) {
                        showSnackBar(getString(R.string.select_products))
                    } else if (editTextYourName.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.your_full_name))
                    } else if (editTextYourMobileNumber.text.toString()
                            .isEmpty() || editTextYourMobileNumber.text.toString().length != 10
                    ) {
                        showSnackBar(getString(R.string.your_mobile_number))
                    } else if (editTextTypeHere.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.description))
                    } else {
                        readData(WEBSITE_DATA) { webData ->
                            if (webData != null) {
                                val data = Gson().fromJson(
                                    webData,
                                    ItemWebsite::class.java
                                )

                                val jsonObjectStatus = JSONObject().apply {
                                    put("file", viewModel.uploadMediaImageBase64)
                                    put("name", data?.name)
                                    put("mobile", data?.mobile_number)
                                    put("customerid", data?.entity_id)
                                    put("storename", data?.website_id)
                                    put("discription", editTextTypeHere.text.toString())
                                    put("subject", viewModel.typeSubject)
                                    put("category", "1")
                                    put("priority", viewModel.priorityType)
                                    put("orderid", editTextSelectOrder.text.toString())
                                    put(
                                        "productid",
                                        viewModel.idsArray.joinToString(separator = ",") { it -> "${it}" })
                                }

                                val from = arguments?.getString("from")
                                Log.e("TAG", "from:: "+from)
                                viewModel.createTicket(jsonObjectStatus) {
                                    if (this.contains("Ticket was successfully sent")) {
                                        showSnackBar("Ticket created successfully")

                                        val from = arguments?.getString("from")
                                        if (from == "order"){
                                            findNavController().navigate(R.id.action_createNew_to_history)
                                        } else if (from == "history"){
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                findNavController().navigateUp()
                                            }, 1000)

                                        } else {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                findNavController().navigateUp()
                                            }, 1000)
                                        }

                                    } else {
                                        showSnackBar("Something went wrong!")
                                    }
                                }
                            }
                        }
                    }
                } else if (type == "2") {
                    Log.e("TAG", "viewModel.idsArrayFeedback" + viewModel.idsArrayFeedback)
                    if (editTextSelectYourChoice.text.toString().isEmpty()) {
                        if (type == "1") {
                            showSnackBar(getString(R.string.subject_of_complaint))
                        } else if (type == "2") {
                            showSnackBar(getString(R.string.subject_of_feedback))
                        }
//                    } else if (viewModel.typeSubject.isEmpty()){
//                        showSnackBar(getString(R.string.select_subject))
                    } else if (editTextSelectOrder.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.select_order))
                    } else if (viewModel.idsArrayFeedback.isEmpty()) {
                        showSnackBar(getString(R.string.select_products))
//                    } else if (editTextYourName.text.toString().isEmpty()){
//                        showSnackBar(getString(R.string.your_full_name))
//                    } else if (editTextYourMobileNumber.text.toString().isEmpty() || editTextYourMobileNumber.text.toString().length != 10){
//                        showSnackBar(getString(R.string.your_mobile_number))
                    } else if (editTextComments.text.toString().isEmpty()) {
                        showSnackBar(getString(R.string.comments))
                    } else {
                        readData(WEBSITE_DATA) { webData ->
                            if (webData != null) {
                                val data = Gson().fromJson(
                                    webData,
                                    ItemWebsite::class.java
                                )

                                val jsonObjectStatus = JSONObject().apply {
                                    put("customer_name", data?.name)
//                                    put("mobile",  "")
                                    put("titel", "")
                                    put("body", editTextComments.text.toString())
                                    put("rating", "" + ratingBar.rating.toInt())
//                                    put("subject", "")
//                                    put("category", "1")
//                                    put("priority", viewModel.priorityType)
//                                    put("orderid", editTextSelectOrder.text.toString())
                                    put("productid", viewModel.idsArrayFeedback)
                                }
                                viewModel.createFeedback(jsonObjectStatus) {
                                    if (this.contains("[]")) {
                                        showSnackBar("Feedback sent successfully")
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            findNavController().navigateUp()
                                        }, 1000)
                                    } else {
                                        showSnackBar("Something went wrong!")
                                    }
                                }
                            }
                        }
                    }
                }
            }


//            layoutSort.singleClick {
//                if(editTextSubjectOfComplaint.text.toString().isEmpty()){
//                    if (viewModel.type == "complaint"){
//                        showSnackBar(getString(R.string.subject_of_complaint))
//                    } else if (viewModel.type == "feedback"){
//                        showSnackBar(getString(R.string.subject_of_feedback))
//                    }
//                } else if (viewModel.type == "complaint" && viewModel.complaintTypeId == 0){
//                    showSnackBar(getString(R.string.select_complaint_type))
//                } else if (editTextYourName.text.toString().isEmpty()) {
//                    showSnackBar(getString(R.string.your_full_name))
//                } else if (editTextYourMobileNumber.text.toString().isEmpty() || editTextYourMobileNumber.text.toString().length != 10){
//                    showSnackBar(getString(R.string.your_mobile_number))
//                } else if (editTextTypeHere.text.toString().isEmpty()) {
//                    showSnackBar(getString(R.string.description))
////                } else if (viewModel.uploadMediaImage == null){
////                    showSnackBar(getString(R.string.upload_media))
//                } else {
////                    Log.e("TAG", "typeXX "+viewModel.type)
//                    val requestBody: MultipartBody.Builder = MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart(user_role, USER_TYPE)
//                    requestBody.addFormDataPart(type, viewModel.type)
//                    requestBody.addFormDataPart(subject, editTextSubjectOfComplaint.text.toString())
//                    if (viewModel.type == "complaint" && viewModel.complaintTypeId != 0){
//                        requestBody.addFormDataPart(complaint_type, ""+viewModel.complaintTypeId)
//                    }
//                    requestBody.addFormDataPart(name, editTextYourName.text.toString())
//                    requestBody.addFormDataPart(mobile_number, editTextYourMobileNumber.text.toString())
//                    requestBody.addFormDataPart(message, editTextTypeHere.text.toString())
//                    if(viewModel.uploadMediaImage != null){
//                        requestBody.addFormDataPart(
//                            media,
//                            File(viewModel.uploadMediaImage!!).name,
//                            File(viewModel.uploadMediaImage!!).asRequestBody("image/*".toMediaTypeOrNull())
//                        )
//                    }
//                    requestBody.addFormDataPart(status, "pending")
//                    readData(LOGIN_DATA) { loginUser ->
//                        if (loginUser != null) {
//                           val user = Gson().fromJson(loginUser, Login::class.java)
//                            requestBody.addFormDataPart(user_id, ""+user?.id)
//                            requestBody.addFormDataPart(state_id, ""+user.residential_state?.id)
//                            requestBody.addFormDataPart(district_id, ""+user.residential_district?.id)
//                            requestBody.addFormDataPart(municipality_id, ""+user.residential_municipality_panchayat?.id)
//                            if(user?.residential_state?.id != null){
//                                if(networkFailed) {
//                                    viewModel.newFeedback(view = requireView(), requestBody.build())
//                                } else {
//                                    requireContext().callNetworkDialog()
//                                }
//                            } else {
//                                showSnackBar(resources.getString(R.string.need_to_add_complete_profile))
//                            }
//                        }
//                    }
//                }
//            }
        }
    }







    private fun callMediaPermissionsWithLocation() {
        try {
//            if (isLocationEnabled(requireActivity()) == true) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                    Log.e("TAG", "AAAAAAAAAAA")
//                    activityResultLauncherWithLocation.launch(
//                        arrayOf(
//                            Manifest.permission.CAMERA,
////                            Manifest.permission.READ_MEDIA_IMAGES,
////                            Manifest.permission.READ_MEDIA_VIDEO,
//                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                        )
//                    )
//                } else


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.e("TAG", "BBBBBBBBB")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
//                            Manifest.permission.READ_MEDIA_IMAGES,
//                            Manifest.permission.READ_MEDIA_VIDEO,
                        )
                    )
                } else {
                    Log.e("TAG", "CCCCCCCCC")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        )
                    )
                }
//            } else {
//                requireActivity().callPermissionDialogGPS {
//                    someActivityResultLauncherWithLocationGPS.launch(this)
//                }
//            }
        } catch (e : Exception){

        }

    }

//    private fun callMediaPermissionsWithLocation2() {
//        try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                    Log.e("TAG", "AAAAAAAAAAA")
//                    activityResultLauncherWithLocation2.launch(
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                            Manifest.permission.READ_MEDIA_IMAGES,
//                            Manifest.permission.READ_MEDIA_VIDEO,
//                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                        )
//                    )
//                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    Log.e("TAG", "BBBBBBBBB")
//                    activityResultLauncherWithLocation2.launch(
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                            Manifest.permission.READ_MEDIA_IMAGES,
////                            Manifest.permission.READ_MEDIA_VIDEO,
//
//                        )
//                    )
//                } else {
//                    Log.e("TAG", "CCCCCCCCC")
//                    activityResultLauncherWithLocation2.launch(
//                        arrayOf(
//                            Manifest.permission.CAMERA,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        )
//                    )
//                }
//
//        } catch (e : Exception){
//
//        }
//
//    }



    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
//                for (permission in permissions.entries) {
//                    Log.e("TAG", "permissions.entries "+permission)
//                }

                if (!permissions.entries.toString().contains("false")) {
                    requireActivity().showOptions {
                        when (this) {
                            1 -> forCamera()
                            2 -> forGallery()
                        }
                    }
                } else {
                    callMediaPermissionsWithLocation()

                }
            } catch (e : Exception){

            }
        }


//    private val activityResultLauncherWithLocation2 =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        )
//        { permissions ->
//            try {
////                for (permission in permissions.entries) {
////                    Log.e("TAG", "permissions.entries "+permission)
////                }
//
//                if (!permissions.entries.toString().contains("false")) {
//                    requireActivity().showOptions {
//                        when (this) {
//                            1 -> forCamera()
//                            2 -> forGallery()
//                        }
//                    }
//                } else {
//                    requireActivity().callPermissionDialog {
//                        someActivityResultLauncherWithLocation.launch(this)
//                    }
//
//                }
//            } catch (e : Exception){
//
//            }
//        }
//
//
//
//    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        try {
//            callMediaPermissionsWithLocation2()
//        } catch (e : Exception){
//
//        }
//    }
//
//    var someActivityResultLauncherWithLocation2 = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        try {
//            callMediaPermissionsWithLocation2()
//        } catch (e : Exception){
//
//        }
//    }

    var someActivityResultLauncherWithLocationGPS = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            Log.e("TAG", "result.resultCode "+result.resultCode)
            callMediaPermissionsWithLocation()
        } catch (e : Exception){

        }
    }







//    private fun openMediaPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "image/*"
//        }
//        galleryLauncher.launch(intent)
//    }
//
//    var imagePosition = 0
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch {
                if (uri != null) {
//                    when (imagePosition) {
//                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.uploadMediaImage = compressedImageFile.path
                            binding.textViewUploadMedia.setText(File(viewModel.uploadMediaImage!!).name)
//                        val encodedUrl = Base64.getUrlEncoder().encodeToString(compressedImageFile.path.toByteArray())
                            val encodedUrl = encodeImage(compressedImageFile.path)
                            Log.e("TAG", "encodedUrlA " + encodedUrl)
                            viewModel.uploadMediaImageBase64 = encodedUrl
                        }
                    }
//                }
//            }
        }


    var uriReal: Uri? = null
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {

                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.uploadMediaImage = compressedImageFile.path
                        binding.textViewUploadMedia.setText(compressedImageFile.name)
                        // val encodedUrl = Base64.getUrlEncoder().encodeToString(compressedImageFile.path.toByteArray())
                        val encodedUrl = encodeImage(compressedImageFile.path)
                        Log.e("TAG", "encodedUrlB " + encodedUrl)
                        viewModel.uploadMediaImageBase64 = encodedUrl
                    }

            }
        }



//    private fun callMediaPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
//                )
//            )
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_MEDIA_IMAGES
//                )
//            )
//        } else {
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            )
//        }
//    }
//
//
////    var isFree = false
//    private val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        )
//        { permissions ->
//            try {
//                if (!permissions.entries.toString().contains("false")) {
//                    requireActivity().showOptions {
//                        when (this) {
//                            1 -> forCamera()
//                            2 -> forGallery()
//                        }
//                    }
//                } else {
//                    requireActivity().callPermissionDialog {
//                        someActivityResultLauncherWithLocation.launch(this)
//                    }
//
//                }
//            } catch (e : Exception){
//
//            }
////            permissions.entries.forEach {
////                val permissionName = it.key
////                val isGranted = it.value
////                if (isGranted) {
////                    if (isFree) {
////                        requireActivity().showOptions {
////                            when (this) {
////                                1 -> forCamera()
////                                2 -> forGallery()
////                            }
////                        }
////                    }
////                    isFree = false
////                }
////            }
//        }
//
//
//
//    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        try {
//            callMediaPermissions()
//        } catch (e : Exception){
//
//        }
//    }


    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}