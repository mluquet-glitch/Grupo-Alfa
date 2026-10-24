package com.example.grupoalfa.data.session

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Guarda el JWT de la sesión cifrado con una clave AES/GCM alojada en Android Keystore.
 *
 * El token nunca se escribe en texto plano: SharedPreferences solo contiene el
 * criptograma y su IV. La clave privada no sale del Keystore del dispositivo.
 */
object SecureCredentialStore {

    private const val PREFERENCES_NAME = "grupo_alfa_secure_session"
    private const val KEY_ALIAS = "grupo_alfa_auth_key"
    private const val TOKEN_KEY = "auth_token"
    private const val IV_KEY = "auth_token_iv"

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH_BITS = 128

    private lateinit var preferences: SharedPreferences

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    fun init(context: Context) {
        if (!::preferences.isInitialized) {
            preferences = context.applicationContext.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    @Synchronized
    fun saveToken(token: String) {
        ensureInitialized()

        if (token.isBlank()) {
            clear()
            return
        }

        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        preferences.edit()
            .putString(TOKEN_KEY, Base64.encodeToString(encryptedToken, Base64.NO_WRAP))
            .putString(IV_KEY, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .apply()
    }

    @Synchronized
    fun getToken(): String? {
        ensureInitialized()

        val encryptedToken = preferences.getString(TOKEN_KEY, null) ?: return null
        val initializationVector = preferences.getString(IV_KEY, null) ?: return null

        return runCatching {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(
                    Cipher.DECRYPT_MODE,
                    getOrCreateKey(),
                    GCMParameterSpec(
                        GCM_TAG_LENGTH_BITS,
                        Base64.decode(initializationVector, Base64.NO_WRAP)
                    )
                )
            }
            val decryptedToken = cipher.doFinal(
                Base64.decode(encryptedToken, Base64.NO_WRAP)
            )
            decryptedToken.toString(Charsets.UTF_8)
        }.getOrElse {
            // Si la clave cambió o los datos son inválidos, se descarta la sesión.
            clear()
            null
        }
    }

    @Synchronized
    fun clear() {
        ensureInitialized()
        preferences.edit().clear().apply()
    }

    private fun getOrCreateKey(): SecretKey {
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun ensureInitialized() {
        check(::preferences.isInitialized) {
            "SecureCredentialStore.init(context) debe ejecutarse antes de usar el almacén."
        }
    }
}
