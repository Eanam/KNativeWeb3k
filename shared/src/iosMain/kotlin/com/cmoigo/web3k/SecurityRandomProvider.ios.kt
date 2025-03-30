package com.cmoigo.web3k

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.sha256(): ByteArray {
    val data = this
    val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)
    data.usePinned { pinnedData ->
        CC_SHA256(pinnedData.addressOf(0), data.size.toUInt(), hash.refTo(0))
    }
    return hash.toByteArray()
}