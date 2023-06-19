package com.example.messenger.data.local.proto.user

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import com.example.messenger.User
import com.example.messenger.data.network.models.user.UserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

private val Context.userPreferencesStore: DataStore<User> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = UserSerializer
)

class UserLocalDataSource @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val TAG: String = "UserLocalDataSource"

    val userPreferencesFlow: Flow<User> = context.userPreferencesStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(User.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateUser(user: UserResponse) {
        context.userPreferencesStore.updateData { preferences ->
            preferences
                .toBuilder()
                .setId(user.id)
                .setFirstName(user.first_name)
                .setLastName(user.last_name)
                .setNickName(user.nick_name)
                .setPhone(user.phone)
                .setEmail(user.email)
                .setAvatar(user.avatar?.path ?: "")
                .setCreatedAt(user.created_at)
                .setUpdatedAt(user.updated_at)
                .build()
        }
    }

    suspend fun updateIsOnline(isOnline: Boolean) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setIsOnline(isOnline).build()
        }
    }

    suspend fun updateFirstName(firstName: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setFirstName(firstName).build()
        }
    }

    suspend fun updateLastName(lastName: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setLastName(lastName).build()
        }
    }

    suspend fun updateNickName(nick: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setNickName(nick).build()
        }
    }

    suspend fun updateEmail(email: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setEmail(email).build()
        }
    }

    suspend fun updatePhone(phone: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setPhone(phone).build()
        }
    }

    suspend fun updateAvatar(avatar: String) {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setAvatar(avatar).build()
        }
    }

    suspend fun clear() {
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }

}