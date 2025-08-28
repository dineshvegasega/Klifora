package com.klifora.shop.models

data class ItemMessageHistoryItem(
    val body: String,
    val body_format: String,
    val created_at: String,
    val customer_email: String,
    val customer_id: String,
    val customer_name: String,
    val email_id: String,
    val is_read: String,
    val message_id: String,
    val third_party_email: String,
    val third_party_name: String,
    val ticket_id: String,
    val triggered_by: String,
    val type: String,
    val uid: String,
    val updated_at: String,
    val user_email: String,
    val user_id: String,
    val user_name: String,
    val imgeurl: String,
    val imgname: String
)