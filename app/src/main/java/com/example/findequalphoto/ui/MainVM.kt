package com.example.findequalphoto.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.findequalphoto.data.Photo
import com.example.findequalphoto.data.PhotoRepo
import com.example.findequalphoto.data.PhotoRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainVM(application: Application, private val photoRepo: PhotoRepo) : AndroidViewModel(application) {



    private val _statePhotoUI: MutableStateFlow<StateUI> = MutableStateFlow(StateUI.Start)
    val statePhoto: StateFlow<StateUI>
        get() = _statePhotoUI


    fun findPhoto(){
        viewModelScope.launch {
            _statePhotoUI.emit(StateUI.Loading(0.01f))
            photoRepo.getAllPhoto()
        }
    }


    fun deletePhoto(){


    }

    init {
        viewModelScope.launch {
            photoRepo.statePhoto.collect{
                if(it.progress in 1..99){
                    _statePhotoUI.emit(StateUI.Loading((it.progress / 100).toFloat()))
                }else if(it.progress==100 || it.photos.isNotEmpty()){
                    _statePhotoUI.emit(StateUI.Loaded(it.photos))
                }else{
                    _statePhotoUI.emit(StateUI.Start)
                }
            }
        }
    }


    companion object{
       fun getMainVM(application: Application, photoRepo: PhotoRepo)=object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MainVM(application, photoRepo) as T
        }
    }

}

/***/
sealed class StateUI(){
    object Start: StateUI()
    class Loading(val progress:Float): StateUI()
    class Loaded(val photos:List<Photo>): StateUI()
}





