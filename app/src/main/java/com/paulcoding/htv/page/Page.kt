package com.paulcoding.htv.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page(
    title: String,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    goBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    content: @Composable () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = title)
        }, navigationIcon = {
            if (goBack != null)
                IconButton(onClick = {
                    goBack()
                }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
        }, actions = actions)
    }) { paddings ->
        Column(modifier = Modifier.padding(paddings)) {
            content()
        }
    }
}