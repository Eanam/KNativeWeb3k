package com.cmoigo.web3k

import com.cmoigo.web3k.utils.getFirstNBits
import com.cmoigo.web3k.utils.toByteArray
import com.cmoigo.web3k.utils.toHexStringWithPaddingZero
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

    @Test
    fun testIntToByteArray() {
        var i = 0x80000000.toInt()
        var result = i.toByteArray().toHexStringWithPaddingZero()

        assertEquals("80000000", result)

        val i2 = 0x80000000.toUInt().or(44.toUInt())
        assertEquals((-2147483604).toUInt(), i2)

    }

}