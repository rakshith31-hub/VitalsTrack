package com.rakshith.vitalstrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmEvaluatorTest {

    private fun reading(hr: Int = 72, spo2: Int = 98, temp: Double = 36.6) =
        Reading(hr, spo2, temp, 0L)

    @Test
    fun normalReading_hasNoAlarms() {
        assertTrue(AlarmEvaluator.evaluate(reading()).isEmpty())
    }

    @Test
    fun highHeartRate_isHighPriority() {
        val alarms = AlarmEvaluator.evaluate(reading(hr = 130))
        assertEquals(1, alarms.size)
        assertEquals(AlarmType.HEART_RATE_HIGH, alarms[0].type)
        assertEquals(Priority.HIGH, alarms[0].priority)
    }

    @Test
    fun lowHeartRate_isHighPriority() {
        val alarms = AlarmEvaluator.evaluate(reading(hr = 40))
        assertEquals(AlarmType.HEART_RATE_LOW, alarms[0].type)
    }

    @Test
    fun spo2_mediumThenHigh() {
        assertEquals(Priority.MEDIUM, AlarmEvaluator.evaluate(reading(spo2 = 92))[0].priority)
        assertEquals(Priority.HIGH, AlarmEvaluator.evaluate(reading(spo2 = 85))[0].priority)
    }

    @Test
    fun boundaryValues_doNotAlarm() {
        // limits are strict: exactly 120 bpm / 50 bpm / 94 % / 38.0 C are still normal
        assertTrue(AlarmEvaluator.evaluate(reading(hr = 120)).isEmpty())
        assertTrue(AlarmEvaluator.evaluate(reading(hr = 50)).isEmpty())
        assertTrue(AlarmEvaluator.evaluate(reading(spo2 = 94)).isEmpty())
        assertTrue(AlarmEvaluator.evaluate(reading(temp = 38.0)).isEmpty())
    }

    @Test
    fun multipleProblems_giveMultipleAlarms() {
        val alarms = AlarmEvaluator.evaluate(reading(hr = 135, spo2 = 88, temp = 38.6))
        assertEquals(3, alarms.size)
    }
}
