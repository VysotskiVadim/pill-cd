package dev.vadzimv.pillcd

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import dev.vadzimv.pillcd.ui.theme.PillCoolDoownTheme
import java.util.Date

class MainActivity : ComponentActivity() {

    private val screenViewModel by viewModels<AndroidViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillCoolDoownTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state by screenViewModel.store.state.collectAsState()
                    Screen(state) {
                        screenViewModel.store.apply(it)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PillCoolDoownTheme {
        Screen(Store.initialScreenState) { }
    }
}

@Composable
private fun Screen(
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
    }
}

fun Activity.insertEvent(durationMillis: Long, title: String) {
    val addEventIntent = Intent(Intent.ACTION_EDIT).apply {
        type = "vnd.android.cursor.item/event"
        putExtra(CalendarContract.Events.TITLE, title)
        val now = Date().time
        putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            now
        )
        putExtra(
            CalendarContract.EXTRA_EVENT_END_TIME,
            now + durationMillis
        )
        putExtra(CalendarContract.Events.ALL_DAY, false)
    }
    startActivity(addEventIntent)
}

class AndroidViewModel : ViewModel() {
    val store: Store = Store()
}