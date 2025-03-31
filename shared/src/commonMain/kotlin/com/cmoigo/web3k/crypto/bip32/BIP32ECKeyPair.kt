package com.cmoigo.web3k.crypto.bip32

import com.cmoigo.web3k.getECKeypairGenerator
import com.ionspin.kotlin.bignum.integer.BigInteger

class BIP32ECKeyPair(
    val privateKey: BigInteger,
    val publicKey: BigInteger,
    val chainCode: ByteArray,
    val childNumber: Int = 0,
) {
    val privateKey32: ByteArray
        get() = privateKey.toByteArray()

    //size: 33
    val encodedPublicKey: ByteArray by lazy {
        getECKeypairGenerator().getEncodedPublicKeyByteArray(privateKey)
    }
}