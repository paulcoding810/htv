package com.paulcoding.htv.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.paulcoding.androidtools.animatedComposable
import com.paulcoding.htv.HTVViewModel
import com.paulcoding.htv.page.home.HomePage
import com.paulcoding.htv.page.web.WebPage

@Composable
fun AppEntry() {
    val navController = rememberNavController()
    val viewModel: HTVViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.readConfigs(context)
    }

    NavHost(navController = navController, startDestination = "home") {
        animatedComposable("home") {
            HomePage(navController, viewModel)
        }
        animatedComposable("web") {
            WebPage(navController, viewModel)
        }
    }
}
