package tech.zaidaziz.assignmentimagesgrid.data.home.models


import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.squareup.moshi.Json

data class ImageModel(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "title")
    val title: String = "",
    @Json(name = "language")
    val language: String = "",
    @Json(name = "thumbnail")
    val thumbnail: ThumbnailDetail = ThumbnailDetail(),
    @Json(name = "mediaType")
    val mediaType: Int = 0,
    @Json(name = "coverageURL")
    val coverageURL: String = "",
    @Json(name = "publishedAt")
    val publishedAt: String = "",
    @Json(name = "publishedBy")
    val publishedBy: String = "",
    @Json(name = "backupDetails")
    val backupDetails: BackupDetails = BackupDetails()

){

}