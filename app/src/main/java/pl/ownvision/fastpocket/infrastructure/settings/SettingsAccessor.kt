package pl.ownvision.fastpocket.infrastructure.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsAccessor<TType : Any>(
    private val key: Preferences.Key<TType>,
    private val dataStore: DataStore<Preferences>,
    private val defaultValue: TType,
) {
    suspend fun write(value: TType) {
        dataStore.edit { settings ->
            settings[key] = value
        }
    }

    suspend fun read(): TType {
        return listen().first()
    }

    fun listen(): Flow<TType> {
        return dataStore.data.map { it[key] ?: defaultValue }
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(key)
        }
    }
}

class NullableSettingsAccessor<TType : Any>(
    private val key: Preferences.Key<TType>,
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun write(value: TType?) {
        if (value == null) {
            clear()
        } else {
            dataStore.edit { settings ->
                settings[key] = value
            }
        }
    }

    suspend fun read(): TType? {
        return listen().first()
    }

    fun listen(): Flow<TType?> {
        return dataStore.data.map { it[key] }
    }

    suspend fun clear() {
        dataStore.edit {
            it.remove(key)
        }
    }
}