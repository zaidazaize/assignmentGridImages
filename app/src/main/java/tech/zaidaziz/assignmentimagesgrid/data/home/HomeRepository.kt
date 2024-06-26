package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.zaidaziz.assignmentimagesgrid.data.di.DispatcherModule
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.Result
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import java.io.File
import javax.inject.Inject
import javax.inject.Named

interface HomeRepository {
    //    suspend fun getMediaCoveragesResponse(): Result<List<ImageModel>>{
    //
    //    }
    suspend fun getMediaCoverages(): Result<List<ImageModel>>

    suspend fun getThumbNail(thumbnailDetail: ThumbnailDetail, sizeGot: Int): ImageBitmap?

    suspend fun getAllLocalThumbnailFileNames(): List<ThumbnailDetail>
    fun refreshMediaCoverages()
}

class HomeRepositoryImpl @Inject constructor(
    private val homeNetworkDataSource: HomeNetworkDataSource,
    private val homeLocalDataSource: HomeLocalDataSource,
    @Named(DispatcherModule.IO_DISPATCHER)
    private val ioDispatcher:CoroutineDispatcher = Dispatchers.IO
) : HomeRepository {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
      var mediaCoverages: List<ImageModel>? = null

    //    suspend fun getMediaCoveragesResponse(): Result<List<ImageModel>>{
    //
    //    }
    override suspend fun getMediaCoverages(): Result<List<ImageModel>> {
       return  if (mediaCoverages == null) {
           val result =  withContext(ioDispatcher) {
                val response = homeNetworkDataSource.getMediaCoverages()
                if (response.isSuccessful) {
                    mediaCoverages = response.body()
                    Result.Success(mediaCoverages!!)
                }else{
                    Result.Error(Exception(response.message()))
                }
            }
           result
        }else{
             Result.Success(mediaCoverages!!)
        }
    }

    override suspend fun getThumbNail(thumbnailDetail: ThumbnailDetail, sizeGot: Int): ImageBitmap? {
        if (thumbnailDetail.thumbnailBitmap != null) {
            return thumbnailDetail.thumbnailBitmap
        }
        return withContext(Dispatchers.IO) {
            val size = if (sizeGot <= 0) 480 else sizeGot

            val thumbnailFile =
                homeLocalDataSource.getSavedThumbnail(thumbnailDetail.getThumbnailFileName())

            if (thumbnailFile != null) {
                val bitmap = decodeSampledBitmapFromFile(thumbnailFile, size, size)
                if (bitmap != null) {
                    thumbnailDetail.thumbnailBitmap = bitmap.asImageBitmap()
                } else {
                    thumbnailDetail.thumbnailError = true
                }
            } else {
                val bitmap = homeNetworkDataSource.downloadImage(thumbnailDetail.getUrl())
                if (bitmap != null) {
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
                    homeLocalDataSource.saveThumbnail(thumbnailDetail.getThumbnailFileName(),
                                                      bitmap)
                    thumbnailDetail.thumbnailBitmap = scaledBitmap.asImageBitmap()
                } else {
                    thumbnailDetail.thumbnailError = true
                }
            }
            thumbnailDetail.thumbnailBitmap
        }
    }

    override suspend fun getAllLocalThumbnailFileNames(): List<ThumbnailDetail> {
        return withContext(Dispatchers.IO) {
            homeLocalDataSource.getLocalThumbnailFileNames().map {
                ThumbnailDetail(id = it.split(".")[0])
            }
        }
    }

    override fun refreshMediaCoverages() {
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