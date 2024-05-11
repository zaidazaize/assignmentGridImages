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

    fun downloadImage(url: String): Bitmap? {
        var connection: URLConnection? = null
        var inputStream: InputStream? = null
        return try {

            connection = URL(url).openConnection()
            connection.useCaches = false
            connection.connect()
            inputStream = connection.getInputStream()
            // save file to disk
            val bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(inputStream))
            bitmap

        } catch (e: Exception) {
            null
        } finally {
            inputStream?.close()
            when (connection) {
                is HttpURLConnection -> {
                    connection.disconnect()
                }

                is HttpsURLConnection -> {
                    connection.disconnect()
                }
            }
        }
    }
}

fun printImageSize(bitmap: Bitmap, type: String) {
    val width = bitmap.allocationByteCount / (1024 * 1024)
    Log.d("HomeNetworkDataSource", "$type :$width ")
    Log.d("HomeNetworkDataSource", "$type :${bitmap.width} ${bitmap.height}")
}