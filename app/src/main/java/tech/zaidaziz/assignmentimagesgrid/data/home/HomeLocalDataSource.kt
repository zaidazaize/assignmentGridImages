package tech.zaidaziz.assignmentimagesgrid.data.home

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.MainApplication
import tech.zaidaziz.assignmentimagesgrid.data.di.StorageModule.Companion.LOCAL_THUMBNAIL_DIR
import java.io.File
import javax.inject.Inject
import javax.inject.Named

interface HomeLocalDataSource {

    fun getSavedThumbnail(thumbnailFileName: String): File?
    fun updateLocalThumbnailFiles()
    fun refreshThumbnailFiles()

    suspend fun getLocalThumbnailFileNames(): List<String>

    // global scope is used here because the thumbnail is saved in the cache directory
    fun saveThumbnail(fileName: String, bitmap: Bitmap)
}

class HomeLocalDataSourceImpl @Inject constructor(
    @ApplicationContext val context: Context,
    @Named(LOCAL_THUMBNAIL_DIR)
    private val thumbnailDir: File
) : HomeLocalDataSource {

    val applicationScopeCoroutines =
        (context.applicationContext as MainApplication).applicationScopeCoroutines

    private var _localThumbnailFiles: Set<String> = emptySet()
    override fun getSavedThumbnail(thumbnailFileName: String): File? {
        // TODO: for more high performance utilize the _localThumbnailFiles set
        return try {
            val thumbnailFile = File(thumbnailDir, thumbnailFileName)
            if (thumbnailFile.exists()) {
                thumbnailFile
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        //        else null
    }

    override fun updateLocalThumbnailFiles() {
        try {
            val files = thumbnailDir.listFiles()
            _localThumbnailFiles = files?.map { it.name }?.toSet() ?: emptySet()
        } catch (e: Exception) {
            Log.e("HomeLocalDataSource", "updateLocalThumbnailFiles: ", e)
        }
    }

    override fun refreshThumbnailFiles() {
        updateLocalThumbnailFiles()
    }

    override suspend fun getLocalThumbnailFileNames(): List<String> {
        return try {
            val files = thumbnailDir.listFiles()
            files?.map { it.name } ?: emptyList()
        } catch (e: Exception) {
            Log.e("HomeLocalDataSource", "getLocalThumbnailFilesNames: ", e)
            emptyList()
        }
    }


    // global scope is used here because the thumbnail is saved in the cache directory
    override fun saveThumbnail(fileName: String, bitmap: Bitmap) {
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
                //                _localThumbnailFiles =
                //                    _localThumbnailFiles + fileName
            } catch (e: Exception) {
                Log.d("HomeLocalDataSource", "saveThumbnail:${e.message}")
                // do nothing for now
            }
        }
    }

}