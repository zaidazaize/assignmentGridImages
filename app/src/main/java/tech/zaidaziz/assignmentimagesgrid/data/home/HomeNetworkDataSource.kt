package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import tech.zaidaziz.assignmentimagesgrid.data.home.services.ApiService
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class HomeNetworkDataSource @Inject constructor(
    val apiService: ApiService
) {

    suspend fun getMediaCoverages() = apiService.getMediaCoverages()
    suspend fun downloadImage(url: String): Bitmap? {
        return try {

            val connection = URL(url).openConnection()
            connection.useCaches = false
            connection.connect()
            val inputStream = connection.getInputStream()
            // save file to disk
            val bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(inputStream))
            inputStream.close()
            bitmap

        } catch (e: Exception) {
            null
        }

    }
}