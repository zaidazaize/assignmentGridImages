package tech.zaidaziz.assignmentimagesgrid.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import tech.zaidaziz.assignmentimagesgrid.util.ShowError
import tech.zaidaziz.assignmentimagesgrid.util.ShowLoading

@Composable
fun Home(
    homeViewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()


    val screenState by homeViewModel.homeScreenState.collectAsStateWithLifecycle()
    Log.d("HomeScreenState", "ScreenState: $screenState")
    val isInternet = homeViewModel.isOnline.value

    val apiFetchError = when(screenState){
        is HomeScreenState.WithInternet -> (screenState as HomeScreenState.WithInternet).error != null
        else -> false
    }

    Scaffold(topBar = {
        Column {

            Text(text = "Welcome",
                 modifier = Modifier.fillMaxWidth(),
                 textAlign = TextAlign.Center,
                 style = MaterialTheme.typography.titleMedium)
            if (isInternet.not()) {
                Text(
                    text = "No Internet Connection",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .background(Color.Red),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

            }else if(apiFetchError){
                Text(
                    text = "Error fetching data from the server",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .background(Color.Red),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

    ) { innerPadding ->


        when (screenState) {

            is HomeScreenState.WithoutInternet -> {

                WithoutInternetScreen(screenState, homeViewModel, scope, innerPadding)
            }

            is HomeScreenState.WithInternet -> {
                WithInternetScreen(screenState, homeViewModel, scope, innerPadding)
            }

        }


    }
}

@Composable
private fun WithInternetScreen(
    screenState: HomeScreenState,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    innerPadding: PaddingValues
) {
    LaunchedEffect(null) {
        if (screenState.mediaCoverage.isEmpty()) {
            homeViewModel.getMediaCoverages()
        }
    }
    when {
        screenState.isLoading -> {
            ShowLoading()
        }

        screenState.error != null && screenState.mediaCoverage.isEmpty() -> {
            if(screenState.localThumbnailDetails.isNotEmpty()){
                HomeScreenForLocalCaches(mediaCoverages = screenState.localThumbnailDetails,
                                         loadImage = homeViewModel::getThumbnail,
                                         scope = scope,
                                         modifier = Modifier.padding(innerPadding))
            }
            else
            ShowError(error = screenState.error!!.message ?: "")

        }

        else -> {
            HomeScreen(mediaCoverages = screenState.mediaCoverage,
                       onRefresh = { homeViewModel.refreshMediaCoverages() },
                       loadImage = homeViewModel::getThumbnail,
                       scope = scope,
                       modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun WithoutInternetScreen(
    screenState: HomeScreenState,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    innerPadding: PaddingValues
) {
    when {

        screenState.mediaCoverage.isEmpty() && (screenState as HomeScreenState.WithoutInternet).localThumbnailDetails.isEmpty() -> {

            ShowError(error = "Please connect to the internet to get the latest media coverages")

        }

        screenState.mediaCoverage.isEmpty() && (screenState as HomeScreenState.WithoutInternet).localThumbnailDetails.isNotEmpty() -> HomeScreenForLocalCaches(
            mediaCoverages = (screenState as HomeScreenState.WithoutInternet).localThumbnailDetails,
            loadImage = homeViewModel::getThumbnail,
            scope = scope,
            modifier = Modifier.padding(innerPadding))


        else -> {
            HomeScreen(modifier = Modifier.padding(innerPadding),
                       mediaCoverages = screenState.mediaCoverage,
                       onRefresh = {
                           homeViewModel.refreshMediaCoverages()
                       },
                       loadImage = homeViewModel::getThumbnail,
                       scope = scope,
                       noInternet = true)
        }

    }
}

@Composable
fun HomeScreen(
    mediaCoverages: List<ImageModel>,
    onRefresh: () -> Unit,
    loadImage: suspend (ThumbnailDetail, Int) -> Boolean,
    scope: CoroutineScope,
    noInternet: Boolean = false,
    modifier: Modifier
) {
    val size = remember { mutableIntStateOf(0) }

    LazyVerticalGrid(columns = GridCells.Fixed(3),
                     verticalArrangement = Arrangement.spacedBy(4.dp),
                     horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                     modifier = modifier
                         .fillMaxSize()
                         .onSizeChanged {
                             size.intValue = it.width / 3
                         },
                     state = rememberLazyGridState()) { //        if (noInternet) {
        // For scrollable hint for no internet
        //            item(span = { GridItemSpan(3) }) {
        //                Text(
        //                    text = "No Internet Connection",
        //                    modifier = Modifier
        //                        .padding(vertical = 4.dp)
        //                        .fillMaxWidth()
        //                        .background(Color.Red),
        //                    color = Color.Black,
        //                    textAlign = TextAlign.Center,
        //                )
        //
        //            }
        //
        //        }
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
    modifier: Modifier,
) {
    val size = remember { mutableIntStateOf(0) }

    LazyVerticalGrid(columns = GridCells.Fixed(3),
                     verticalArrangement = Arrangement.spacedBy(4.dp),
                     horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                     modifier = modifier
                         .fillMaxSize()
                         .onSizeChanged {
                             size.intValue = it.width / 3
                         },
                     state = rememberLazyGridState()) { //        item(span = { GridItemSpan(3) }) {
        //            Text(
        //                text = "No Internet Connection",
        //                modifier = Modifier
        //                    .padding(vertical = 4.dp)
        //                    .fillMaxWidth()
        //                    .background(Color.Red),
        //                color = Color.Black,
        //                textAlign = TextAlign.Center,
        //            )
        //
        //
        //        }
        items(mediaCoverages.size) { index ->
            LoadImage(thumbnailModel = mediaCoverages[index],
                      loadImage = loadImage,
                      size = size.intValue,
                      noInternet = true,
                      onclick = {})
        }
    }
}

