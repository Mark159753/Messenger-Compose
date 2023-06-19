package com.example.messenger.data.local.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
){

    private val Context.sessionData: DataStore<Preferences> by preferencesDataStore(name = SESSION_PREFERENCES_NAME)

    companion object{
        private const val SESSION_PREFERENCES_NAME = "session_data"

        private val AUTH_TOKEN_SESSION = stringPreferencesKey("AUTH_TOKEN_SESSION")
        private val REFRESH_TOKEN_SESSION = stringPreferencesKey("REFRESH_TOKEN_SESSION")
    }

    val authToken: Flow<String?> = context.sessionData.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[AUTH_TOKEN_SESSION]
    }

    val refreshToken: Flow<String?> = context.sessionData.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[REFRESH_TOKEN_SESSION]
    }

    suspend fun saveAuthToken(token: String?){
        context.sessionData.edit { preferences ->
            if (token.isNullOrBlank()){
                preferences.remove(AUTH_TOKEN_SESSION)
            }else{
                preferences[AUTH_TOKEN_SESSION] = token
            }
        }
    }

    suspend fun saveRefreshToken(token: String?){
        context.sessionData.edit { preferences ->
            if (token.isNullOrBlank()){
                preferences.remove(REFRESH_TOKEN_SESSION)
            }else{
                preferences[REFRESH_TOKEN_SESSION] = token
            }
        }
    }

    suspend fun clear(){
        context.sessionData.edit { preferences ->
            preferences.clear()
        }
    }

}