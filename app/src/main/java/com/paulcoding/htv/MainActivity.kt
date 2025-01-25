package com.paulcoding.htv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.paulcoding.htv.page.AppEntry
import com.paulcoding.htv.ui.theme.HTVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HTVTheme {
                AppEntry()
            }
        }
    }
}
