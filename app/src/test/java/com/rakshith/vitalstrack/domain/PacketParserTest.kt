package com.rakshith.vitalstrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PacketParserTest {

    @Test
    fun validPacket_isParsed() {
        val packet = PacketParser.build(72, 98, 36.6)
        val r = PacketParser.parse(packet, now = 1000L)
        assertNotNull(r)
        assertEquals(72, r!!.heartRate)
        assertEquals(98, r.spo2)
        assertEquals(36.6, r.temperature, 0.001)
        assertEquals(1000L, r.timestamp)
    }

    @Test
    fun wrongChecksum_isRejected() {
        val packet = PacketParser.build(72, 98, 36.6).dropLast(1) + "Z"
        assertNull(PacketParser.parse(packet))
    }

    @Test
    fun missingStartOrStar_isRejected() {
        assertNull(PacketParser.parse("VT,72,98,36.6*00"))
        assertNull(PacketParser.parse("\$VT,72,98,36.6"))
    }

    @Test
    fun wrongFieldCount_isRejected() {
        val body = "VT,72,98"
        val packet = "\$" + body + "*" + String.format("%02X", PacketParser.checksum(body))
        assertNull(PacketParser.parse(packet))
    }

    @Test
    fun nonNumericField_isRejected() {
        val body = "VT,abc,98,36.6"
        val packet = "\$" + body + "*" + String.format("%02X", PacketParser.checksum(body))
        assertNull(PacketParser.parse(packet))
    }

    @Test
    fun impossibleValue_isRejected() {
        val packet = PacketParser.build(300, 98, 36.6)
        assertNull(PacketParser.parse(packet))
    }
}
