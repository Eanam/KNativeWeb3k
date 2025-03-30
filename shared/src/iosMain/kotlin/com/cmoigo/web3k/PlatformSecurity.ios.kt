package com.cmoigo.web3k

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA512
import platform.posix.arc4random_buf

@OptIn(ExperimentalForeignApi::class)
actual fun getSecurityRandomProvider(): SecurityRandomProvider {
    return object: SecurityRandomProvider {
        override fun nextBytes(size: Int): ByteArray {
            val byteArray = ByteArray(size)
            byteArray.usePinned { pinned ->
                arc4random_buf(pinned.addressOf(0), size.toULong())
            }
            return byteArray
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun pbkdf2(password: String, salt: String): ByteArray? {
    val passwordData = password.encodeToByteArray()
    val saltData = salt.encodeToByteArray()
    val derivedKey = ByteArray(64)
    passwordData.usePinned { _ ->
        saltData.usePinned { pinnedSalt ->
            derivedKey.usePinned { pinnedDerivedKey ->
                val status = CCKeyDerivationPBKDF(
                    kCCPBKDF2,
                    password,
                    passwordData.size.toULong(),
                    pinnedSalt.addressOf(0).reinterpret(),
                    saltData.size.toULong(),
                    kCCPRFHmacAlgSHA512,
                    2048u,
                    pinnedDerivedKey.addressOf(0).reinterpret(),
                    64u
                )

                if (status != 0) return null
            }
        }
    }
    return derivedKey
}