package com.example.myapplication.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DetailResponse(
    @Json(name = "document_id")
    val documentId: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "description")
    val description: String = "",
    @Json(name = "published_date")
    val publishedDate: String = "",
    @Json(name = "origin_url")
    val originUrl: String = "",
    @Json(name = "publisher")
    val publisher: Publisher = Publisher(),
    @Json(name = "avatar")
    val avatar: Image? = null,
    @Json(name = "images")
    val images: List<Image>? = emptyList(),
    @Json(name = "content")
    val content: Any? = null,
    @Json(name = "template_type")
    val templateType: String? = null,
    @Json(name = "sections")
    val sections: List<DetailSection>? = null
)

@JsonClass(generateAdapter = true)
data class DetailSection(
    @Json(name = "section_type")
    val sectionType: Int = 0,
    @Json(name = "content")
    val content: DetailSectionContent = DetailSectionContent()
)

@JsonClass(generateAdapter = true)
data class DetailSectionContent(
    @Json(name = "text")
    val text: String? = null,
    @Json(name = "markups")
    val markups: List<DetailMarkup>? = null,

    @Json(name = "href")
    val href: String? = null,
    @Json(name = "caption")
    val caption: String? = null,
    @Json(name = "duration")
    val duration: Int? = null,
    @Json(name = "preview_image")
    val previewImage: Image? = null,

    @Json(name = "main_color")
    val mainColor: String? = null,
    @Json(name = "original_width")
    val originalWidth: Int? = null,
    @Json(name = "original_height")
    val originalHeight: Int? = null
)

@JsonClass(generateAdapter = true)
data class DetailMarkup(
    @Json(name = "markup_type")
    val markupType: Int = 0,
    @Json(name = "start")
    val start: Int = 0,
    @Json(name = "end")
    val end: Int = 0,
    @Json(name = "href")
    val href: String? = null
)
