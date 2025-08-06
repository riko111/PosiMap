package com.isoffice.posimap

import platform.UIKit.UIDevice

// iOS向けのPlatform実装
class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

// iOSで実行中のPlatformを返す
actual fun getPlatform(): Platform = IOSPlatform()
