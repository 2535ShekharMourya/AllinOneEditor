package com.example.collageproject.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper

class NetworkConnectionMonitor(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isMonitoring = false

    // Listeners for different events
    private var onConnectionLost: (() -> Unit)? = null
    private var onConnectionRestored: (() -> Unit)? = null
    private var onConnectionChanged: ((Boolean) -> Unit)? = null

    fun startMonitoring(
        onLost: (() -> Unit)? = null,
        onRestored: (() -> Unit)? = null,
        onChanged: ((Boolean) -> Unit)? = null
    ) {
        if (isMonitoring) return

        onConnectionLost = onLost
        onConnectionRestored = onRestored
        onConnectionChanged = onChanged
        isMonitoring = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Handler(Looper.getMainLooper()).post {
                        onConnectionLost?.invoke()
                        onConnectionChanged?.invoke(false)
                    }
                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    // Double check that we actually have internet capability
                    val capabilities = connectivityManager.getNetworkCapabilities(network)
                    val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                    if (hasInternet) {
                        Handler(Looper.getMainLooper()).post {
                            onConnectionRestored?.invoke()
                            onConnectionChanged?.invoke(true)
                        }
                    }
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                    Handler(Looper.getMainLooper()).post {
                        onConnectionChanged?.invoke(hasInternet)
                        if (hasInternet) {
                            onConnectionRestored?.invoke()
                        } else {
                            onConnectionLost?.invoke()
                        }
                    }
                }
            }

            val builder = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback!!)
        } else {
            // For older Android versions, use a different approach
            startLegacyMonitoring()
        }
    }

    @Suppress("DEPRECATION")
    private fun startLegacyMonitoring() {
        // For API < 24, use broadcast receiver or polling
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val isConnected = NetworkUtils.isInternetAvailable(context!!)
                Handler(Looper.getMainLooper()).post {
                    onConnectionChanged?.invoke(isConnected)
                    if (isConnected) {
                        onConnectionRestored?.invoke()
                    } else {
                        onConnectionLost?.invoke()
                    }
                }
            }
        }
        context.registerReceiver(receiver, filter)
    }

    fun stopMonitoring() {
        if (!isMonitoring) return

        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                // Callback might already be unregistered
            }
        }
        networkCallback = null
        isMonitoring = false

        // Clear listeners
        onConnectionLost = null
        onConnectionRestored = null
        onConnectionChanged = null
    }

    fun getCurrentStatus(): Boolean {
        return NetworkUtils.isInternetAvailable(context)
    }
}