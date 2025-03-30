package com.cmoigo.web3k

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

actual fun getSecurityRandomProvider(): SecurityRandomProvider {
    return object : SecurityRandomProvider {
        private val securityRandom = SecureRandom()

        override fun nextBytes(size: Int): ByteArray {
            return ByteArray(size).apply {
                securityRandom.nextBytes(this)
            }
        }
    }
}

actual fun pbkdf2(password: String, salt: String): ByteArray? {
    val keySpec = PBEKeySpec(
        password.toCharArray(),
        salt.toByteArray(),
        2048,
        64 * 8
    )
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    return keyFactory.generateSecret(keySpec).encoded
}