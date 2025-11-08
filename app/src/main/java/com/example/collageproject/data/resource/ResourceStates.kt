package com.example.collageproject.data.resource

import android.net.http.NetworkException
sealed class ResourceStates<out R> {
    data class Success<out R>(val result: R): ResourceStates<R>()
    data class Failure(val exception: NetworkException?=null, val errorMessage: String): ResourceStates<Nothing>()
    data object Loading: ResourceStates<Nothing>()
}