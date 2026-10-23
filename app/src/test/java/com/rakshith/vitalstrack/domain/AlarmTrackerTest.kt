package com.rakshith.vitalstrack.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmTrackerTest {

    private val high = Alarm(AlarmType.HEART_RATE_HIGH, Priority.HIGH, "Heart rate high")

    @Test
    fun sameAlarm_isReportedOnlyOnce() {
        val tracker = AlarmTracker()
        assertEquals(1, tracker.newAlarms(listOf(high)).size)
        assertTrue(tracker.newAlarms(listOf(high)).isEmpty())
    }

    @Test
    fun alarmReturnsAfterClearing_isReportedAgain() {
        val tracker = AlarmTracker()
        tracker.newAlarms(listOf(high))
        tracker.newAlarms(emptyList())
        assertEquals(1, tracker.newAlarms(listOf(high)).size)
    }

    @Test
    fun escalation_isReportedAsNew() {
        val tracker = AlarmTracker()
        val medium = Alarm(AlarmType.SPO2_LOW, Priority.MEDIUM, "SpO2 low")
        val critical = Alarm(AlarmType.SPO2_LOW, Priority.HIGH, "SpO2 critically low")
        tracker.newAlarms(listOf(medium))
        assertEquals(listOf(critical), tracker.newAlarms(listOf(critical)))
    }
}
