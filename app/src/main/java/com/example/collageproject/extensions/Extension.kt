package com.example.collageproject.extensions

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.collageproject.utils.NetworkUtils
import com.example.collageproject.utils.OnOneClickListener

// for making view translation and animation
fun View.animateClick(scale: Float = 0.95f, duration: Long = 100) {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
            }
        }
        false // Let click event pass through
    }
}
fun Context.isInternetAvailable(): Boolean = NetworkUtils.isInternetAvailable(this)
fun Fragment.isInternetAvailable(): Boolean = NetworkUtils.isInternetAvailable(requireContext())
fun Activity.isInternetAvailable(): Boolean = NetworkUtils.isInternetAvailable(this)

// for logging purpose only
private const val LOGGING_TAG = "SHEKHAR"
fun Any?.mLog() = Log.d(LOGGING_TAG, ":-: ${this ?: "null"} :-:")
private const val LOG_MASSAGE_TAG = "LOG_MASSAGE_TAG"
fun Any?.logMessage(logLevel: Int = Log.DEBUG) {
    when (logLevel) {
        Log.VERBOSE -> Log.v(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
        Log.DEBUG -> Log.d(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
        Log.INFO -> Log.i(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
        Log.WARN -> Log.w(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
        Log.ERROR -> Log.e(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
        Log.ASSERT -> Log.wtf(LOG_MASSAGE_TAG, "::- ${this ?: "null"} -::")
    }
}

fun Activity?.isRunning(): Boolean = if (this == null) false else !(isDestroyed || isFinishing)
fun Any?.isActivityRunning(): Boolean = if (this == null) false else when {
    this is Activity -> isRunning()
    this is Fragment -> if (activity == null) false else activity!!.isRunning()
    else -> false
}

private val mainHandler = Handler(Looper.getMainLooper())
fun runOnMain(code: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        code()
    } else {
        mainHandler.post {
            try {
                code()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
fun Activity.runOnUiThreadIfRunning(code: () -> Unit) {
    if (isRunning()) {
        Handler(Looper.getMainLooper()).post {
            code()
        }
    }
}

var globalButtonClickTIme: Long = 0
fun View.onOneClick(time: Long = 1000, callback: () -> Unit) {
    setOnClickListener(object : OnOneClickListener(time) {
        override fun onOneClick(v: View) {
            callback()
        }
    })
}


