package dev.vadzimv.pillcd

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dev.vadzimv.pillcd.mainscreen.MainScreen
import dev.vadzimv.pillcd.mainscreen.Store
import dev.vadzimv.pillcd.ui.theme.PillCoolDoownTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
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
                    val state by screenViewModel.store.mainScreenState.collectAsState()
                    MainScreen(state) {
                        screenViewModel.store.apply(it)
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            screenViewModel.activityActionChannel.consumeAsFlow().collect {
                it(this@MainActivity)
            }
        }
    }
}

class AndroidViewModel : ViewModel() {
    val store: Store = Store(
        addCalendarEvent = { event ->
            activityActionChannel.trySend {
                it.insertEvent(event)
            }
        },
        currentTimeProvider = { Date().time }
    )

    val activityActionChannel = Channel<ActivityAction>(BUFFERED)
}

typealias ActivityAction = (Activity) -> Unit