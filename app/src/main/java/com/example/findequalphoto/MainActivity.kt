package com.example.findequalphoto

import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.findequalphoto.data.PhotoRepoImpl
import com.example.findequalphoto.ui.MainVM
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import kotlinx.coroutines.launch
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity(), LoaderCallbackInterface, DeletePhotoExternal {

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>


    val repo: PhotoRepoImpl
        get() = (application as App).getRepo(contentResolver, ::deletePhotoFromExternalStorage)

    private val mainVM: MainVM by viewModels(
        factoryProducer = { MainVM.getMainVM(repo) }

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // OpenCVLoader.initAsync("4.6.0", this, this);
        if (!OpenCVLoader.initDebug()) {
            Log.d(
                "OpenCV",
                "Internal OpenCV library not found. Using OpenCV Manager for initialization"
            );
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, this);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            this.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        lifecycleScope.launch {
                            mainVM.deletePhoto()
                        }
                    } else {
                        mainVM.deletePhoto(true)
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Photo deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Photo couldn't be deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        setContent {
            FindEqualPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.onBackground
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

    override fun deletePhotoFromExternalStorage(sender: IntentSender) {
        intentSenderLauncher.launch(
            IntentSenderRequest.Builder(sender).build()
        )
    }

}

interface DeletePhotoExternal {

    fun deletePhotoFromExternalStorage(sender: IntentSender)


}



