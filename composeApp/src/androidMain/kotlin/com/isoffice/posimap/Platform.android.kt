package com.isoffice.posimap

import android.os.Build

// Android向けのPlatform実装
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

// Androidで実行中のPlatformを返す
actual fun getPlatform(): Platform = AndroidPlatform()
