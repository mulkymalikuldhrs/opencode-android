package ai.opencode.mobile.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for sensitive data like API keys and server credentials.
 * Uses AndroidX EncryptedSharedPreferences to encrypt values at rest.
 */
object SecureStorage {

    private const val FILE_NAME = "opencode_secure_prefs"
    private const val KEY_SERVER_URL = "server_url"
    private const val KEY_SERVER_PASSWORD = "server_password"
    private const val KEY_API_KEY = "api_key"

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            getMasterKey(context),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save server URL (non-sensitive, but kept together for consistency)
     */
    fun saveServerUrl(context: Context, url: String) {
        getEncryptedPrefs(context).edit().putString(KEY_SERVER_URL, url).apply()
    }

    /**
     * Get saved server URL
     */
    fun getServerUrl(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_SERVER_URL, null)
    }

    /**
     * Save server password (encrypted at rest)
     */
    fun saveServerPassword(context: Context, password: String) {
        getEncryptedPrefs(context).edit().putString(KEY_SERVER_PASSWORD, password).apply()
    }

    /**
     * Get saved server password
     */
    fun getServerPassword(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_SERVER_PASSWORD, null)
    }

    /**
     * Save API key (encrypted at rest)
     */
    fun saveApiKey(context: Context, apiKey: String) {
        getEncryptedPrefs(context).edit().putString(KEY_API_KEY, apiKey).apply()
    }

    /**
     * Get saved API key
     */
    fun getApiKey(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_API_KEY, null)
    }

    /**
     * Clear all stored credentials
     */
    fun clearAll(context: Context) {
        getEncryptedPrefs(context).edit().clear().apply()
    }

    /**
     * Migrate from plain SharedPreferences to encrypted storage.
     * Call once on app upgrade, then remove the old plain values.
     */
    fun migrateFromPlainPrefs(context: Context) {
        val plainPrefs = context.getSharedPreferences("opencode", Context.MODE_PRIVATE)
        val encryptedPrefs = getEncryptedPrefs(context)

        val serverUrl = plainPrefs.getString("server_url", null)
        val password = plainPrefs.getString("server_password", null)

        if (serverUrl != null) {
            encryptedPrefs.edit().putString(KEY_SERVER_URL, serverUrl).apply()
            plainPrefs.edit().remove("server_url").apply()
        }

        if (password != null) {
            encryptedPrefs.edit().putString(KEY_SERVER_PASSWORD, password).apply()
            plainPrefs.edit().remove("server_password").apply()
        }
    }
}
