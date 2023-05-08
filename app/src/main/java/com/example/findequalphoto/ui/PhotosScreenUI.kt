package com.example.findequalphoto.ui

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
import com.example.findequalphoto.MainActivity
import com.example.findequalphoto.NavHost
import com.example.findequalphoto.data.Photo
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme

@Composable
fun PhotosScreenUI(
    navController: NavController,
    vm: MainVM = viewModel(factory = MainVM.getMainVM(
        (LocalContext.current as MainActivity).repo))
) {
    val stateUI = vm.statePhoto.collectAsState()

    PhotosScreenUI(stateUI = stateUI.value, isDelete = {
        vm.deletePhoto()
        navController.navigate(NavHost.DELETE)
    }, isSelectPhoto = vm::selectPhoto)

}


@Composable
fun PhotosScreenUI(
    stateUI: StateUI,
    isDelete: () -> Unit,
    isSelectPhoto: (photo: Photo, indexPhotos: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 19.dp)
    ) {
        Text("Похожие фотографии")

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Будет доступно после удаления выбранных похожих фотографий")
            Text(text = "X гб")
        }
        if (stateUI is StateUI.Loaded) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(11.dp), modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = stateUI.photos,
                    key = { _, photo -> photo.first().uri }) { indexPhotos, photos ->
                    Column {
                        var rows = photos.size / 3
                        if (photos.size.mod(3) > 0) {
                            rows += 1
                        }
                        for (rowId in 0 until rows) {
                            val firstIndex = rowId * 3
                            Row {
                                for (columnId in 0 until 3) {
                                    val index = (firstIndex + columnId)
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    ) {
                                        if (index < photos.size) {
                                            Card(modifier = Modifier.clickable {
                                                isSelectPhoto(photos[index], indexPhotos)
                                            }) {
                                                AsyncImage(
                                                    model = photos[index].uri,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Checkbox(
                                                checked = photos[index].isSelect,
                                                onCheckedChange = {
                                                    isSelectPhoto(photos[index], indexPhotos)
                                                },
                                                modifier = Modifier.align(Alignment.BottomEnd)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colors.onBackground)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = isDelete, Modifier.fillMaxWidth()) {
                    Text(text = "Удалить")
                }
                TextButton(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                    Text(text = "Пропустить")
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