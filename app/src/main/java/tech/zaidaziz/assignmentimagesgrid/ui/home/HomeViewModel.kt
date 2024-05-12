package tech.zaidaziz.assignmentimagesgrid.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeRepository
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.Result
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail
import tech.zaidaziz.assignmentimagesgrid.util.ConnectivityObserver
import javax.inject.Inject

sealed interface HomeScreenState {

    val isLoading: Boolean
    val mediaCoverage: List<ImageModel>
    val error: Exception?
    val localThumbnailDetails: List<ThumbnailDetail>

    data class WithoutInternet(
        val message: String,
        override val isLoading: Boolean,
        override val mediaCoverage: List<ImageModel>,
        override val error: Exception? = null,
        override val localThumbnailDetails: List<ThumbnailDetail> = emptyList(),
    ) : HomeScreenState

    data class WithInternet(
        override val isLoading: Boolean,
        override val mediaCoverage: List<ImageModel>,
        override val error: Exception? = null,
        override val localThumbnailDetails: List<ThumbnailDetail> = emptyList(),
    ) : HomeScreenState

}

data class ViewModalScreenState(
    val isLoading: Boolean = true,
    val internetAvailable: Boolean = false,
    val mediaCoverage: List<ImageModel> = emptyList(),
    val error: Exception? = null,
    val localThumbnailDetails: List<ThumbnailDetail> = emptyList()
) {

    fun toUiState(): HomeScreenState {
        return when (internetAvailable) {
            true -> {
                HomeScreenState.WithInternet(
                    isLoading = isLoading,
                    mediaCoverage = mediaCoverage,
                    error = error,
                    localThumbnailDetails = localThumbnailDetails
                )
            }

            false -> {
                HomeScreenState.WithoutInternet(
                    message = "No internet connection",
                    isLoading = false,
                    mediaCoverage = mediaCoverage,
                    error = error,
                    localThumbnailDetails = localThumbnailDetails
                )
            }
        }

    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val _mediaCoverages: MutableState<List<ImageModel>> = mutableStateOf(emptyList())

    var isOnline = mutableStateOf(false)

    private val _viewModalScreenState: MutableStateFlow<ViewModalScreenState> = MutableStateFlow(
        ViewModalScreenState(
            isLoading = true,
            internetAvailable = true,
            mediaCoverage = emptyList()
        )
    )

    val homeScreenState = _viewModalScreenState.map(ViewModalScreenState::toUiState).stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = _viewModalScreenState.value.toUiState())


    fun getMediaCoverages() {
        if (isOnline.value.not()) {
            _viewModalScreenState.value = _viewModalScreenState.value.copy(
                internetAvailable = false,
                isLoading = false
            )
            return
        }

        viewModelScope.launch {
            _viewModalScreenState.value = _viewModalScreenState.value.copy(
                isLoading = true
            )
            try {
                when (val result = homeRepository.getMediaCoverages()) {
                    is Result.Success -> {
                        _mediaCoverages.value = result.data
                        _viewModalScreenState.value = _viewModalScreenState.value.copy(
                            isLoading = false,
                            mediaCoverage = result.data,
                            error = null,
                            localThumbnailDetails = emptyList()
                        )
                    }

                    is Result.Error -> {
                        _viewModalScreenState.value = _viewModalScreenState.value.copy(
                            isLoading = false,
                            error = result.exception,
                        )
                        getLocalThumbnailDetails()
                        return@launch
                    }

                    is Result.Loading -> {
                        _viewModalScreenState.value = _viewModalScreenState.value.copy(
                            isLoading = true,
                            error = null
                        )
                        return@launch
                    }
                }
            } catch (e: Exception) {
                _viewModalScreenState.value = _viewModalScreenState.value.copy(
                    isLoading = false,
                    error = e
                )
                return@launch
            }

        }
    }

    fun refreshMediaCoverages() {
        homeRepository.refreshMediaCoverages()
    }

    suspend fun getThumbnail(thumbnailDetails: ThumbnailDetail, size: Int): Boolean {
        return homeRepository.getThumbNail(thumbnailDetails, size) != null
    }

    private suspend fun getLocalThumbnailDetails() {
        viewModelScope.launch {
            val localThumbnailDetails = homeRepository.getAllLocalThumbnailFileNames()
            _viewModalScreenState.value = _viewModalScreenState.value.copy(
                localThumbnailDetails = localThumbnailDetails,
                isLoading = false,
                internetAvailable = isOnline.value
            )
        }
    }

    init {
        viewModelScope.launch {
            connectivityObserver.isOnline().collect {
                _viewModalScreenState.value = _viewModalScreenState.value.copy(
                    internetAvailable = it
                )
                isOnline.value = it
                if (it.not()) {
                    getLocalThumbnailDetails()
                } else if(_mediaCoverages.value.isEmpty()) {
                    getMediaCoverages()
                }
            }
        }
    }

}