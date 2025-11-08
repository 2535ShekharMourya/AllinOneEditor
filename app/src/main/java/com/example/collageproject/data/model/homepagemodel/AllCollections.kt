package com.example.collageproject.data.model.homepagemodel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class AllCollections(
    val `data`: List<CollectionsItem>? = null,
    val success: Boolean? = null
) : Parcelable

@Parcelize
data class CollectionsItem(
    val collection_id: String? = null,
    val title: String? = null,
    val type: String? = null,
    val thumb: String? = null
) : Parcelable
data class CollectionType(val type: String,val collectionTypeList: List<CollectionsItem>)