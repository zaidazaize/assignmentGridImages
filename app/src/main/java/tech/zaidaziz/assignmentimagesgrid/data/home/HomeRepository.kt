package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import java.io.File
import javax.inject.Inject

class HomeRepository @Inject constructor(){
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

    fun refreshMediaCoverages() {
        mediaCoverages = null
    }
}