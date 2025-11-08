package com.example.collageproject.data.remote


import com.example.collageproject.data.model.feedmodel.Feeds
import com.example.collageproject.data.model.homepagemodel.AllCollections
import com.example.collageproject.data.model.homepagemodel.CollectionTemplate
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter.All

interface ApiService {
    @GET("api/collections")
    suspend fun getTemplateCollections(
        @Query("paginate") paginate: Boolean = true,
        @Query("page") page: Int
    ): Response<AllCollections>

    @GET("api/templates")
    suspend fun getAllTemplate(
    ): Response<Feeds>

    @GET("api/collections")
    suspend fun getCollections(): Response<AllCollections>

    @GET("api/collections/{id}")
    suspend fun getTemplatesFromCollection(@Path("id") id: String?): Response<CollectionTemplate>
}