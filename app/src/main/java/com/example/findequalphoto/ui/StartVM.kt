package com.example.findequalphoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.findequalphoto.data.PhotoRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartVM(private val photoRepo: PhotoRepo):ViewModel() {

    private val _statePhotoUI: MutableStateFlow<StateUI> = MutableStateFlow(StateUI.Start)
    val statePhoto: StateFlow<StateUI>
        get() = _statePhotoUI


    private val _navToNext:MutableSharedFlow<Boolean> = MutableSharedFlow()
    val navToNext:MutableSharedFlow<Boolean> = _navToNext

    fun findPhoto(){
        viewModelScope.launch {
            _statePhotoUI.emit(StateUI.Loading(0.01f))
            withContext(Dispatchers.IO) {
                photoRepo.findDuplicatesPhoto()
            }
        }
    }

    init {
        viewModelScope.launch {
            photoRepo.statePhoto.collect{
                if(it.progress >0.0f && it.progress<1.0f){
                    _statePhotoUI.emit(StateUI.Loading(it.progress))
                }else if( it.photos.isNotEmpty()){
                    _statePhotoUI.emit(StateUI.Loaded(it.photos))
                    _navToNext.emit(true)
                }else if (it.progress==1.0f){
                    _statePhotoUI.emit(StateUI.Empty)
                }else{
                    _statePhotoUI.emit(StateUI.Start)
                }
            }
        }
    }


    companion object{
        fun getStartVM(photoRepo: PhotoRepo)=object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = StartVM(photoRepo) as T
        }
    }


}