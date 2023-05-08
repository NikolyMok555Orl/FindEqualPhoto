package com.example.findequalphoto.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.findequalphoto.MainActivity
import com.example.findequalphoto.NavHost
import com.example.findequalphoto.data.Photo
import com.example.findequalphoto.ui.component.AppButtonUI
import com.example.findequalphoto.ui.component.HeaderTextUI
import com.example.findequalphoto.ui.theme.FindEqualPhotoTheme

@Composable
fun PhotosScreenUI(
    navController: NavController,
    vm: MainVM = viewModel(
        factory = MainVM.getMainVM(
            (LocalContext.current as MainActivity).repo
        )
    )
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
    Surface(
        color = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 19.dp)
        ) {
            HeaderTextUI("Похожие фотографии")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 13.dp)
            ) {
                Text(
                    text = "Будет доступно после удаления выбранных похожих фотографий",
                    style = MaterialTheme.typography.body2
                )
                Row() {
                    Text(text = "X", style = MaterialTheme.typography.h3.copy(fontSize = 38.sp))
                    Text(text = "гб", style = MaterialTheme.typography.body2.copy(fontSize = 12.sp))
                }

            }
            if (stateUI is StateUI.Loaded) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(11.dp),
                    modifier = Modifier.weight(1f)
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
                                Row(
                                    modifier = Modifier.padding(
                                        top =
                                        if (rowId != 0)
                                            11.dp else 0.dp
                                    )
                                ) {
                                    for (columnId in 0 until 3) {
                                        CardPhotoUI(
                                            firstIndex,
                                            columnId,
                                            photos,
                                            isSelectPhoto,
                                            indexPhotos,
                                            Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                        )
                                    }

                                }
                            }
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                                    .height(1.dp)
                                    .background(Color(0xFF393B4A))
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppButtonUI(text = "Удалить", onClick = isDelete, Modifier.fillMaxWidth())
                    TextButton(onClick = { /*TODO*/ }, Modifier.fillMaxWidth()) {
                        Text(text = "Пропустить", color = MaterialTheme.colors.onBackground)
                    }
                }


            }
        }
    }
}

@Composable
private fun CardPhotoUI(
    firstIndex: Int,
    columnId: Int,
    photos: List<Photo>,
    isSelectPhoto: (photo: Photo, indexPhotos: Int) -> Unit,
    indexPhotos: Int,
    modifier: Modifier
) {
    val index = (firstIndex + columnId)
    if (columnId == 1) {
        Spacer(
            modifier = Modifier
                .width(11.dp)
                .background(Color.Transparent)
        )
    }
    Box(
        modifier
    ) {
        if (index < photos.size) {


            val modifier = if (photos[index].isSelect) Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(Color.Black.copy(alpha = 0.6f)) else Modifier

            Card(shape = RoundedCornerShape(10.dp),
                modifier = modifier
                    .padding(2.dp)
                    .clickable {
                        isSelectPhoto(
                            photos[index],
                            indexPhotos
                        )
                    }) {
                Box() {
                    AsyncImage(
                        model = photos[index].uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (photos[index].isSelect) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f)),
                        )
                    }
                }


            }
            if (photos[index].isSelect) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                    )

                }

            }
        }
    }
    if (columnId == 1) {
        Spacer(
            modifier = Modifier
                .width(11.dp)
                .background(Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotosScreenUIPreview() {
    FindEqualPhotoTheme {
        PhotosScreenUI(StateUI.Loaded(
            listOf(
                listOf(
                    Photo(android.net.Uri.EMPTY, "", 0, true),
                    Photo(android.net.Uri.EMPTY, "", 0, false),
                    Photo(android.net.Uri.EMPTY, "", 0, true),
                    Photo(android.net.Uri.EMPTY, "", 0, false)
                )
            )
        ), {}, { _, _ -> })

    }
}