package pl.ownvision.fastpocket.infrastructure.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

abstract class SettingsBaseClass(context: Context, name: String) {
    private val Context.privateStore: DataStore<Preferences> by preferencesDataStore(name)
    val store: DataStore<Preferences> = context.privateStore

    suspend fun clear() {
        store.edit {
            clear()
        }
    }

    protected fun <T : Any> accessor(key: Preferences.Key<T>, defaultValue: T) =
        SettingsAccessor(key, store, defaultValue)

    protected fun <T : Any> nullableAccessor(key: Preferences.Key<T>) =
        NullableSettingsAccessor(key, store)
}