package com.example.myapplication.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsFeedResponse(
    @Json(name = "items")
    val items: List<NewsItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class NewsItem(
    @Json(name = "document_id")
    val documentId: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "description")
    val description: String = "",
    @Json(name = "content_type")
    val contentType: String = "",
    @Json(name = "published_date")
    val publishedDate: String = "",
    @Json(name = "publisher")
    val publisher: Publisher = Publisher(),
    @Json(name = "origin_url")
    val originUrl: String = "",
    @Json(name = "avatar")
    val avatar: Image? = null,
    @Json(name = "images")
    val images: List<Image>? = emptyList(),
    @Json(name = "content")
    val content: Any? = null
)

@JsonClass(generateAdapter = true)
data class Publisher(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "icon")
    val icon: String = ""
)

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "href")
    val href: String = "",
    @Json(name = "main_color")
    val mainColor: String = "",
    @Json(name = "width")
    val width: Int = 0,
    @Json(name = "height")
    val height: Int = 0
)
