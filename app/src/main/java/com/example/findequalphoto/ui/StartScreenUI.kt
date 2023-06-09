package com.example.findequalphoto.ui

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.findequalphoto.MainActivity
import com.example.findequalphoto.NavHost
import com.example.findequalphoto.ui.component.AppButtonUI
import com.example.findequalphoto.ui.component.AppProgressBar
import com.example.findequalphoto.ui.component.HeaderTextUI
import com.example.findequalphoto.ui.component.LogoUI
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreenUI(
    navController: NavController,
    vm: StartVM = viewModel(
        factory = StartVM.getStartVM(
            (LocalContext.current as MainActivity).repo
        )
    )
) {


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


    LaunchedEffect(key1 = true) {
        vm.navToNext.collect {
            if (it) {
                navController.navigate(NavHost.PHOTOS)
            }
        }

    }


    StartScreenUI(state = state.value, findPhoto = {
        if (photoPermissionState.allPermissionsGranted) {
            vm.findPhoto()
        } else {
            photoPermissionState.launchMultiplePermissionRequest()
        }
    })

}


@Composable
fun StartScreenUI(state: StateUI, findPhoto: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 33.dp, end = 33.dp, top = 17.dp)
    ) {

        HeaderTextUI("Похожие фотографии")
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LogoUI()
            Spacer(modifier = Modifier.padding(top=34.dp))
            when (state) {
                is StateUI.Loading -> {
                    AppProgressBar(state.progress, Modifier.fillMaxWidth())
                    Text(
                        text = "Поиск похожих фотографий",
                        modifier = Modifier.padding(top = 20.dp)
                    )

                }
                is StateUI.Start -> {
                    AppButtonUI(
                        text = "Найти похожие фотографии",
                        onClick = findPhoto,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is StateUI.Loaded -> {
                    AppProgressBar(1.0f, Modifier.fillMaxWidth())
                    Text(
                        text = "Поиск похожих фотографий",
                        modifier = Modifier.padding(top = 20.dp)
                    )

                }
                StateUI.Empty -> {
                    Text("Дубликаты не найдены")
                    AppButtonUI(
                        text = "Найти похожие фотографии",
                        onClick = findPhoto,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun StartScreenUIPreview() {
    FindEqualPhotoTheme {

        StartScreenUI(StateUI.Start, {})

    }
}



