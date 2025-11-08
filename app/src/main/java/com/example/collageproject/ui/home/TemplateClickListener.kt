package com.example.collageproject.ui.home

import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.homepagemodel.Data

interface TemplateClickListener {
    fun onTemplateClick(data: TemplateItem)
}