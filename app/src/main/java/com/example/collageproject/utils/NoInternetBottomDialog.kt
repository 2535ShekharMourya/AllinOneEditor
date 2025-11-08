package com.example.collageproject.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.collageproject.R
import com.example.collageproject.databinding.DialogNoInternetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import android.provider.Settings
import android.view.Gravity
import android.view.Window
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.DialogFragment


class NoInternetDialog : DialogFragment() {

    private var _binding: DialogNoInternetBinding? = null
    private val binding get() = _binding!!

    private var onRetryClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNoInternetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        setupClickListeners()
        startAnimations()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Make dialog cancelable
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog window properties
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.requestFeature(Window.FEATURE_NO_TITLE)

            // Optional: Set dialog animations
            window.attributes?.windowAnimations = R.style.DialogAnimation
        }

        return dialog
    }

    override fun onStart() {
        super.onStart()

        // Set dialog size and position
        dialog?.window?.let { window ->
            val params = window.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER

            // Add margins
            val displayMetrics = resources.displayMetrics
            val margin = (24 * displayMetrics.density).toInt() // 24dp margins
            params.width = displayMetrics.widthPixels - (margin * 2)

            window.attributes = params
        }
    }

    private fun setupDialog() {
        // Make dialog cancelable by touching outside or back button
        isCancelable = true
    }

    private fun setupClickListeners() {
        binding.btnRetry.setOnClickListener {
            if (NetworkUtils.isInternetAvailable(requireContext())) {
                onRetryClickListener?.invoke()
                dismiss()
            } else {
                // Show feedback that internet is still not available
                binding.tvStatus.text = "Still no internet connection"
                binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_high))

                // Reset text after 2 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded && _binding != null) {
                        binding.tvStatus.text = "Check your connection"
                        binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    }
                }, 2000)
            }
        }

        binding.btnSettings.setOnClickListener {
            // Open device settings for network/wifi
            try {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback to general settings
                val intent = Intent(Settings.ACTION_SETTINGS)
                startActivity(intent)
            }
        }

        // Optional: Close button or dismiss on click outside content
        binding.root.setOnClickListener {
            // Click outside content area - dismiss dialog
            dismiss()
        }

        binding.contentLayout.setOnClickListener {
            // Prevent dismiss when clicking inside content
        }
    }

    private fun startAnimations() {
        // Animate the no internet icon with rotation
        binding.ivNoInternet.animate()
            .rotationBy(360f)
            .setDuration(800)
            .start()

        // Scale and fade in animation for content
        binding.contentLayout.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    fun setOnRetryClickListener(listener: () -> Unit) {
        onRetryClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NoInternetDialog"

        fun newInstance(): NoInternetDialog {
            return NoInternetDialog()
        }
    }

    // Internet availability check function
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}