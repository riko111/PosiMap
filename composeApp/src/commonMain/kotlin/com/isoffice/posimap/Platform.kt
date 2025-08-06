package com.isoffice.posimap

// 実行中のプラットフォーム情報を表すインターフェース
interface Platform {
    val name: String
}

// 各プラットフォーム固有の実装を返す
expect fun getPlatform(): Platform
