package com.example.collageproject.core

sealed class NetworkException(message: String) : Exception(message) {
    data class NoInternetConnection(override val message:String = "No internet connection") : NetworkException(message)
    data class ServerException(override val message:String = "Server error") : NetworkException(message)
    data class SerializationException(override val message: String = "Serialization error") : NetworkException(message)
    data class  UnAuthorisedException(override val message: String = "Login or signUp"):NetworkException(message)
    data class  UnKnownException(override val message: String = "Unknown Exception"):NetworkException(message)
}