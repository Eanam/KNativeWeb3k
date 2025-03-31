package com.cmoigo.web3k

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.crypto.ec.CustomNamedCurves
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.math.ec.FixedPointCombMultiplier

class AndroidECKeypair : ECKeypairGenerator {
    //TODO: change to configurable
    private val curveParam = ECNamedCurveTable.getParameterSpec("secp256k1")
    private val ecDomainParam = ECDomainParameters(
        curveParam.curve,
        curveParam.g,
        curveParam.n,
        curveParam.h
    )

    override fun createPublicKey(privateKey: BigInteger): BigInteger {
        var key = java.math.BigInteger(privateKey.signum(), privateKey.toByteArray())
        if (privateKey.bitLength() > ecDomainParam.n.bitLength()) {
            key = key.mod(ecDomainParam.n)
        }

        val ecPoint = FixedPointCombMultiplier().multiply(ecDomainParam.g, key)
        val publicKeyByteArray = ecPoint.getEncoded(false)
        return BigInteger.fromByteArray(
            publicKeyByteArray.copyOfRange(1, publicKeyByteArray.size),
            Sign.POSITIVE
        )
    }

    override fun getEncodedPublicKeyByteArray(privateKey: BigInteger): ByteArray {
        var key = java.math.BigInteger(privateKey.signum(), privateKey.toByteArray())
        if (privateKey.bitLength() > ecDomainParam.n.bitLength()) {
            key = key.mod(ecDomainParam.n)
        }

        val ecPoint = FixedPointCombMultiplier().multiply(ecDomainParam.g, key)
        return ecPoint.getEncoded(true)
    }
}

actual fun getECKeypairGenerator(): ECKeypairGenerator = AndroidECKeypair()