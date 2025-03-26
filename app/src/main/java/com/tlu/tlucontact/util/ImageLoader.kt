// ImageLoader.kt
package com.tlu.tlucontact.util

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.annotation.DrawableRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

object ImageLoader {
    fun loadImage(imageView: ImageView, url: String?, @DrawableRes placeholderResId: Int) {
        // Set the placeholder
        imageView.setImageResource(placeholderResId)

        if (url.isNullOrEmpty()) return

        // Use coroutines to load the image in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()

                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // On error, keep the placeholder image
            }
        }
    }
}