package com.example.collageproject.ui.common

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.data.datamodel.Data
import com.example.collageproject.data.datamodel.HomePageData
import com.example.collageproject.data.datamodel.Pagination
import com.example.collageproject.data.datamodel.TemplateImage
import kotlinx.coroutines.launch

class CollageViewModel: ViewModel() {
        // 2. Create a MutableLiveData object.  This is where you'll *modify* the data.
         val _pickedTemplate = MutableLiveData<TemplateImage>()
    // 3. Create a LiveData object. This is what your UI (e.g., Activity or Fragment) will *observe*.
    val pickedTemplate: LiveData<TemplateImage> = _pickedTemplate

    val _userImages = MutableLiveData<ArrayList<Uri>>()
    val userImages: LiveData<ArrayList<Uri>> = _userImages
    // LiveData for the homepage data
    private val _homePageData = MutableLiveData<HomePageData>()
    val homePageData: LiveData<HomePageData> = _homePageData

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error state
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _templateList = MutableLiveData<MutableList<Data>>(mutableListOf())
    val templateList: LiveData<MutableList<Data>> = _templateList

    private val _pagination = MutableLiveData<Pagination>()
    val pagination: LiveData<Pagination> = _pagination

    private var currentPage = 1
    private var isLoadingMore = false

   // val _pickedTemplate = MutableLiveData<TemplateImage>()

//    fun fetchTemplateCollections() {
//        if (isLoadingMore) return
//
//        // Check if we've already reached the last page
//        val totalPages = _pagination.value?.total_pages
//        if (totalPages != null && currentPage > totalPages) return
//
//        isLoadingMore = true
//
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getTemplateCollections(page = currentPage)
//                if (response.isSuccessful) {
//                    response.body()?.let { data ->
//                        val currentList = _templateList.value ?: mutableListOf()
//                        currentList.addAll(data.data)
//                        _templateList.value = currentList
//                        _pagination.value = data.pagination
//                        currentPage = data.pagination.current_page + 1 // Move to next page
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("PaginationError", e.localizedMessage ?: "Unknown error")
//            } finally {
//                isLoadingMore = false
//            }
//        }
//    }


}