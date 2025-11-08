package com.example.collageproject.data.model.homepagemodel
import android.os.Parcelable
import com.example.collageproject.data.model.commonmodel.ImageCoordinate
import com.example.collageproject.data.model.commonmodel.TemplateItem
import kotlinx.android.parcel.Parcelize
@Parcelize
data class CollectionTemplate(
    val `data`: Data? = null,
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class Data(
    val collection_id: String? = null,
    val items: List<TemplateItem>? = null,
    val title: String? = null,
    val type: String? = null
) : Parcelable