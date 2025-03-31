package com.cmoigo.web3k

import com.ionspin.kotlin.bignum.integer.BigInteger

class IOSECKeyPair: ECKeypairGenerator {
    override fun createPublicKey(privateKey: BigInteger): BigInteger {
        TODO("Not yet implemented")
    }

    override fun getEncodedPublicKeyByteArray(privateKey: BigInteger): ByteArray {
        TODO("Not yet implemented")
    }
}

actual fun getECKeypairGenerator(): ECKeypairGenerator = IOSECKeyPair()