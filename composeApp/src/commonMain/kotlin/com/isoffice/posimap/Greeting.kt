package com.isoffice.posimap

// プラットフォームに依存した挨拶メッセージを生成するクラス
class Greeting {
    private val platform = getPlatform()

    // 挨拶文を返す
    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
