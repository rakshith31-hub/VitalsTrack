package com.rakshith.vitalstrack.domain

/** One set of vital signs from the (simulated) device. */
data class Reading(
    val heartRate: Int,      // beats per minute
    val spo2: Int,           // oxygen saturation, %
    val temperature: Double, // degrees Celsius
    val timestamp: Long
)

enum class Priority { MEDIUM, HIGH }

enum class AlarmType { HEART_RATE_HIGH, HEART_RATE_LOW, SPO2_LOW, TEMP_HIGH, TEMP_LOW }

/** An alarm. The message has no live values, so the same condition compares equal. */
data class Alarm(
    val type: AlarmType,
    val priority: Priority,
    val message: String
)
