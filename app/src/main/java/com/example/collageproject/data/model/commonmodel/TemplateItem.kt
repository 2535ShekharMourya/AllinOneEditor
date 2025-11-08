package com.example.collageproject.data.model.commonmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TemplateItem(
    val access_type: String? = null,
    val collection_id: String? = null,
    val created_at: String? = null,
    val deleted_at: String? = null,
    val disabled_description: String? = null,
    val dynamic_link: String? = null,
    val edge_image_id: String? = null,
    val group_id: String? = null,
    val has_ai_features: Int? = null,
    val image_coordinates: List<ImageCoordinate>? = null,
    val image_url: String? = null,
    val img_height: Int? = null,
    val img_width: Int? = null,
    val isEditable: Int? = null,
    val isIscommentDisable: Int? = null,
    val is_active: Int? = null,
    val is_archived: Int? = null,
    val is_disabled_by_admin: Int? = null,
    val is_liked: Int? = null,
    val language_id: String? = null,
    val logo_coordinates: String? = null,
    val logo_url: String? = null,
    val print_logo: Int? = null,
    val studio_id: String? = null,
    val thumb_url: String? = null,
    val thumb_url_domain: String? = null,
    val thumb_url_id: String? = null,
    val title: String? = null,
    val total_comments: Int? = null,
    val total_likes: Int? = null,
    val total_shares: Int? = null,
    val total_tries: Int? = null,
    val total_views: Int? = null,
    val type: String? = null,
    val updated_at: String? = null
): Parcelable

@Parcelize
data class ImageCoordinate(
    val height_in_px: Int? = null,
    val height_percentage: Double? = null,
    val left_in_px: Int? = null,
    val left_percentage: Double? = null,
    val rotation_degrees: Int? = null,
    val top_in_px: Int? = null,
    val top_percentage: Double? = null,
    val width_in_px: Int? = null,
    val width_percentage: Double? = null,
    var left_ratio: Float?=null,
    var top_ratio: Float?=null,
    var width_ratio: Float?= null,
    var height_ratio: Float? = null,
    var matrixValues: FloatArray? = null

): Parcelable

