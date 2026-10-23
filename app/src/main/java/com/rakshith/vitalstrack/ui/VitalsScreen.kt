package com.rakshith.vitalstrack.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VitalsScreen(vm: VitalsViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val history by vm.alarmHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("VitalsTrack", style = MaterialTheme.typography.headlineMedium)
        Text("Simulated device - learning project, not a medical device")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VitalCard("Heart rate", state.reading?.heartRate?.toString() ?: "--", "bpm")
            VitalCard("SpO2", state.reading?.spo2?.toString() ?: "--", "%")
            VitalCard(
                "Temp",
                state.reading?.temperature?.let { String.format(Locale.US, "%.1f", it) } ?: "--",
                "C"
            )
        }

        if (state.activeAlarms.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.activeAlarms.forEach { Text("${it.priority}: ${it.message}") }
                    if (state.acknowledged) {
                        Text("Acknowledged")
                    } else {
                        Button(onClick = { vm.acknowledge() }) { Text("Acknowledge") }
                    }
                }
            }
        } else {
            Text("All vitals normal")
        }

        Text("Dropped (invalid) packets: ${state.droppedPackets}")

        Text("Alarm history", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(history, key = { it.id }) { a ->
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(a.timestamp))
                Text("$time  ${a.priority}  ${a.message}", modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
private fun VitalCard(label: String, value: String, unit: String) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(label)
            Text("$value $unit", style = MaterialTheme.typography.titleLarge)
        }
    }
}
