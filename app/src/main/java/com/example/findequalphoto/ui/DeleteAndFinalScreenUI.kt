package com.example.findequalphoto

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.findequalphoto.data.Photo
import com.example.findequalphoto.ui.MainVM
import com.example.findequalphoto.ui.StateUI
import com.example.findequalphoto.ui.component.AppProgressBar
import com.example.findequalphoto.ui.component.HeaderTextUI
import com.example.findequalphoto.ui.component.LogoUI
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme

@Composable
fun DeleteAndFinalScreenUI(
    navController: NavController,
    vm: MainVM = viewModel(
        factory = MainVM.getMainVM(
            (LocalContext.current as MainActivity).repo
        )
    )
) {

    val stateUI = vm.statePhoto.collectAsState()
    DeleteAndFinalScreenUI(stateUI.value)


}

@Composable
fun DeleteAndFinalScreenUI(stateUI: StateUI, modifier: Modifier = Modifier) {
    Surface(
        contentColor = MaterialTheme.colors.onBackground,
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 33.dp, end = 33.dp, top = 17.dp)
        ) {

            HeaderTextUI("Похожие фотографии")
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoUI()
                Spacer(modifier = Modifier.padding(top=34.dp))
                if (stateUI is StateUI.Loading) {

                    AppProgressBar(stateUI.progress, Modifier.fillMaxWidth())
                    Text(
                        text = "Поиск похожих фотографий",
                        modifier = Modifier.padding(top = 20.dp)
                    )
                } else {
                    Text(
                        text = "Все дубликаты удалены",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteAndFinalScreenUIPreview() {
    FindEqualPhotoTheme {

        DeleteAndFinalScreenUI(
            stateUI = StateUI.Loaded(
                listOf(
                    listOf(
                        Photo(Uri.EMPTY, "", 0, true),
                        Photo(Uri.EMPTY, "", 0, false),
                        Photo(Uri.EMPTY, "", 0, true),
                        Photo(Uri.EMPTY, "", 0, false)
                    )
                )
            )
        )

    }
}