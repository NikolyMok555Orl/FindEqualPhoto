package com.example.findequalphoto

import android.content.Loader
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity(), LoaderCallbackInterface {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // OpenCVLoader.initAsync("4.6.0", this, this);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, this);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            this.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        setContent {
            FindEqualPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   AppNavHost()
                }
            }
        }
    }

    override fun onManagerConnected(status: Int) {
        when (status) {
            LoaderCallbackInterface.SUCCESS -> {
                Log.i("OPENCVS", "OpenCV loaded successfully")
            }
            else -> {
                Log.i("OPENCVS", "OpenCV loaded error")
            }
        }
    }

    override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface?) {
        Log.i("OPENCVS", "OpenCV onPackageInstall")



    }
}
