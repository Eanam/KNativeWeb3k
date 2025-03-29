package com.cmoigo.web3k.crypto.bip39

import com.cmoigo.web3k.getSecurityRandomProvider
import com.cmoigo.web3k.readResourceFile
import com.cmoigo.web3k.sha256
import com.cmoigo.web3k.utils.getFirstNBits

class BIP39WalletGenerator(
    var mnemonicWordListPath: String = "bip39-en-mnemonic-word-list.txt",
    var mnemonicCount: Int = 12
) {

    companion object {
        fun create(
            initialization: (BIP39WalletGenerator.() -> Unit)? = null
        ): BIP39WalletGenerator {
            return BIP39WalletGenerator().apply {
                initialization?.invoke(this)
            }
        }
    }

    private fun generateMnemonics() {
        //generate entropy
        val entropy = generateEntropy()

        //generate checksum
        val checksum = generateChecksum(entropy)

        //generate word list
        val wordList = getMnemonicWordList() ?: return
        val combinedBinaryString = (entropy + checksum).let {
            it.joinToString("") { byte ->
                (byte.toInt() and 0xFF).toString(2).padStart(8, '0')
            }
        }

        combinedBinaryString.chunked(11) { chunk ->
            val index = chunk.toString().toInt(2)
            wordList[index]
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

    fun getMnemonicWordList(): List<String>? {
        return try {
            val fileContent = readResourceFile(filePath = mnemonicWordListPath)
            fileContent?.split("\n")
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}