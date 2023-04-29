package com.example.findequalphoto.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PhotoRepoImpl(val context:Context) :PhotoRepo{



    private val photoList = mutableListOf<Photo>()

    private val _statePhoto:MutableStateFlow<StatePhoto> = MutableStateFlow(StatePhoto())
    override val statePhoto:StateFlow<StatePhoto>
        get() = _statePhoto

    override  suspend fun getAllPhoto() {

        _statePhoto.emit(StatePhoto())
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
        _statePhoto.emit(StatePhoto(progress =  1))
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
            _statePhoto.emit(StatePhoto(photoList.toList(), 100))
        }

    }

    override suspend fun deletePhoto() {
        TODO("Not yet implemented")
    }

    override suspend fun findDuplicatesPhoto() {
       /* val img1: Mat = Highgui.imread("mnt/sdcard/IMG-20121228.jpg")
        val img2: Mat = Highgui.imread("mnt/sdcard/IMG-20121228-1.jpg")
        val result = Mat()

        Core.compare(img1, img2, result, Core.CMP_NE)

        val `val`: Int = Core.countNonZero(result)

        if (`val` == 0) {
            //Duplicate Image
        } else {
            //Different Image
        }*/
    }


}


data class StatePhoto(val photos:List<Photo> = emptyList(),val progress:Int=0)



interface PhotoRepo{



    val statePhoto:StateFlow<StatePhoto>


    suspend fun  getAllPhoto()


    suspend fun deletePhoto()

    suspend  fun findDuplicatesPhoto()


}