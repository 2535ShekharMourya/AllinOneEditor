package com.example.collageproject.ui.feed


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Feeds
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.repository.FeedRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val _feedState = MutableLiveData<Resource<List<TemplateItem>?>>()
    val feedState: LiveData<Resource<List<TemplateItem>?>> = _feedState

    fun fetchFeeds(context: Context) {
        viewModelScope.launch {
            _feedState.value = Resource.Loading()
          //  _feedState.value = feedRepository.getFeeds()
            _feedState.value = getFeeds(context)
        }
    }


    fun getFeeds(context: Context) : Resource.Success<List<TemplateItem>?> {
        val json = context.assets.open("feeds.json").bufferedReader().use { it.readText() }
        val response = Gson().fromJson(json, Feeds::class.java)

        return Resource.Success(response.data)
    }
}