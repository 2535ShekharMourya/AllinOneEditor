package com.example.collageproject.data.datamodel

data class Data(
    val collection_id: Int,
    val collection_title: String,
    val `data`: List<TemplateImage>
)