package com.cmoigo.web3k

import com.cmoigo.web3k.utils.getFirstNBits
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class UtilsTest {

    @Test
    fun testGetFirstNBits() {
        val byteArray = getSecurityRandomProvider().nextBytes(8)
        val sha256ByteArray = byteArray.sha256()

        assertEquals(1, sha256ByteArray.getFirstNBits(4).size)
        assertEquals(1, sha256ByteArray.getFirstNBits(5).size)
        assertEquals(1, sha256ByteArray.getFirstNBits(6).size)
        assertEquals(1, sha256ByteArray.getFirstNBits(7).size)
        assertEquals(1, sha256ByteArray.getFirstNBits(8).size)
    }


    @Test
    fun testBinaryString() {
        val byteArray = getSecurityRandomProvider().nextBytes(8)
        val binaryString = byteArray.joinToString("") { byte ->
            (byte.toInt() and 0xFF).toString(2).padStart(8, '0')
        }

        assertSame("", binaryString)
    }

}