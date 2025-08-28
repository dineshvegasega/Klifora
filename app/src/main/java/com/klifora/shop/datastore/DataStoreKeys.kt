package com.klifora.shop.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object DataStoreKeys {
    const val DATA_STORE_NAME = "ApplicationTemplate"
    val LOGIN_DATA by lazy { stringPreferencesKey("LOGIN_DATA") }
    val ADMIN_TOKEN by lazy { stringPreferencesKey("ADMIN_TOKEN") }
    val CUSTOMER_TOKEN by lazy { stringPreferencesKey("CUSTOMER_TOKEN") }
    val STORE_DETAIL by lazy { stringPreferencesKey("STORE_DETAIL") }
    val WEBSITE_ID by lazy { stringPreferencesKey("WEBSITE_ID") }
    val WEBSITE_DATA by lazy { stringPreferencesKey("WEBSITE_DATA") }
    val QUOTE_ID by lazy { stringPreferencesKey("QUOTE_ID") }

    val FILTER_PRICE by lazy { stringPreferencesKey("FILTER_PRICE") }
    val FILTER_CATEGORY by lazy { stringPreferencesKey("FILTER_CATEGORY") }
    val FILTER_MATERIAL by lazy { stringPreferencesKey("FILTER_MATERIAL") }
    val FILTER_GENDER by lazy { stringPreferencesKey("FILTER_GENDER") }

    val DEVICE_TOKEN by lazy { stringPreferencesKey("DEVICE_TOKEN") }

    val MOBILE_NUMBER by lazy { stringPreferencesKey("MOBILE_NUMBER") }

    val AUTH by lazy { stringPreferencesKey("AUTH") }
    val LIVE_SCHEME_DATA by lazy { stringPreferencesKey("LIVE_SCHEME_DATA") }
    val LIVE_NOTICE_DATA by lazy { stringPreferencesKey("LIVE_NOTICE_DATA") }
    val LIVE_TRAINING_DATA by lazy { stringPreferencesKey("LIVE_TRAINING_DATA") }
    val Complaint_Feedback_DATA by lazy { stringPreferencesKey("Complaint_Feedback_DATA") }
    val Information_Center_DATA by lazy { stringPreferencesKey("Information_Center_DATA") }

    val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)
}
