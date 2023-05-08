package com.example.findequalphoto.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor


class FindDuplicatesPhoto {


    private var photos: Array<PhotoWithMap>

    private var contentResolver: ContentResolver

    private val _progress: MutableStateFlow<Float> = MutableStateFlow(0.0f)
    val progress: StateFlow<Float>
        get() = _progress

    private var step: Float

    constructor(photos: List<Photo>, contentResolver: ContentResolver, progress: Float) {
        this.photos = photos.map { PhotoWithMap(it.uri, it.name, it.size) }.toTypedArray()
        this.contentResolver = contentResolver
        this._progress.value = progress
        step = ((1.0f - _progress.value) / photos.size) / 2
    }

    /**Поиск дубликатов*/
    suspend fun findDublicates(): List<List<Photo>> {
        val result = mutableListOf<MutableList<Photo>>()
        val size = photos.size
        loadAllImage()

        val photosFind = photos.clone().toMutableList()
        var sizeFind = photosFind.size
        while (photosFind.isNotEmpty()) {
            val photoCheck = photosFind.first()
            val iImage = mutableListOf<Photo>()
            Log.i("FIND_DUB", "${photosFind.size}/${size}")
            var j = 1
            while (j < sizeFind) {
                if (photoCheck.mat != null && photosFind[j].mat != null
                    && compareImage(photoCheck.mat!!, photosFind[j].mat!!)
                ) {
                    if (iImage.isEmpty()) {
                        iImage.add(photoCheck.toPhoto())
                        iImage.add(photosFind[j].toPhoto())
                    } else {
                        iImage.add(photosFind[j].toPhoto())
                    }
                    photosFind.remove(photosFind[j])
                    sizeFind--
                } else {
                    j += 1
                }
            }
            if (iImage.isNotEmpty()) {
                result.add(iImage)
            }
            photosFind.remove(photoCheck)
            sizeFind--
            val res = MainScope().async {
                _progress.value = _progress.value + step
                _progress.emit(_progress.value)
            }
            res.await()
        }
        return result
    }


    private suspend fun loadAllImage() {
        withContext(Dispatchers.IO) {
            val res = photos.map {
                async {
                    decodeSampledBitmapFromUri(it.uri, 64, 64)
                }
            }
            res.joinAll()
            withContext(Dispatchers.Default) {
                photos.zip(res).forEach {
                    it.second.await()?.let { image ->
                        println("Фото загрузилось ${it.first.name}, размеры: ${image.width}:${image.height}")
                        it.first.mat = bitmapToMap(image)
                        Imgproc.cvtColor(it.first.mat, it.first.mat, Imgproc.COLOR_RGB2GRAY);
                    }

                }
            }
        }
    }


    /**Сравнение фото*/
    private fun compareImage(image1: Mat, image2: Mat): Boolean {
        return try {
            if (image1.width() != image2.width() || image1.height() != image2.height()) return false
            val result = Mat()
            Core.compare(image1, image2, result, Core.CMP_NE)
            val clippingLine = (image1.width() * image1.height() / 100.0) * allowableDiffPercentage
            val res = Core.countNonZero(result)
            res <= clippingLine
        } catch (e: Exception) {
            Log.e("LOAD IMG", "${e.message}")
            false
        }
    }
    private suspend fun decodeSampledBitmapFromUri(
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val onlyBoundsOptions = BitmapFactory.Options()
        // First decode with inJustDecodeBounds=true to check dimensions
        onlyBoundsOptions.inJustDecodeBounds = true
        var input: InputStream = contentResolver?.openInputStream(uri) ?: return null
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null
        }
        input.close()
        // Calculate inSampleSize
        onlyBoundsOptions.inSampleSize =
            calculateInSampleSize(onlyBoundsOptions, reqWidth, reqHeight)
        input = contentResolver?.openInputStream(uri) ?: return null
        // Decode bitmap with inSampleSize set
        onlyBoundsOptions.inJustDecodeBounds = false
        val res = BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input.close()
        //TODO криво, потом перенесу.
        MainScope().launch {
            _progress.value = _progress.value + step
            _progress.emit(_progress.value)
        }.join()
        return res
    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    private fun bitmapToMap(image: Bitmap): Mat {
        val imageMat = Mat(image.height, image.width, CvType.CV_8U, Scalar(4.0))
        val myBitmap32: Bitmap = image.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(myBitmap32, imageMat)
        return imageMat
    }

    companion object {
        const val allowableDiffPercentage = 10


    }

}

data class PhotoWithMap(
    val uri: Uri,
    val name: String,
    val size: Int, var mat: Mat? = null
)


fun PhotoWithMap.toPhoto() = Photo(uri, name, size)