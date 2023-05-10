package com.example.findequalphoto.data

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import com.example.findequalphoto.BuildConfig
import com.example.findequalphoto.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.RoundingMode
import java.text.DecimalFormat


class PhotoRepoImpl(
    private val contentResolver: ContentResolver,
    var senderAsDelete: (sended: IntentSender) -> Unit
) : PhotoRepo {


    private val photoList = mutableListOf<Photo>()

    private val _statePhoto: MutableStateFlow<StatePhoto> = MutableStateFlow(StatePhoto())
    override val statePhoto: StateFlow<StatePhoto>
        get() = _statePhoto


    private suspend fun getAllPhoto(): Boolean {
        withContext(Dispatchers.Main) {
            _statePhoto.emit(StatePhoto())
        }
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )

        val queryArgs = Bundle()
        // Set the reverse order
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )

            // Set the reverse condition - file add time
            queryArgs.putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
            )
        }

        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        val query = contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )
        withContext(Dispatchers.Main) {
            _statePhoto.emit(StatePhoto(progress = 0.01f))
        }
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                photoList += Photo(contentUri, name, size)
            }
            withContext(Dispatchers.Main) {
                _statePhoto.emit(StatePhoto(progress = 0.02f))
            }
            return true
        }
        return false
    }

    override suspend fun deleteAllPhoto() {
        withContext(Dispatchers.Main) {
            _statePhoto.emit(_statePhoto.value.copy(progress = 0.01f))
        }
        val deletesPhoto = _statePhoto.value.getSelectPhoto()
        val step = 1.0f / deletesPhoto.size
        try {
            withContext(Dispatchers.IO) {
                deletesPhoto.forEach {
                    val res = contentResolver.delete(it.uri, null, null)
                    if (res >= 1) {
                        MainScope().launch {
                            val progress = _statePhoto.value.progress + step
                            _statePhoto.emit(_statePhoto.value.copy(progress = if (progress > 1.0f) 1.0f else progress))
                        }.join()
                    }
                }
                withContext(Dispatchers.Main) {
                    _statePhoto.emit(_statePhoto.value.copy(progress = 1.0f))
                    _statePhoto.emit(StatePhoto())
                }
            }
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    MediaStore.createDeleteRequest(
                        contentResolver,
                        deletesPhoto.map { it.uri }.toList()
                    ).intentSender
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException =
                        e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            withContext(Dispatchers.Main) {
                _statePhoto.emit(StatePhoto())
            }
            intentSender?.let { sender ->
                senderAsDelete(sender)
            }
        }
    }


    override suspend fun selectPhoto(photo: Photo, indexAllPhotos: Int) {
        if (indexAllPhotos > _statePhoto.value.photos.size) return
        val indexPhoto = _statePhoto.value.photos[indexAllPhotos].indexOf(photo)
        if (indexPhoto >= 0) {
            var photos = _statePhoto.value.photos[indexAllPhotos].toMutableList()
            photos[indexPhoto] = photos[indexPhoto].copy(isSelect = !photos[indexPhoto].isSelect)
            val photosTimeStap = _statePhoto.value.photos.toMutableList()
            photosTimeStap[indexAllPhotos] = photos
            _statePhoto.emit(StatePhoto(photosTimeStap.toList(), progress = 1.0f))
        }
    }

    override suspend fun finishProgress() {
        withContext(Dispatchers.Main) {
            _statePhoto.emit(_statePhoto.value.copy(progress = 1.0f))
            _statePhoto.emit(StatePhoto())
        }
    }


    override suspend fun findDuplicatesPhoto() {
        val isFindPhoto = getAllPhoto()
        if (isFindPhoto) {
            val duplicatesPhoto =
                FindDuplicatesPhoto(
                    photoList.subList(0, 500),
                    contentResolver,
                    _statePhoto.value.progress
                )
            MainScope().launch {
                duplicatesPhoto.progress.collect {
                    _statePhoto.emit(StatePhoto(progress = it))
                }
            }
            val res = duplicatesPhoto.findDublicates()
            withContext(Dispatchers.Main) {
                _statePhoto.emit(StatePhoto(res, 1.0f))
            }
        }

    }


}

data class StatePhoto(val photos: List<List<Photo>> = emptyList(), val progress: Float = 0.0f) {

    fun getSelectPhoto() = photos.flatten().filter { it.isSelect }

    /**Получаем потенциальное свободно пространство после удаление*/
    fun getFreeSize(): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        val allDeletePhoto = getSelectPhoto()
        val size=allDeletePhoto.sumOf { it.size } / 1073741824.0
        return  df.format(size).toDouble()
    }
}


interface PhotoRepo {


    val statePhoto: StateFlow<StatePhoto>

    suspend fun deleteAllPhoto()

    suspend fun findDuplicatesPhoto()

    suspend fun selectPhoto(photo: Photo, indexPhoto: Int)

    suspend fun finishProgress()

}