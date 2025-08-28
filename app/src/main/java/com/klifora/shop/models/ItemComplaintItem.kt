package com.klifora.shop.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemComplaintItem(
    val admin_note: String,
    val bcc: String,
    val category_id: String,
    val cc: String,
    val channel: String,
    val channel_data: String,
    val code: String,
    val created_at: String,
    val creditmemo_id: String,
    val customer_email: String,
    val customer_id: String,
    val customer_name: String,
    val department_id: String,
    val description: String,
    val email_id: String,
    val first_reply_at: String,
    val first_solved_at: String,
    val folder: String,
    val fp_department_id: String,
    val fp_execute_at: String,
    val fp_is_remind: String,
    val fp_period_unit: String,
    val fp_period_value: String,
    val fp_priority_id: String,
    val fp_remind_email: String,
    val fp_status_id: String,
    val fp_user_id: String,
    val is_read: String,
    val last_reply_at: String,
    val last_reply_name: String,
    val merged_ticket_id: String,
    val order_id: String,
    val price: String,
    val priority_id: String,
    val product_id: String,
    val qty_requested: String,
    val qty_returned: String,
    val quote_address_id: String,
    val rating: String,
    val reply_cnt: String,
    val search_index: String,
    val status_id: String,
    val store_id: String,
    val subject: String,
    val third_party_email: String,
    val ticket_id: String,
    val updated_at: String,
    val user_id: String
): Parcelable{
    override fun hashCode(): Int {
        val result = category_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemComplaintItem

        if (admin_note != other.admin_note) return false
        if (bcc != other.bcc) return false
        if (category_id != other.category_id) return false
        if (cc != other.cc) return false
        if (channel != other.channel) return false
        if (channel_data != other.channel_data) return false
        if (code != other.code) return false
        if (created_at != other.created_at) return false
        if (creditmemo_id != other.creditmemo_id) return false
        if (customer_email != other.customer_email) return false
        if (customer_id != other.customer_id) return false
        if (customer_name != other.customer_name) return false
        if (department_id != other.department_id) return false
        if (description != other.description) return false
        if (email_id != other.email_id) return false
        if (first_reply_at != other.first_reply_at) return false
        if (first_solved_at != other.first_solved_at) return false
        if (folder != other.folder) return false
        if (fp_department_id != other.fp_department_id) return false
        if (fp_execute_at != other.fp_execute_at) return false
        if (fp_is_remind != other.fp_is_remind) return false
        if (fp_period_unit != other.fp_period_unit) return false
        if (fp_period_value != other.fp_period_value) return false
        if (fp_priority_id != other.fp_priority_id) return false
        if (fp_remind_email != other.fp_remind_email) return false
        if (fp_status_id != other.fp_status_id) return false
        if (fp_user_id != other.fp_user_id) return false
        if (is_read != other.is_read) return false
        if (last_reply_at != other.last_reply_at) return false
        if (last_reply_name != other.last_reply_name) return false
        if (merged_ticket_id != other.merged_ticket_id) return false
        if (order_id != other.order_id) return false
        if (price != other.price) return false
        if (priority_id != other.priority_id) return false
        if (product_id != other.product_id) return false
        if (qty_requested != other.qty_requested) return false
        if (qty_returned != other.qty_returned) return false
        if (quote_address_id != other.quote_address_id) return false
        if (rating != other.rating) return false
        if (reply_cnt != other.reply_cnt) return false
        if (search_index != other.search_index) return false
        if (status_id != other.status_id) return false
        if (store_id != other.store_id) return false
        if (subject != other.subject) return false
        if (third_party_email != other.third_party_email) return false
        if (ticket_id != other.ticket_id) return false
        if (updated_at != other.updated_at) return false
        if (user_id != other.user_id) return false

        return true
    }
}