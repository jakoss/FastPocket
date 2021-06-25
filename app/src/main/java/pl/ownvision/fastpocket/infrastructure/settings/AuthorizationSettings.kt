package pl.ownvision.fastpocket.infrastructure.settings

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// TODO : encrypt settings: https://github.com/StylingAndroid/DataStore
@Singleton
class AuthorizationSettings @Inject constructor(@ApplicationContext context: Context) :
    SettingsBaseClass(context, "settings") {
    val accessToken = nullableAccessor(key = stringPreferencesKey("access_token"))
    val username = nullableAccessor(key = stringPreferencesKey("username"))
}