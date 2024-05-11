package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import tech.zaidaziz.assignmentimagesgrid.data.home.services.ApiService
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.inject.Inject

class HomeNetworkDataSource @Inject constructor(){
class HomeNetworkDataSource @Inject constructor(
    val apiService: ApiService
) {
    suspend fun getMediaCoverages() = apiService.getMediaCoverages()
}