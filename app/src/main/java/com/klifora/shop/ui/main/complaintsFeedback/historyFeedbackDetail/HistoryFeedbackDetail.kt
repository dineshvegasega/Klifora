package com.klifora.shop.ui.main.complaintsFeedback.historyFeedbackDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.klifora.shop.databinding.HistoryFeedbackDetailBinding
import com.klifora.shop.datastore.DataStoreKeys.WEBSITE_DATA
import com.klifora.shop.datastore.DataStoreUtil.readData
import com.klifora.shop.models.ItemFeedbackItem
import com.klifora.shop.models.ItemWebsite
import com.klifora.shop.ui.mainActivity.MainActivity
import com.klifora.shop.utils.changeDateFormat
import com.klifora.shop.utils.parcelable
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFeedbackDetail : Fragment() {
    private val viewModel: HistoryFeedbackDetailVM by viewModels()
    private var _binding: HistoryFeedbackDetailBinding? = null
    private val binding get() = _binding!!


    var feedbackId : String = ""
    var status = ""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryFeedbackDetailBinding.inflate(inflater, container, false)
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

        val consentIntent = arguments?.parcelable<ItemFeedbackItem>("key")
        feedbackId = "" + consentIntent?.review_id


        binding.apply {
            topBarClose.apply {
                includeBackButton.layoutBack.singleClick {
                    findNavController().navigateUp()
                }
            }

            textTrackingTxt.text = "Feedback Id: " + feedbackId
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
            topBarClose.btClose.visibility = View.INVISIBLE

            inclideHistoryType.textTypeValue.text = "Feedback"
            val date =
                consentIntent?.created_at?.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy")
            inclideHistoryType.textRegistrationDateValue.text = date
            inclideHistoryType.textSubjectValue.text = consentIntent?.detail
            inclideHistoryType.textSubjectValue.visibility = View.GONE
            inclideHistoryType.textSubjectTxt.visibility = View.GONE

            ratingBar.rating = consentIntent?.value?.toFloat()!!
            editTextComments.setText(consentIntent.detail)


            readData(WEBSITE_DATA) { webData ->
                if (webData != null) {
                    val data = Gson().fromJson(
                        webData,
                        ItemWebsite::class.java
                    )
//                    viewModel.messageHistory(feedbackId, data.entity_id)
                }
            }

        }


    }
}