package com.example.findequalphoto.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import org.opencv.core.Core
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.core.Mat


class PhotoRepoImpl(private val context: Context) : PhotoRepo {


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
        queryArgs.putInt(
            ContentResolver.QUERY_ARG_SORT_DIRECTION,
            ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        )
        // Set the reverse condition - file add time
        queryArgs.putStringArray(
            ContentResolver.QUERY_ARG_SORT_COLUMNS,
            arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
        )
        // Page setting
        /*   queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, page * mPageSize)
           queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, mPageSize)*/


        val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
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

    override suspend fun deletePhoto() {
        TODO("Not yet implemented")
    }

    override suspend fun findDuplicatesPhoto() {
        val isFindPhoto = getAllPhoto()
        if (isFindPhoto) {
            val duplicatesPhoto = FindDuplicatesPhoto(photoList.subList(0, 300), context, _statePhoto.value.progress)
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


//data class StatePhoto(val photos:List<Photo> = emptyList(),val progress:Int=0)
data class StatePhoto(val photos: List<List<Photo>> = emptyList(), val progress: Float = 0.0f)


interface PhotoRepo {


    val statePhoto: StateFlow<StatePhoto>


    //  suspend  fun  getAllPhoto()


    suspend fun deletePhoto()

    suspend fun findDuplicatesPhoto()


}