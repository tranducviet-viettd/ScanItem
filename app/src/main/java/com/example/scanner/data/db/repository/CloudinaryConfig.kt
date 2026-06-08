package com.example.scanner.data.db.repository


import android.content.Context
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager

object CloudinaryConfig {
    private const val CLOUD_NAME = "dnumg1qkt" // Thay bằng cloud_name của bạn
    private const val API_KEY = "353258968761637" // Thay bằng api_key
    private const val API_SECRET = "qAt-hWeolHtGmVfNUmyP5lUjPlI" // Thay bằng api_secret

    fun init(context: Context) {
        val config = hashMapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET,
            "secure" to true
        )
        MediaManager.init(context, config)
    }

    fun getCloudinary(): MediaManager? {
        return MediaManager.get()
    }
}