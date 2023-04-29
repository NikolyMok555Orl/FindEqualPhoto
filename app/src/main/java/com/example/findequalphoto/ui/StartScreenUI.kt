package com.example.findequalphoto

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.findequalphoto.ui.MainVM
import com.example.findequalphoto.ui.StateUI
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreenUI(navController: NavController,
                  vm: MainVM = viewModel()) {


    // Camera permission state
    val photoPermissionState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        }
    )

    val state = vm.statePhoto.collectAsState()


    LaunchedEffect(key1 = state.value) {
        if (state.value is StateUI.Loaded) {
            navController.navigate(NavHost.PHOTOS)
        }
    }


    StartScreenUI(state=state.value,findPhoto = {
        if (photoPermissionState.allPermissionsGranted) {
            vm.findPhoto()
        } else {
            photoPermissionState.launchMultiplePermissionRequest()
        }
    })

}


@Composable
fun StartScreenUI(state: StateUI, findPhoto: () -> Unit, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()) {
        Text(text = "Старт")
        if(state is StateUI.Loading) {
            LinearProgressIndicator(progress = (state as StateUI.Loading).progress)
        }else{
            TextButton(onClick = findPhoto) {
                Text("Найти фото")
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun StartScreenUIPreview() {
    FindEqualPhotoTheme {

        StartScreenUI(StateUI.Start,{})

    }
}



