package com.example.findequalphoto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

@Composable
fun DeleteAndFinalScreenUI( navController: NavController,
                            vm: MainVM = viewModel(factory = MainVM.getMainVM(
                                (LocalContext.current as MainActivity).repo))
){

    val stateUI=vm.statePhoto.collectAsState()
    DeleteAndFinalScreenUI(stateUI.value)
    
    
}

@Composable
fun DeleteAndFinalScreenUI(stateUI: StateUI){
    Column() {
        Text(text="Похожие фотографии")
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if(stateUI is StateUI.Loading) {
                LinearProgressIndicator(progress = (stateUI).progress)
            }else if(stateUI is StateUI.Loaded){
                Text(text = "Все дубликаты удалены")
            }
        }
    }

}

    @Preview(showBackground = true)
@Composable
private fun DeleteAndFinalScreenUIPreview(){
    FindEqualPhotoTheme{



    }
}