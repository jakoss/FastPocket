package pl.ownvision.fastpocket.list

import android.content.Intent
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import pl.ownvision.fastpocket.authentication.AuthenticationActivity
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import pl.ownvision.fastpocket.infrastructure.ui.theme.FastPocketTheme

@Composable
fun ListScreen(authorizationSettings: AuthorizationSettings) {
    val context = LocalContext.current
    FastPocketTheme {
        Surface(color = MaterialTheme.colors.background) {
            val accessTokenState by
            authorizationSettings.accessToken.listen().collectAsState(initial = null)
            if (accessTokenState != null) {
                Hello(name = "User logged In!")
            } else {
                Button(onClick = {
                    context.startActivity(Intent(context, AuthenticationActivity::class.java))
                }) {
                    Text(text = "Log in")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Hello(name: String = "Test") {
    Text(text = name)
}