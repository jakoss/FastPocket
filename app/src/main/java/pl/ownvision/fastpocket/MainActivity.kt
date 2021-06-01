package pl.ownvision.fastpocket

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import pl.ownvision.fastpocket.list.ListScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authorizationSettings: AuthorizationSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ListScreen(authorizationSettings)
        }
    }
}