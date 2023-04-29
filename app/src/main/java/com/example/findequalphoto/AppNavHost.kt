package com.example.findequalphoto

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findequalphoto.data.PhotoRepoImpl
import com.example.findequalphoto.ui.MainVM
import com.example.findequalphoto.ui.PhotosScreenUI

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    vm: MainVM = viewModel(
        factory = MainVM.getMainVM(
            (LocalContext.current as Activity).application,
            PhotoRepoImpl((LocalContext.current as Activity).application.applicationContext)
        )
    ),
    startDestination: String = NavHost.START
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavHost.START) {
            StartScreenUI(navController, vm)
        }
        composable(NavHost.PHOTOS) {
            PhotosScreenUI(navController, vm)
        }


    }
}


object NavHost {
    const val START = "start"
    const val PHOTOS = "photos"

}