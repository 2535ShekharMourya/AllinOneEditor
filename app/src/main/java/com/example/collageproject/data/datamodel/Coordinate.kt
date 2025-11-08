package com.example.collageproject.data.datamodel

data class Coordinate(
    val height_in_px: Int,
    val left_in_px: Int,
    val top_in_px: Int,
    val type: String,
    val width_in_px: Int,
    var left_ratio: Float?=null,
    var top_ratio: Float?=null,
    var width_ratio: Float?= null,
    var height_ratio: Float? = null,
    var matrixValues: FloatArray? = null
)