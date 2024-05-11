package tech.zaidaziz.assignmentimagesgrid.ui.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeRepository
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val homeRepository: HomeRepository
) : ViewModel() {

    val _mediaCoverages: MutableState<List<ImageModel>> = mutableStateOf(emptyList())
    val mediaCoverages = _mediaCoverages

    fun getMediaCoverages() {
        Log.d("HomeViewModel", "getMediaCoverages: ")
        viewModelScope.launch {
            _mediaCoverages.value = homeRepository.getMediaCoverages()
            Log.d("HomeViewModel", "getMediaCoverages: ${_mediaCoverages.value}")
        }
    }

    fun refreshMediaCoverages() {
        homeRepository.refreshMediaCoverages()
    }

    suspend fun getThumbnail(imageModel: ImageModel, size: Int): Boolean {
        return homeRepository.getThumbNail(imageModel.thumbnail, size) != null
    }

}