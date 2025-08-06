package com.isoffice.posimap

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // ブラウザのDOMにComposeコンテンツを描画する
    ComposeViewport(document.body!!) {
        App()
    }
}