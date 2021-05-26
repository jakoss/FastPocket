package pl.ownvision.fastpocket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import pl.ownvision.fastpocket.authentication.AuthenticationActivity
import pl.ownvision.fastpocket.infrastructure.settings.AuthorizationSettings
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authorizationSettings: AuthorizationSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.testBtn)

        btn.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }

        authorizationSettings.accessToken.listen().asLiveData().observe(this@MainActivity) {
            if (it != null) {
                btn.text = "Logged in"
            }
        }
    }
}