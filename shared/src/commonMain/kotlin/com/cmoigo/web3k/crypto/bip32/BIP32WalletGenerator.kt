package com.cmoigo.web3k.crypto.bip32

import com.cmoigo.web3k.hmacSHA512
import com.cmoigo.web3k.utils.toByteArray

class BIP32WalletGenerator {

    companion object {
        private const val HARDENED_OFFSET = 0x80000000
    }

    fun derivedKeyFromPath(
        seed: ByteArray,
        path: String = "m/44'/60'/0'/0/0"
    ): Pair<ByteArray, ByteArray> {
        val indices = path.split('/').drop(1).map { parsePathIndex(it) }
        var key = seed
        for (i in indices) {
            key = hmacSHA512(key, i.toByteArray())
        }

        //the first 32bytes is private key
        val privateKey = key.sliceArray(0..31)
        val publicKey = generatePublicKey(privateKey)
        return privateKey to publicKey
    }

    private fun parsePathIndex(index: String): Int {
        return if (index.endsWith("'")) {
            index.dropLast(1).toInt() + HARDENED_OFFSET.toInt()
        } else {
            index.toInt()
        }
    }

    private fun generatePublicKey(privateKey: ByteArray): ByteArray {
        return ByteArray(32)
    }
}