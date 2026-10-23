package com.rakshith.vitalstrack.data

import com.rakshith.vitalstrack.domain.PacketParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Fake medical device. Emits one packet per second.
 * Mostly normal values, an abnormal burst every 40 seconds, and an
 * occasional corrupted packet to exercise the parser's error handling.
 * (Simulated: there is no real Bluetooth/serial hardware in this project.)
 */
class DeviceSimulator(
    private val random: Random = Random.Default,
    private val intervalMs: Long = 1000L
) {
    fun packets(): Flow<String> = flow {
        var tick = 0
        while (true) {
            tick++
            val abnormal = tick % 40 in 30..34

            val hr = if (abnormal) 135 else 72 + random.nextInt(-3, 4)
            val spo2 = if (abnormal) 88 else 97 + random.nextInt(-1, 2)
            val temp = if (abnormal) 38.6 else 36.6 + random.nextInt(-2, 3) / 10.0

            var packet = PacketParser.build(hr, spo2, temp)
            if (random.nextInt(15) == 0) {
                packet = packet.dropLast(1) + "Z" // break the checksum
            }
            emit(packet)
            delay(intervalMs)
        }
    }
}
