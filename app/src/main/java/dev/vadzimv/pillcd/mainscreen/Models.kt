package dev.vadzimv.pillcd.mainscreen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ScreenState(
    val coolDownTimeFormatted: String,
    val title: String,
    val addToCalendarButtonEnabled: Boolean,
    val latestPills: List<PillCoolDown>
)

data class PillCoolDown(
    val duration: Duration,
    val title: String
)

sealed interface Action {
    data class CoolDownTimeChanged(val newValue: String) : Action
    object AddToCalendarClicked : Action
    data class TitleChanged(val newTitle: String) : Action
}

class Store(
    val currentTimeProvider: () -> Long,
    val addCalendarEvent: (CalendarEvent) -> Unit
) {

    companion object {
        val initialScreenState = ScreenState(
            coolDownTimeFormatted = "5",
            title = "Ibum",
            addToCalendarButtonEnabled = true,
            latestPills = emptyList()
        )
    }

    private val _state = MutableStateFlow(initialScreenState)
    val mainScreenState: StateFlow<ScreenState> = _state

    fun apply(action: Action) {
        when (action) {
            Action.AddToCalendarClicked -> {
                val currentTime = currentTimeProvider()
                val coolDownDuration = mainScreenState.value.coolDownTimeFormatted.toLong().toDuration(DurationUnit.HOURS)
                val title = mainScreenState.value.title
                val event = CalendarEvent(
                    title = title,
                    begin = currentTime,
                    end = currentTime + coolDownDuration.inWholeMilliseconds
                )
                addCalendarEvent(event)
                updateLatestCoolDown(coolDownDuration, title)
            }
            is Action.CoolDownTimeChanged -> {
                val longValue = action.newValue.toLongOrNull()
                val isValidDuration =
                    action.newValue.isBlank() || (longValue != null && longValue > 0)
                if (isValidDuration) {
                    updateState {
                        it.copy(
                            coolDownTimeFormatted = action.newValue,
                            addToCalendarButtonEnabled = action.newValue.isNotBlank()
                        )
                    }
                }
            }
            is Action.TitleChanged ->
                updateState { it.copy(title = action.newTitle) }
        }
    }

    private fun updateLatestCoolDown(coolDownDuration: Duration, title: String) {
        updateState { state ->
            state.copy(
                latestPills = state.latestPills.toMutableList().apply {
                    val indexOfTheSame =
                        indexOfFirst { it.duration == coolDownDuration && it.title == title }
                    if (indexOfTheSame == -1) {
                        add(
                            0, PillCoolDown(
                                title = title,
                                duration = coolDownDuration
                            )
                        )
                    } else {
                        val element = removeAt(indexOfTheSame)
                        add(0, element)
                    }
                    if (size > 3) {
                        removeLast()
                    }
                }
            )
        }
    }

    private fun updateState(block: (ScreenState) -> ScreenState) {
        _state.value = block(_state.value)
    }
}

data class CalendarEvent(
    val title: String,
    val begin: Long,
    val end: Long,
)