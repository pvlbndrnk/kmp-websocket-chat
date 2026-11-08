package com.pvlbndrnk.chat.session.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.pvlbndrnk.chat.session.data.local.SessionStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSessionStore(
    context: Context,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SessionStore {

    private companion object {
        const val ENCRYPTED_TOKEN_KEY = "encrypted_refresh_token"
        const val ENCRYPTED_TOKEN_KEY_ALIAS = "chat_refresh_token_aes_key"
    }

    private val preferences = context.applicationContext.getSharedPreferences(
        "auth_token_store",
        Context.MODE_PRIVATE,
    )

    override suspend fun read(): String? = withContext(coroutineDispatcher) {
        val encryptedToken =
            preferences.getString(ENCRYPTED_TOKEN_KEY, null) ?: return@withContext null
        try {
            return@withContext decryptToken(encryptedToken)
        } catch (_: IllegalArgumentException) {
            clear()
            return@withContext null
        } catch (_: AEADBadTagException) {
            clear()
            return@withContext null
        }
    }

    override suspend fun write(token: String) = withContext(coroutineDispatcher) {
        val encryptedToken = encryptToken(token)
        preferences.edit(commit = true) {
            putString(ENCRYPTED_TOKEN_KEY, encryptedToken)
        }
    }

    override suspend fun clear() = withContext(coroutineDispatcher) {
        preferences.edit(commit = true) {
            remove(ENCRYPTED_TOKEN_KEY)
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val existingKey = keyStore.getKey(ENCRYPTED_TOKEN_KEY_ALIAS, null)
        if (existingKey != null) return existingKey as SecretKey

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore",
        )
        val keySpec = KeyGenParameterSpec.Builder(
            ENCRYPTED_TOKEN_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    private fun encryptToken(token: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }
        val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val ciphertext = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        return "$iv:$ciphertext"
    }

    private fun decryptToken(token: String): String {
        val parts = token.split(":", limit = 2).apply {
            require(size == 2) { "Invalid refresh token storage format" }
        }
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(128, iv),
            )
        }
        val tokenBytes = cipher.doFinal(ciphertext)
        return String(tokenBytes, Charsets.UTF_8)
    }
}
