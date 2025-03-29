package com.cmoigo.web3k

import java.security.MessageDigest
import java.security.SecureRandom

actual fun getSecurityRandomProvider(): SecurityRandomProvider {
    return object: SecurityRandomProvider {
        private val securityRandom = SecureRandom()

        override fun nextBytes(size: Int): ByteArray {
            return ByteArray(size).apply {
                securityRandom.nextBytes(this)
            }
        }
    }
}

actual fun ByteArray.sha256(): ByteArray {
    return MessageDigest.getInstance("SHA-256").digest()
}