package com.example.findequalphoto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.findequalphoto.ui.MainVM
import com.example.findequalphoto.ui.PhotosScreenUI
import com.example.findequalphoto.ui.StartScreenUI

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavHost.START
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavHost.START) {
            StartScreenUI(navController)
        }
        composable(NavHost.PHOTOS) {
            PhotosScreenUI(navController)
        }
        composable(NavHost.DELETE) {
            DeleteAndFinalScreenUI(navController)


        }


    }
}


object NavHost {
    const val START = "start"
    const val PHOTOS = "photos"
    const val DELETE = "delete"

}