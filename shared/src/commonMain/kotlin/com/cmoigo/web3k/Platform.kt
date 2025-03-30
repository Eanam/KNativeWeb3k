package com.cmoigo.web3k

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun ByteArray.sha256(): ByteArray

expect fun readResourceFile(filePath: String): String?