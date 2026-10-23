package com.rakshith.vitalstrack.domain

/**
 * Checks a reading against fixed limits.
 * NOTE: limits are illustrative for a learning project, NOT clinical values.
 */
object AlarmEvaluator {
    const val HR_HIGH = 120
    const val HR_LOW = 50
    const val SPO2_CRITICAL = 90
    const val SPO2_LOW = 94
    const val TEMP_HIGH_CRITICAL = 39.5
    const val TEMP_HIGH = 38.0
    const val TEMP_LOW = 35.0

    fun evaluate(r: Reading): List<Alarm> {
        val alarms = mutableListOf<Alarm>()

        if (r.heartRate > HR_HIGH) {
            alarms += Alarm(AlarmType.HEART_RATE_HIGH, Priority.HIGH, "Heart rate high")
        } else if (r.heartRate < HR_LOW) {
            alarms += Alarm(AlarmType.HEART_RATE_LOW, Priority.HIGH, "Heart rate low")
        }

        if (r.spo2 < SPO2_CRITICAL) {
            alarms += Alarm(AlarmType.SPO2_LOW, Priority.HIGH, "SpO2 critically low")
        } else if (r.spo2 < SPO2_LOW) {
            alarms += Alarm(AlarmType.SPO2_LOW, Priority.MEDIUM, "SpO2 low")
        }

        if (r.temperature > TEMP_HIGH_CRITICAL) {
            alarms += Alarm(AlarmType.TEMP_HIGH, Priority.HIGH, "Temperature very high")
        } else if (r.temperature > TEMP_HIGH) {
            alarms += Alarm(AlarmType.TEMP_HIGH, Priority.MEDIUM, "Temperature high")
        } else if (r.temperature < TEMP_LOW) {
            alarms += Alarm(AlarmType.TEMP_LOW, Priority.MEDIUM, "Temperature low")
        }

        return alarms
    }
}
