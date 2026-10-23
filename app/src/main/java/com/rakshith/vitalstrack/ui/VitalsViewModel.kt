package com.rakshith.vitalstrack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rakshith.vitalstrack.data.AlarmDatabase
import com.rakshith.vitalstrack.data.AlarmEntity
import com.rakshith.vitalstrack.data.DeviceSimulator
import com.rakshith.vitalstrack.domain.Alarm
import com.rakshith.vitalstrack.domain.AlarmEvaluator
import com.rakshith.vitalstrack.domain.AlarmTracker
import com.rakshith.vitalstrack.domain.PacketParser
import com.rakshith.vitalstrack.domain.Reading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val reading: Reading? = null,
    val activeAlarms: List<Alarm> = emptyList(),
    val acknowledged: Boolean = false,
    val droppedPackets: Int = 0
)

class VitalsViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AlarmDatabase.get(app).alarmDao()
    private val tracker = AlarmTracker()

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    val alarmHistory: StateFlow<List<AlarmEntity>> =
        dao.recent().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            DeviceSimulator().packets().collect { handlePacket(it) }
        }
    }

    private suspend fun handlePacket(packet: String) {
        val reading = PacketParser.parse(packet)
        if (reading == null) {
            _state.update { it.copy(droppedPackets = it.droppedPackets + 1) }
            return
        }

        val alarms = AlarmEvaluator.evaluate(reading)
        val fresh = tracker.newAlarms(alarms)

        for (alarm in fresh) {
            dao.insert(
                AlarmEntity(
                    type = alarm.type.name,
                    priority = alarm.priority.name,
                    message = alarm.message,
                    timestamp = reading.timestamp
                )
            )
        }

        _state.update {
            it.copy(
                reading = reading,
                activeAlarms = alarms,
                // a new alarm must be acknowledged again; no alarms -> reset
                acknowledged = if (alarms.isEmpty() || fresh.isNotEmpty()) false else it.acknowledged
            )
        }
    }

    fun acknowledge() {
        _state.update { it.copy(acknowledged = true) }
    }
}
