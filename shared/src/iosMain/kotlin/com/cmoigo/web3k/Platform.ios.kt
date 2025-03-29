package com.cmoigo.web3k

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalForeignApi::class)
actual fun readResourceFile(filePath: String): String? {
    val path = NSBundle.mainBundle.pathForResource(filePath, ofType = null)
        ?: return null

    return NSString.stringWithContentsOfFile(path, encoding = 4u, error = null).toString()
}