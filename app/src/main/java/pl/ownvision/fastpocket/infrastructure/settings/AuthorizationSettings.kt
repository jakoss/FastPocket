package pl.ownvision.fastpocket.infrastructure.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// TODO : encrypt settings: https://github.com/StylingAndroid/DataStore
@Singleton
class AuthorizationSettings @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    val accessToken = context.dataStore.accessor(KEY_ACCESS_TOKEN)
    val username = context.dataStore.accessor(KEY_USERNAME)

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }
}