package xyz.thingapps.rssliveslider.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import xyz.thingapps.rssliveslider.R

class WebViewActivity : AppCompatActivity() {

    companion object {
        const val ITEM_URL = "item_url"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val url = intent?.getStringExtra(ITEM_URL)

        url?.let {
            webView.apply {
                loadUrl(it)
                webViewClient = CustomWebView().apply {
                    this.onStart = {
                        progressBar.visibility = View.VISIBLE
                    }
                    this.onFinish = {
                        progressBar.visibility = View.GONE
                    }
                }
                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
                isScrollbarFadingEnabled = true
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }

            webView.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportMultipleWindows(true)
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
            }
        }
    }

    class CustomWebView : WebViewClient() {
        var onStart: (() -> Unit)? = null
        var onFinish: (() -> Unit)? = null

        private var loadingFinished = true
        private var redirect = false

        override fun shouldOverrideUrlLoading(
            view: WebView?, request: WebResourceRequest?
        ): Boolean {
            if (!loadingFinished) {
                redirect = true
            }

            loadingFinished = false
            view?.loadUrl(request?.url.toString())
            return true
        }

        override fun onPageStarted(
            view: WebView?, url: String?, favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            loadingFinished = false
            onStart?.invoke()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            if (!redirect) {
                loadingFinished = true
                onFinish?.invoke()
            } else {
                redirect = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
