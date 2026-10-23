# VitalsTrack

A small Android app that shows live vital signs (heart rate, SpO2, temperature) from a **simulated** device, raises alarms when values leave fixed limits, and keeps an alarm history.

> Learning project. The device is simulated (no real Bluetooth/serial hardware) and the alarm limits are illustrative, **not clinical values**. This is not a medical device.

## Tech
Kotlin · Jetpack Compose · MVVM · Room (SQLite) · Kotlin Coroutines/Flow · JUnit

## Architecture
```
DeviceSimulator --packet string--> VitalsViewModel --> PacketParser --> AlarmEvaluator --> AlarmTracker
 (fake device, 1/s,                      |                (checksum +       (limits ->      (log each alarm
  sometimes corrupted)                   |                 range check)      alarms)          once)
                                         |--> Room (alarms table)
                                         +--> StateFlow --> VitalsScreen (Compose)
```
- `domain/` pure Kotlin logic (no Android code), fully unit tested
- `data/` simulator and Room database
- `ui/` ViewModel and Compose screen

## Device packet format
`$VT,<heartRate>,<spo2>,<temp>*<checksum>` e.g. `$VT,72,98,36.6*XX`.
The checksum is the XOR of the characters between `$` and `*`, written as 2 hex digits. Bad checksum, wrong field count, non-numeric or impossible values are rejected and counted as "dropped packets".

## Alarm rules
| Vital | MEDIUM | HIGH |
|---|---|---|
| Heart rate | - | > 120 or < 50 bpm |
| SpO2 | < 94 % | < 90 % |
| Temperature | > 38.0 C or < 35.0 C | > 39.5 C |

An alarm is logged once when it starts (not every second), must be acknowledged, and an escalation (e.g. MEDIUM to HIGH) counts as new.

## Requirements to tests
| Requirement | Test |
|---|---|
| Valid packets are decoded | `PacketParserTest.validPacket_isParsed` |
| Corrupted packets are rejected | `wrongChecksum`, `missingStartOrStar`, `wrongFieldCount`, `nonNumericField`, `impossibleValue` |
| Normal vitals raise no alarm | `AlarmEvaluatorTest.normalReading_hasNoAlarms` |
| Limits raise the right priority | `highHeartRate`, `lowHeartRate`, `spo2_mediumThenHigh` |
| Boundary values behave as specified | `boundaryValues_doNotAlarm` |
| Several problems at once | `multipleProblems_giveMultipleAlarms` |
| An alarm is logged once, not every second | `AlarmTrackerTest.sameAlarm_isReportedOnlyOnce` |
| Alarm can re-trigger after clearing | `alarmReturnsAfterClearing_isReportedAgain` |
| Escalation is reported | `escalation_isReportedAsNew` |

15 unit tests in total.

## Run it
1. Install Android Studio and open this folder (let it sync Gradle; accept any suggested plugin/Gradle upgrade).
2. Run the `app` on an emulator or phone.
3. Unit tests: `./gradlew test` (or right-click `app/src/test` > Run).

## Known limitations / next steps
- Simulated data only; real Bluetooth LE would replace `DeviceSimulator`.
- Alarm history only (readings are not stored).
- No sound or notification on alarm; no settings screen for limits.
