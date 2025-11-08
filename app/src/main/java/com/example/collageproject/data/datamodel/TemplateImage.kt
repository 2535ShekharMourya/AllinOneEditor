package com.example.collageproject.data.datamodel

data class TemplateImage(
    val background: String,
    val coordinates: List<Coordinate>,
    val id: Int,
    val img_height: Int,
    val img_width: Int,
    val isCircularOverlay: Boolean,
    val overlay: Any,
    val overlayAlignment: String,
    val overlayOffsetX: Int,
    val overlayOffsetY: Int,
    val overlayScale: Int,
    val overlaySize: Int,
    val thumbnailOverlayOffsetX: Int,
    val thumbnailOverlayOffsetY: Int,
    val thumbnailOverlaySize: Int
)