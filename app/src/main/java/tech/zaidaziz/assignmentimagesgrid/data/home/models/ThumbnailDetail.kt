package tech.zaidaziz.assignmentimagesgrid.data.home.models


import androidx.compose.ui.graphics.ImageBitmap
import com.squareup.moshi.Json

data class ThumbnailDetail(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "version")
    val version: Int = 0,
    @Json(name = "domain")
    val domain: String = "",
    @Json(name = "basePath")
    val basePath: String = "",
    @Json(name = "key")
    val key: String = "",
    @Json(name = "qualities")
    val qualities: List<Int> = listOf(),
    @Json(name = "aspectRatio")
    val aspectRatio: Double = 0.0
) {
    @Transient
    var thumbnailBitmap : ImageBitmap? = null
    fun getUrl(): String {
        return "$domain/$basePath/0/$key"

    }
    @Transient
    var thumbnailError : Boolean = false
    fun getThumbnailFileName(): String {
        return "$id.jpg"
    }
}