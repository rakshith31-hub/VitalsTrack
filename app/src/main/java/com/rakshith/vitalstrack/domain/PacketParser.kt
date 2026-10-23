package com.rakshith.vitalstrack.domain

import java.util.Locale

/**
 * Device packet format (UART/NMEA style):   $VT,<hr>,<spo2>,<temp>*<checksum>
 * Example:  $VT,72,98,36.6*XX
 * The checksum is the XOR of all characters between '$' and '*', as 2 hex digits.
 * Anything malformed is rejected (parse returns null).
 */
object PacketParser {

    fun checksum(body: String): Int {
        var sum = 0
        for (c in body) sum = sum xor c.code
        return sum and 0xFF
    }

    fun build(heartRate: Int, spo2: Int, temperature: Double): String {
        val body = "VT,$heartRate,$spo2,${String.format(Locale.US, "%.1f", temperature)}"
        return "\$" + body + "*" + String.format(Locale.US, "%02X", checksum(body))
    }

    fun parse(packet: String, now: Long = System.currentTimeMillis()): Reading? {
        if (!packet.startsWith('$')) return null
        val star = packet.indexOf('*')
        if (star < 0) return null

        val body = packet.substring(1, star)
        val given = packet.substring(star + 1).toIntOrNull(16) ?: return null
        if (given != checksum(body)) return null

        val parts = body.split(',')
        if (parts.size != 4 || parts[0] != "VT") return null

        val hr = parts[1].toIntOrNull() ?: return null
        val spo2 = parts[2].toIntOrNull() ?: return null
        val temp = parts[3].toDoubleOrNull() ?: return null

        // basic range check: reject impossible values
        if (hr !in 20..250 || spo2 !in 50..100 || temp < 30.0 || temp > 45.0) return null

        return Reading(hr, spo2, temp, now)
    }
}
