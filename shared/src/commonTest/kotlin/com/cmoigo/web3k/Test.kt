package com.cmoigo.web3k

import com.cmoigo.web3k.crypto.bip32.BIP32WalletGenerator
import com.cmoigo.web3k.crypto.bip39.BIP39WalletGenerator
import com.cmoigo.web3k.utils.toHexStringWithPaddingZero
import kotlin.test.Test
import kotlin.test.assertSame
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
        println("mnemonics: ${mnemonics.joinToString(" ")}")
        assertEquals(generator.mnemonicCount, mnemonics.size)
    }

    @Test
    fun testGeneratePBKDF2Seed() {
        val mnemonics =
            "able early brand myself rate feature trim shed give stuff win balcony".split(" ")
        val seed = BIP39WalletGenerator.create().generatePBKDF2Seed(mnemonics)
        val seedStr = seed?.joinToString("") {
            (it.toInt() and 0xFF).toString(16).padStart(2, '0')
        }
        println("seedStr: $seedStr")
        assertEquals(
            "18d0b2f924b68ecdd51fd0914f811b0afdebd5070b16b1fbff0357057c518d49b506033d7d5b83bc513f521a9170eef54f7a3775a1c25e705ade519ec43ce608",
            seedStr
        )
    }

    @Test
    fun testGenerateBIP32KeyPair() {
        val mnemonics =
            "able early brand myself rate feature trim shed give stuff win balcony".split(" ")
        val seed = BIP39WalletGenerator.create().generatePBKDF2Seed(mnemonics)
        require(seed != null)
        val biP32WalletGenerator = BIP32WalletGenerator()
        val keyPairs = biP32WalletGenerator.derivedKeyFromPath(seed)
        assertEquals(256, keyPairs.privateKey32.size * 8)
        val publicKeyStr = keyPairs.privateKey32.joinToString("") { byte ->
            (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
        }.let {
            "0x$it"
        }
        assertEquals("0x8a39d1a4fba36de1b6ff22dcb3776a8e0bb8f43774b8e06acdfbec707a2fb915", publicKeyStr)
    }

    @Test
    fun testNormalSalt() {
        val salt = "mnemonicNFKD"
        val saltByteArray = salt.encodeToByteArray()
    }

    @Test
    fun testKeysPairsFromOKX() {
        val mnemonics =
            "vintage oil cool nominee annual size anger paper catch style merge later".split(" ")
        val seed = BIP39WalletGenerator.create().generatePBKDF2Seed(mnemonics)
        assertEquals(
            "0203dbedabdb6bdaf1ffd09900dac9caea13d63fb53659b1812bef1ca7652da00bdea8857fea3255f6b1343c65f6ac4399bf2c3275b65aafcbbfc4c08e6eeca3",
            seed?.toHexStringWithPaddingZero(),
            "generated seed is different"
        )

        //verify master private key
        val biP32WalletGenerator = BIP32WalletGenerator()
        val masterPrivateKeyPair = biP32WalletGenerator.generateMasterPrivateKey(seed!!)
        assertEquals(
            "c720674c3e921061e7e7ab1eb474f8b822a278beda700ea55ca9c53d9f8ed59b",
            masterPrivateKeyPair.privateKey32.toHexStringWithPaddingZero(),
            "generated master private key is different"
        )

        var currentPrivateKey = biP32WalletGenerator.deriveKeyWithChildNumber(
            masterPrivateKeyPair, 44.toUInt() or 0x80000000.toUInt()
        )
        //"m/44'/60'/0'/0/0"
        //verify derived after input 44
        //4c83521ed71ff498f38510d4baa152ae415c9e6055e1c2cbe145ef9447f836e5
        assertEquals(
            "4c83521ed71ff498f38510d4baa152ae415c9e6055e1c2cbe145ef9447f836e5",
            currentPrivateKey.privateKey32.toHexStringWithPaddingZero(),
            "generated private key with childNumber[44] is different"
        )
        //verify derived after input 60
        //2d53db514bab985b850ff78b9873da9fc1be79b9f4c9582a10f8458a6cc12c11
        currentPrivateKey = biP32WalletGenerator.deriveKeyWithChildNumber(
            currentPrivateKey, 60.toUInt() or 0x80000000.toUInt()
        )
        assertEquals(
            "2d53db514bab985b850ff78b9873da9fc1be79b9f4c9582a10f8458a6cc12c11",
            currentPrivateKey.privateKey32.toHexStringWithPaddingZero(),
            "generated private key with childNumber[60] is different"
        )
        //verify derived after input 0
        //f20610c75e575f28bd050219d47c7354d7fecdc19b4ec7e388a1134fb81db700
        currentPrivateKey = biP32WalletGenerator.deriveKeyWithChildNumber(
            currentPrivateKey, 0.toUInt() or 0x80000000.toUInt()
        )
        assertEquals(
            "f20610c75e575f28bd050219d47c7354d7fecdc19b4ec7e388a1134fb81db700",
            currentPrivateKey.privateKey32.toHexStringWithPaddingZero(),
            "generated private key with childNumber[0(1)] is different"
        )
        //verify derived after input 0
        //fad630a659127bebba27bfa48db3702a84cf6f72150f784ab44d537e05971abb
        currentPrivateKey = biP32WalletGenerator.deriveKeyWithChildNumber(
            currentPrivateKey, 0.toUInt()
        )
        assertEquals(
            "fad630a659127bebba27bfa48db3702a84cf6f72150f784ab44d537e05971abb",
            currentPrivateKey.privateKey32.toHexStringWithPaddingZero(),
            "generated private key with childNumber[0(2)] is different"
        )
        //verify derived after input 0
        //b5e4238a86ee69741673c7ba67ce17d2c3d7c91db95bb45bbc856f1ea91e6292
        currentPrivateKey = biP32WalletGenerator.deriveKeyWithChildNumber(
            currentPrivateKey, 0.toUInt()
        )
        assertEquals(
            "b5e4238a86ee69741673c7ba67ce17d2c3d7c91db95bb45bbc856f1ea91e6292",
            currentPrivateKey.privateKey32.toHexStringWithPaddingZero(),
            "generated private key with childNumber[0(3)] is different"
        )



        val keyPairs = biP32WalletGenerator.derivedKeyFromPath(seed)
        assertEquals(256, keyPairs.privateKey32.size * 8)
        val publicKeyStr = keyPairs.privateKey32.toHexStringWithPaddingZero().let {
            "0x$it"
        }
        assertEquals("0xb5e4238a86ee69741673c7ba67ce17d2c3d7c91db95bb45bbc856f1ea91e6292", publicKeyStr)
    }
}