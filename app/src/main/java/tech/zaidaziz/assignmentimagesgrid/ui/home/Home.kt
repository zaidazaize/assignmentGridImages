package tech.zaidaziz.assignmentimagesgrid.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.R
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail

@Composable
fun Home(
    homeViewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()


    val screenState by homeViewModel.homeScreenState.collectAsStateWithLifecycle()
    Log.d("HomeScreenState", "ScreenState: $screenState")



    when (screenState) {

        is HomeScreenState.WithoutInternet -> {
            if (screenState.mediaCoverage.isEmpty()) { // load images from cache
                HomeScreenForLocalCaches(mediaCoverages = (screenState as HomeScreenState.WithoutInternet).localThumbnailDetails,
                                         loadImage = homeViewModel::getThumbnail,
                                         scope = scope)

            } else {
                HomeScreen(mediaCoverages = screenState.mediaCoverage, onRefresh = {
                    homeViewModel.refreshMediaCoverages()
                }, loadImage = homeViewModel::getThumbnail, scope = scope, noInternet = true)
            }


        }

        is HomeScreenState.WithInternet -> {
            LaunchedEffect(null) {

                if (screenState.mediaCoverage.isEmpty()) {
                    homeViewModel.getMediaCoverages()
                }
            }
            if (screenState.isLoading) {
                ShowLoading()
            } else if (screenState.error != null && screenState.mediaCoverage.isEmpty()) {
                ShowError(error = screenState.error!!.message ?: "")
            } else {
                HomeScreen(mediaCoverages = screenState.mediaCoverage, onRefresh = {
                    homeViewModel.refreshMediaCoverages()
                }, loadImage = homeViewModel::getThumbnail, scope = scope)
            }
        }

    }


}

@Composable
fun ShowLoading() {
    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
    }
}

@Composable
fun ShowError(
    error: String
) {
    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = if (error.isEmpty()) "An error occurred" else error)
    }
}

@Composable
fun HomeScreen(
    mediaCoverages: List<ImageModel>,
    onRefresh: () -> Unit,
    loadImage: suspend (ThumbnailDetail, Int) -> Boolean,
    scope: CoroutineScope,
    noInternet: Boolean = false
) {
    val size = remember { mutableIntStateOf(0) }

    LazyVerticalGrid(columns = GridCells.Fixed(3),
                     verticalArrangement = Arrangement.spacedBy(4.dp),
                     horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                     modifier = Modifier
                         .fillMaxSize()
                         .onSizeChanged {
                             size.intValue = it.width / 3
                         },
                     state = rememberLazyGridState()) {
        if (noInternet) {
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "No Internet Connection",
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .background(Color.Red),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

            }

        }
        items(mediaCoverages.size) { index ->
            LoadImage(thumbnailModel = mediaCoverages[index].thumbnail,
                      loadImage = loadImage,
                      size = size.intValue,
                      noInternet = noInternet,
                      onclick = {})
        }
    }
}

@Composable
fun HomeScreenForLocalCaches(
    mediaCoverages: List<ThumbnailDetail>,
    loadImage: suspend (ThumbnailDetail, Int) -> Boolean,
    scope: CoroutineScope,
) {
    val size = remember { mutableIntStateOf(0) }

    LazyVerticalGrid(columns = GridCells.Fixed(3),
                     verticalArrangement = Arrangement.spacedBy(4.dp),
                     horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                     modifier = Modifier
                         .fillMaxSize()
                         .onSizeChanged {
                             size.intValue = it.width / 3
                         },
                     state = rememberLazyGridState()) {
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "No Internet Connection",
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .background(Color.Red),
                color = Color.Black,
                textAlign = TextAlign.Center,
            )


        }
        items(mediaCoverages.size) { index ->
            LoadImage(thumbnailModel = mediaCoverages[index],
                      loadImage = loadImage,
                      size = size.intValue,
                      noInternet = true,
                      onclick = {})
        }
    }
}

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
