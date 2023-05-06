package com.example.findequalphoto.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    private var context: Context

    private val _progress: MutableStateFlow<Float> = MutableStateFlow(0.0f)
    val progress: StateFlow<Float>
        get() = _progress


    constructor(photos: List<Photo>, context: Context, progress: Float) {
        this.photos = photos.map { PhotoWithMap(it.uri, it.name, it.size) }.toTypedArray()
        this.context = context
        this._progress.value = progress
    }

    /**Поиск дубликатов*/
    suspend fun findDublicates(): List<List<Photo>> {
        val result = mutableListOf<MutableList<Photo>>()
        val size = photos.size
        val step = (1.0f - _progress.value) / size
        loadAllImage()

        for (i in photos.indices) {
            val iImage = mutableListOf<Photo>()
            Log.i("FIND_DUB", "${i}/${size}")
            for (j in i + 1 until  photos.size) {
                if (photos[i].mat != null && photos[j].mat != null
                    && compareImage(photos[i].mat!!, photos[j].mat!!)
                ) {
                    var isFinded=false
                    result.forEach {photosFind->
                        isFinded=photosFind.find { it.uri==photos[j].uri }!=null

                    }
                    if(isFinded) break
                    if (iImage.isEmpty()) {
                        iImage.add(photos[i].toPhoto())
                        iImage.add(photos[j].toPhoto())
                    } else {
                        iImage.add(photos[j].toPhoto())
                    }
                }
            }
            if (iImage.isNotEmpty()) {
                result.add(iImage)
            }
           val res= MainScope().async {
                _progress.value = _progress.value + step
                _progress.emit(_progress.value)
            }
            res.await()
        }


        return result
    }


    private suspend fun loadAllImage() {
        photos.forEach {
            val img = decodeSampledBitmapFromUri(it.uri, 64, 64)
            img?.let { image ->
                println("Фото загрузилось ${it.name}, размеры: ${image.width}:${image.height}")
                it.mat = bitmapToMap(image)
                Imgproc.cvtColor(it.mat, it.mat, Imgproc.COLOR_RGB2GRAY);
            }
        }


    }


    /**Сравнение фото*/
    private fun compareImage(image1: Mat, image2: Mat): Boolean {
        //val img1: Mat = Imgcodecs.imread("mnt/sdcard/IMG-20121228.jpg")
        return try {
            /*var startTime = System.currentTimeMillis()
                // ваш код, который нужно измерить

            var bitmap1: Bitmap = loadImage(image1)
            var bitmap2: Bitmap = loadImage(image2)
            var totalTime = System.currentTimeMillis() - startTime
            println("Время загрузки $totalTime ms")

            startTime = System.currentTimeMillis()
            bitmap1=Bitmap.createScaledBitmap(
                bitmap1, 64, 64, false);
            bitmap2=Bitmap.createScaledBitmap(
                bitmap2, 64, 64, false);
            totalTime = System.currentTimeMillis() - startTime
            println("Время уменьшение размера $totalTime ms")

            startTime = System.currentTimeMillis()
            var img1=bitmapToMap(bitmap1)
            var img2=bitmapToMap(bitmap2)
            totalTime = System.currentTimeMillis() - startTime
            println("Время конвертации в bitmap map $totalTime ms")

            startTime = System.currentTimeMillis()
            Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
            Imgproc.cvtColor(img2, img2, Imgproc.COLOR_RGB2GRAY);
            totalTime = System.currentTimeMillis() - startTime
            println("Время конвертации в серый цвет $totalTime ms")

            startTime = System.currentTimeMillis()*/
            if(image1.width()!=image2.width() || image1.height()!=image2.height()) return false
            val result = Mat()
            Core.compare(image1, image2, result, Core.CMP_NE)
            val clippingLine=(image1.width()*image1.height()/100.0)*allowableDiffPercentage
            val res = Core.countNonZero(result)
            res <= clippingLine
        } catch (e: Exception) {
            Log.e("LOAD IMG", "${e.message}")
            false
        }
    }

    private fun loadImage(imageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
    }


    //TODO удалить
   /* @Throws(FileNotFoundException::class, IOException::class)
    fun getThumbnail(uri: Uri,  reqWidth: Int,
                     reqHeight: Int): Bitmap? {
        var input: InputStream? = context.contentResolver?.openInputStream(uri)
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null
        }
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, reqWidth, reqHeight)
        bitmapOptions.inDither = true //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888 //
        input = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input?.close()
        return bitmap
    }*/


    /* private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
         val k = Integer.highestOneBit(floor(ratio).toInt())
         return if (k == 0) 1 else k
     }*/

   private fun decodeSampledBitmapFromUri(
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val onlyBoundsOptions = BitmapFactory.Options()
        // First decode with inJustDecodeBounds=true to check dimensions
        onlyBoundsOptions.inJustDecodeBounds = true
        var input: InputStream = context.contentResolver?.openInputStream(uri) ?: return null
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
            return null
        }
        input.close()
        // Calculate inSampleSize
        onlyBoundsOptions.inSampleSize = calculateInSampleSize(onlyBoundsOptions, reqWidth, reqHeight)
        input = context.contentResolver?.openInputStream(uri) ?: return null
        // Decode bitmap with inSampleSize set
        onlyBoundsOptions.inJustDecodeBounds = false
        val res = BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input.close()
        return res
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
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

    companion object{
        const val allowableDiffPercentage=10


    }

}

data class PhotoWithMap(
    val uri: Uri,
    val name: String,
    val size: Int, var mat: Mat? = null
)


fun PhotoWithMap.toPhoto() = Photo(uri, name, size)