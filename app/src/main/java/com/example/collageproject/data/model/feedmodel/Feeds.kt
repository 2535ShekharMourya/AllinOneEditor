package com.example.collageproject.data.model.feedmodel
import android.os.Parcelable
import com.example.collageproject.data.model.commonmodel.TemplateItem
import kotlinx.android.parcel.Parcelize
@Parcelize
data class Feeds(
    val `data`: List<TemplateItem>? = null,
    val pagination: Pagination? = null,
    val success: Boolean? = null
) : Parcelable
@Parcelize
data class Dashboard(
    val `data`: List<TemplateItem>? = null,
    val pagination: Pagination? = null,
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class Pagination(
    val limit: Int? = null,
    val page: Int? = null,
    val total: Int? = null,
    val totalPages: Int? = null
) : Parcelable