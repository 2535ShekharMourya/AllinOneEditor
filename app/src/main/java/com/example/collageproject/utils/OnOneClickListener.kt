package com.example.collageproject.utils

import android.os.SystemClock
import android.view.View
import com.example.collageproject.extensions.globalButtonClickTIme

abstract class OnOneClickListener(private val minClockInterval: Long) : View.OnClickListener {
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - globalButtonClickTIme < 300) {
            return
        } else {
            if (currentTime - lastClickTime > minClockInterval) {
                lastClickTime = currentTime
                globalButtonClickTIme = currentTime
                onOneClick(v)
            }
        }
    }

    abstract fun onOneClick(v: View)
}
