package com.isoffice.posimap.gateway

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.presentViewController

actual typealias ShareGatewayContext = UIViewController

actual class ShareGateway actual constructor(
    private val viewController: ShareGatewayContext?,
) {
    actual fun share(bytes: ByteArray, filename: String, mime: String) {
        val data = bytes.toNSData()
        val fileUrl = temporaryFileUrl(filename)
        data.writeToURL(fileUrl, atomically = true)

        val activityController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )

        present(activityController)
    }

    private fun temporaryFileUrl(filename: String): NSURL {
        val tempDirectory = NSTemporaryDirectory()
        val directoryUrl = NSURL.fileURLWithPath(tempDirectory)
        return directoryUrl.URLByAppendingPathComponent(filename)
    }

    private fun present(controller: UIActivityViewController) {
        val application = UIApplication.sharedApplication()
        val presenter = viewController
            ?: application.keyWindow?.rootViewController
            ?: (application.windows.firstOrNull() as? UIWindow)?.rootViewController

        presenter?.presentViewController(controller, animated = true, completion = null)
    }
}

private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}
