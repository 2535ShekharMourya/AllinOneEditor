package com.example.collageproject.data.repository

import android.util.Log
import com.example.collageproject.data.model.commonmodel.TemplateItem
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.remote.ApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Using Resource sealed class
    suspend fun getFeeds(): Resource<List<TemplateItem>?> {
        return try {

            val response = apiService.getAllTemplate()
            if (response.isSuccessful) {
                response.body()?.let { users ->

                    Log.d("Response", "getFeeds: ${users}")
                    Resource.Success(users.data)
                } ?: Resource.Error("Empty response body")
            } else {
                Resource.Error("HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: IOException) {
            Resource.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Resource.Error("Server error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.message}")
        }
    }
}