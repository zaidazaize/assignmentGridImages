package tech.zaidaziz.assignmentimagesgrid.data.home.models


import com.squareup.moshi.Json

data class BackupDetails(
    @Json(name = "pdfLink")
    val pdfLink: String = "",
    @Json(name = "screenshotURL")
    val screenshotURL: String = ""
)