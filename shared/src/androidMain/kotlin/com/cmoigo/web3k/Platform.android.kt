package com.cmoigo.web3k

import java.nio.charset.StandardCharsets

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun readResourceFile(filePath: String): String? {
    val classLoader = Thread.currentThread().contextClassLoader ?: return null
    val inputStream = classLoader.getResourceAsStream(filePath)
    return inputStream.bufferedReader(StandardCharsets.UTF_8).use {
        it.readText()
    }
}