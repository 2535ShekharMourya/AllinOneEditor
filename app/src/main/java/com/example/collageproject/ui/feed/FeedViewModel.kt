package com.example.collageproject.ui.feed


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
) : ViewModel() {
    private val _feedState = MutableLiveData<Resource<List<TemplateItem>?>>()
    val feedState: LiveData<Resource<List<TemplateItem>?>> = _feedState

    fun fetchFeeds() {
        viewModelScope.launch {
            _feedState.value = Resource.Loading()
            _feedState.value = feedRepository.getFeeds()
        }
    }

}