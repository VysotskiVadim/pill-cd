package dev.vadzimv.pillcd

import android.app.Activity
import android.app.Application
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dev.vadzimv.pillcd.mainscreen.MainScreen
import dev.vadzimv.pillcd.mainscreen.SharedPreferencesCoolDownStorage
import dev.vadzimv.pillcd.mainscreen.MainScreenViewModel
import dev.vadzimv.pillcd.ui.theme.PillCoolDoownTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
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
                    val state by screenViewModel.viewModel.mainScreenState.collectAsState()
                    MainScreen(state) {
                        screenViewModel.viewModel.apply(it)
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

class AndroidViewModel(app: Application) : androidx.lifecycle.AndroidViewModel(app) {
    val viewModel: MainScreenViewModel = MainScreenViewModel(
        addCalendarEvent = { event ->
            activityActionChannel.trySend {
                it.insertEvent(event)
            }
        },
        currentTimeProvider = { Date().time },
        pillsCoolDownStorage = SharedPreferencesCoolDownStorage(app.applicationContext),
        scope = viewModelScope
    )

    val activityActionChannel = Channel<ActivityAction>(BUFFERED)
}

typealias ActivityAction = (Activity) -> Unit