package com.cmoigo.web3k.utils

fun ByteArray.getFirstNBits(n: Int): ByteArray {
    require(n in 1..this.size * 8) { "unable to take the first $n bits" }

    val byteCount = n / 8
    val remainingBitsCount = n % 8

    val result = this.copyOfRange(0, byteCount)
    if (remainingBitsCount > 0) {
        val extraByte = this[byteCount].toInt() and (0xFF shl (8-remainingBitsCount))
        return result + extraByte.toByte()
    }

    return result
}

fun Int.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    this.toByte(),
)