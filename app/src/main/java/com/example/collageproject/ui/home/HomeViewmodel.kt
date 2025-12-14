package com.example.collageproject.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.core.CarouselItem
import com.example.collageproject.core.MultiviewDataItem
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Dashboard
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

    private val _dashboardData = MutableLiveData<Resource<List<MultiviewDataItem>>>()
    val dashboardData: LiveData<Resource<List<MultiviewDataItem>>> = _dashboardData

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

    fun getDashboardData(context: Context) {
        viewModelScope.launch {
            _dashboardData.value = Resource.Loading()
            _dashboardData.value = dashboardData(context)              //homeRepository.getDashboardData(context)
        }
    }
    fun dashboardData(context: Context):Resource<List<MultiviewDataItem>> {
        val json = context.assets.open("home_page_data.json")
            .bufferedReader()
            .use { it.readText() }

        val response = Gson().fromJson(json, Dashboard::class.java)
        var mList: ArrayList<MultiviewDataItem> = ArrayList()
        var carouselList:List<TemplateItem> = response.data?.filter { it.access_type == "Carousel"} ?: emptyList()
        var recentlyList:List<TemplateItem> = response.data?.filter { it.access_type == "Recently"} ?: emptyList()
        var recommendedList:List<TemplateItem> = response.data?.filter { it.access_type == "Recommended"} ?: emptyList()
        var topInIndiaList:List<TemplateItem> = response.data?.filter { it.access_type == "TopInIndia"} ?: emptyList()
        var youMayLikeList:List<TemplateItem> = response.data?.filter { it.access_type == "YouMayLike"} ?: emptyList()
        var exploreMoreList:List<TemplateItem> = response.data?.filter { it.access_type == "ExploreMore"} ?: emptyList()
        var banner:TemplateItem? = response.data?.find { it.access_type == "Banner"}
        var largeBanner: TemplateItem? = response.data?.find { it.access_type =="LargeBanner"}
        var fullScreenBanner: TemplateItem? = response.data?.find { it.access_type == "FullScreenBanner"}


        mList.add(MultiviewDataItem.Carousel(carouselList))
        mList.add(MultiviewDataItem.RecentlyViewed(recentlyList))
        mList.add(MultiviewDataItem.Banner(banner))
        mList.add(MultiviewDataItem.Recommended(recommendedList))
        mList.add(MultiviewDataItem.TopInIndia(topInIndiaList))
        mList.add(MultiviewDataItem.BigBanner(largeBanner))
        mList.add(MultiviewDataItem.YouMayLike(youMayLikeList))
        mList.add(MultiviewDataItem.FullScreenBanner(fullScreenBanner))
        mList.add(MultiviewDataItem.ExploreMore(exploreMoreList))

        return Resource.Success(mList)

    }
}