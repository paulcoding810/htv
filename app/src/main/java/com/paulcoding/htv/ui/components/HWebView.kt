package com.paulcoding.htv.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.paulcoding.htv.DIR
import com.paulcoding.htv.Site
import com.paulcoding.htv.utils.log
import com.paulcoding.htv.utils.readFile
import com.paulcoding.htv.utils.toInputStream


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
        }
    }
}

class HWebViewClient(
    private val site: Site,
    private val adsBlackList: List<String>,
    private val onPageStarted: (webView: WebView) -> Unit
) :
    WebViewClient() {
    override fun shouldOverrideUrlLoading(
        webview: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        for (key in adsBlackList) {
            if (request?.url.toString().contains(key)) {
                log("Blocking $key")
                return true
            }
        }

        for (key in site.urlBlocks ?: emptyList()) {
            if (request?.url.toString().contains(key)) {
                log("Blocking $key")
                return true
            }
        }

        return super.shouldOverrideUrlLoading(webview, request)
    }

    override fun onPageFinished(webview: WebView, url: String?) {
        super.onPageFinished(webview, url)

        if (!site.cssBlocks.isNullOrEmpty()) {
            val js = getInjectCssScript(site.cssBlocks)
            webview.evaluateJavascript(js, null)
        }

        site.onLoadedScripts?.forEach {
            val js = loadScriptFile(webview.context, filePath = it)
            log("Loading $it")
            webview.evaluateJavascript(js, null)
        }
    }

    override fun onPageStarted(webview: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(webview, url, favicon)
        site.startupScripts?.forEach {
            val js = loadScriptFile(webview.context, filePath = it)
            webview.evaluateJavascript(js, null)
        }
        onPageStarted(webview)
    }

    private fun loadScriptFile(context: Context, filePath: String): String {
        val js = context.readFile("$DIR/$filePath")
        return js
    }

    private fun getInjectCssScript(listSelector: List<String>): String {
        val js = """
                var customCss = document.createElement("style");

                customCss.setAttribute("type", "text/css");
                customCss.textContent = "${listSelector.joinToString(", ")} { display:none; }";
                console.log("injectCss")
                document.head.append(customCss);
        """.trimIndent()
        return js
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url.toString()

        site.interceptMap[url]?.let { des ->
            log("Intercepting $url to $des")
            val js = view.context.readFile("$DIR/$des")
            val inputStream = js.toInputStream()
            return WebResourceResponse("application/javascript", "UTF-8", inputStream)
        }
        return null
    }
}