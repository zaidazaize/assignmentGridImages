package tech.zaidaziz.assignmentimagesgrid.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel

@Composable
fun Home(
    homeViewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()

    val mediaCoverages = homeViewModel.mediaCoverages.value
    LaunchedEffect(null) {

        if (mediaCoverages.isEmpty()) {
            homeViewModel.getMediaCoverages()
        }
    }

    HomeScreen(
        mediaCoverages = mediaCoverages,
        onRefresh = {
            homeViewModel.refreshMediaCoverages()
        },
        loadImage = homeViewModel::getThumbnail,
        scope = scope
    )
}

@Composable
fun HomeScreen(
    mediaCoverages: List<ImageModel>,
    onRefresh: () -> Unit,
    loadImage: suspend (ImageModel, Int) -> Boolean,
    scope: CoroutineScope
) {
    val size = remember { mutableStateOf(0) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                size.value = it.width / 3
            },
        content = {
            items(mediaCoverages.size) { index ->
                LoadImage(
                    imageModel = mediaCoverages[index],
                    loadImage = loadImage,
                    size = size.value,
                    scope = scope
                )
                //                val url = mediaCoverages[index].thumbnail.getUrl()
                //                AsyncImage(model =url , contentDescription = mediaCoverages[index].title)
            }
        }

    )
}

@Composable
fun LoadImage(
    imageModel: ImageModel,
    loadImage: suspend (ImageModel, Int) -> Boolean,
    size: Int,
    scope: CoroutineScope
) {

}
