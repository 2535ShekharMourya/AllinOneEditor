package com.example.collageproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionTemplate
import com.example.collageproject.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _collections = MutableLiveData<Resource<AllCollections>>()
    val collections: LiveData<Resource<AllCollections>> = _collections

    private val _collectionTemplates = MutableLiveData<Resource<CollectionTemplate>>()
    val collectionTemplates: LiveData<Resource<CollectionTemplate>> = _collectionTemplates

    fun fetchCollections() {
        viewModelScope.launch {
            _collections.value = Resource.Loading()
            _collections.value = homeRepository.getCollections()
        }
    }

    fun fetchCollectionTemplates(collectionId: String?) {
        viewModelScope.launch {
            _collectionTemplates.value = Resource.Loading()
            _collectionTemplates.value = homeRepository.getTemplatesFromCollection(collectionId)
        }
    }

}