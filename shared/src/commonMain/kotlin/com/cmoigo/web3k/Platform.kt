package com.cmoigo.web3k

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

interface SecurityRandomProvider {
    fun nextBytes(size: Int): ByteArray
}

expect fun getSecurityRandomProvider(): SecurityRandomProvider

expect fun ByteArray.sha256(): ByteArray

expect fun readResourceFile(filePath: String): String?