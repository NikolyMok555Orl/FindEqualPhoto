package com.example.findequalphoto.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.findequalphoto.data.Photo
import com.example.findequalphoto.data.PhotoRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainVM(private val photoRepo: PhotoRepo) : ViewModel() {


    private val _statePhotoUI: MutableStateFlow<StateUI> = MutableStateFlow(StateUI.Start)
    val statePhoto: StateFlow<StateUI>
        get() = _statePhotoUI

    fun selectPhoto(photo: Photo, indexPhoto: Int) {
        viewModelScope.launch {
            photoRepo.selectPhoto(photo, indexPhoto)
        }
    }

    fun deletePhoto(isFinish: Boolean = false) {
        viewModelScope.launch {
            if (!isFinish) {
                photoRepo.deleteAllPhoto()
            } else {
                photoRepo.finishProgress()
            }
        }
    }

    init {
        viewModelScope.launch {
            photoRepo.statePhoto.collect {
                if (it.progress > 0.0f && it.progress < 1.0f) {
                    _statePhotoUI.emit(StateUI.Loading(it.progress))
                } else if (it.photos.isNotEmpty()) {
                    _statePhotoUI.emit(StateUI.Loaded(it.photos, "${it.getFreeSize()}"))
                } else if (it.progress == 1.0f) {
                    _statePhotoUI.emit(StateUI.Empty)
                } else {
                    _statePhotoUI.emit(StateUI.Start)
                }
            }
        }
    }


    companion object {
        fun getMainVM(photoRepo: PhotoRepo) = object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MainVM(photoRepo) as T
        }
    }

}

/***/
sealed class StateUI() {
    object Start : StateUI()
    class Loading(val progress: Float) : StateUI()
    class Loaded(val photos: List<List<Photo>>, val sizeDelete: String = "X") : StateUI()
    object Empty : StateUI()
}





