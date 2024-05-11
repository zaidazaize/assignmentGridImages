package tech.zaidaziz.assignmentimagesgrid.ui.home

import androidx.lifecycle.ViewModel
import javax.inject.Inject


class HomeViewModel  @Inject constructor() : ViewModel() {
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

}