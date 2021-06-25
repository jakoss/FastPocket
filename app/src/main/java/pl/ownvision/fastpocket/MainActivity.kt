package pl.ownvision.fastpocket

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.ownvision.fastpocket.infrastructure.settings.ApplicationSettings
import pl.ownvision.fastpocket.list.ListScreen
import pl.ownvision.fastpocket.settings.SettingsScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var applicationSettings: ApplicationSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "list") {
                composable("list") {
                    ListScreen(navController = navController)
                }
                composable("settings") {
                    SettingsScreen(
                        applicationSettings = applicationSettings,
                        navController = navController
                    )
                }
            }

        }
    }
}