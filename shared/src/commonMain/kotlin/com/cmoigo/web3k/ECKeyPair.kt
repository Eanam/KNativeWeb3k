package com.cmoigo.web3k

import com.ionspin.kotlin.bignum.integer.BigInteger

interface ECKeypairGenerator {
    fun createPublicKey(privateKey: BigInteger): BigInteger

    fun getEncodedPublicKeyByteArray(privateKey: BigInteger): ByteArray
}

expect fun getECKeypairGenerator(): ECKeypairGenerator