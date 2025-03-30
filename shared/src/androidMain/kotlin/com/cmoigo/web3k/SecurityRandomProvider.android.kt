package com.cmoigo.web3k

import java.security.MessageDigest

actual fun ByteArray.sha256(): ByteArray {
    return MessageDigest.getInstance("SHA-256").digest()
}