package com.paulcoding.htv.page.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.paulcoding.htv.HTVViewModel
import com.paulcoding.htv.page.Page

@Composable
fun HomePage(navController: NavHostController, viewModel: HTVViewModel) {
    val uiState by viewModel.stateFlow.collectAsState()
    val context = LocalContext.current

    Page(goBack = { navController.popBackStack() }, title = "Home", actions = {
        IconButton(onClick = {
            viewModel.readConfigs(context)
        }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }) {
        uiState.config.sites.run {
            LazyColumn {
                if (isEmpty())
                    item {
                        Text("Empty")
                    }
                items(this@run) {
                    Card(onClick = {
                        viewModel.setSite(it)
                        navController.navigate("web")
                    }) {
                        Text(it.name)
                    }
                }
            }
        }
    }
}

