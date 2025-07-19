package com.vhra.game.solitaire.gl.utils

import android.util.Log

object Logger {
//    const val TAG = "solitaire"
    const val TAG = "devlog"

    fun logd(text: String) {
        Log.d(TAG, text)
    }

    fun loge(text: String, e: Exception) {
        Log.e(TAG, text, e)
    }
}