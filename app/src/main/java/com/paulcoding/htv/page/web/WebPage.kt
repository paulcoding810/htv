package com.paulcoding.htv.page.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.paulcoding.htv.HTVViewModel
import com.paulcoding.htv.ui.components.HWebView

@Composable
fun WebPage(navController: NavHostController, viewModel: HTVViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    val site = state.selectedSite ?: return

    HWebView(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        site = site,
        adsBlackList = state.adsBlackList
    ) {
        navController.popBackStack()
    }
}
