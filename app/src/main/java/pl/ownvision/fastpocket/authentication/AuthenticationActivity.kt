package pl.ownvision.fastpocket.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.viewModel
import dagger.hilt.android.AndroidEntryPoint
import pl.ownvision.fastpocket.BuildConfig

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), MavericksView {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val customTabsIntent = CustomTabsIntent.Builder()
            .build()

        viewModel.onAsync(AuthenticationState::requestCode, deliveryMode = uniqueOnly()) { code ->
            val authorizationUrl =
                "https://getpocket.com/auth/authorize?request_token=${code}&redirect_uri=${BuildConfig.REDIRECT_URL}"

            customTabsIntent.launchUrl(this@AuthenticationActivity, authorizationUrl.toUri())
        }

        viewModel.onAsync(AuthenticationState::authorizationState) {
            // successfully logged in
            finish()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        // validate if intent was called with url
        intent?.data?.toString() ?: return

        viewModel.authorize()
    }

    override fun invalidate() {
        // TODO : handle error state
    }
}