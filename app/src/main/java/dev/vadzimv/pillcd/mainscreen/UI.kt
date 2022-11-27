package dev.vadzimv.pillcd.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.vadzimv.pillcd.ui.theme.PillCoolDoownTheme

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PillCoolDoownTheme {
        MainScreen(Store.initialScreenState) { }
    }
}

@Composable
fun MainScreen(
    state: ScreenState,
    actions: (Action) -> Unit
) {
    Column {
        TextField(
            label = { Text("Cool down in hours") },
            value = state.coolDownTimeFormatted,
            onValueChange = { actions(Action.CoolDownTimeChanged(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            label = { Text("Title") },
            value = state.title,
            onValueChange = { actions(Action.TitleChanged(it)) }
        )
        Button(
            onClick = {
                actions(Action.AddToCalendarClicked)
            },
            enabled = state.addToCalendarButtonEnabled
        ) {
            Text(text = "Add to calendar")
        }
        if (state.latestPills.isNotEmpty()) {
            Text(text = "Recently used values")
            state.latestPills.forEach { pillCoolDown ->
                Text(
                    text = "${pillCoolDown.title}: ${pillCoolDown.duration}",
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            actions(Action.LatestPillCoolDownClicked(pillCoolDown))
                        }
                )
            }
        }

    }
}