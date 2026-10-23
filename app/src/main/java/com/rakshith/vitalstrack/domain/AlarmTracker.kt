package com.rakshith.vitalstrack.domain

/**
 * Stops the same alarm from being logged every second while it stays active.
 * An alarm is "new" only when it was not active in the previous reading
 * (an escalation, e.g. MEDIUM -> HIGH, counts as new).
 */
class AlarmTracker {
    private var active: Set<Alarm> = emptySet()

    fun newAlarms(current: List<Alarm>): List<Alarm> {
        val now = current.toSet()
        val fresh = now - active
        active = now
        return fresh.toList()
    }
}
