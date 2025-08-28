package com.klifora.shop.models

data class ItemHistory(
    val complaint_type: String = "",
    val date: String= "",
    val feedback_id: Int = 0,
    val media: Media = Media(),
    val message: String = "",
    val name: String = "",
    val status: String = "",
    val subject: String = "",
    val type: String = "",
    val user_id: Int = 0
)

data class Media(
    val name: String = "",
    val url: String = ""
)