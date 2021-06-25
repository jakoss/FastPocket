package pl.ownvision.fastpocket.infrastructure.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationSettings @Inject constructor(@ApplicationContext context: Context) :
    SettingsBaseClass(context, "app_settings") {

    val useExternalBrowser = accessor(
        key = booleanPreferencesKey("use_external_browser"),
        defaultValue = false
    )
}