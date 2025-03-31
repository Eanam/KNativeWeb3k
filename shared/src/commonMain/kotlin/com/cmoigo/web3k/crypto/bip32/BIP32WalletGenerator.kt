package com.cmoigo.web3k.crypto.bip32

import com.cmoigo.web3k.getECKeypairGenerator
import com.cmoigo.web3k.hmacSHA512
import com.cmoigo.web3k.utils.hexToByteArray
import com.cmoigo.web3k.utils.toByteArray
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class BIP32WalletGenerator {

    companion object {
        private const val HARDENED_OFFSET = 0x80000000
    }

    fun generateMasterPrivateKey(seed: ByteArray): BIP32ECKeyPair {
        val rawData = hmacSHA512(
            "Bitcoin seed".encodeToByteArray(),
            seed
        )

        val privateKey = BigInteger.fromByteArray(
            rawData.sliceArray(0..31),
            Sign.POSITIVE
        )
        return BIP32ECKeyPair(
            privateKey = privateKey,
            publicKey = getECKeypairGenerator().createPublicKey(privateKey),
            chainCode = rawData.sliceArray(32..63)
        )
    }

    fun derivedKeyFromPath(
        seed: ByteArray,
        path: String = "m/44'/60'/0'/0/0"
    ): BIP32ECKeyPair {
        val indices = path.split('/').drop(1).map { parsePathIndex(it) }
        val masterPrivateKey = generateMasterPrivateKey(seed)
        var key = masterPrivateKey
        for (i in indices) {
            key = deriveKeyWithChildNumber(key, i)
        }

        return key
    }

    fun deriveKeyWithChildNumber(
        keyPair: BIP32ECKeyPair,
        childNumber: UInt
    ): BIP32ECKeyPair {
        val newByteArray = ByteArray(37)
        if ((childNumber and HARDENED_OFFSET.toUInt()) != 0.toUInt()) {
            keyPair.privateKey32.copyInto(newByteArray, 1)
        } else {
            keyPair.encodedPublicKey.copyInto(newByteArray, 0)
        }

        childNumber.toByteArray().copyInto(newByteArray, 1 + keyPair.privateKey32.size)
        val rawData = hmacSHA512(keyPair.chainCode, newByteArray)
        val tempPrivateKey = BigInteger.fromByteArray(
            rawData.sliceArray(0..31),
            Sign.POSITIVE
        )
        val childPrivateKey = keyPair.privateKey.add(tempPrivateKey).mod(
            BigInteger.fromByteArray(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141".hexToByteArray(),
                Sign.POSITIVE
            )
        )

        return BIP32ECKeyPair(
            privateKey = childPrivateKey,
            publicKey = getECKeypairGenerator().createPublicKey(childPrivateKey),
            chainCode = rawData.sliceArray(32..63)
        )
    }

    private fun parsePathIndex(index: String): UInt {
        return if (index.endsWith("'")) {
            index.dropLast(1).toUInt() or HARDENED_OFFSET.toUInt()
        } else {
            index.toUInt()
        }
    }

    private fun generatePublicKey(privateKey: ByteArray): ByteArray {
        return ByteArray(32)
    }
}