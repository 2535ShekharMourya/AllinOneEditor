package com.example.collageproject.data.repository



import android.util.Log
import com.example.collageproject.data.model.feedmodel.Resource
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionTemplate
import com.example.collageproject.data.remote.ApiService
import com.example.collageproject.extensions.logMessage
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Using Resource sealed class
    suspend fun getCollections(): Resource<AllCollections> {
        return try {
            val response = apiService.getCollections()
            if (response.isSuccessful) {
                response.body()?.let { collections ->
                    "Resource.Success(collections): ${Resource.Success(collections).data?.data}".logMessage()
                    Resource.Success(collections)
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
    suspend fun getTemplatesFromCollection(collectionId: String?): Resource<CollectionTemplate> {
        return try {
            val response = apiService.getTemplatesFromCollection(collectionId)
            if (response.isSuccessful) {
                response.body()?.let { collectionTemplates ->
                    Resource.Success(collectionTemplates)
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
