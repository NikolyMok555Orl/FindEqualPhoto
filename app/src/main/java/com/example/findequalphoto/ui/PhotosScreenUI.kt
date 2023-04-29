package com.example.findequalphoto.ui

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme

@Composable
fun PhotosScreenUI(
    navController: NavController,
    vm: MainVM = viewModel()
) {

    val stateUI = vm.statePhoto.collectAsState()
    PhotosScreenUI(stateUI.value)

}


@Composable
fun PhotosScreenUI(stateUI: StateUI) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 19.dp)) {
        Text("Экран со всеми фото")
        if (stateUI is StateUI.Loaded) {
            LazyVerticalGrid(columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(11.dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                itemsIndexed(
                    items = stateUI.photos,
                    key = { _, photo -> photo.uri }) { _, photo ->
                    Box(Modifier.aspectRatio(1f)) {
                        Card() {
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun PhotosScreenUIPreview() {
    FindEqualPhotoTheme {


    }
}