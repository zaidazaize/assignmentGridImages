package tech.zaidaziz.assignmentimagesgrid.ui.home

import android.util.Log
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
import tech.zaidaziz.assignmentimagesgrid.util.ConnectivityObserver
import javax.inject.Inject

sealed interface HomeScreenState {

    val isLoading: Boolean
    val mediaCoverage: List<ImageModel>

    data class WithoutInternet(
        val message: String,
        override val isLoading: Boolean,
        override val mediaCoverage: List<ImageModel>
    ) : HomeScreenState

    data class WithInternet(
        override val isLoading: Boolean,
        override val mediaCoverage: List<ImageModel>
    ) : HomeScreenState

}

data class ViewModalScreenState(
    val isLoading: Boolean = true,
    val internetAvailable: Boolean = false,
    val mediaCoverage: List<ImageModel> = emptyList(),
) {

    fun toUiState(): HomeScreenState {
        return when (internetAvailable) {
            true -> {
                HomeScreenState.WithInternet(
                    isLoading = isLoading,
                    mediaCoverage = mediaCoverage
                )
            }

            false -> {
                HomeScreenState.WithoutInternet(
                    message = "No internet connection",
                    isLoading = false,
                    mediaCoverage = mediaCoverage
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
    val mediaCoverages = _mediaCoverages

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

//    val isOnline =
//        connectivityObserver.isOnline().stateIn(viewModelScope, SharingStarted.Eagerly, true).also {
//            _viewModalScreenState.value = _viewModalScreenState.value.copy(
//                internetAvailable = it.value
//            )
//        }

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
                _mediaCoverages.value = homeRepository.getMediaCoverages()
            } catch (e: Exception) {
                return@launch
            }

            _viewModalScreenState.value = _viewModalScreenState.value.copy(
                mediaCoverage = _mediaCoverages.value,
                isLoading = false
            )

            Log.d("HomeViewModel", "getMediaCoverages: ${_mediaCoverages.value}")
        }
    }

    fun refreshMediaCoverages() {
        homeRepository.refreshMediaCoverages()
    }

    suspend fun getThumbnail(imageModel: ImageModel, size: Int): Boolean {
        return homeRepository.getThumbNail(imageModel.thumbnail, size) != null
    }

    init{
        viewModelScope.launch{
            connectivityObserver.isOnline().collect {
                _viewModalScreenState.value = _viewModalScreenState.value.copy(
                    internetAvailable = it
                )
                isOnline.value = it
            }
        }
    }

}