package dev.vadzimv.pillcd

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

data class ScreenState(
    val coolDownTimeFormatted: String,
    val title: String,
    val addToCalendarButtonEnabled: Boolean
) {
}

sealed interface Action {
    data class CoolDownTimeChanged(val newValue: String): Action
    object AddToCalendarClicked: Action
    data class TitleChanged(val newTitle: String): Action
}

class Store(
    val currentTimeProvider: () -> Long,
    val addCalendarEvent: (CalendarEvent) -> Unit
) {

    companion object {
        val initialScreenState = ScreenState(
            coolDownTimeFormatted = "5",
            title = "Ibum",
            addToCalendarButtonEnabled = true
        )
    }

    private val _state = MutableStateFlow(initialScreenState)
    val state: StateFlow<ScreenState> = _state

    fun apply(action: Action) {
        when (action) {
            Action.AddToCalendarClicked -> {
                val currentTime = currentTimeProvider()
                val event = CalendarEvent(
                    title = state.value.title,
                    begin = currentTime,
                    end = currentTime + TimeUnit.HOURS.toMillis(state.value.coolDownTimeFormatted.toLong())
                )
                addCalendarEvent(event)
            }
            is Action.CoolDownTimeChanged -> {
                val longValue = action.newValue.toLongOrNull()
                val isValidDuration = action.newValue.isBlank() || (longValue != null && longValue > 0)
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

    private fun updateState(block: (ScreenState) -> ScreenState) {
        _state.value = block(_state.value)
    }
}

data class CalendarEvent(
    val title: String,
    val begin: Long,
    val end: Long,
)