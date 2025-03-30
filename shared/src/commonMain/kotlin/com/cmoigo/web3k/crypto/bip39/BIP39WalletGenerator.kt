package com.cmoigo.web3k.crypto.bip39

import com.cmoigo.web3k.getSecurityRandomProvider
import com.cmoigo.web3k.mnemonic.BIP39ENWordListProvider
import com.cmoigo.web3k.pbkdf2
import com.cmoigo.web3k.sha256
import com.cmoigo.web3k.utils.getFirstNBits

class BIP39WalletGenerator private constructor(var mnemonicCount: Int = 12) {

    companion object {
        fun create(
            initialization: (BIP39WalletGenerator.() -> Unit)? = null
        ): BIP39WalletGenerator {
            return BIP39WalletGenerator().apply {
                initialization?.invoke(this)
            }
        }
    }

    private val mnemonicWordListProvider by lazy { BIP39ENWordListProvider() }

    fun generatePBKDF2Seed(
        mnemonics: List<String> = generateMnemonics(),
        passPhrase: String = ""
    ): ByteArray? {
        val salt = "mnemonic${passPhrase}NFKD"
        return pbkdf2(mnemonics.joinToString(" "), salt)
    }

    fun generateMnemonics(): List<String> {
        //generate entropy
        val entropy = generateEntropy()

        //generate checksum
        val checksum = generateChecksum(entropy)

        //generate word list
        val wordList = mnemonicWordListProvider.wordList
        val combinedBinaryString = (entropy + checksum).let {
            it.joinToString("") { byte ->
                (byte.toInt() and 0xFF).toString(2).padStart(8, '0')
            }
        }

        return combinedBinaryString.chunked(11) { chunk ->
            if (chunk.length < 11) return@chunked -1
            return@chunked chunk.toString().toInt(2)
        }.mapNotNull { index ->
            if (index in wordList.indices) wordList[index] else null
        }
    }


    private fun generateEntropy(): ByteArray {
        //entropy_bits + checksum_bits = 11 * mnemonic_count
        //checksum_bits = entropy_bits / 32
        val entropyBits = 11 * mnemonicCount * 32 / 33
        val securityRandomProvider = getSecurityRandomProvider()
        return securityRandomProvider.nextBytes(entropyBits / 8)
    }

    private fun generateChecksum(entropy: ByteArray): ByteArray {
        //checksum_bits = entropy_bits / 32
        val checksumBits = entropy.size * 8 / 32
        //calculate sha-256 of entropy
        val digest = entropy.sha256()
        return digest.getFirstNBits(checksumBits)
    }
}