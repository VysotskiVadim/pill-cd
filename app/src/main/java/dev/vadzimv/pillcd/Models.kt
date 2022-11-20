package dev.vadzimv.pillcd

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ScreenState(
    val coolDownTimeFormatted: String,
    val title: String,
    var actions: List<PlatformAction>,
    val addToCalendarButtonEnabled: Boolean
) {
}

sealed interface Action {
    data class CoolDownTimeChanged(val newValue: String): Action
    object AddToCalendarClicked: Action
    data class TitleChanged(val newTitle: String): Action
    data class PlatformActionConfirmed(val action: PlatformAction): Action
}

class Store {

    companion object {
        val initialScreenState = ScreenState(
            coolDownTimeFormatted = "5",
            title = "Ibum",
            actions = emptyList(),
            addToCalendarButtonEnabled = true
        )
    }

    private val _state = MutableStateFlow(initialScreenState)
    val state: StateFlow<ScreenState> = _state

    fun apply(action: Action) {
        when (action) {
            Action.AddToCalendarClicked -> TODO()
            is Action.CoolDownTimeChanged ->
                updateState { it.copy(
                    coolDownTimeFormatted = action.newValue,
                    addToCalendarButtonEnabled = action.newValue.isNotBlank()
                ) }
            is Action.PlatformActionConfirmed -> TODO()
            is Action.TitleChanged ->
                updateState { it.copy(title = action.newTitle) }
        }
    }

    private fun updateState(block: (ScreenState) -> ScreenState) {
        _state.value = block(_state.value)
    }
}

sealed class PlatformAction {
    data class AddEventToCalendar(
        val startTimeMilliseconds: Long,
        val endTimeMilliseconds: Long,
        val title: String
    )
}