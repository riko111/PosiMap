package com.isoffice.posimap

// Kotlin/Wasm 向けのPlatform実装
class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

// Web環境で実行中のPlatformを返す
actual fun getPlatform(): Platform = WasmPlatform()
