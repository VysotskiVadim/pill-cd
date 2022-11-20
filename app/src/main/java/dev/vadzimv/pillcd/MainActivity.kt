package dev.vadzimv.pillcd

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import dev.vadzimv.pillcd.ui.theme.PillCoolDoownTheme
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PillCoolDoownTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Screen { duration, title ->
                        insertEvent(duration, title)
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
        Screen { _,_ -> }
    }
}

@Composable
private fun Screen(
    insertEvent: (duration: Long, title: String) -> Unit
) {
    Column() {
        var coolDownValue by remember { mutableStateOf("6") }
        TextField(
            label = { Text("Cool down in hours") },
            value = coolDownValue,
            onValueChange = { coolDownValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        var coolDownTitle by remember { mutableStateOf("Ibum") }
        TextField(
            label = { Text("Title") },
            value = coolDownTitle,
            onValueChange = { coolDownTitle = it }
        )
        Button(onClick = {
            insertEvent(TimeUnit.HOURS.toMillis(coolDownValue.toLong()), coolDownTitle)
        }) {
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