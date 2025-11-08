package com.example.collageproject.data.datamodel

data class Pagination(
    val current_page: Int,
    val has_next_page: Boolean,
    val has_prev_page: Boolean,
    val total_pages: Int
)