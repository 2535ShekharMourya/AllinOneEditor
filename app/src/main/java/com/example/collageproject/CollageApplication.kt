package com.example.collageproject

import android.app.Application
import android.content.Intent
import com.example.collageproject.utils.NetworkConnectionMonitor
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CollageApplication: Application() {
    lateinit var networkMonitor: NetworkConnectionMonitor
    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkConnectionMonitor(this)
        // Optional: Start global network monitoring for app-wide features
        startGlobalNetworkMonitoring()
    }
    private fun startGlobalNetworkMonitoring() {
        networkMonitor.startMonitoring(
            onLost = {
                // Handle global network lost
                handleGlobalNetworkLost()
            },
            onRestored = {
                // Handle global network restored
                handleGlobalNetworkRestored()
            }
        )
    }

    private fun handleGlobalNetworkLost() {
        // You can implement global actions when network is lost
        // For example: pause background sync, cancel ongoing uploads, etc.

        // Send broadcast to notify other components
        sendBroadcast(Intent("com.yourapp.NETWORK_LOST"))
    }

    private fun handleGlobalNetworkRestored() {
        // You can implement global actions when network is restored
        // For example: resume background sync, retry failed operations, etc.

        // Send broadcast to notify other components
        sendBroadcast(Intent("com.yourapp.NETWORK_RESTORED"))
    }

    override fun onTerminate() {
        super.onTerminate()
        networkMonitor.stopMonitoring()
    }

    // Utility method to get current network status
    fun isNetworkAvailable(): Boolean {
        return networkMonitor.getCurrentStatus()
    }
}