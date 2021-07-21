package pl.ownvision.fastpocket.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.ownvision.fastpocket.infrastructure.settings.ApplicationSettings
import pl.ownvision.fastpocket.infrastructure.ui.theme.FastPocketTheme

@Composable
fun SettingsScreen(applicationSettings: ApplicationSettings, navController: NavController) {
    FastPocketTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Settings") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            content = {
                SettingsList(applicationSettings = applicationSettings)
            }
        )
    }
}

@Composable
fun SettingsList(applicationSettings: ApplicationSettings) {
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        val useExternalBrowserState by applicationSettings
            .useExternalBrowser
            .listen()
            .collectAsState(initial = false)
        SwitchPreferenceEntry(checked = useExternalBrowserState) {
            coroutineScope.launch {
                applicationSettings.useExternalBrowser.write(it)
            }
        }
    }
}

@Preview
@Composable
fun SwitchPreferenceEntry(checked: Boolean = false, onCheckedChange: ((Boolean) -> Unit)? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked, onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = "Open links in external browser", style = MaterialTheme.typography.h5)
            Text(
                text = "By default links are opened in Chrome Web Tab",
                style = MaterialTheme.typography.body1
            )
        }
    }
}