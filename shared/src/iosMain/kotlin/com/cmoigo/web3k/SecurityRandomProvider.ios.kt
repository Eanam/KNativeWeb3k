package com.cmoigo.web3k

import kotlinx.cinterop.*
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.posix.*

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
actual fun ByteArray.sha256(): ByteArray {
    val data = this
    val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)
    data.usePinned { pinnedData ->
        CC_SHA256(pinnedData.addressOf(0), data.size.toUInt(), hash.refTo(0))
    }
    return hash.toByteArray()
}