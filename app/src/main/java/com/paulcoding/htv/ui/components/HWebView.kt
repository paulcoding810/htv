package com.paulcoding.htv.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = HWebViewClient(site, adsBlackList)
                loadUrl(site.baseUrl)
            }
        }
    )
}

class HWebViewClient(private val site: Site, private val adsBlackList: List<String>) :
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