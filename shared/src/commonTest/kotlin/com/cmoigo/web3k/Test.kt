package com.cmoigo.web3k

import com.cmoigo.web3k.crypto.bip39.BIP39WalletGenerator
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.reflect.KClass
import kotlin.test.assertEquals

class CommonGreetingTest {

    @Test
    fun testCreateBIP39WalletGenerator() {
        val biP39WalletGenerator = BIP39WalletGenerator.create {
            mnemonicCount = 32
        }

        assertSame(
            "/testPath",
            "bip39 wallet generator got the wrong file path"
        )

        assertSame(
            32,
            biP39WalletGenerator.mnemonicCount,
            "bip39 wallet generator got the wrong mnemonic amount"
        )
    }

    @Test
    fun testEntropyGeneratingProcess() {
//        val testInputs = listOf(
//            12 to 128,
//            15 to 160,
//            18 to 192,
//            21 to 224,
//            24 to 256
//        )
//
//        testInputs.forEach {
//            val biP39WalletGenerator = BIP39WalletGenerator.create {
//                mnemonicWordListPath = "/testPath"
//                mnemonicCount = it.first
//            }
//
//            assertEquals(
//                it.second / 8,
//                biP39WalletGenerator.generateEntropy().size,
//                "entropy generated wrongly when mnemonicCount = ${it.first}" +
//                        "[expected: ${11 * it.first * 32 / 33}]"
//            )
//        }
    }

    @Test
    fun testGenerateMnemonic() {
        val generator = BIP39WalletGenerator.create()
        val mnemonics = generator.generateMnemonics()
        println("mnemonics: $mnemonics")
        assertEquals(generator.mnemonicCount, mnemonics.size)
    }
}