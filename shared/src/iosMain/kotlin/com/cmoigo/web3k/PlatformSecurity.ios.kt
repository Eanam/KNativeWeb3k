package com.cmoigo.web3k

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCHmac
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.CC_SHA512_DIGEST_LENGTH
import platform.CoreCrypto.kCCHmacAlgSHA512
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

@OptIn(ExperimentalForeignApi::class)
actual fun hmacSHA512(key: ByteArray, data: ByteArray): ByteArray {
    val result = ByteArray(CC_SHA512_DIGEST_LENGTH)
    key.usePinned { pinnedKey ->
        data.usePinned { pinnedData ->
            result.usePinned { pinnedResult ->
                CCHmac(
                    kCCHmacAlgSHA512,
                    pinnedKey.addressOf(0),
                    key.size.toULong(),
                    pinnedData.addressOf(0),
                    data.size.toULong(),
                    pinnedResult.addressOf(0)
                )
            }
        }
    }
    return result
}