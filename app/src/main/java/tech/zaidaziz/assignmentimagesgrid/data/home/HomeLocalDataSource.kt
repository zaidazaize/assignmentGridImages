package tech.zaidaziz.assignmentimagesgrid.data.home

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.MainApplication
import tech.zaidaziz.assignmentimagesgrid.data.di.StorageModule.Companion.LOCAL_THUMBNAIL_DIR
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class HomeLocalDataSource @Inject constructor(
    @ApplicationContext val context: Context,
    @Named(LOCAL_THUMBNAIL_DIR)
    private val thumbnailDir: File
) {

    val applicationScopeCoroutines =
        (context.applicationContext as MainApplication).applicationScopeCoroutines

    private var _localThumbnailFiles: Set<String> = emptySet()
    fun getSavedThumbnail(thumbnailDetail: ThumbnailDetail): File? {
        //        if (_localThumbnailFiles.isEmpty()) {
        //            return null
        //        }
        //        return if (thumbnailDetail.id in _localThumbnailFiles)
        return try {
            val thumbnailFile = File(thumbnailDir, thumbnailDetail.getThumbnailFileName())
            if (thumbnailFile.exists()) {
                thumbnailFile
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        //        else null
    }

    private fun updateLocalThumbnailFiles() {
        try {
            val files = thumbnailDir.listFiles()
            _localThumbnailFiles = files?.map { it.name }?.toSet() ?: emptySet()
        } catch (e: Exception) {
            Log.e("HomeLocalDataSource", "updateLocalThumbnailFiles: ", e)
        }
    }

    fun refreshThumbnailFiles() {
        updateLocalThumbnailFiles()
    }


    // global scope is used here because the thumbnail is saved in the cache directory
    fun saveThumbnail(fileName: String, bitmap: Bitmap) {
        applicationScopeCoroutines.launch(Dispatchers.IO) {
            try {
                val thumbnailFile = File(thumbnailDir, fileName)
                if (thumbnailFile.exists()) {
                    thumbnailFile.delete()
                }
                thumbnailFile.outputStream().use { fileOut ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut)
                    // TODO: create the viewSize cache
                }
                _localThumbnailFiles =
                    _localThumbnailFiles + fileName
            } catch (_: Exception) {
                // do nothing for now
            }
        }
    }

}