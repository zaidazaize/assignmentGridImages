package tech.zaidaziz.assignmentimagesgrid.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.R
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail

@Composable
fun LoadImage(
    thumbnailModel: ThumbnailDetail,
    loadImage: suspend (ThumbnailDetail, Int) -> Boolean,
    size: Int,
    noInternet: Boolean,
    onclick: () -> Unit,
) { // TODO: add onclick to open the image in a new screen or dialog
    val imageBitmap = remember { mutableStateOf(thumbnailModel.thumbnailBitmap) }

    val scope = rememberCoroutineScope()

    DisposableEffect(thumbnailModel.id) {
        var job: Job? = null
        if (thumbnailModel.thumbnailBitmap == null) job = scope.launch {
            val isdown = loadImage(thumbnailModel, size)
            if (isdown.not()) {
                return@launch
            }
            imageBitmap.value = thumbnailModel.thumbnailBitmap
        }
        onDispose {
            job?.cancel()
        }
    }
    Image(painter = if (imageBitmap.value != null) BitmapPainter(imageBitmap.value!!)
    else if (noInternet || thumbnailModel.thumbnailError) painterResource(id = R.drawable.baseline_broken_image_24)
    else painterResource(id = R.drawable.baseline_image_24),
          contentDescription = null,
          modifier = Modifier
              .aspectRatio(1f)
              .fillMaxSize())

}