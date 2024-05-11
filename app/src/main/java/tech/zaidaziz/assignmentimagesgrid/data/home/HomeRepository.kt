package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import java.io.File
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeNetworkDataSource: HomeNetworkDataSource,
    private val homeLocalDataSource: HomeLocalDataSource,
) {

    private var mediaCoverages: List<ImageModel>? = null
    suspend fun getMediaCoverages(): List<ImageModel> {
        if (mediaCoverages == null) {
            withContext(Dispatchers.IO) {
                val response = homeNetworkDataSource.getMediaCoverages()
                if (response.isSuccessful) {
                    mediaCoverages = response.body()
                    Log.d("HomeRepository", "getMediaCoverages: ${mediaCoverages?.size}")
                }
            }
        }
        return mediaCoverages ?: emptyList()
    }

    suspend fun getThumbNail(thumbnailDetail: ThumbnailDetail, sizeGot: Int): ImageBitmap? {
        if (thumbnailDetail.thumbnailBitmap != null) {
            return thumbnailDetail.thumbnailBitmap
        }
        return withContext(Dispatchers.IO) {
            val size = if (sizeGot <= 0) 480 else sizeGot
            val thumbnailFile = homeLocalDataSource.getSavedThumbnail(thumbnailDetail)

            Log.d("HomeRepository", "thumbnailExists: ${thumbnailFile}")

            if (thumbnailFile != null) {
                val bitmap = decodeSampledBitmapFromFile(thumbnailFile, size, size)
                if (bitmap != null) {
                    thumbnailDetail.thumbnailBitmap = bitmap.asImageBitmap()
                }
            } else {
                val bitmap = homeNetworkDataSource.downloadImage(thumbnailDetail.getUrl())
                if (bitmap != null) {
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
                    homeLocalDataSource.saveThumbnail(thumbnailDetail.getThumbnailFileName(), bitmap)
                    thumbnailDetail.thumbnailBitmap = scaledBitmap.asImageBitmap()
                }
            }
            thumbnailDetail.thumbnailBitmap
        }
    }
    fun refreshMediaCoverages() {
        mediaCoverages = null
    }

}

fun decodeSampledBitmapFromFile(
    fileRes: File,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    return try {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(fileRes.absolutePath, this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }
        BitmapFactory.decodeFile(fileRes.absolutePath, options)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}