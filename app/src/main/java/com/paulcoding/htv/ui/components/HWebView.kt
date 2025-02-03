package com.paulcoding.htv.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

@OptIn(ExperimentalMaterialApi::class)
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
    var isRefreshing by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing, {
        isRefreshing = true
    })
    var dropdownExpanded by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(JSInterface(this), "Android")
            webViewClient = HWebViewClient(site, adsBlackList) { webview ->
                canGoBack = webview.canGoBack()
                canGoForward = webview.canGoForward()
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progress = newProgress
                    isRefreshing = newProgress < 100
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    pageTitle = title ?: ""
                }

            }
            loadUrl(site.baseUrl)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.apply {
                // the WebView must be removed from the view hierarchy before calling destroy
                // to prevent a memory leak
                removeAllViews()
                webChromeClient = null
                destroy()
            }
        }
    }

    BackHandler(enabled = canGoBack) {
        webView.goBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ) {
        ProgressIndicator(progress)
        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.background
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(pageTitle, fontSize = 12.sp, maxLines = 1, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            Box {
                val dropdownItemColors = MenuDefaults.itemColors(
                    leadingIconColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.primary,
                )
                IconButton(onClick = {
                    dropdownExpanded = true
                }) {
                    Icon(Icons.Filled.MoreVert, "More", tint = Color.Black)
                }
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    DropdownMenuItem(
                        colors = dropdownItemColors,
                        onClick = {
                            webView.loadUrl("javascript:window.Android.sendHtml(document.documentElement.outerHTML);")
                            dropdownExpanded = false
                        },
                        text = { Text("Html") },
                        leadingIcon = {
                            Icon(Icons.Filled.Code, "Html")
                        }
                    )
                    DropdownMenuItem(
                        colors = dropdownItemColors,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webView.url))
                            context.startActivity(intent)
                            dropdownExpanded = false
                        },
                        text = { Text("Open in browser") },
                        leadingIcon = {
                            Icon(Icons.Filled.Http, "Open in browser")
                        }
                    )
                }
            }
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
                    tint = if (canGoBack) Color.Black else Color.Gray
                )
            }
            IconButton(onClick = {
                webView.goForward()
            }) {
                Icon(
                    Icons.Filled.ChevronRight,
                    "Forward",
                    tint = if (canGoForward) Color.Black else Color.Gray
                )
            }
            IconButton(onClick = {
                webView.reload()
            }) {
                Icon(Icons.Filled.Refresh, "Refresh")
            }
            IconButton(onClick = {
                webView.scrollTo(0, 0)
            }) {
                Icon(Icons.Filled.ArrowUpward, "Top")
            }
        }
    }
}


class JSInterface(private val webView: WebView) {
    @JavascriptInterface
    fun sendHtml(html: String) {
        println("HTML Content: $html")
    }
}