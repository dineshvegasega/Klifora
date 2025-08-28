package com.klifora.shop.models

data class ItemBannerItem(
    val banner_id: String,
    val content: Any,
    val created_at: String,
    val image: String,
    val name: String,
    val newtab: String,
    val status: String,
    val title: String,
    val type: String,
    val updated_at: String,
    val url_banner: String = ""
)