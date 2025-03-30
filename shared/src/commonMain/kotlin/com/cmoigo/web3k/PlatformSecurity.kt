package com.cmoigo.web3k

interface SecurityRandomProvider {
    fun nextBytes(size: Int): ByteArray
}

expect fun getSecurityRandomProvider(): SecurityRandomProvider

expect fun pbkdf2(password: String, salt: String): ByteArray?