package com.example.collageproject.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionTemplate
import com.example.collageproject.data.repository.HomeRepository
import com.google.gson.Gson
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

    fun fetchCollections(context: Context) {
        viewModelScope.launch {
            _collections.value = Resource.Loading()
           // _collections.value = homeRepository.getCollections()
            _collections.value = getCollections(context)
        }
    }

    fun fetchCollectionTemplates(context: Context,collectionId: String?) {
        viewModelScope.launch {
            _collectionTemplates.value = Resource.Loading()
           // _collectionTemplates.value = homeRepository.getTemplatesFromCollection(collectionId)
            _collectionTemplates.value = getTemplatesFromCollection(context, collectionId)
        }
    }
    // just for mock
    fun getTemplatesFromCollection(context: Context, collectionId: String?):Resource<CollectionTemplate> {
        val json = context. assets.open("collection_template.json").bufferedReader().use { it.readText() }
        val response = Gson().fromJson(json, CollectionTemplate::class.java)
        return Resource.Success(response)
    }
    // just for mock
fun getCollections(context: Context): Resource<AllCollections> {
    val json = context.assets.open("all_collections.json")
        .bufferedReader()
        .use { it.readText() }

    val response = Gson().fromJson(json, AllCollections::class.java)
    return Resource.Success(response)
}
}