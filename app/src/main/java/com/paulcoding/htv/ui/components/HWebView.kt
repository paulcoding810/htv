package com.paulcoding.htv.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.paulcoding.htv.Site


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HWebView(
    modifier: Modifier = Modifier,
    site: Site,
    adsBlackList: List<String>,
    navBack: () -> Unit
) {
    val context = LocalContext.current

    var pageTitle by remember { mutableStateOf("") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = HWebViewClient(site, adsBlackList) { webview ->
                canGoBack = webview.canGoBack()
                canGoForward = webview.canGoForward()
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    pageTitle = title ?: ""
                }

            }
            loadUrl(site.baseUrl)
        }
    }

    BackHandler(enabled = canGoBack) {
        webView.goBack()
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(8.dp)
        ) {
            Text(pageTitle, fontSize = 12.sp, maxLines = 1)
        }
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { webView }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(Color.Red.copy(alpha = 0.5f))
                .height(56.dp)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = {
                navBack()
            }) {
                Icon(Icons.Filled.Home, "Back")
            }
            IconButton(onClick = {
                webView.goBack()
            }) {
                Icon(
                    Icons.Filled.ChevronLeft,
                    "Back",
                    tint = if (canGoBack) Color.White else Color.Gray
                )
            }
            IconButton(onClick = {
                webView.goForward()
            }) {
                Icon(
                    Icons.Filled.ChevronRight,
                    "Forward",
                    tint = if (canGoForward) Color.White else Color.Gray
                )
            }
            IconButton(onClick = {
                webView.scrollTo(0, 0)
            }) {
                Icon(Icons.Filled.ArrowUpward, "Top")
            }
        }
    }
}
